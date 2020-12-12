package symbols;
import java.util.*;

//把字符串词法单元映射为类Id的对象
public class AsmEnv {
    private Hashtable<String,Integer> local_table;
    private Hashtable<String,Integer> parm_table;
    protected AsmEnv prev;
    public int amount_of_local = 0;
    public int amount_of_parm = 0;
    public AsmEnv(AsmEnv n){
        local_table=new Hashtable<String,Integer>();
        parm_table= new Hashtable<String,Integer>();
        prev=n;
    }
    
    public void putLocal(String name){
        local_table.put(name,this.amount_of_local);
        this.amount_of_local++;
    }
    public void putParm(String name){
        parm_table.put(name,this.amount_of_parm);
        this.amount_of_parm++;
    }
    public String getAddrOf(String name)
    {
        Integer pos = this.getLocal(name);
        if(pos != null)
        {
            return "[esp +" +  Integer.toString(4 * this.amount_of_local - 4 * pos) + "]";
        }
        pos = this.getParm(name);
        if(pos != null)
        {
            return "[ebp + " + Integer.toString(8 + pos * 4) + "]";
        }
        return "[]";
    }
    public Integer getLocal(String name){
        for(AsmEnv e=this;e!=null;e=e.prev){
            Integer found = e.local_table.get(name);
            if(found!=null) return found;
        }
        return null;
    }
    public Integer getParm(String name){
        for(AsmEnv e=this;e!=null;e=e.prev){
            Integer found = e.parm_table.get(name);
            if(found!=null) return found;
        }
        return null;
    }
}