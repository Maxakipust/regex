import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
        String regex = "*(&(&(|(\"0\",|(\"1\",|(\"2\",|(\"3\",|(\"4\",|(\"5\",|(\"6\",|(\"7\",|(\"8\",\"9\"))))))))),|(\"0\",|(\"1\",|(\"2\",|(\"3\",|(\"4\",|(\"5\",|(\"6\",|(\"7\",|(\"8\",\"9\")))))))))),\",\"))";
        Random random = new Random();
        StringBuilder str = new StringBuilder();
        for(int i = 0; i<100; i++){
            String first = Integer.toString(random.nextInt(9));
            String second = Integer.toString(random.nextInt(9));
            str.append(first).append(second).append(",");
        }
        Lexer l = new Lexer(regex);
        Parser p = new Parser(l);
        AST ast = p.parse();
        System.out.println("regex: "+regex);
        System.out.println("string: "+ str);

        System.out.println("Finished with setup");
        System.out.println("Starting interpreter");

        long interpstart = new Date().getTime();
        for(int i = 0; i< 100; i++){
            runRegex(ast, str.toString());
        }
        long interpend = new Date().getTime();
        System.out.println("Interpreter ran in "+(interpend - interpstart) +"ms");

        System.out.println("Compiling to DFA");
        long compilestart = new Date().getTime();
        Dfa dfa = new Dfa(ast);
        long compileend = new Date().getTime();
        System.out.println("Compile ran in "+(compileend - compilestart) +"ms");
        System.out.println("Running DFA");
        long DFAstart = new Date().getTime();
        for(int i = 0; i< 100; i++){
            dfa.run(str.toString());
        }
        long DFAend = new Date().getTime();
        System.out.println("DFA ran in "+(DFAend - DFAstart) +"ms");
    }
}
