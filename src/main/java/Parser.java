import java.util.LinkedList;
import java.util.List;

public class Parser {
    List<Token> tokens = new LinkedList<>();
    int tokenIndex = 0;

    Lexer lexer;

    public Parser(Lexer lexer){
        this.lexer = lexer;
    }

    public Token peek() throws Exception {
        while(tokens.size() <= tokenIndex){
            tokens.add(lexer.getToken());
        }
        return tokens.get(tokenIndex);
    }

    public Token consume() throws Exception {
        Token t = peek();
        tokenIndex++;
        return t;
    }

    public Token expect(Token t) throws Exception{
        Token other = peek();
        if(t.getClass().equals(other.getClass())){
            return consume();
        }
        throw new Exception("expected " + t.getClass().toGenericString() + "but got " + other.getClass().toGenericString());
    }

    public AST parse() throws Exception{
        Token t = consume();
        if(t instanceof Token.TokQuestionMark){
            expect(new Token.TokOpenParen());
            AST ret = new AST.ZeroOrOne(parse());
            expect(new Token.TokCloseParen());
            return ret;
        }else if(t instanceof Token.TokPlus){
            expect(new Token.TokOpenParen());
            AST ret = new AST.OneOrMore(parse());
            expect(new Token.TokCloseParen());
            return ret;
        }else if(t instanceof Token.TokAsterisk){
            expect(new Token.TokOpenParen());
            AST ret = new AST.ZeroOrMore(parse());
            expect(new Token.TokCloseParen());
            return ret;
        }else if(t instanceof Token.TokBar){
            expect(new Token.TokOpenParen());
            AST left = parse();
            expect(new Token.TokComma());
            AST ret = new AST.Or(left, parse());
            expect(new Token.TokCloseParen());
            return ret;
        }else if(t instanceof Token.TokOpenParen){
            expect(new Token.TokOpenParen());
            AST ret = new AST.Group(parse());
            expect(new Token.TokCloseParen());
            return ret;
        }else if(t instanceof Token.TokAmpersand){
            expect(new Token.TokOpenParen());
            AST left = parse();
            expect(new Token.TokComma());
            AST ret = new AST.And(left, parse());
            expect(new Token.TokCloseParen());
            return ret;
        }else if(t instanceof Token.TokConstant){
            return new AST.Constant(((Token.TokConstant) t).value);
        } else {
            throw new Exception("parse error");
        }
    }
}
