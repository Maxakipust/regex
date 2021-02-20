public class EvaluatorVisitor implements Visitor{
    String str;
    public EvaluatorVisitor(String str){
        this.str = str;
    }

    public String getStr(){
        return str;
    }

    @Override
    public void visitZeroOrMore(AST.ZeroOrMore ast) {
        while(true){
            String temp = str;
            try{
                ast.child.accept(this);
            }catch (RuntimeException ex){
                this.str = temp;
                break;
            }
        }
    }

    @Override
    public void visitOr(AST.Or ast) {
        String orig = str;
        String leftStr = null;
        String rightStr = null;
        try{
            ast.left.accept(this);
            leftStr = str;
            str = orig;
        }catch (RuntimeException ex){

        }
        try{
            ast.right.accept(this);
            rightStr = str;
            str = orig;
        }catch (RuntimeException ex){

        }
        if(rightStr != null){
            if(leftStr != null){
                str = leftStr.length() <= rightStr.length() ? leftStr : rightStr;
                return;
            }
            str = rightStr;
            return;
        }else{
            if(leftStr != null){
                str = leftStr;
                return;
            }
        }
        throw new RuntimeException("Nope");
    }

    @Override
    public void visitAnd(AST.And ast) {
        ast.left.accept(this);
        ast.right.accept(this);
    }

    @Override
    public void visitConstant(AST.Constant ast) {
        if("".equals(ast.value)){
            return;
        }
        if(str.startsWith(ast.value)){
            str = str.replaceFirst(ast.value, "");
        }else {
            throw new RuntimeException("nope");
        }
    }

    @Override
    public void visitRange(AST.Range ast) {
        char startChar = str.charAt(0);
        if(ast.matchesChar(startChar)){
            str = str.substring(1);
        }else{
            throw new RuntimeException("nope");
        }
    }

    @Override
    public void visitEmptySet(AST.EmptySet ast) {
        throw new RuntimeException("nope");
    }
}
