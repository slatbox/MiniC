package inter;
import lexer.*;
import java.util.ArrayList;
import symbols.*;
public class CallFun extends Expr{
    private ArrayList<String> pList = null;
    private Id fun = null;
    public CallFun(ArrayList<String> ps,Id funId)
    {
        super(funId.wordObject,Type.Int);
        this.pList = ps;
        this.fun = funId;
    }
    public Expr reduce()
    {
        Temp t= new Temp(type);
        for(int i = 0 ; i < this.pList.size();i++)
        {
            emit("useParm "+this.pList.get(i));
        }
        emit("call " + this.fun.toString());
        emit(t.toString()+" = "+ "$r");
        return t;   
    }
    public Expr gen()
    {
        Temp t= new Temp(type);
        for(int i = 0 ; i < this.pList.size();i++)
        {
            emit("useParm "+this.pList.get(i));
        }
        emit("call " + this.fun.toString());
        emit(t.toString()+" = "+ "$r");
        return t;   
    }
    public String toString(){
        return this.fun.toString();
    }
    
}
