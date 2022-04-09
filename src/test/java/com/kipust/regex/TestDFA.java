package com.kipust.regex;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestDFA {
    public Dfa createDFA(String regex){
        Lexer l = new Lexer(regex);
        Parser p = new Parser(l);
        AST ast = null;
        try {
            ast = p.parse();
        } catch (Exception e) {
            assertTrue(false);
        }
        return new Dfa(ast);
    }

    @Test
    public void testConst(){
        Dfa dfa = createDFA("'a'");
        assertTrue(dfa.run("a").success());
        assertFalse(dfa.run("b").success());
        assertFalse(dfa.run("").success());
    }

    @Test
    public void testAnd(){
        Dfa dfa = createDFA("&('a','b')");
        assertTrue(dfa.run("ab").success());
        assertFalse(dfa.run("a").success());
        assertFalse(dfa.run("b").success());
        assertFalse(dfa.run("ba").success());
        assertFalse(dfa.run("").success());
    }

    @Test
    public void testOr(){
        Dfa dfa = createDFA("|('a','b')");
        assertTrue(dfa.run("a").success());
        assertTrue(dfa.run("b").success());
        assertFalse(dfa.run("ab").success());
        assertFalse(dfa.run("").success());
        assertFalse(dfa.run("ba").success());
        assertFalse(dfa.run("aa").success());
    }

    @Test
    public void testZeroOrMore(){
        Dfa dfa = createDFA("*('a')");
        assertTrue(dfa.run("a").success());
        assertTrue(dfa.run("aa").success());
        assertTrue(dfa.run("").success());
        assertFalse(dfa.run("ab").success());
        assertFalse(dfa.run("ba").success());
        assertFalse(dfa.run("baa").success());
    }

    @Test
    public void testSimple(){
        Dfa dfa = createDFA("*(|(&('a','b'),'c'))");
        assertTrue(dfa.run("ab").success());
        assertTrue(dfa.run("c").success());
        assertTrue(dfa.run("abc").success());
        assertTrue(dfa.run("abab").success());
        assertTrue(dfa.run("cab").success());
        assertTrue(dfa.run("cc").success());
        assertTrue(dfa.run("").success());

        assertFalse(dfa.run("ac").success());
        assertFalse(dfa.run("ca").success());
        assertFalse(dfa.run("abca").success());
    }

    @Test
    public void testNested(){
        String regex = "&(|('0',|('1',|('2',|('3',|('4',|('5',|('6',|('7',|('8','9'))))))))),|('0',|('1',|('2',|('3',|('4',|('5',|('6',|('7',|('8','9'))))))))))";
        Dfa dfa = createDFA(regex);
        for(Integer i = 0; i<10; i++){
            for(Integer j = 0; j<10; j++){
                assertTrue(dfa.run(i.toString() + j.toString()).success());
            }
        }

        assertFalse(dfa.run("").success());
        assertFalse(dfa.run("100").success());
        assertFalse(dfa.run("111").success());
    }
}
