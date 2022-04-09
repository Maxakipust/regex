package com.kipust.regex;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An implementation of regex darivatives.
 */
public class Dfa {
    /**
     * a class to represent the result of a dfa run
     */
    public abstract class DFAResult{
        public abstract boolean success();
    }

    /**
     * The given string was matched by the dfa
     * In other words, the ending state in the dfa was an accepting state
     */
    public class AcceptingResult extends DFAResult{
        @Override
        public boolean success() {
            return true;
        }
    }

    /**
     * The given string was not matched by the dfa
     * In other words, the dfa went into a trash state or finished in a non-accepting state
     */
    public class TrashResult extends DFAResult{
        private String remainingTokens;
        private String[] acceptableOptions;
        private int index;
        public TrashResult(String remainingTokens, int index, String[] acceptableOptions){
            this.remainingTokens = remainingTokens;
            this.acceptableOptions = acceptableOptions;
            this.index = index;
        }

        /**
         * Get the remaining tokens that were not matched
         * @return
         */
        public String getRemainingTokens(){return remainingTokens;}

        /**
         * get a list of acceptable tokens that could have been used for the DFA to take a valid step
         * @return
         */
        public String[] getAcceptableOptions(){return acceptableOptions;}

        /**
         * get the index where the DFA failed at
         * @return
         */
        public int getIndex() {return index;}

        @Override
        public boolean success(){
            return false;
        }
    }

    List<DfaNode> allNodes = new ArrayList<>();
    Set<Const> allConsts;
    DfaNode root;
    String finalStr;
    Boolean anyDfa = false;

    /**
     * Create a DFA from a regex by using Brzozowski Derivatives
     * https://en.wikipedia.org/wiki/Brzozowski_derivative
     * @param regex the regex to compile
     */
    public Dfa(AST regex){
        FindConstantsVisitor fcv = new FindConstantsVisitor();
        regex.accept(fcv);
        allConsts = fcv.constants;
        root = new DfaNode(regex);
        root.createTransitions();
    }

    /**
     * A DFA that will accept anything
     */
    protected Dfa(){
        this.anyDfa = true;
    }

    /**
     * Run the DFA against a string
     * @param str the string to try and match
     * @return a DFAResult of AcceptingResult or TrashResult
     */
    public DFAResult run(String str){
        if(anyDfa) {
            return new AcceptingResult();
        }
        return root.run(str, 0);
    }

    /**
     * Represents a single node in the DFA
     */
    public class DfaNode{
        AST regex;
        Map<Const, DfaNode> transitions = new HashMap<>();
        Boolean accepting = false;
        Boolean trash = false;

        /**
         * Create a new DFA node that represents a regex
         * @param regex the regex that the node represents
         */
        public DfaNode(AST regex){
            allNodes.add(this);
            this.regex = regex;
            this.trash = regex instanceof AST.EmptySet;
        }

        /**
         * The main construction function to convert from a Regex to a DFA
         * For each constant in the regex we take the derivative of this regex with respect to the constant
         * Then if the resulting regex already exists as a node we create a new transition to it,
         * if it doesn't then we create a new node and transition
         * Then we recursively call this function on the other nodes.
         * This node will be an accepting node if the regex that it represents accepts the empty string
         */
        public void createTransitions(){
            // if we have already visited this node, we dont need to do anything
            if(transitions.size() != 0 || this.trash){
                return;
            }
            // for each constant we need to take the derivative with respect to it and create a new transition.
            for(Const cons: allConsts) {
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
            accepting = regex.acceptsEmpty();
        }

        /**
         * recursively runs the DFA against a string
         * @param str the remaining string to match against
         * @param index the index into the string that we are at
         * @return
         */
        public DFAResult run(String str, int index){
            finalStr = str;
            // if this is a trash state, we can exit early
            if(this.trash){
                return new TrashResult(str, index, transitions.keySet().stream().filter(key -> !transitions.get(key).trash).map(key -> "\"" + key + "\"").toArray(String[] ::new));
            }
            // if we are at the end of the string if we are an accepting state then we successfully matched
            // if not then we give a fail result
            // the fail result will have a list of all the transitions to non-trash nodes
            if(str.length() == 0){
                return accepting ? new AcceptingResult() : new TrashResult(str, index, transitions.keySet().stream().filter(key -> !transitions.get(key).trash).map(key -> "\"" + key + "\"").toArray(String[] ::new));
            }
            // for each non-wildcard key that we can transition to
            for(Const key: transitions.keySet().stream().filter(t-> t instanceof Const.Value).collect(Collectors.toList())) {
                String keyVal = ((Const.Value) key).value;
                // if the string starts with the key then make the transition
                if (str.startsWith(keyVal)) {
                    DfaNode next = transitions.get(key);
                    if (!next.trash) {
                        //"consume" the transition in the string
                        return next.run(str.replaceFirst(keyVal, ""), index + keyVal.length());
                    }
                }
            }
            // for each wildcard key that we can transition to
            for(Const key: transitions.keySet().stream().filter(t-> t instanceof Const.Wildcard).collect(Collectors.toList())) {
                // try to transition to it
                DfaNode next = transitions.get(key);
                if(!next.trash){
                    //"consume" the next character
                    return next.run(str.substring(1), index + 1);
                }
            }
            // if we dont find any transitions to take then we dont match the string
            return new TrashResult(str, index, transitions.keySet().stream().filter(key -> !transitions.get(key).trash).map(key -> "\"" + key + "\"").toArray(String[] ::new));
        }
    }

}
