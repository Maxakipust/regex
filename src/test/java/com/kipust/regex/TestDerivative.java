package com.kipust.regex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDerivative {
    private AST createAST(String str){
        Lexer l = new Lexer(str);
        Parser p = new Parser(l);
        try {
            return p.parse();
        }catch (Exception ex){
            assertTrue(false);
        }
        return null;
    }

    private boolean derivative(String regex, String withRespectTo, String test){
        AST ast = createAST(regex);
        AST d = ast.derivative(new Const.Value(withRespectTo));
        Dfa dfa = new Dfa(d);
        return dfa.run(test).success();
    }

    @Test
    public void testConstant(){
        String regex = "'a'";
        assertTrue(derivative(regex, "a", ""));
        assertFalse(derivative(regex, "a", "a"));
        assertFalse(derivative(regex, "a", "b"));
    }

    @Test
    public void testAnd(){
        String regex = "&('a','b')";
        assertTrue(derivative(regex, "a", "b"));
        assertFalse(derivative(regex, "a", "ab"));
        assertFalse(derivative(regex, "a", "a"));
        String regex2 = "&('','b')";
        assertTrue(derivative(regex2, "b", ""));
        assertFalse(derivative(regex2, "b", "b"));
        assertFalse(derivative(regex2, "b", "a"));
    }
    @Test
    public void testOr(){
        String regex = "|('a','b')";
        assertTrue(derivative(regex, "a", ""));
        assertTrue(derivative(regex, "b", ""));
        assertFalse(derivative(regex, "a", "a"));
        assertFalse(derivative(regex, "a", "b"));
        assertFalse(derivative(regex, "b", "a"));
        assertFalse(derivative(regex, "b", "b"));
    }

    @Test
    public void testZeroOrMore(){
        String regex = "*(&('a','b'))";
        assertTrue(derivative(regex, "a", "b"));
        assertTrue(derivative(regex, "a", "bab"));
        assertFalse(derivative(regex, "a", "ba"));
        assertFalse(derivative(regex, "a", "ab"));
    }
}
