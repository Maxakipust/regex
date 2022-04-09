package com.kipust.regex;

/**
 * A class to compile a regex into DFAs and run them against strings
 */
public class Pattern {
    /**
     * Returns a pattern that accepts any string
     * @return
     */
    public static Pattern AcceptAny(){
        return new Pattern(new Dfa());
    }

    /**
     * Compile a regex into a dfa
     * Grammar:
     * And: &(A,B)
     * Or: |(A,B)
     * Kleene Star: *(A)
     * Constant: 'abc'
     * Wildcard: .
     * @param regex the regex to compile
     * @return A pattern that matches the regex
     */
    public static Pattern Compile(String regex){
        try {
            Lexer l = new Lexer(regex);
            Parser p = new Parser(l);
            AST ast = p.parse();
            Dfa dfa = new Dfa(ast);
            return new Pattern(dfa);
        }catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }
    private Dfa dfa;
    private Pattern (Dfa dfa){
        this.dfa = dfa;
    }

    /**
     * Run the pattern against an input string
     * @param input the string to run the pattern against
     * @return A DFAResult of AcceptingResult or TrashResult
     */
    public Dfa.DFAResult match(String input){
        return dfa.run(input);
    }
}
