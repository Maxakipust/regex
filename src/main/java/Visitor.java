public interface Visitor<T> {
    void visitZeroOrOne(AST.ZeroOrOne ast);
    void visitOneOrMore(AST.OneOrMore ast);
    void visitZeroOrMore(AST.ZeroOrMore ast);
    void visitOr(AST.Or ast);
    void visitGroup(AST.Group ast);
    void visitAnd(AST.And ast);
    void visitConstant(AST.Constant ast);
}
