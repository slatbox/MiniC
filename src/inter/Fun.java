package inter;//文件 Stmt.java
import symbols.*;
import lexer.*;
public class Fun extends Node{
    public Id funHead = null;
    public P parameters = null;
    public Stmt body = null;
    public String name = null;
    public Env top = null;
    public static Fun Null = new Fun();
    public Fun(){}
    public static Fun printNum = new Fun(
        new Id(new Word("printNum", Tag.ID),Type.Int,0),
        new P(new Id(new Word("x", Tag.ID),Type.Int,0),null));
    public static Fun inputNum = new Fun(new Id(new Word("inputNum", Tag.ID),Type.Int,0),null);
    public Fun(Id functionHead,P parameters)
    {
        this.funHead = functionHead;
        this.parameters = parameters;
        // this.body = body;
        this.name = this.funHead.toString();
        this.top = new Env(null);
        //添加环境
        if(this.parameters == null)
            return;
        if(this.parameters.firstP != null)
        {
           
            this.top.put(this.parameters.firstP.wordObject, this.parameters.firstP);
        }
        Pl plList = this.parameters.pl;
        if(plList == null)
            return;
        for(int i = 0 ; i < plList.ps.size();i++)
        {
            Id eachId = plList.ps.get(i);
           
            this.top.put(eachId.wordObject,eachId);
        } 
        
    }
    //b是当前语句（块）的其实标签，a是语句块的结束标签
    public void gen()
    {
        if(this == Fun.Null)
            return;
        
        int begin = this.body.newlabel();
        int after = this.body.newlabel();
        this.emitFunStart(this.name);
        this.parameters.gen();
        this.body.emitlabel(begin);
        this.body.gen(begin, after);
        this.body.emitlabel(after);
        this.emitFunEnd(this.name);
    }
    public void genDecls()
    {
        
    }
    int after=0;//保存语句的下一条指令的标号
}