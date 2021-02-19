import com.google.gson.annotations.SerializedName;

public abstract class AST<T> {
    public abstract boolean acceptsEmpty();
    public abstract void accept(Visitor visitor);
    public abstract AST derivative(String withRespectTo);

    public static class ZeroOrMore extends AST{
        @SerializedName(value="ZeroOrMore")
        AST child;

        @Override
        public String toString() {
            return "ZeroOrMore{" +
                    "child=" + child +
                    '}';
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
            return new AST.And(child.derivative(withRespectTo), new ZeroOrMore(child));
        }


    }
    public static class Or extends AST{
        @SerializedName(value="OrL")
        AST left;
        @SerializedName(value="OrR")
        AST right;

        @Override
        public String toString() {
            return "Or{" +
                    "left=" + left +
                    ", right=" + right +
                    '}';
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
            return new Or(left.derivative(withRespectTo), right.derivative(withRespectTo));
        }
    }

    public static class And extends AST{
        @SerializedName(value="AndL")
        AST left;
        @SerializedName(value="AndR")
        AST right;

        @Override
        public String toString() {
            return "And{" +
                    "left=" + left +
                    ", right=" + right +
                    '}';
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
                return new AST.Or(new AST.And(l, right), right.derivative(withRespectTo));
            }
            return new AST.And(l, right);
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
            return "Constant{" +
                    "value='" + value + '\'' +
                    '}';
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
            return "EmptySet{}";
        }
    }
}
