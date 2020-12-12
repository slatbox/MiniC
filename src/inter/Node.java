package inter;//文件 Node.java
import java.io.FileOutputStream;
import java.io.*;
import lexer.*;
import Io.*;
public class Node{
    int lexline=0;
    private static FileOutputStream output;
    public static void initOutputStream() throws IOException
    {
        if(Node.output != null)
            return;
        try {
            String fileName = io.outputName;
            fileName = fileName.substring(0,fileName.lastIndexOf('.')) + "_inter.txt";
            String output_dir = io.outputDir;
            File file_path = new File(output_dir + File.separator + fileName);
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
        Temp.count = 0;
        
    }
    public void emitFunStart(String funName)
    {
        try {
            Node.write("\n" + funName + " proc\n\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
    }
    public void emitFunEnd(String funName)
    {
        try {
            Node.write("\n\n" + funName + " endp\n");
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