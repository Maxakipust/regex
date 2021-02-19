import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main {

    public static boolean runRegex(AST regex, String str){
        EvaluatorVisitor v = new EvaluatorVisitor(str);
        try {
            regex.accept(v);
        }catch (RuntimeException ex){
            return false;
        }
        return "".equals(v.getStr());
    }

    public static void main(String[] args) throws Exception {
        Lexer l = new Lexer("&(\"<\",&(&(\"a\",*(\",a\")),\">\"))");
        Parser p = new Parser(l);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();;
        //"&("<",&(&("a",+(",a")),">"))"
        AST d0 = p.parse();
        System.out.println(runRegex(d0, "<a,a,a>")?"d0 success": "d0 fail");
        AST d1 = d0.derivative("<");//"&(&("a",+(",a")),">")"
        System.out.println(runRegex(d1, "a,a,a>")?"d1 success": "d1 fail");
        AST d2 = d1.derivative("a");//"&(+(",a"),">")
        System.out.println(runRegex(d2, ",a,a>")?"d2 success": "d2 fail");
        System.out.println(!runRegex(d2, "a,a,a>")?"d2 success": "d2 fail");
        AST d3 = d2.derivative(",a");//"&(*(",a"),">")
        System.out.println(runRegex(d3, ">")?"d3 success": "d3 fail");
        System.out.println(!runRegex(d3, "a>")?"d3 success": "d3 fail");

        AST d4 = d3.derivative(">");//""        System.out.println(runRegex(d0, "<a,a,a>")?"d0 success": "d0 fail");

        System.out.println(gson.toJson(d0));
        System.out.println("<");
        System.out.println(gson.toJson(d1));
        System.out.println("a");
        System.out.println(gson.toJson(d2));
        System.out.println(",a");
        System.out.println(gson.toJson(d3));
        System.out.println(">");
        System.out.println(gson.toJson(d4));

    }
}
