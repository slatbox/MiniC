package inter;//文件 Stmt.java
public class Stmt extends Node{
    public Stmt(){}
    public static Stmt Null=new Stmt();
    //b是当前语句（块）的其实标签，a是语句块的结束标签
    public void gen(int b,int a){
        if(this == Null)
        {
            emit("");
        }
    }//调用时的参数是语句开始处的标号和语句的下一条指令的标号
    int after=0;//保存语句的下一条指令的标号
    public static Stmt Enclosing=Stmt.Null;//用于break 语句
}