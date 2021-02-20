import java.util.*;

public class Dfa {
    List<DfaNode> allNodes = new ArrayList<>();
    Set<String> allConsts;
    DfaNode root;
    public Dfa(AST regex){
        FindConstantsVisitor fcv = new FindConstantsVisitor();
        regex.accept(fcv);
        allConsts = fcv.constants;
        root = new DfaNode(regex);
        root.createTransitions();
    }

    public boolean run(String str){
        return root.run(str);
    }

    public class DfaNode{
        AST regex;
        Map<String, DfaNode> transitions = new HashMap<>();

        public DfaNode(AST regex){
            allNodes.add(this);
            this.regex = regex;
        }

        public void createTransitions(){
            if(transitions.size() != 0){
                return;
            }
            for(String cons: allConsts){
                AST derivative = regex.derivative(cons);
                DfaNode otherNode = null;
                for(DfaNode node: allNodes){
                    if(node.regex.equals(derivative)){
                        otherNode = node;
                        break;
                    }
                }
                if(otherNode == null){
                    otherNode = new DfaNode(derivative);
                }
                transitions.put(cons, otherNode);
            }
            for(DfaNode node: transitions.values()){
                node.createTransitions();;
            }
        }
        public boolean isAccepting(){
            return regex.acceptsEmpty();
        }

        public boolean run(String str){
            if("".equals(str)){
                return isAccepting();
            }
            for(String cons: transitions.keySet()){
                if(str.startsWith(cons)){
                    return transitions.get(cons).run(str.replaceFirst(cons, ""));
                }
            }
            return false;
        }
    }

}
