
// File:   MH_Lexer.java

// Java template file for lexer component of Informatics 2A Assignment 1.
// Concerns lexical classes and lexer for the language MH (`Micro-Haskell').


import java.io.* ;

class MH_Lexer extends GenLexer implements LEX_TOKEN_STREAM {

	static class VarAcceptor extends Acceptor implements DFA {
		public String lexClass() {return "VAR" ;} ;
		public int numberOfStates() {return 3 ;} ;

		int next (int state, char c) {
			switch (state) {
			//checks if first character is lower casr
			case 0: if (CharTypes.isSmall(c)) return 1 ; else return 2 ;
			//checks if remaining characters are lower case, upper case, a digit or an apostrophe 
			case 1: if (CharTypes.isSmall(c) || CharTypes.isLarge(c) || CharTypes.isDigit(c) || c == '\'') return 1 ; else return 2 ;
			default: return 2 ; // garbage state
			}
		}

		boolean accepting (int state) {return (state == 1) ;}
		int dead () {return 2 ;}
	}

	static class NumAcceptor extends Acceptor implements DFA {
		public String lexClass() {return "NUM" ;} ;
		public int numberOfStates() {return 4 ;} ;

		int next (int state, char c) {
			switch (state) {
			// checks if first digit is 0 or is a non zero digit
			case 0: if (c == 0) return 1 ; else if (CharTypes.isDigit(c) && c != 0) return 2 ; else return 3;
			// if the first digit was a 0 and there are digits following it then it is declared dead
			case 1: return 3;
			// if first digit was non zero, then checks if remaining characters are all digits
			case 2: if (CharTypes.isDigit(c)) return 2; else return 3;
			default: return 3 ; // garbage state
			}
		}

		boolean accepting (int state) {return (state == 1 || state == 2) ;}
		int dead () {return 3 ;}
	}

	static class BooleanAcceptor extends Acceptor implements DFA {
		public String lexClass() {return "BOOLEAN" ;} ;
		public int numberOfStates() {return 9 ;} ;

		int next (int state, char c) {
			switch (state) {
			// Checks if either the word 'True' or 'False' is spelled
			case 0: if (c == 'T') return 1; else if(c == 'F') return 4; else return 8;
			case 1: if (c == 'r') return 2; else return 8;
			case 2: if (c == 'u') return 3; else return 8;
			// Letter 'e' appears in both words so case is reused
			case 3: if (c == 'e') return 7; else return 8;
			case 4: if (c == 'a') return 5; else return 8;
			case 5: if (c == 'l') return 6; else return 8;
			case 6: if (c == 's') return 3; else return 8;
			case 7: return 8;

			default: return 8 ; // garbage state
			}
		}

		boolean accepting (int state) {return (state == 7) ;}
		int dead () {return 8 ;}
	}

	static class SymAcceptor extends Acceptor implements DFA {
		public String lexClass() {return "SYM" ;} ;
		public int numberOfStates() {return 3 ;} ;

		int next (int state, char c) {
			switch (state) {
			//checks if first character is a symbol, if so then can be accepted
			case 0: if (CharTypes.isSymbolic(c)) return 1 ; else return 2 ;
			//ensures any following characters are also symbols
			case 1: if (CharTypes.isSymbolic(c)) return 1 ; else return 2 ;
			default: return 2 ; // garbage state
			}
		}

		boolean accepting (int state) {return (state == 1) ;}
		int dead () {return 2 ;}
	}

	static class WhitespaceAcceptor extends Acceptor implements DFA {
		public String lexClass() {return "" ;} ;
		public int numberOfStates() {return 3 ;} ;

		int next (int state, char c) {
			switch (state) {
			//checks if first character is a whitespace character, if so then can be accepted
			case 0: if (CharTypes.isWhitespace(c)) return 1 ; else return 2 ;
			//checks to make sure any following characters are also whitespace characters
			case 1: if (CharTypes.isWhitespace(c)) return 1 ; else return 2 ;
			default: return 2 ; // garbage state
			}
		}

		boolean accepting (int state) {return (state == 1) ;}
		int dead () {return 2 ;}
	}

	static class CommentAcceptor extends Acceptor implements DFA {
		public String lexClass() {return "" ;} ;
		public int numberOfStates() {return 5 ;} ;

		int next (int state, char c) {
			switch (state) {
			//checks to see if at least the first two characters are hyphens
			case 0: if (c == '-') return 1; else return 4;
			case 1: if (c == '-') return 2; else return 4;
			//when a non hyphen character is found check if it is a nonSymbolNewline character
			case 2: if (c == '-') return 2; else if (CharTypes.isSymbolic(c) == false && CharTypes.isNewline(c) == false) return 3; else return 4;
			//finally checks any remaining characters are nonNewline
			case 3: if (CharTypes.isNewline(c) == false) return 3; else return 4;
			default: return 4 ; // garbage state
			}
		}

		boolean accepting (int state) {return (state == 3 || state == 2) ;}
		int dead () {return 4 ;}
	}

	static class TokAcceptor extends Acceptor implements DFA {


		String tok ;
		int tokLen ;
		//integer to store the garbage state
		int garbageState;
		TokAcceptor (String tok) {this.tok = tok ; tokLen = tok.length() ; this.garbageState = tokLen+1;}

		public String lexClass() {return tok ;} ;
		//number of states is always the token length + 2
		public int numberOfStates() {return tokLen +2 ;} ;



		int next (int state, char c) {
			if (state >= tokLen) { return  garbageState; }
			else {
				//every time a character in the token matches up, add 1 to the state
				if (c == tok.charAt(state)) return state+1;
				//if they ever do not match, return the garbage state
				else return garbageState;
			}
		}

		//when the state equals the token length then all characters have matched and the token should be accepted
		boolean accepting (int state) {return state == tokLen;}
		int dead () {return garbageState;}
	}

	static DFA[] MH_acceptors = new DFA[] {
			new TokAcceptor("Integer"), 
			new TokAcceptor("Bool"), 
			new TokAcceptor("if"),
			new TokAcceptor("then"), 
			new TokAcceptor("else"), 
			new TokAcceptor("("), 
			new TokAcceptor(")"), 
			new TokAcceptor(";") ,
			new VarAcceptor(),
			new NumAcceptor(),
			new BooleanAcceptor(),
			new SymAcceptor(),
			new WhitespaceAcceptor(),
			new CommentAcceptor()};



	MH_Lexer (Reader reader) {
		super(reader,MH_acceptors) ;
	}

}
class MH_LexerDemo {

	public static void main (String[] args) 
			throws StateOutOfRange, IOException {


		BufferedReader consoleReader = new BufferedReader (new InputStreamReader (System.in)) ;
		while (0==0) {
			System.out.print ("Lexer> ") ;
			String inputLine = consoleReader.readLine() ;
			Reader lineReader = new BufferedReader (new StringReader (inputLine)) ;
			GenLexer demoLexer = new MH_Lexer (lineReader) ;
			try {
				LexToken currTok = demoLexer.pullProperToken() ;
				while (currTok != null) {
					System.out.println (currTok.value() + " \t" + 
							currTok.lexClass()) ;
					currTok = demoLexer.pullProperToken() ;
				}
			} catch (LexError x) {
				System.out.println ("Error: " + x.getMessage()) ;
			}
		} 
	}
}









