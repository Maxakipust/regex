public class EvaluatorVisitor implements Visitor{
    String str;
    public EvaluatorVisitor(String str){
        this.str = str;
    }

    public String getStr(){
        return str;
    }

    @Override
    public void visitZeroOrOne(AST.ZeroOrOne ast) {
        String temp = str;
        try {
            ast.child.accept(this);
        }catch (RuntimeException ex){
            str = temp;
        }
    }

    @Override
    public void visitOneOrMore(AST.OneOrMore ast) {
        ast.child.accept(this);
        while(true){
            String s = str;
            try{
                ast.child.accept(this);
            }catch (Exception ex){
                str = s;
                break;
            }
        }
    }

    @Override
    public void visitZeroOrMore(AST.ZeroOrMore ast) {
        while(true){
            String s = str;
            try{
                ast.child.accept(this);
            }catch (RuntimeException ex){
                str = s;
                break;
            }
        }
    }

    @Override
    public void visitOr(AST.Or ast) {
        String s = str;
        try{
            ast.left.accept(this);
        }catch (Exception ex){
            str = s;
            ast.right.accept(this);
        }
    }

    @Override
    public void visitGroup(AST.Group ast) {
        ast.child.accept(this);
    }

    @Override
    public void visitAnd(AST.And ast) {
        ast.left.accept(this);
        ast.right.accept(this);
    }

    @Override
    public void visitConstant(AST.Constant ast) {
        if(str.startsWith(ast.value)){
            str = str.replace(ast.value, "");
        }else {
            throw new RuntimeException("nope");
        }
    }
}
