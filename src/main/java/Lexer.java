public class Lexer {
    String source = "";
    int index = 0;

    public Lexer(String source){
        this.source = source;
    }

    public Token getToken() throws Exception{
        if(index >= source.length()){
            return new Token.TokEOF();
        }
        switch (source.charAt(index)){
//            case '?':
//                return lexQuestionMark();
//            case '+':
//                return lexPlus();
            case '*':
                return lexAsterisk();
            case '|':
                return lexBar();
            case '(':
                return lexOpenParen();
            case ')':
                return lexCloseParen();
            case '"':
                return lexQuote();
            case '&':
                return lexAmpersand();
            case ',':
                return lexComma();
            case '[':
                return lexRange();
            default:
                throw new Exception("invalid symbol at "+index);
        }
    }

    private Token lexRange(){
        index ++;
        char start = source.charAt(index);
        index++;
        if(source.charAt(index) != '-'){
            throw new RuntimeException("expected -");
        }
        index++;
        char end = source.charAt(index);
        index++;
        if(source.charAt(index) != ']'){
            throw new RuntimeException("expected ]");
        }
        index++;
        return new Token.TokRange(Character.toString(start), Character.toString(end));
    }

    private Token lexQuote() {
        index++;
        int start = index;
        while(source.charAt(index) != '"'){
            index++;
        }
        Token t = new Token.TokConstant(source.substring(start, index));
        index++;
        return t;
    }

    private Token lexComma() {
        index++;
        return new Token.TokComma();
    }

    private Token lexAmpersand() {
        index++;
        return new Token.TokAmpersand();
    }

    private Token lexCloseParen() {
        index++;
        return new Token.TokCloseParen();
    }

    private Token lexOpenParen() {
        index++;
        return new Token.TokOpenParen();
    }

    private Token lexBar() {
        index++;
        return new Token.TokBar();
    }

    private Token lexAsterisk() {
        index++;
        return new Token.TokAsterisk();
    }

//    private Token lexPlus() {
//        index++;
//        return new Token.TokPlus();
//    }
//
//    private Token lexQuestionMark() {
//        index++;
//        return new Token.TokQuestionMark();
//    }

}
