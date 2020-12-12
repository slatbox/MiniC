package inter;

public class Ret extends Stmt{
    public Id id = null;
    public Expr index = null;
    public Ret(Id id)
    {
        this.id = id;
    }
    public Ret(Access x)
    {
        this.id = x.array;
        this.index = x.index;
    }
    public void gen(int b,int a){
        if(this.index == null)
        {
            emit("ret " + id.toString() );
        }
        else
        {
            String s1 = index.reduce().toString();
            emit("ret " + id.toString() + "[" + s1 + "]");
        }
    }
}
