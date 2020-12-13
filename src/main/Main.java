package main;

import java.io.*;
import lexer.*;
import parser.*;

import inter.*;
import backend.*;
import Io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        dealWithArgs(args);
        Lexer lex = new Lexer();
        Parser parse = new Parser(lex);
        parse.program();
        Node.write("\n");
        BackendParser parser = new BackendParser(new BackendLexer());
        BackendParser.initOutputStream();
        parser.generateTargetCode();
        parser.generateBat();
        parser.generateExe();
        System.out.println("compilation succeed!");
    }

    public static String getDir(String path) {
        int index = path.lastIndexOf('/');
        if(index == -1)
            index = path.lastIndexOf('\\');
        return path.substring(0, index);
    }

    public static String getfileName(String path) {
        int index = path.lastIndexOf('/');
        if(index == -1)
            index = path.lastIndexOf('\\');
        String tem = path.substring(index + 1);
        return tem.substring(0,tem.lastIndexOf('.'));
    }

    public static String getAbsolutePath(String path) {
        String current_dir = System.getProperty("user.dir");
        if (path.charAt(0) == '.')
            return current_dir + path.substring(1);
        else
            return path;
    }

    public static void dealWithArgs(String[] args) throws IOException {
        String source_path = args[0];
        String des_path = args[2];
        String ab_s_path = getAbsolutePath(source_path);
        String ab_d_path = getAbsolutePath(des_path);
        io.inputDir = getDir(ab_s_path);
        io.inputName = getfileName(ab_s_path);
        io.outputDir = getDir(ab_d_path);
        io.outputName = getfileName(ab_d_path);
        io.programPath = System.getProperty("user.dir");
    }

}