package inter;//文件Expr.java
import lexer.*;
import symbols.*;
public class Expr extends Node{
    public Token op;
    public Type type;
    Expr(Token tok,Type p){op = tok;type=p;}
    public Expr gen(){return this;}
    public Expr reduce(){return this;}
    public void jumping(int t,int f){ emitjumps(toString(),t,f);}
    public void emitjumps(String test,int t,int f) {
        if (t != 0 && f != 0) {
            emit("if " + test + " goto L" + t);
            emit("goto L" + f);
        } else if (t != 0) emit("if " + test + " goto L" + t);
        else if (f != 0) emit("iffalse " + test + " goto L" + f);
        else ;//不生成指令，因为t和f都直接穿越
    }
    public String toString(){ return op.toString();}
}
