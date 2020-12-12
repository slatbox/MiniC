package inter;
import java.util.*;

public class Pl extends Node{
    ArrayList<Id> ps = new ArrayList<Id>();
    public Pl(){}
    public void addParm(Id p)
    {
        this.ps.add(p);
    }
    public void gen()
    {
        for(int i = 0 ; i < ps.size();i++)
        {
            emit("parm " + ps.get(i).toString());
        }
    }
}
