public class Main {
    public static void main(String[] args) throws Exception {
        Lexer l = new Lexer("&(*(|(\"abc\",\"123\")),+(\"4\"))");
        Parser p = new Parser(l);
        AST ast = p.parse();
        System.out.println(ast);
        EvaluatorVisitor visitor = new EvaluatorVisitor("4");
        try {
            ast.accept(visitor);
        }catch (RuntimeException ex){
            System.out.println("fail");
            return;
        }
        if("".equals(visitor.getStr())) {
            System.out.println("success");
        }else {
            System.out.println("fail");
        }
    }
}
