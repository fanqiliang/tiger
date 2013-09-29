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
	String str;
	boolean b;

	public Lexer(String fname, InputStream fstream) {
		this.fname = fname;
		this.fstream = fstream;
		this.token.lineNum = 1;
	}

	// When called, return the next token (refer to the code "Token.java")
	// from the input stream.
	// Return TOKEN_EOF when reaching the end of the input stream.
	private Token nextTokenInternal() throws Exception {
		int c = this.fstream.read();
		token.lexeme = "";
		b = true;
		if (-1 == c)
			// The value for "lineNum" is now "null",
			// you should modify this to an appropriate
			// line number for the "EOF" token.
			return new Token(Kind.TOKEN_EOF, token.lineNum);

		// skip all kinds of "blanks"
		while (' ' == c || '\t' == c || '\n' == c || '/' == c) {
			if (c == '\n') {
				token.lineNum++;
			}
			if (c == '/') {
				while (c != '\n') {
					c = this.fstream.read();
				}
				token.lineNum++;
			}
			c = this.fstream.read();
		}
		
		if (-1 == c)
			return new Token(Kind.TOKEN_EOF, token.lineNum);
		if (c >= '0' && c <= '9') {
			token.lexeme += (char) c;
			while (true) {
				this.fstream.mark(1);
				c = this.fstream.read();
				if (c >= '0' && c <= '9') {
					token.lexeme += (char) c;
				} else {
					this.fstream.reset();
					return new Token(Kind.TOKEN_NUM, token.lineNum, token.lexeme);
				}
			}
			
		}
		if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c == '_')) {
			token.lexeme += (char) c;
			switch (c) {
			case 'b':
				str = "oolean";
				if (Comparing(str, c)) {
					return new Token(Kind.TOKEN_BOOLEAN, token.lineNum);
				}
				return returnback(c);
			case 'c':
				str = "lass";
				if (b = Comparing(str, c)) {
					return new Token(Kind.TOKEN_CLASS, token.lineNum);
				}
				return returnback(c);
			case 'e':
				this.fstream.mark(1);
				c = this.fstream.read();
				if (c == 'l') {
					token.lexeme += (char) c;
					str = "se";
					if (Comparing(str, c)) {
						return new Token(Kind.TOKEN_ELSE, token.lineNum);
					}
				} else if (c == 'x') {
					token.lexeme += (char) c;
					str = "tends";
					if (Comparing(str, c)) {
						return new Token(Kind.TOKEN_EXTENDS, token.lineNum);
					}
				}
			    if (!(c >= 'A' && c <= 'Z') && !(c >= 'a' && c <= 'z')
						&& !(c == '_') && !(c >= '0' && c <= '9')) {
			    	c = this.fstream.read();
			    } else {
			    	token.lexeme += (char) c;
			    }
			    return returnback(c);
			case 'f':
				str = "alse";
				if (Comparing(str, c)) {
					return new Token(Kind.TOKEN_FALSE, token.lineNum);
				}
				return returnback(c);
			case 'i':
				this.fstream.mark(1);
				c = this.fstream.read();			
				if (c == 'n') {
					token.lexeme += (char) c;
					str = "t";
					if (Comparing(str, c)) {
						return new Token(Kind.TOKEN_INT, token.lineNum);
					}
				} else if (c == 'f') {
					token.lexeme += (char) c;
					return new Token(Kind.TOKEN_IF, token.lineNum);
				}
			    if (!(c >= 'A' && c <= 'Z') && !(c >= 'a' && c <= 'z')
						&& !(c == '_') && !(c >= '0' && c <= '9')) {
			    	this.fstream.reset();
			    } else {
			    	token.lexeme += (char) c;
			    }
			    return returnback(c);
			case 'l':
				str = "ength";
				if (Comparing(str, c)) {
					return new Token(Kind.TOKEN_LENGTH, token.lineNum);
				}
				return returnback(c);
			case 'm':
				str = "ain";
				if (Comparing(str, c)) {
					return new Token(Kind.TOKEN_MAIN, token.lineNum);
				}
				return returnback(c);
			case 'n':
				str = "ew";
				if (Comparing(str, c)) {
					return new Token(Kind.TOKEN_NEW, token.lineNum);
				}
				return returnback(c);
			case 'o':
				str = "ut";
				if (Comparing(str, c)) {
					return new Token(Kind.TOKEN_OUT, token.lineNum);
				}
				return returnback(c);
			case 'p':
				this.fstream.mark(1);
				c = this.fstream.read();
				if (c == 'r') {
					token.lexeme += (char) c;
					str = "intln";
					if (Comparing(str, c)) {
						return new Token(Kind.TOKEN_PRINTLN, token.lineNum);
					}
				} else if (c == 'u') {
					token.lexeme += (char) c;
					str = "blic";
					if (Comparing(str, c)) {
						return new Token(Kind.TOKEN_PUBLIC, token.lineNum);
					}
				}
				if (!(c >= 'A' && c <= 'Z') && !(c >= 'a' && c <= 'z')
							&& !(c == '_') && !(c >= '0' && c <= '9')) {
				    	this.fstream.reset();
				    } else {
				    	token.lexeme += (char) c;
				    }
				return returnback(c);
			case 'r':
				str = "eturn";
				if (Comparing(str, c)) {
					return new Token(Kind.TOKEN_RETURN, token.lineNum);
				}
				return returnback(c);
			case 's':
				str = "tatic";
				if (Comparing(str, c)) {
					return new Token(Kind.TOKEN_STATIC, token.lineNum);
				}
				return returnback(c);
			case 'S':
				this.fstream.mark(1);
				c = this.fstream.read();
				
				if (c == 't') {
					token.lexeme += (char) c;
					str = "ring";
					if (Comparing(str, c)) {
						return new Token(Kind.TOKEN_STRING, token.lineNum);
					}
				} else if (c == 'y') {
					token.lexeme += (char) c;
					str = "stem";
					if (Comparing(str, c)) {
						return new Token(Kind.TOKEN_SYSTEM, token.lineNum);
					}
				}
				if (!(c >= 'A' && c <= 'Z') && !(c >= 'a' && c <= 'z')
							&& !(c == '_') && !(c >= '0' && c <= '9')) {
				    	this.fstream.reset();
				    } else {
				    	token.lexeme += (char) c;
				    }
				return returnback(c);
			case 't':
				this.fstream.mark(1);
				c = this.fstream.read();
				
				if (c == 'h') {
					token.lexeme += (char) c;
					str = "is";
					if (Comparing(str, c)) {
						return new Token(Kind.TOKEN_THIS, token.lineNum);
					}
				} else if (c == 'r') {
					token.lexeme += (char) c;
					str = "ue";
					if (Comparing(str, c)) {
						return new Token(Kind.TOKEN_TRUE, token.lineNum);
					}
				}
				if (!(c >= 'A' && c <= 'Z') && !(c >= 'a' && c <= 'z')
						&& !(c == '_') && !(c >= '0' && c <= '9')) {
			    	this.fstream.reset();
			    } else {
			    	token.lexeme += (char) c;
			    }
				return returnback(c);
			case 'v':
				str = "oid";
				if (Comparing(str, c)) {
					return new Token(Kind.TOKEN_VOID, token.lineNum);
				}
				return returnback(c);
			case 'w':
				str = "hile";
				if (Comparing(str, c)) {
					return new Token(Kind.TOKEN_WHILE, token.lineNum);
				}
				return returnback(c);
			default:
				return returnback(c);
			}
		}

		switch (c) {
		case '+':
			return new Token(Kind.TOKEN_ADD, token.lineNum);
		case '&':
			c = this.fstream.read();
			if (c == '&') {
				return new Token(Kind.TOKEN_BOOLEAN, token.lineNum);
			}
		case '=':
			return new Token(Kind.TOKEN_ASSIGN, token.lineNum);
		case ',':
			return new Token(Kind.TOKEN_COMMER, token.lineNum);
		case '.':
			return new Token(Kind.TOKEN_DOT, token.lineNum);
		case '{':
			return new Token(Kind.TOKEN_LBRACE, token.lineNum);
		case '[':
			return new Token(Kind.TOKEN_LBRACK, token.lineNum);
		case '(':
			return new Token(Kind.TOKEN_LPAREN, token.lineNum);
		case '<':
			return new Token(Kind.TOKEN_LT, token.lineNum);
		case '>':
			return new Token(Kind.TOKEN_RT, token.lineNum);
		case '!':
			return new Token(Kind.TOKEN_NOT, token.lineNum);
		case '}':
			return new Token(Kind.TOKEN_RBRACE, token.lineNum);
		case ']':
			return new Token(Kind.TOKEN_RBRACK, token.lineNum);
		case ')':
			return new Token(Kind.TOKEN_RPAREN, token.lineNum);
		case ';':
			return new Token(Kind.TOKEN_SEMI, token.lineNum);
		case '-':
			return new Token(Kind.TOKEN_SUB, token.lineNum);
		case '*':
			return new Token(Kind.TOKEN_TIMES, token.lineNum);

		default:
			// Lab 1, exercise 2: supply missing code to
			// lex other kinds of tokens.
			// Hint: think carefully about the basic
			// data structure and algorithms. The code
			// is not that much and may be less than 50 lines. If you
			// find you are writing a lot of code, you
			// are on the wrong way.
			System.out.format("debug: current is '%s', next is '%s'\n", (char)c, (char)this.fstream.read());
			new Todo();
			return null;
		}
	}
	public Token returnback(int c) throws Exception{
		while (true) {
			this.fstream.mark(1);
			c = this.fstream.read();
			if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
					|| (c == '_') || (c >= '0' && c <= '9')) {
				token.lexeme += (char) c;
			} else {
				this.fstream.reset();
				return new Token(Kind.TOKEN_ID, token.lineNum,
						token.lexeme);
			}
		}
	}

	public boolean Comparing(String str, int c) {
		int index = 0;
		
		while (b && index <= str.length()) {
			try {
				this.fstream.mark(1);
				c = this.fstream.read();				
				if (index == str.length()) {
					if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
							|| (c == '_') || (c >= '0' && c <= '9')) {
						this.fstream.reset();
						b =false;
						return b;
					}
					this.fstream.reset();
					return b;
				}
				if (c == str.charAt(index)) {
					token.lexeme += (char) c;
					index++;
				} else {
					b = false;
					if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
							|| (c == '_') || (c >= '0' && c <= '9')) {
						token.lexeme += (char) c;
						return b;
					}
					this.fstream.reset();
					return b;
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
