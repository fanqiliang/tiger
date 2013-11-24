package parser;

import java.util.LinkedList;
import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;

public class Parser {
	Lexer lexer;
	Token current;
	Token previous;
	int   flag;

	public Parser(String fname, java.io.InputStream fstream) {
		lexer = new Lexer(fname, fstream);
		current = lexer.nextToken();
		//previous = new Token();
		this.flag = 0;
	}

	// /////////////////////////////////////////////
	// utility methods to connect the lexer
	// and the parser.

	private void advance() {
		//previous = new Token();
		//previous = current;
		if (this.flag == 0) {
			current = lexer.nextToken();
		} else {
			current = previous;
			this.flag = 0;
		}
	}
	

	private void eatToken(Kind kind) {
		if (kind == current.kind)
			advance();
		else {
			System.out.format("lineNum is :%d", current.lineNum);
			System.out.println();
			System.out.println("Expects: " + kind.toString());
			System.out.println("But got: " + current.kind.toString());
			System.exit(1);
		}
	}

	private void error() {
		System.out.format("error LineNum is :%d", current.lineNum);
		System.out.println();
		System.out.println("current == "+current);
		System.out.println("Syntax error: compilation aborting...\n");
		System.exit(1);
		return;
	}

	// ////////////////////////////////////////////////////////////
	// below are method for parsing.

	// A bunch of parsing methods to parse expressions. The messy
	// parts are to deal with precedence and associativity.

	// ExpList -> Exp ExpRest*
	// ->
	// ExpRest -> , Exp
	private LinkedList<ast.exp.T> parseExpList() {
		LinkedList<ast.exp.T> result = new LinkedList<ast.exp.T>();
		if (current.kind == Kind.TOKEN_RPAREN)
			return result;
		result.add(parseExp());
		while (current.kind == Kind.TOKEN_COMMER) {
			advance();
			result.add(parseExp());
		}
		return result;
	}

	// AtomExp -> (exp)
	// -> INTEGER_LITERAL
	// -> true
	// -> false
	// -> this
	// -> id
	// -> new int [exp]
	// -> new id ()
	private ast.exp.T parseAtomExp() {
		ast.exp.T result = null;
		switch (current.kind) {
		case TOKEN_LPAREN:
			advance();
			result = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			return result;
		case TOKEN_NUM:
			result = new ast.exp.Num(Integer.valueOf(current.lexeme));
			advance();
			return result;
		case TOKEN_TRUE:
			result = new ast.exp.True();
			advance();			
			return result;
		case TOKEN_FALSE:
			result = new ast.exp.False();
			advance();
			return result;
		case TOKEN_THIS:
			result = new ast.exp.This();
			advance();
			return result;
		case TOKEN_ID:
			result = new ast.exp.Id(current.lexeme);
			advance();
			return result;
		case TOKEN_NEW: {
			advance();
			switch (current.kind) {
			case TOKEN_INT:
				advance();
				eatToken(Kind.TOKEN_LBRACK);
				result = parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				return new ast.exp.NewIntArray(result);
			case TOKEN_ID:
				result = new ast.exp.NewObject(current.lexeme);
				advance();
				eatToken(Kind.TOKEN_LPAREN);
				eatToken(Kind.TOKEN_RPAREN);
				return result;
			default:
				error();
				return null;
			}
		}
		default:
			error();
			return null;
		}
	}

	// NotExp -> AtomExp
	// -> AtomExp .id (expList)
	// -> AtomExp [exp]
	// -> AtomExp .length
	private ast.exp.T parseNotExp() {
		ast.exp.T atomExp = parseAtomExp();
		while (current.kind == Kind.TOKEN_DOT
				|| current.kind == Kind.TOKEN_LBRACK) {
			if (current.kind == Kind.TOKEN_DOT) {
				advance();
				if (current.kind == Kind.TOKEN_LENGTH) {
					advance();
					atomExp = new ast.exp.Length(atomExp);
					return atomExp;
				}
				String id = current.lexeme;
				eatToken(Kind.TOKEN_ID);
				eatToken(Kind.TOKEN_LPAREN);
				LinkedList<ast.exp.T> explist = parseExpList();
				eatToken(Kind.TOKEN_RPAREN);
				return new ast.exp.Call(atomExp, id, explist);
			} else {
				advance();
				atomExp = new ast.exp.ArraySelect(atomExp, parseAtomExp());
				eatToken(Kind.TOKEN_RBRACK);
			}
		}
		return atomExp;
	}

	// TimesExp -> ! TimesExp
	// -> NotExp
	private ast.exp.T parseTimesExp() {
		int nest = 0;
		while (current.kind == Kind.TOKEN_NOT) {
			nest += 1;
			advance();
		}
		ast.exp.T notExp = parseNotExp();
		for (int i = 0; i < nest; i++) {
			notExp = new ast.exp.Not(notExp);
		}
		return notExp;
	}

	// AddSubExp -> TimesExp * TimesExp
	// -> TimesExp
	private ast.exp.T parseAddSubExp() {
		ast.exp.T timesExp = parseTimesExp();
		while (current.kind == Kind.TOKEN_TIMES) {
			advance();
			timesExp = new ast.exp.Times(timesExp, parseTimesExp());
		}
		return timesExp;
	}

	// LtExp -> AddSubExp + AddSubExp
	// -> AddSubExp - AddSubExp
	// -> AddSubExp
	private ast.exp.T parseLtExp() {
		ast.exp.T subExp = parseAddSubExp();
		while (current.kind == Kind.TOKEN_ADD || current.kind == Kind.TOKEN_SUB) {
			advance();
			subExp = new ast.exp.Sub(subExp, parseAddSubExp());
		}
		return subExp;
	}

	// AndExp -> LtExp < LtExp
	// -> LtExp
	private ast.exp.T parseAndExp() {
		ast.exp.T ltExp = parseLtExp();
		while (current.kind == Kind.TOKEN_LT) {
			advance();
			ltExp = new ast.exp.Lt(ltExp,parseLtExp());
		}
		return ltExp;
	}

	// Exp -> AndExp && AndExp
	// -> AndExp
	private ast.exp.T parseExp() {
		ast.exp.T andExp = parseAndExp();
		while (current.kind == Kind.TOKEN_AND) {
			advance();
			andExp = new ast.exp.And(andExp, parseAndExp());
		}
		return andExp;
	}

	// Statement -> { Statement* }
	// -> if ( Exp ) Statement else Statement
	// -> while ( Exp ) Statement
	// -> System.out.println ( Exp ) ;
	// -> id = Exp ;
	// -> id [ Exp ]= Exp ;
	private ast.stm.T parseStatement() {
		switch (current.kind) {
		case TOKEN_LBRACE:
			advance();
			LinkedList<ast.stm.T> statements = parseStatements();
			eatToken(Kind.TOKEN_RBRACE);
			return new ast.stm.Block(statements);
		case TOKEN_IF:
			advance();
			eatToken(Kind.TOKEN_LPAREN);
			ast.exp.T condition = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			ast.stm.T thenn = parseStatement();
			eatToken(Kind.TOKEN_ELSE);
			ast.stm.T elsee = parseStatement();
			return new ast.stm.If(condition, thenn, elsee);
		case TOKEN_WHILE:
			advance();
			eatToken(Kind.TOKEN_LPAREN);
			ast.exp.T condition1 = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			ast.stm.T body = parseStatement();
			return new ast.stm.While(condition1, body);
		case TOKEN_SYSTEM:
			advance();
			eatToken(Kind.TOKEN_DOT);
			eatToken(Kind.TOKEN_OUT);
			eatToken(Kind.TOKEN_DOT);
			eatToken(Kind.TOKEN_PRINTLN);
			eatToken(Kind.TOKEN_LPAREN);
			ast.exp.T exp = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			eatToken(Kind.TOKEN_SEMI);
			return new ast.stm.Print(exp);
		case TOKEN_ID:
			String id = current.lexeme;
			advance();
			switch (current.kind) {
			case TOKEN_ASSIGN:
				advance();
				ast.exp.T exp1 = parseExp();
				eatToken(Kind.TOKEN_SEMI);
				return new ast.stm.Assign(id, exp1);
			case TOKEN_LBRACK:
				eatToken(Kind.TOKEN_LBRACK);
				ast.exp.T index = parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				eatToken(Kind.TOKEN_ASSIGN);
				ast.exp.T exp2 = parseExp();
				eatToken(Kind.TOKEN_SEMI);
				return new ast.stm.AssignArray(id, index, exp2);
			default:
				//System.out.println("linenum :"+current.lineNum);
				error();
				return null;
			}
		default:
			error();
			return null;
		}
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a statement.
	}

	// Statements -> Statement Statements
	// ->
	private LinkedList<ast.stm.T> parseStatements() {
		LinkedList<ast.stm.T> statements = new LinkedList<ast.stm.T>();
		while (current.kind == Kind.TOKEN_LBRACE
				|| current.kind == Kind.TOKEN_IF
				|| current.kind == Kind.TOKEN_WHILE
				|| current.kind == Kind.TOKEN_SYSTEM
				|| current.kind == Kind.TOKEN_ID) {
			statements.add(parseStatement());
		}
		return statements;
	}

	// Type -> int []
	// -> boolean
	// -> int
	// -> id
	private ast.type.T parseType() {
		//System.out.println("current.line: "+current.lineNum);
		//System.out.println("current.kind: "+current.kind);
		switch (current.kind) {
		case TOKEN_ID:
			String id = current.lexeme;
			advance();
			return new ast.type.Class(id);
		case TOKEN_BOOLEAN:
			advance();
			return new ast.type.Boolean();
		case TOKEN_INT:
			advance();
			if (current.kind == Kind.TOKEN_LBRACK) {
				advance();
				eatToken(Kind.TOKEN_RBRACK);
				return new ast.type.IntArray();
			}
			return new ast.type.Int();
		default:
			error();
			return null;
		}
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a type.
	}

	// VarDecl -> Type id ;
	private ast.dec.T parseVarDecl() {
		// to parse the "Type" nonterminal in this method, instead of writing
		// a fresh one.
		ast.type.T type = parseType();
		String id = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_SEMI);
		return new ast.dec.Dec(type, id);
	}

	// VarDecls -> VarDecl VarDecls
	// ->
	private LinkedList<ast.dec.T> parseVarDecls() {
		LinkedList<ast.dec.T> vardecls = new LinkedList<ast.dec.T>();
		while (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_ID) {
			if (current.kind == Kind.TOKEN_ID) {
				Token token = current;
				advance();
				if (current.kind == Kind.TOKEN_ID) {
					previous = current;
					current = token;
					this.flag = 1;
					vardecls.add(parseVarDecl());
				} else {
					previous = current;
					current = token;
					this.flag = 1;
					return vardecls;
				}
			} else {
				vardecls.add(parseVarDecl());
			}
		}
		return vardecls;
	}

	// FormalList -> Type id FormalRest*
	// ->
	// FormalRest -> , Type id
	private LinkedList<ast.dec.T> parseFormalList() {
		LinkedList<ast.dec.T> formallist = new LinkedList<ast.dec.T>();
		if (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_ID) {
			ast.type.T type = parseType();
			String id = current.lexeme;
			eatToken(Kind.TOKEN_ID);
			formallist.add(new ast.dec.Dec(type, id));
			while (current.kind == Kind.TOKEN_COMMER) {
				advance();
				ast.type.T type1 = parseType();
				String id1 = current.lexeme;
				eatToken(Kind.TOKEN_ID);
				formallist.add(new ast.dec.Dec(type1, id1));
			}
		}
		return formallist;
	}

	// Method -> public Type id ( FormalList )
	// { VarDecl* Statement* return Exp ;}
	private ast.method.T parseMethod() {
		eatToken(Kind.TOKEN_PUBLIC);
		ast.type.T retType = parseType();
		String id = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_LPAREN);
		LinkedList<ast.dec.T> formals = parseFormalList();
		eatToken(Kind.TOKEN_RPAREN);
		eatToken(Kind.TOKEN_LBRACE);
		LinkedList<ast.dec.T> locals = parseVarDecls();
		LinkedList<ast.stm.T> stms = parseStatements();
		eatToken(Kind.TOKEN_RETURN);
		ast.exp.T retExp = parseExp();
		eatToken(Kind.TOKEN_SEMI);
		eatToken(Kind.TOKEN_RBRACE);
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a method.
		// new util.Todo();
		return new ast.method.Method(retType, id, formals, locals, stms, retExp);
	}

	// MethodDecls -> MethodDecl MethodDecls
	// ->
	private LinkedList<ast.method.T> parseMethodDecls() {
		LinkedList<ast.method.T> methoddecls = new LinkedList<ast.method.T>();
		while (current.kind == Kind.TOKEN_PUBLIC) {
			methoddecls.add(parseMethod());
		}
		return methoddecls;
	}

	// ClassDecl -> class id { VarDecl* MethodDecl* }
	// -> class id extends id { VarDecl* MethodDecl* }
	private ast.classs.T parseClassDecl() {
		eatToken(Kind.TOKEN_CLASS);
		String id = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		String extendss = null;
		if (current.kind == Kind.TOKEN_EXTENDS) {
			eatToken(Kind.TOKEN_EXTENDS);
			extendss = current.lexeme;
			eatToken(Kind.TOKEN_ID);
		}
		eatToken(Kind.TOKEN_LBRACE);
		LinkedList<ast.dec.T> decs = parseVarDecls();
		LinkedList<ast.method.T> methods = parseMethodDecls();
		eatToken(Kind.TOKEN_RBRACE);
		return new ast.classs.Class(id, extendss, decs, methods);
	}

	// ClassDecls -> ClassDecl ClassDecls
	// ->
	private LinkedList<ast.classs.T> parseClassDecls() {
		LinkedList<ast.classs.T> classDecls = new LinkedList<ast.classs.T>();
		while (current.kind == Kind.TOKEN_CLASS) {
			classDecls.add(parseClassDecl());
		}
		return classDecls;
	}

	// MainClass -> class id
	// {
	// public static void main ( String [] id )
	// {
	// Statement
	// }
	// }
	private ast.mainClass.T parseMainClass() {
		eatToken(Kind.TOKEN_CLASS);
		String id = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_LBRACE);
		eatToken(Kind.TOKEN_PUBLIC);
		eatToken(Kind.TOKEN_STATIC);
		eatToken(Kind.TOKEN_VOID);
		eatToken(Kind.TOKEN_MAIN);
		eatToken(Kind.TOKEN_LPAREN);
		eatToken(Kind.TOKEN_STRING);
		eatToken(Kind.TOKEN_LBRACK);
		eatToken(Kind.TOKEN_RBRACK);
		String arg = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_RPAREN);
		eatToken(Kind.TOKEN_LBRACE);
		ast.stm.T stm = parseStatement();
		eatToken(Kind.TOKEN_RBRACE);
		eatToken(Kind.TOKEN_RBRACE);
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a main class as described by the
		// grammar above.
		// new util.Todo();
		return new ast.mainClass.MainClass(id, arg, stm);
	}

	// Program -> MainClass ClassDecl*
	private ast.program.T parseProgram() {
		ast.mainClass.T mainClass = parseMainClass();
		LinkedList<ast.classs.T> classs = parseClassDecls();
		eatToken(Kind.TOKEN_EOF);
		return new ast.program.Program(mainClass, classs);
	}

  public ast.program.T parse()
  {
    return parseProgram();
  }
}
