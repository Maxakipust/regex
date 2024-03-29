package com.kipust.regex;

import java.util.HashSet;
import java.util.Set;

/**
 * A visitor to find all the constants that a regex uses.
 * This is used to construct the DFA.
 */
class FindConstantsVisitor implements Visitor {
    Set<Const> constants = new HashSet<>();

    public FindConstantsVisitor(){

    }

    @Override
    public void visitZeroOrMore(AST.ZeroOrMore ast) {
        ast.child.accept(this);
    }

    @Override
    public void visitOr(AST.Or ast) {
        ast.right.accept(this);
        ast.left.accept(this);
    }

    @Override
    public void visitAnd(AST.And ast) {
        ast.right.accept(this);
        ast.left.accept(this);
    }

    @Override
    public void visitConstant(AST.Constant ast) {
        constants.add(new Const.Value(ast.value));
    }

    @Override
    public void visitEmptySet(AST.EmptySet ast) {

    }

    @Override
    public void visitRange(AST.Range ast) {
        for(int i = ast.start; i <=ast.end; i++){
            constants.add(new Const.Value(Character.toString((char)i)));
        }
    }

    @Override
    public void visitWildcard(AST.Wildcard ast) {
        constants.add(new Const.Wildcard());
    }
}
