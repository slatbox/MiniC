package inter;
import symbols.*;
public class Funseq extends Fun{
    Fun fun1;
    Fun fun2;
    public Funseq(Fun f1,Fun f2)
    {
        this.fun1 = f1;
        this.fun2 = f2;
    }
    public void gen() {
        if (fun1 == Fun.Null) fun2.gen();
        else if (fun2 == Fun.Null) fun1.gen();
        else {
            
            fun1.gen();
            fun2.gen();
        }
    }
}
