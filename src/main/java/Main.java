public class Main {
    public static void main(String[] args) throws Exception {
        Lexer l = new Lexer("&(|(\"abc\",\"123\"),\"4\")");
        Parser p = new Parser(l);
        AST ast = p.parse();
        System.out.println(ast);
        if("".equals(ast.accept("abc45"))) {
            System.out.println("success");
        }else {
            System.out.println("fail");
        }
    }
}
