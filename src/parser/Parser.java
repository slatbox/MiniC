package parser;//文件Parser.java

import java.io.*;
import lexer.*;
import symbols.*;
import inter.*;

import java.util.ArrayList;
import java.util.Hashtable;

public class Parser {
    private Lexer lex;// 这个语法分析器的词法分析器
    private Token look;// 向前看词法单元
    public Env top = null;// 当前或顶层的符号表
    int used = 0;// 用于变量声明的存储位置
    int funId = 0;
    private Hashtable<String,Fun> funEnv;
    public Parser(Lexer l) throws IOException {
        Node.initOutputStream();
        lex = l;
        move();
    }

    void move() throws IOException {
        look = lex.scan();
    }

    void error(String s) {
        throw new Error("near line " + Lexer.line + ": " + s);
    }

    void match(int t) throws IOException {
        if (look.tag == t)
            move();
        else
            error("syntax error");
    }

    public void program() throws IOException {// program->block
        this.funEnv = new Hashtable<String,Fun>();
        this.funEnv.put("printNum", Fun.printNum);
        this.funEnv.put("inputNum",Fun.inputNum);
        Fun functions = funs();
        functions.gen();
    }
    

    Stmt block() throws IOException { // block->{ decls stmts}
        match('{');
        Env savedEnv = top;
        top = new Env(top);
        Stmt s = stmts();
        match('}');
        top = savedEnv;
        return s;
    }

    void decls() throws IOException {
        while (look.tag == Tag.BASIC) {// D->type ID;
            Type p = type();
            Token tok = look;
            match(Tag.ID);
            match(';');
            Id id = new Id((Word) tok, p, used);
            top.put(tok, id);
            used = used + p.width;
        }
    }

    Stmt decl() throws IOException {
        Type p = type();
        Token tok = look;
        match(Tag.ID);
        Id id = new Id((Word) tok, p, used);
        Stmt stmt = new Decl(id);
        top.put(tok, id);
        used = used + p.width;
        if(this.look.tag == '=')
        {
            match('=');
            Set tem = new Set(id, bool());
            tem.firstTime = 1;
            stmt = tem;
        }
        match(';');
        return stmt;
    }


    Type type() throws IOException {
        Type p = (Type) look;// 期望look.tag==Tag.BASIC
        match(Tag.BASIC);
        if (look.tag != '[')
            return p;// T->basic
        else
            return dims(p);// 返回数组类型
    }

    Type dims(Type p) throws IOException {
        match('[');
        Token tok = look;
        match(Tag.NUM);
        match(']');
        if (look.tag == '[')
            p = dims(p);
        return new Array(((Num) tok).value, p);
    }

    Stmt stmts() throws IOException {
        if (look.tag == '}')
            return Stmt.Null;
        else
            return new Seq(stmt(), stmts());
    }
    Fun funs() throws IOException{
        if(look.tag == Lexer.END_OF_FILE)
            return Fun.Null;
        else
            return new Funseq(fun(),funs());
    }
    Fun fun() throws IOException{
        if(this.look.tag == Lexer.END_OF_FILE)
            return Fun.Null;
        P parms;
        Stmt block;
        Type p = type();
        Token tok = look;
        match(Tag.ID);
        Id funHead = new Id((Word) tok, p, this.funId);
        this.funId = this.funId + 1;
        match('(');
        parms = p();
        match(')');
        Fun function = new Fun(funHead,parms);
        this.top = function.top;
        this.funEnv.put(funHead.toString(),function);
        block = block();
        function.body = block; 
        return function;
    }
    P p() throws IOException
    {
        Token tok = look;
        if(tok.tag == Tag.BASIC)
        {
            Type p = type();
            tok = look;
            match(Tag.ID);
            Id firstP = new Id((Word) tok, p, 0);
            Pl plist = pl();
            return new P(firstP,plist);
        }
        else 
        {
            return P.Null;
        }
        
    }
    Pl pl() throws IOException
    {
        Pl plist = new Pl();
        while(this.look.tag != ')')
        {
            match(',');
            Type p = type();
            Token tok = look;
            match(Tag.ID);
            Id eachParm = new Id((Word) tok, p, 0);
            plist.addParm(eachParm);
        }
        return plist;
    }
    Ret ret() throws IOException
    {
        match(Tag.RETURN);
        Token t = look;
        Id id = top.get(t);
        match(Tag.ID); 
        if (id == null)
            error(t.toString() + "undeclared");
        Ret ret;
        if(look.tag != ';')
        {
            Access x = offset(id);
            ret = new Ret(x);
        }
        else
        {
            ret = new Ret(id);
        }
        match(';');
        return ret;
    }
    Stmt stmt() throws IOException {
        Expr x;
        Stmt s1, s2, s3;
        Stmt savedStmt;// 用于为break语句保存外层的循环语句
        switch (look.tag) {
            case ';':
                move();
                return Stmt.Null;
            case Tag.IF:
                match(Tag.IF);
                match('(');
                x = bool();
                match(')');
                s1 = stmt();
                if (look.tag != Tag.ELSE)
                    return new If(x, s1);
                match(Tag.ELSE);
                s2 = stmt();
                return new Else(x, s1, s2);
            case Tag.WHILE:
                While whilenode = new While();
                savedStmt = Stmt.Enclosing;
                Stmt.Enclosing = whilenode;
                match(Tag.WHILE);
                match('(');
                x = bool();
                match(')');
                s1 = stmt();
                whilenode.init(x, s1);
                Stmt.Enclosing = savedStmt;// 重置 Stmt.Enclosing
                return whilenode;
            case Tag.DO:
                Do donode = new Do();
                savedStmt = Stmt.Enclosing;
                Stmt.Enclosing = donode;
                match(Tag.DO);
                s1 = stmt();
                match(Tag.WHILE);
                match('(');
                x = bool();
                match(')');
                match(';');
                donode.init(s1, x);
                Stmt.Enclosing = savedStmt;// 重置Stmt,Enclosing
                return donode;
            case Tag.FOR:
                For fornode = new For();
                savedStmt = Stmt.Enclosing;
                Stmt.Enclosing = fornode;
                match(Tag.FOR);
                match('(');
                s1 = stmt();
                x = bool();
                match(';');
                s2 = stmt();
                match(')');
                s3 = stmt();
                fornode.init(s1, x, s2, s3);
                Stmt.Enclosing = savedStmt;// 重置 Stmt.Enclosing
                return fornode;
            case Tag.BREAK:
                match(Tag.BREAK);
                match(';');
                return new Break();
            case Tag.RETURN:
                return ret();
            case '{':
                return block();
            case Tag.BASIC:
                return decl();
            default:
                return assign();
        }
    }
    
    Stmt assign() throws IOException {
        Stmt stmt;
        Token t = look;
        match(Tag.ID);
        Id id = top.get(t);
        if (id == null)
            error(t.toString() + "undeclared");
        if (look.tag == '=') { // S->id=E；
            move();
            stmt = new Set(id, bool());
        } else { // S->L=E；
            Access x = offset(id);
            match('=');
            stmt = new SetElem(x, bool());
        }
        match(';');
        return stmt;
    }

    Expr bool() throws IOException {
        Expr x = join();
        while (look.tag == Tag.OR) {
            Token tok = look;
            move();
            x = new Or(tok, x, join());
        }
        return x;
    }

    Expr join() throws IOException {
        Expr x = equality();
        while (look.tag == Tag.AND) {
            Token tok = look;
            move();
            x = new And(tok, x, equality());
        }
        return x;
    }

    Expr equality() throws IOException {
        Expr x = rel();
        while (look.tag == Tag.EQ || look.tag == Tag.NE) {
            Token tok = look;
            move();
            x = new Rel(tok, x, rel());
        }
        return x;
    }

    Expr rel() throws IOException {
        Expr x = expr();
        switch (look.tag) {
            case '<':
            case Tag.LE:
            case Tag.GE:
            case '>':
                Token tok = look;
                move();
                return new Rel(tok, x, expr());
            default:
                return x;
        }
    }

    Expr expr() throws IOException {
        Expr x = term();
        while (look.tag == '+' || look.tag == '-') {
            Token tok = look;
            move();
            x = new Arith(tok, x, term());
        }
        return x;
    }

    Expr term() throws IOException {
        Expr x = unary();
        while (look.tag == '*' || look.tag == '/') {
            Token tok = look;
            move();
            x = new Arith(tok, x, unary());
        }
        return x;
    }

    Expr unary() throws IOException {
        if (look.tag == '-') {
            move();
            return new Unary(Word.minus, unary());
        } else if (look.tag == '!') {
            Token tok = look;
            move();
            return new Not(tok, unary());
        } else
            return factor();
    }

    Expr factor() throws IOException {
        Expr x = null;
        switch (look.tag) {
            case '(':
                move();
                x = bool();
                match(')');
                return x;
            case Tag.NUM:
                x = new Constant(look, Type.Int);
                move();
                return x;
            case Tag.REAL:
                x = new Constant(look, Type.Float);
                move();
                return x;
            case Tag.TRUE:
                x = Constant.True;
                move();
                return x;
            case Tag.FALSE:
                x = Constant.False;
                move();
                return x;
            default:
                error("syntax error");
                return x;
            case Tag.ID:
                // String s = look.toString();
                Id id = top.get(look);
                if(id == null)
                {
                    Fun fun = this.funEnv.get(look.toString());
                    move();
                    if(fun == null)
                        error("undeclared fun");
                    if(look.tag =='(')
                    {
                        move();
                        return callFun(fun.funHead);
                    }
                }
                move();
                if (look.tag != '[')
                    return id;
                else 
                    return offset(id);
        }
    }
    CallFun callFun(Id funId) throws IOException
    {
        ArrayList<String> parms = new ArrayList<String>();

        while(look.tag != ')')
        {
            parms.add(look.toString());
            move();
            if(look.tag == ')')
                break;
            match(',');
        }
        match(')');
        CallFun call = new CallFun(parms,funId);
        return call;
    }
    Access offset(Id a) throws IOException { // I->[E]I[E]I
        Expr i;
        Expr w;
        Expr t1, t2;
        Expr loc;// 继承id
        Type type = a.type;
        match('[');
        i = bool();
        match(']');// 第一个下标，I->[E]
        type = ((Array) type).of;
        w = new Constant(type.width);
        t1 = new Arith(new Token('*'), i, w);
        loc = t1;
        while (look.tag == '[') {// 多维下标，I->[E]I
            match('[');
            i = bool();
            match(']');
            type = ((Array) type).of;
            w = new Constant(type.width);
            t1 = new Arith(new Token('*'), i, w);
            t2 = new Arith(new Token('+'), loc, t1);
            loc = t2;
        }
        return new Access(a, loc, type);
    }
}