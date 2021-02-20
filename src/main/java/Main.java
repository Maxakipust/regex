import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String generateStr(Random r){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i<100; i++){
            if(r.nextBoolean()) {
                String first = Integer.toString(r.nextInt(9));
                str.append(first);
            }else {
                int min = 'a';
                int max = 'z';
                char ch = (char)(r.nextInt((max - min) + 1) + min);
                str.append(ch);
            }
        }
        return str.toString();
    }

    public static void main(String[] args) throws Exception {
        String regex = "*(|([0-9],[a-z]))";

        Random random = new Random();

        Lexer l = new Lexer(regex);
        Parser p = new Parser(l);
        AST ast = p.parse();
        System.out.println("regex: "+regex);

        System.out.println("Finished with setup");
        System.out.println("Starting interpreter");

        long interpstart = new Date().getTime();
        for(int i = 0; i< 100; i++){
            if(!runRegex(ast, generateStr(random))){
                System.err.println("No match");
            }
        }
        long interpend = new Date().getTime();
        System.out.println("Interpreter ran in "+(interpend - interpstart) +"ms");

        System.out.println("Running DFA");
        long DFAstart = new Date().getTime();
        for(int i = 0; i< 100; i++){
            String str = generateStr(random);
            Dfa dfa = new Dfa(ast);
            if(!dfa.run(str)){
                System.err.println("no match");
            }
        }
        long DFAend = new Date().getTime();
        System.out.println("DFA ran in "+(DFAend - DFAstart) +"ms");
        System.out.println("DFA ran "+ ((double)(interpend-interpstart)/(DFAend-DFAstart))+" times faster");
        System.out.println("built in regex");
        long builtInStart = new Date().getTime();
        for(int i =0; i<100; i++){
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher matcher = pattern.matcher(generateStr(random));
            boolean found = matcher.find();
            if(!found)
                System.err.println("no match");
        }
        long builtInEnd = new Date().getTime();
        System.out.println("built in  ran in "+(builtInEnd - builtInStart) +"ms");

    }
}
