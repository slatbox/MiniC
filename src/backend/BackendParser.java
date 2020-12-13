package backend;
import lexer.*;
import symbols.*;
import java.io.IOException;
import Io.*;
import java.io.*;

import java.io.FileOutputStream;
import java.io.File;
public class BackendParser {
    private BackendLexer lex;// 这个语法分析器的词法分析器
    private Token look;// 向前看词法单元
    public Env top = null;// 当前或顶层的符号表
    private static int line = 0;
    public int usedTemp = 0;
    private AsmEnv currentEnv;
    private static FileOutputStream output;
    private static String []includes = {
        "windows.inc",
        "masm32.inc",
        "gdi32.inc",
        "user32.inc",
        "kernel32.inc",
        "Comctl32.inc",
        "comdlg32.inc",
        "shell32.inc",
        "oleaut32.inc",
        "ole32.inc",
        "msvcrt.inc",
        "dialogs.inc  "
    };
    private static String [] libs = {
        "masm32.lib",
        "gdi32.lib",
        "user32.lib",
        "kernel32.lib",
        "Comctl32.lib",
        "comdlg32.lib",
        "shell32.lib",
        "oleaut32.lib",
        "ole32.lib",
        "msvcrt.lib"
    };
    private static String macro = "macros.asm ";
    public void emitAsmHead() throws IOException {
        String head = ".486\n.model flat, stdcall\noption casemap :none\n";
        emit(head);
        for(int i = 0 ; i < includes.length;i++)
            emit("include " + io.programPath + "\\masm32\\include\\" + includes[i] + "\n");
        emit("include " + io.programPath + "\\masm32\\macros\\" + macro + "\n");
        emit("\n");
        for(int i = 0 ; i < libs.length;i++)
            emit("includelib " + io.programPath + "\\masm32\\lib\\" + libs[i] + "\n");
        emit("\n");
        this.emit(Template.asmHead);
    }
    public void emitFunctionHead() throws IOException {
        this.emit(Template.functionHead);
    }
    public void emitFunctionEnd() throws IOException {
        this.emit(Template.functionEnd);
    }
    public void emitAsmEnd() throws IOException {
        this.emit(Template.asmEnd);
    }
    public static void initOutputStream() throws IOException
    {
        if(BackendParser.output != null)
            return;
        try {
            
            File file_path = new File(io.outputDir+ File.separator + io.outputName + ".asm");
            if(!file_path.exists())
                file_path.createNewFile();
            BackendParser.output = new FileOutputStream(file_path);
        } catch (Exception e) {
            throw e;
        }
    }
    public static void write(String content) throws IOException
    {
        byte[] tem = content.getBytes();
        BackendParser.output.write(tem, 0, tem.length);
    }
    public void emit(String s){ 
        try {
            BackendParser.write(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
    }
    public void emitThree(String des,String left,String right,String op)
    {
        String content = 
        "\tmov edx," + left + "\n" +
        "\t" + op + " edx," + right + "\n" + 
        "\tmov " + des + ",edx\n";
        emit(content);
    }
    public BackendParser(BackendLexer lex) throws IOException
    {
        this.lex = lex;
        this.currentEnv = new AsmEnv(null);
        this.move();
    }
    void move() throws IOException {
        look = lex.scan();
    }

    void error(String s) {
        throw new Error("near line " + BackendLexer.line + ": " + s);
    }

    String match(int t) throws IOException {
        String content = look.toString();
        if (look.tag == t)
            move();
        else
            error("syntax error");
        return content;
    }
    void match(String content) throws IOException {
        
        if(content.equals(look.toString()))
        {
            move();
        }   
        else
            error("syntax error");
    }
    void fun() throws IOException
    {
        String funName = match(BackendTag.ID);
        emit(funName);
        match("proc");
        emit("\tproc\n");
        this.emitFunctionHead();
        while(!look.toString().equals(funName))
        {
            if(!stmt(funName))
                break;
        }
        this.emitFunctionEnd();
        this.emit(funName);
        match(funName);
        match("endp");
        this.emit("\tendp\n");

    }
    boolean stmt(String funName) throws IOException
    {
        if(look.tag == BackendTag.LABEL)
        {
            String label = match(BackendTag.LABEL);
            emit(label + "\t");
        }
        String head = look.toString();
        if(head.equals(funName))
            return false;
        if(head.equals("local"))
        {
            this.local();
        }
        else if(head.equals("parm"))
        {
            this.parm();
        }
        else if(head.equals("iffalse"))
        {
            this.iffalse();
        }
        else if(head.equals("if"))
        {
            this.ifHead();
        }
        else if(head.equals("goto"))
        {
            this.gotto();
        
        }
        else if(look.tag == BackendTag.LABEL)
        {
            
            String label = match(BackendTag.LABEL);
            emit("\n" + label + "\t");
        }
        else if(head.equals("ret"))
        {
            this.ret();
        }
        else if(head.equals("call"))
        {
            this.call();
        }
        else if(head.equals("useParm"))
        {
            this.useParm();
        }
        else
        {
            this.head();
        }
        return true;
    }
    void useParm()throws IOException
    {
        match("useParm");
        String addr = this.getAddr();
        emit("\tpush " + addr + "\n");
    }
    void call() throws IOException {
        move();
        emit("\tcall " + look.toString() + "\n");
        move(); 
    }
    void ret() throws IOException
    {
        match("ret");
        String src_addr = this.getAddr();
        emit("\tmov eax," + src_addr + "\n");
        this.emitFunctionEnd();
        this.emit("\tret\n");
    }
    void gotto() throws IOException
    {
        match("goto");
        String label = look.toString();
        move();
        emit("\tjmp " + label + "\n");
    }
    void local() throws IOException
    {
        match("local");
        String var = match(BackendTag.ID);
        this.currentEnv.putLocal(var);
        emit("push 0\n");
    }
    void parm() throws IOException
    {
        match("parm");
        String var = match(BackendTag.ID);
        this.currentEnv.putParm(var);
    }
    String getOperCommand() throws IOException
    {
        String command = "";
        String tem = look.toString();
        if(tem.equals("+"))
        {
            command = "add";
        }
        else if(tem.equals("-"))
        {
            command = "sub";
        }
        else if(tem.equals("*"))
        {
            command = "imul";
        }
        else if(tem.equals("/"))
        {
            command = "idiv";
        }
        else
        {
            error("unknow operator");
        }
        move();
        return command;
    }
    String getAddr() throws IOException
    {
        String addr = "";

        if(look.tag == BackendTag.ID)
        {
            addr = "DWORD PTR " + this.currentEnv.getAddrOf(look.toString());
        }
        else if(look.tag == BackendTag.TEMP)
        {
            if(look.toString().equals("r"))
                addr = "eax";
            else
                addr = look.toString();
        }
        else if(look.tag == Tag.REAL || look.tag == Tag.NUM)
        {
            addr = look.toString();
        }
        else
        {
            error("syntax error");
        }
        move();
        return addr;
        
    }
    
    void head() throws IOException
    {
        String desAddr = this.getAddr();
        match("=");
        if(look.tag == BackendTag.OP)
        {
            String operatorCommand = getOperCommand();
            String leftOp = this.getAddr();
            emit("\tneg " + leftOp + "\n");
        }
        else
        {
            String leftOp = this.getAddr();
            if(look.tag != BackendTag.OP)
            {
                emit("\tmov edx," + leftOp + "\n");
                emit("\tmov " + desAddr +",edx\n");
            }
            else 
            {
                String operatorCommand = getOperCommand();
                String rightOP = this.getAddr();
                emitThree(desAddr, leftOp, rightOP, operatorCommand);
            }
        }
        
    }
    String getCmpCommand() throws IOException{
        String tem = look.toString();
        String command = "";
        if(tem.equals("<"))
            command = "jl";
        else if(tem.equals("<="))
            command = "jle";
        else if(tem.equals(">"))
            command = "jg";
        else if(tem.equals(">="))
            command = "jge";
        else if(tem.equals("=="))
            command = "je";
        else if(tem.equals("!="))
            command = "jne";
        else
            error("invalid jump command");
        move();
        return command;
    }
    String getInverseCommand(String command)
    {
        String tem = command;
        if(tem.equals("jl"))
            tem = "jge";
        else if(tem.equals("jle"))
            tem = "jg";
        else if(tem.equals("jg"))
            tem = "jle";
        else if(tem.equals("jge"))
            tem = "jl";
        else if(tem.equals("je"))
            tem = "jne";
        else if(tem.equals("jne"))
            tem = "je";
        else
            error("invalid jump command");
        return tem;
    }
    void iffalse() throws IOException
    {
        match("iffalse");
        String left = getAddr();
        String op = this.getInverseCommand(getCmpCommand());
        String right = getAddr();
        match("goto");
        String label = look.toString();
        move();
        emit("\tmov edx," + left + "\n");
        emit("\tcmp edx," + right + "\n");
        emit("\t" + op + " " + label + "\n");
    }
    void ifHead() throws IOException{
        match("if");
        String left = getAddr();
        String op = getCmpCommand();
        String right = getAddr();
        match("goto");
        String label = look.toString();
        move();
        emit("\tmov edx," + left + "\n");
        emit("\tcmp edx," + right + "\n");
        emit("\t" + op + " " + label + "\n");
    }
    public void generateTargetCode() throws IOException 
    {
        this.emitAsmHead();
        while(!look.toString().equals( BackendLexer.END_OF_FILE+""))
        {
            this.fun();
        }
        this.emitAsmEnd();
    }
    public void generateBat() throws IOException
    {
        String filePureName = io.outputName;
        try {
            String batFileName = filePureName + ".bat";
            File file_path = new File(io.outputDir+ File.separator + batFileName);
            if(!file_path.exists())
                file_path.createNewFile();
            BackendParser.output = new FileOutputStream(file_path);
        } catch (Exception e) {
            throw e;
        }
        emit("@echo off\n");
        String objName = "\"" + io.outputDir + File.separator + filePureName + ".obj" + "\"";
        String exeName = "\"" + io.outputDir + File.separator +filePureName + ".exe" + "\"";
        String asmName = "\"" + io.outputDir + File.separator +filePureName + ".asm" + "\"";
        String starName = "\"" + io.outputDir + File.separator +filePureName + ".*" + "\"";
        String masm32Path = io.programPath + File.separator + "masm32\\bin\\";
        emit("\tif exist " + objName +" del " +objName + "\n");
        emit("\tif exist " + exeName + " del " + exeName + "\n");
        emit("\t" + masm32Path + "ml /c /coff " + asmName + "\n");
        emit("\tif errorlevel 1 goto errasm\n");
        emit("\t" + masm32Path + "PoLink /SUBSYSTEM:CONSOLE " + objName  + "\n");
        emit("\tif errorlevel 1 goto errlink\n");
        emit("\tdir " + starName + "\n");
        emit("\tgoto TheEnd\n");
        emit("\n");
        emit(":errlink\n\techo _\n\techo Link error\t\ngoto TheEnd\n");
        emit(":errasm\n\techo _\n\techo Assembly Error\n\tgoto TheEnd\n");
        emit("\n");
        emit(":TheEnd\n");
        emit("pause");
        BackendParser.output.close();
    }
    public void generateExe()throws IOException
    {
        String batPath = io.outputDir + File.separator + io.outputName + ".bat";
        File batFile = new File(batPath);
        boolean batFileExist = batFile.exists();
        if (batFileExist) {
            callCmd(batPath);
        }
    }
    private static void  callCmd(String locationCmd){
        StringBuilder sb = new StringBuilder();
        try {
            Process child = Runtime.getRuntime().exec(locationCmd);
            InputStream in = child.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(in));
            String line;
            while((line=bufferedReader.readLine())!=null)
            {
                sb.append(line + "\n");
            }
               in.close();
            try {
                child.waitFor();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            System.out.println("sb:" + sb.toString());
            System.out.println("callCmd execute finished");           
        } catch (IOException e) {
            System.out.println(e);
        }
     }
}
