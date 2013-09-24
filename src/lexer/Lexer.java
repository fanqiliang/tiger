package lexer;

import java.io.IOException;
import java.io.InputStream;

import com.sun.org.apache.regexp.internal.recompile;

import util.Todo;

import lexer.Token.Kind;

@SuppressWarnings("unused")
public class Lexer {
	String fname; // the input file name to be compiled
	InputStream fstream; // input stream for the above file
	Token token = new Token();
	boolean b = true;
	int index = 0;

	public Lexer(String fname, InputStream fstream) {
		this.fname = fname;
		this.fstream = fstream;
	}

	// When called, return the next token (refer to the code "Token.java")
	// from the input stream.
	// Return TOKEN_EOF when reaching the end of the input stream.
	private Token nextTokenInternal() throws Exception {
		int c = this.fstream.read();
		if (-1 == c)
			// The value for "lineNum" is now "null",
			// you should modify this to an appropriate
			// line number for the "EOF" token.
			return new Token(Kind.TOKEN_EOF, token.lineNum);

		// skip all kinds of "blanks"
		while (' ' == c || '\t' == c || '\n' == c) {
			c = this.fstream.read();
		}
		if (-1 == c)
			return new Token(Kind.TOKEN_EOF, token.lineNum++);

		switch (c) {
		case '+':
			return new Token(Kind.TOKEN_ADD, token.lineNum);
		case '&':
			c = this.fstream.read();
			switch (c) {
			case '&':
				return new Token(Kind.TOKEN_AND, token.lineNum);
			default:
				new Todo();
				return null;
			}
		case '=':
			return new Token(Kind.TOKEN_ASSIGN, token.lineNum);
		case 'b':
			String str = "oolean";

			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_BOOLEAN, token.lineNum);
			}
			b = true;
		case 'c':
			str = "lass";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_CLASS, token.lineNum);
			}
			b = true;
		case ',':
			return new Token(Kind.TOKEN_COMMER, token.lineNum);
		case '.':
			return new Token(Kind.TOKEN_DOT, token.lineNum);
		case 'e':
			str = "lse";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_ELSE, token.lineNum);
			}
			b = true;
			str = "xtends";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_EXTENDS, token.lineNum);
			}
			b = true;
			
	//	case 'EOF'
			
		case 'f':
			str = "alse";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_FALSE, token.lineNum);
			}
			b = true;
			
	//	case 'IN'

		case 'i':
			str = "f";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_IF, token.lineNum);
			}
			b = true;
	/* zheli*/		str = "xtends";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_EXTENDS, token.lineNum);
			}
			b = true;
		case '{':
			return new Token(Kind.TOKEN_LBRACE, token.lineNum);
		case '[':
			return new Token(Kind.TOKEN_LBRACK, token.lineNum);
		case 'l':
			str = "ength";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_LENGTH, token.lineNum);
			}
			b = true;
		case '(':
			return new Token(Kind.TOKEN_LPAREN, token.lineNum);
		case '<':
			return new Token(Kind.TOKEN_LT, token.lineNum);
		case 'm':
			str = "ain";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_MAIN, token.lineNum);
			}
			b = true;
		case 'n':
			str = "ew";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_NEW, token.lineNum);
			}
			b = true;
		case '!':
			return new Token(Kind.TOKEN_NOT, token.lineNum);
		case 'o':
			str = "ut";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_OUT, token.lineNum);
			}
			b = true;
		
		case 'p':
			str = "rintln";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_PRINTLN, token.lineNum);
			}
			b = true;
			str = "ublic";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_PUBLIC, token.lineNum);
			}
			b = true;
		case '}':
			return new Token(Kind.TOKEN_RBRACE, token.lineNum);	
		case ']':
			return new Token(Kind.TOKEN_RBRACK, token.lineNum);
		case 'r':
			str = "eturn";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_RETURN, token.lineNum);
			}
			b = true;
		case ')':
			return new Token(Kind.TOKEN_RPAREN, token.lineNum);
		case ';':
			return new Token(Kind.TOKEN_SEMI, token.lineNum);
		case 's':
			str = "tatic";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_STATIC, token.lineNum);
			}
			b = true;
		case 'S':
			str = "tring";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_STRING, token.lineNum);
			}
			b = true;
			str = "ystem";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_SYSTEM, token.lineNum);
			}
			b = true;
		case '-':
			return new Token(Kind.TOKEN_SUB, token.lineNum);
		case 't':
			str = "tatic";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_THIS, token.lineNum);
			}
			b = true;
			str = "rue";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_TRUE, token.lineNum);
			}
			b = true;
		case '*':
			return new Token(Kind.TOKEN_TIMES, token.lineNum);
		case 'v':
			str = "oid";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_VOID, token.lineNum);
			}
			b = true;
		case 'w':
			str = "hile";
			b = Comparing(str, c);
			if (b) {
				return new Token(Kind.TOKEN_WHILE, token.lineNum);
			}
			b = true;
		default:
			// Lab 1, exercise 2: supply missing code to
			// lex other kinds of tokens.
			// Hint: think carefully about the basic
			// data structure and algorithms. The code
			// is not that much and may be less than 50 lines. If you
			// find you are writing a lot of code, you
			// are on the wrong way.
			new Todo();
			return null;
		}
	}
	
	public boolean Comparing(String str, int c) {
		while (b && index < str.length()) {
			try {
				c = this.fstream.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (c != str.charAt(index)) {
				b = false;
				break;
			}				
		}
		return b;
	}

	public Token nextToken() {
		Token t = null;

		try {
			t = this.nextTokenInternal();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if (control.Control.lex)
			System.out.println(t.toString());
		return t;
	}
}
