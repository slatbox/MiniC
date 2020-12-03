package inter;//文件 Node.java
import java.io.FileOutputStream;
import java.io.*;
import lexer.*;
public class Node{
    int lexline=0;
    private static FileOutputStream output;
    public static void initOutputStream() throws IOException
    {
        if(Node.output != null)
            return;
        try {
            String current_path = System.getProperty("user.dir");
            String file_name = "output.txt";
            File file_path = new File(current_path + File.separator + file_name);
            if(!file_path.exists())
                file_path.createNewFile();
            Node.output = new FileOutputStream(file_path);
        } catch (Exception e) {
            throw e;
        }
    }
    public static void write(String content) throws IOException
    {
        byte[] tem = content.getBytes();
        Node.output.write(tem, 0, tem.length);
    }
    Node(){ lexline=Lexer.line; }
    void error(String s){
        throw new Error("near line "+lexline+": "+s);
    }
    static int labels=0;
    public int newlabel(){ return ++labels;}
    public void emitlabel(int i){
        try {
            Node.write("L"+i+":");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
        
    }
    public void emit(String s){ 
        try {
            Node.write("\t" +s + '\n');
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
        
    }
}