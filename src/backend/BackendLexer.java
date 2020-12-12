package backend;
import java.io.*;
import java.util.*;

import Io.io;
import symbols.*;
import lexer.*;
public class BackendLexer {
    public static int line = 1;
    
    private static FileInputStream reader;
    public  static char END_OF_FILE = (char)-1;
    char peek = ' ';
    Hashtable<String, Word> words = new Hashtable<String, Word>();

    void reserve(Word w) {
        words.put(w.lexeme, w);
    }

    public BackendLexer() throws IOException {
        try {
            String outName = io.outputName;
            String inter_name = outName.substring(0,outName.lastIndexOf('.')) + "_inter.txt";
            BackendLexer.reader = new FileInputStream(io.outputDir + File.separator + inter_name);
        } catch (IOException e) {
            System.out.println(e);
        }
        
    }

    // 把下一个输入字符读到变量peek中，可以复用或重载
    void readch() throws IOException {
        int tem = BackendLexer.reader.read();
        peek = (char)tem;
        // peek2=(char)Lexer.reader.read();
    }

    boolean readch(char c) throws IOException {
        readch();
        if (peek != c)
            return false;
        peek = ' ';
        return true;
    }

    // 主方法scan，识别数字、标识符和保留字。首先略过所有空白字符，然后识别复合词法单元，不成功则读入一个字符串
    private void scanBlank() throws IOException {
        for (;; readch()) {
            if (peek == ' ' || peek == '\t')
                continue;
            else if (peek == '\n')
                line = line + 1;
            else if (peek == '\r')
                continue;
            else
                break;
        }
    }
    public Token scan() throws IOException {
        this.scanBlank();
        new Word(peek + "",BackendTag.OP);
        switch (peek) {
            case '=':
                if (readch('='))
                    return new Word("==",BackendTag.OP);
                else
                    return new Word("=",BackendTag.OP);
            case '!':
                if (readch('='))
                    return new Word("!=",BackendTag.OP);
                else
                    return new Word("!",BackendTag.OP);
            case '<':
                if (readch('='))
                    return new Word("<=",BackendTag.OP);
                else
                    return new Word("<",BackendTag.OP);
            case '>':
                if (readch('='))
                    return new Word(">=",BackendTag.OP);
                else
                    return new Word(">=",BackendTag.OP);
        }
        if (Character.isDigit(peek)) {
            int v = 0;
            do {
                v = 10 * v + Character.digit(peek, 10);
                readch();
            } while (Character.isDigit(peek));
            if (peek != '.')
                return new Num(v);
            float x = v;
            float d = 10;
            for (;;) {
                readch();
                if (!Character.isDigit(peek))
                    break;
                x = x + Character.digit(peek, 10) / d;
                d = d * 10;
            }
            return new Real(x);
        }
        if (Character.isLetter(peek)) {
            StringBuffer b = new StringBuffer();
            do {
                b.append(peek);
                readch();
            } while (Character.isLetterOrDigit(peek) || peek == ':');
            int tag = BackendTag.ID;
            if(b.charAt(b.length() - 1) == ':')
            {
                tag = BackendTag.LABEL;
            }
            String s = b.toString();
            Word w = new Word(s, tag);
            return w;
        }
        if(peek == '$')
        {
            StringBuffer b = new StringBuffer();
            readch();
            do {
                b.append(peek);
                readch();
            } while (Character.isDigit(peek));
            return new Word(b.toString(),BackendTag.TEMP);
        }
        Token tok = new Word(peek + "",BackendTag.OP);
        peek = ' ';
        return tok;
    }
    
}
