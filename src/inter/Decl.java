package inter;

public class Decl extends Stmt{
    Id id;
    public Decl(Id id)
    {
        this.id = id;
    }
    public void gen(int b,int a)
    {
        emit("local " + id.toString());
    }
}
