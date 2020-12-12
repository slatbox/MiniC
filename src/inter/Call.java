package inter;

import lexer.Token;

public class Call extends Op{
    Fun function = null;
    public Call(Fun fun)
    {
        super(new Token('\0'),null);
        this.function = fun;
    }
    public void gen(int b,int a)
    {

    }
}
