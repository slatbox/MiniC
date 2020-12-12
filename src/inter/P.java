package inter;

public class P extends Node{
    public static P Null = new P();
    public Id firstP = null;
    public Pl pl = null;
    public P(){}
    public P(Id p1,Pl pl)
    {
        this.firstP = p1;
        this.pl = pl;
    }
    public void gen()
    {
        if(this.firstP != null)
            emit("parm " + firstP.toString());
        if(this.pl != null)
            pl.gen();
    }
}
