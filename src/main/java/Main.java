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
        Lexer l = new Lexer("*(&(\"a\",\"b\"))");
        Parser p = new Parser(l);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();;

        AST d0 = p.parse();//"&("<",&(&("a",+(",a")),">"))"
        System.out.println("d0");
        System.out.println(runRegex(d0, ">") == false?"success": "fail");
        System.out.println(runRegex(d0, ",a,a>") == false?"success": "fail");
        System.out.println(runRegex(d0, "a,a,a>") == false?"success": "fail");
        System.out.println(runRegex(d0, "<a,a,a>") == true?"success": "fail");
        System.out.println("");

        AST d1 = d0.derivative("a");//"&(&("a",+(",a")),">")"
//        System.out.println("d1");
//        System.out.println(runRegex(d1, ">") == false?"success": "fail");
//        System.out.println(runRegex(d1, ",a,a>") == false?"success": "fail");
//        System.out.println(runRegex(d1, "a,a,a>") == true?"success": "fail");
//        System.out.println(runRegex(d1, "<a,a,a>") == false?"success": "fail");
//        System.out.println("");
//
//        AST d2 = d1.derivative("a");//"&(+(",a"),">")
//        System.out.println("d2");
//        System.out.println(runRegex(d2, ">") == true?"success": "fail");
//        System.out.println(runRegex(d2, ",a,a>") == true?"success": "fail");
//        System.out.println(runRegex(d2, "a,a,a>") == false?"success": "fail");
//        System.out.println(runRegex(d2, "<a,a,a>") == false?"success": "fail");
//        System.out.println("");
//
//        AST d3 = d2.derivative(",a");//"&(*(",a"),">")
//        System.out.println("d3");
//        System.out.println(runRegex(d3, ">") == true?"success": "fail");
//        System.out.println(runRegex(d3, ",a,a>") == true?"success": "fail");
//        System.out.println(runRegex(d3, "a,a,a>") == false?"success": "fail");
//        System.out.println(runRegex(d3, "<a,a,a>") == false?"success": "fail");
//        System.out.println("");
//
//        AST d4 = d3.derivative(">");//""
//        System.out.println("d4");
//        System.out.println(runRegex(d4, ">") == false?"success": "fail");
//        System.out.println(runRegex(d4, ",a,a>") == false?"success": "fail");
//        System.out.println(runRegex(d4, "a,a,a>") == false?"success": "fail");
//        System.out.println(runRegex(d4, "<a,a,a>") == false?"success": "fail");
//        System.out.println("");

        System.out.println(gson.toJson(d0));
//        System.out.println("<");
        System.out.println(gson.toJson(d1));
//        System.out.println("a");
//        System.out.println(gson.toJson(d2));
//        System.out.println(",a");
//        System.out.println(gson.toJson(d3));
//        System.out.println(">");
//        System.out.println(gson.toJson(d4));

    }
}
