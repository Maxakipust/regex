import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public abstract class AST<T> {
    public abstract boolean acceptsEmpty();
    public abstract void accept(Visitor visitor);
    public abstract AST derivative(String withRespectTo);

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

    private static AST createOr(AST left, AST right){
        if(left instanceof EmptySet){
            return right;
        }
        if(right instanceof EmptySet){
            return left;
        }
        return new Or(left, right);
    }

    public static class ZeroOrMore extends AST{
        @SerializedName(value="ZeroOrMore")
        AST child;

        @Override
        public String toString() {
            return "*(" + child + ')';
        }

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

        @Override
        public AST derivative(String withRespectTo) {
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
    public static class Or extends AST{
        @SerializedName(value="OrL")
        AST left;
        @SerializedName(value="OrR")
        AST right;

        @Override
        public String toString() {
            return "|(" + left + "," + right + ')';
        }

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

        @Override
        public AST derivative(String withRespectTo) {
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

    public static class And extends AST{
        @SerializedName(value="AndL")
        AST left;
        @SerializedName(value="AndR")
        AST right;

        @Override
        public String toString() {
            return "&(" + left + "," + right + ')';
        }

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

        @Override
        public AST derivative(String withRespectTo) {
            AST l = left.derivative(withRespectTo);
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
        public AST derivative(String withRespectTo) {
            if(withRespectTo.startsWith(value) && !"".equals(value)){
                return new Constant(value.replaceFirst(withRespectTo, ""));
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
        public AST derivative(String withRespectTo) {
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
}
