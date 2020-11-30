package lexer;

import java.io.*;
import java.util.*;
import symbols.*;

//把字符串映射为字
public class Lexer {
    public static int line = 1;
    private static String source_path = "C:\\school_work\\computer_learning\\Java\\MiniC\\MiniC\\src\\main\\test.txt";
    private static FileInputStream reader;
    private static char END_OF_FILE = (char)-1;
    char peek = ' ';
    Hashtable<String, Word> words = new Hashtable<String, Word>();

    void reserve(Word w) {
        words.put(w.lexeme, w);
    }

    private void loadKeyWord() {
        // 保留选定关键字
        reserve(new Word("if", Tag.IF));
        reserve(new Word("else", Tag.ELSE));
        reserve(new Word("while", Tag.WHILE));
        reserve(new Word("do", Tag.DO));
        reserve(new Word("break", Tag.BREAK));
        // 保留其他地方定义的对象的词素
        reserve(Word.True);
        reserve(Word.False);
        reserve(Type.Int);
        reserve(Type.Char);
        reserve(Type.Bool);
        reserve(Type.Float);
    }

    public Lexer() throws IOException {
        try {
            Lexer.reader = new FileInputStream(Lexer.source_path);
        } catch (IOException e) {
            System.out.println(e);
        }
        this.loadKeyWord();

    }

    // 把下一个输入字符读到变量peek中，可以复用或重载
    void readch() throws IOException {
        int tem = Lexer.reader.read();
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
        // 消除注释
        if (peek == '/') {
            try {
                if (this.readch('*'))
                    while (!(this.peek == '*' && this.readch('/')))
                    {
                        if(this.peek == Lexer.END_OF_FILE)
                            throw new IOException("uncompleted comment ");
                        this.readch();
                    }
                else if(this.peek == '/')
                    while(this.peek != '\n')
                        this.readch();
                else
                    return new Token('/');
                    
                
            } catch (IOException e) {
                throw e;
            }

        }

        this.scanBlank();
        switch (peek) {
            case '&':
                if (readch('&'))
                    return Word.and;
                else
                    return new Token('&');
            case '|':
                if (readch('|'))
                    return Word.or;
                else
                    return new Token('|');
            case '=':
                if (readch('='))
                    return Word.eq;
                else
                    return new Token('=');
            case '!':
                if (readch('='))
                    return Word.ne;
                else
                    return new Token('!');
            case '<':
                if (readch('='))
                    return Word.le;
                else
                    return new Token('<');
            case '>':
                if (readch('='))
                    return Word.ge;
                else
                    return new Token('>');
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
            } while (Character.isLetterOrDigit(peek));
            String s = b.toString();
            Word w = (Word) words.get(s);
            if (w != null)
                return w;
            w = new Word(s, Tag.ID);
            words.put(s, w);
            return w;
        }
        Token tok = new Token(peek);
        peek = ' ';
        return tok;
    }
}
