package com.kipust.regex;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * A class to represent the internal representation of a regex.
 * This regex cannot be run directly. It must be compiled into a
 * DFA and then it can be run.
 */
abstract class AST {
    /**
     * returns true if the AST can accept the empty string
     * @return
     */
    public abstract boolean acceptsEmpty();

    /**
     * accept method for a visitor
     * @param visitor the visitor to visit
     */
    public abstract void accept(Visitor visitor);

    /**
     * Uses Brzozowski derivatives to take the derivative of the regex.
     * This is used to create a DFA. It is the result of the regex after it matches
     * withRespectTo
     * https://en.wikipedia.org/wiki/Brzozowski_derivative
     * @param withRespectTo the string to take the derivative with respect to
     * @return
     */
    public abstract AST derivative(Const withRespectTo);

    /**
     * a helper method to create and nodes and cut out empty children.
     * @param left the left child
     * @param right the right child
     * @return an AST node that represents left and right
     */
    private static AST createAnd(AST left, AST right){
        if(left instanceof Constant){
            if(((Constant)left).value.equals("")){
                return right;
            }
        }
        if(right instanceof Constant){
            if(((Constant)right).value.equals("")){
                return left;
            }
        }
        if(left instanceof EmptySet || right instanceof EmptySet){
            return new EmptySet();
        }
        return new And(left, right);
    }

    /**
     * A helper function to create or nodes and remove unessasary children.
     * @param left the left child
     * @param right the right child
     * @return an AST node that represents left or right
     */
    private static AST createOr(AST left, AST right){
        if(left instanceof EmptySet){
            return right;
        }
        if(right instanceof EmptySet){
            return left;
        }
        return new Or(left, right);
    }

    /**
     * Represents the kleene star operator in regex
     * *(A)
     */
    public static class ZeroOrMore extends AST{
        @SerializedName(value="ZeroOrMore")
        AST child;

        @Override
        public String toString() {
            return "*(" + child + ')';
        }

        /**
         * creates a ZeroOrMore node that will match zero or more of its children
         * *(A)
         * @param child the child to match zero or more of
         */
        ZeroOrMore(AST child){
            this.child = child;
        }

        @Override
        public boolean acceptsEmpty() {
            return true;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitZeroOrMore(this);
        }

        /**
         * The derivative of *(A) is &(A', *(A))
         * @param withRespectTo the string to take the derivative with respect to
         * @return the ast node representing the derivative
         */
        @Override
        public AST derivative(Const withRespectTo) {
            return createAnd(child.derivative(withRespectTo), new ZeroOrMore(child));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ZeroOrMore that = (ZeroOrMore) o;
            return Objects.equals(child, that.child);
        }

        @Override
        public int hashCode() {
            return Objects.hash(child);
        }
    }

    /**
     * Represents the Or operator in a regex
     * |(A,B)
     */
    public static class Or extends AST{
        @SerializedName(value="OrL")
        AST left;
        @SerializedName(value="OrR")
        AST right;

        @Override
        public String toString() {
            return "|(" + left + "," + right + ')';
        }

        /**
         * create a new Or operator
         * |(A,B)
         * @param left the left child
         * @param right the right child
         */
        Or(AST left, AST right){
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean acceptsEmpty() {
            return left.acceptsEmpty() || right.acceptsEmpty();
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitOr(this);
        }

        /**
         * The derivative of |(A, B) is |(A', B')
         * @param withRespectTo the string to take the derivative with respect to
         * @return the node representing the derivative
         */
        @Override
        public AST derivative(Const withRespectTo) {
            AST dLeft = left.derivative(withRespectTo);
            AST dRight = right.derivative(withRespectTo);

            return createOr(dLeft, dRight);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Or or = (Or) o;
            return Objects.equals(left, or.left) && Objects.equals(right, or.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }
    }

    /**
     * Represents the And operator
     * &(A,B)
     */
    public static class And extends AST{
        @SerializedName(value="AndL")
        AST left;
        @SerializedName(value="AndR")
        AST right;

        @Override
        public String toString() {
            return "&(" + left + "," + right + ')';
        }

        /**
         * Create a new And node
         * &(A,B)
         * @param left the left child
         * @param right the right child
         */
        And(AST left, AST right){
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean acceptsEmpty() {
            return left.acceptsEmpty() && right.acceptsEmpty();
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitAnd(this);
        }

        /**
         * Takes the derivative of the node
         * @param withRespectTo the string to take the derivative with respect to
         * @return
         */
        @Override
        public AST derivative(Const withRespectTo) {
            AST l = left.derivative(withRespectTo);
//            This case is a bit weird since it depends on the accepts empty
            if(left.acceptsEmpty()){
                return createOr(createAnd(l, right), right.derivative(withRespectTo));
            }
            return createAnd(l, right);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            And and = (And) o;
            return Objects.equals(left, and.left) && Objects.equals(right, and.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }
    }
    public static class Constant extends AST{
        @SerializedName(value="Constant")
        String value;
        Constant(String value){
            this.value = value;
        }

        @Override
        public String toString() {
            return "\"" + value + '\"';
        }

        @Override
        public boolean acceptsEmpty() {
            return "".equals(value);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitConstant(this);
        }

        @Override
        public AST derivative(Const withRespectTo) {
            if(withRespectTo instanceof Const.Value) {
                String withRespectToValue = ((Const.Value) withRespectTo).value;
                if(withRespectToValue.equals(value) && !"".equals(value)){
                    return new Constant(value.replaceFirst(withRespectToValue, ""));
                }else{
                    return new EmptySet();
                }
            }else{
                return new EmptySet();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Constant constant = (Constant) o;
            return Objects.equals(value, constant.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static class Range extends  AST {
        int start, end;

        public Range(String start, String end){
            this.start = start.charAt(0);
            this.end = end.charAt(0);
        }

        @Override
        public boolean acceptsEmpty() {
            return false;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitRange(this);
        }

        public boolean matchesChar(char ch){
            return ch >= start && ch <= end;
        }

        @Override
        public String toString(){
            return "["+(char)start +"-"+(char)end+"]";
        }

        @Override
        public AST derivative(Const withRespectTo) {
            if(withRespectTo instanceof Const.Value) {
                String withRespectToVal = ((Const.Value) withRespectTo).value;
                Character withRespectToStart = withRespectToVal.charAt(0);
                if (matchesChar(withRespectToStart)) {
                    return new Constant("");
                } else {
                    return new EmptySet();
                }
            }else{
                return new EmptySet();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Range range = (Range) o;
            return start == range.start && end == range.end;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }
    }

    public static class EmptySet extends AST {
        String type = "EmptySet";
        @Override
        public boolean acceptsEmpty() {
            return false;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitEmptySet(this);
        }

        @Override
        public AST derivative(Const withRespectTo) {
            return new EmptySet();
        }

        @Override
        public String toString() {
            return "∅";
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof EmptySet){
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(type);
        }
    }

    public static class Wildcard extends AST {

        @Override
        public String toString() {
            return ".";
        }

        @Override
        public boolean acceptsEmpty() {
            return false;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitWildcard(this);
        }

        @Override
        public AST derivative(Const withRespectTo) {
            if(withRespectTo instanceof Const.Value && "".equals(((Const.Value) withRespectTo).value)){
                return new EmptySet();
            }
            return new Constant("");
        }
    }
}
