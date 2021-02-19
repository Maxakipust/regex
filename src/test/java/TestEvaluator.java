
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestEvaluator {
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

    private boolean runRegex(String regex, String str){
        AST ast = createAST(regex);
        EvaluatorVisitor v = new EvaluatorVisitor(str);
        try {
            ast.accept(v);
        }catch (RuntimeException ex){
            return false;
        }
        return "".equals(v.getStr());
    }

    @Test
    public void testConst(){
        assertTrue(runRegex("\"a\"", "a"));
    }
    @Test
    public void testConstFail(){
        assertFalse(runRegex("\"a\"", "b"));
    }
    @Test
    public void testEmptyStr(){
        assertFalse(runRegex("\"\"", "a"));
    }
    @Test
    public void testAnd(){
        assertTrue(runRegex("&(\"a\",\"b\")", "ab"));
    }
    @Test
    public void testAndMissingEnd(){
        assertFalse(runRegex("&(\"a\",\"b\")", "a"));
    }
    @Test
    public void testAndMissingStart(){
        assertFalse(runRegex("&(\"a\",\"b\")", "b"));
    }
    @Test
    public void testOrStart(){
        assertTrue(runRegex("|(\"a\",\"b\")", "a"));
    }
    @Test
    public void testOrEnd(){
        assertTrue(runRegex("|(\"a\",\"b\")", "b"));
    }
    @Test
    public void testOrBoth(){
        assertFalse(runRegex("|(\"a\",\"b\")", "ab"));
    }
    @Test
    public void testZeroOrOneOne(){
        assertTrue(runRegex("|(\"\",\"a\")", "a"));
    }
    @Test
    public void testZeroOrOneZero(){
        assertTrue(runRegex("|(\"\",\"a\")", ""));
    }
    @Test
    public void testZeroOrOneTwo(){
        assertFalse(runRegex("|(\"\",\"a\")", "aa"));
    }
    @Test
    public void testZeroOrMoreZero(){
        assertTrue(runRegex("*(\"a\")", ""));
    }
    @Test
    public void testZeroOrMoreOne(){
        assertTrue(runRegex("*(\"a\")", "a"));
    }
    @Test
    public void testZeroOrMoreTwo(){
        assertTrue(runRegex("*(\"a\")", "aa"));
    }
    @Test
    public void testZeroOrMoreThree(){
        assertTrue(runRegex("*(\"a\")", "aaa"));
    }
    @Test
    public void testZeroOrMoreWrong(){
        assertFalse(runRegex("*(\"a\")", "b"));
    }
    @Test
    public void testZeroOrMoreWrongWrightAfter(){
        assertFalse(runRegex("*(\"a\")", "baa"));
    }
    @Test
    public void testAndOr(){
        assertTrue(runRegex("&(\"a\",|(\"b\",\"c\"))", "ab"));
        assertTrue(runRegex("&(\"a\",|(\"b\",\"c\"))", "ac"));
        assertFalse(runRegex("&(\"a\",|(\"b\",\"c\"))", "ad"));
        assertFalse(runRegex("&(\"a\",|(\"b\",\"c\"))", "a"));
    }
    @Test
    public void testOrAnd(){
        assertTrue(runRegex("|(\"a\",&(\"b\",\"c\"))", "a"));
        assertTrue(runRegex("|(\"a\",&(\"b\",\"c\"))", "bc"));
        assertFalse(runRegex("|(\"a\",&(\"b\",\"c\"))", "ab"));
        assertFalse(runRegex("|(\"a\",&(\"b\",\"c\"))", "ac"));
    }
    @Test
    public void testOneOrMore(){
        String regex = "&(\"a\",*(\"a\"))";
        assertFalse(runRegex(regex, ""));
        assertTrue(runRegex(regex, "a"));
        assertTrue(runRegex(regex, "aa"));
        assertFalse(runRegex(regex, "ab"));
    }
    @Test
    public void testOrZeroOrMore(){
        String regex = "|(\"b\",*(\"a\"))";
        assertTrue(runRegex(regex, "a"));
        assertTrue(runRegex(regex, "aa"));
        assertTrue(runRegex(regex, "b"));
        assertFalse(runRegex(regex, "bb"));
        assertFalse(runRegex(regex, "ba"));
    }
    @Test
    public void testList(){
        String regex = "&(\"<\",&(&(\"a\",*(\",a\")),\">\"))";
        assertTrue(runRegex(regex, "<a>"));
        assertTrue(runRegex(regex, "<a,a>"));
        assertTrue(runRegex(regex, "<a,a,a>"));
        assertFalse(runRegex(regex, "<>"));
        assertFalse(runRegex(regex, "<b,b,b>"));
        assertFalse(runRegex(regex, "b,b,b"));
    }

}
