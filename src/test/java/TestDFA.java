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
        Dfa dfa = createDFA("\"a\"");
        assertTrue(dfa.run("a"));
        assertFalse(dfa.run("b"));
        assertFalse(dfa.run(""));
    }

    @Test
    public void testAnd(){
        Dfa dfa = createDFA("&(\"a\",\"b\")");
        assertTrue(dfa.run("ab"));
        assertFalse(dfa.run("a"));
        assertFalse(dfa.run("b"));
        assertFalse(dfa.run("ba"));
        assertFalse(dfa.run(""));
    }

    @Test
    public void testOr(){
        Dfa dfa = createDFA("|(\"a\",\"b\")");
        assertTrue(dfa.run("a"));
        assertTrue(dfa.run("b"));
        assertFalse(dfa.run("ab"));
        assertFalse(dfa.run(""));
        assertFalse(dfa.run("ba"));
        assertFalse(dfa.run("aa"));
    }

    @Test
    public void testZeroOrMore(){
        Dfa dfa = createDFA("*(\"a\")");
        assertTrue(dfa.run("a"));
        assertTrue(dfa.run("aa"));
        assertTrue(dfa.run(""));
        assertFalse(dfa.run("ab"));
        assertFalse(dfa.run("ba"));
        assertFalse(dfa.run("baa"));
    }

    @Test
    public void testSimple(){
        Dfa dfa = createDFA("*(|(&(\"a\",\"b\"),\"c\"))");
        assertTrue(dfa.run("ab"));
        assertTrue(dfa.run("c"));
        assertTrue(dfa.run("abc"));
        assertTrue(dfa.run("abab"));
        assertTrue(dfa.run("cab"));
        assertTrue(dfa.run("cc"));
        assertTrue(dfa.run(""));

        assertFalse(dfa.run("ac"));
        assertFalse(dfa.run("ca"));
        assertFalse(dfa.run("abca"));
    }

    @Test
    public void testNested(){
        String regex = "&(|(\"0\",|(\"1\",|(\"2\",|(\"3\",|(\"4\",|(\"5\",|(\"6\",|(\"7\",|(\"8\",\"9\"))))))))),|(\"0\",|(\"1\",|(\"2\",|(\"3\",|(\"4\",|(\"5\",|(\"6\",|(\"7\",|(\"8\",\"9\"))))))))))";
        Dfa dfa = createDFA(regex);
        for(Integer i = 0; i<10; i++){
            for(Integer j = 0; j<10; j++){
                assertTrue(dfa.run(i.toString() + j.toString()));
            }
        }

        assertFalse(dfa.run(""));
        assertFalse(dfa.run("100"));
        assertFalse(dfa.run("111"));
    }
}
