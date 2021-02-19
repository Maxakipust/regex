public abstract class AST<T> {
    public boolean accepting = false;
    public abstract AST derivative(String str);
    public abstract void accept(Visitor visitor);

    public static class ZeroOrOne extends AST{
        AST child;
        ZeroOrOne(AST child){
            this.child = child;
            accepting = true;
        }

        @Override
        public String toString() {
            return "ZeroOrOne{" +
                    "child=" + child +
                    '}';
        }

        @Override
        public AST derivative(String str) {
            return null;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitZeroOrOne(this);
        }
    }
    public static class OneOrMore extends AST{
        AST child;

        @Override
        public String toString() {
            return "OneOrMore{" +
                    "child=" + child +
                    '}';
        }

        OneOrMore(AST child){
            this.child = child;
        }

        @Override
        public AST derivative(String str) {
            return new ZeroOrMore(child);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitOneOrMore(this);
        }
    }
    public static class ZeroOrMore extends AST{
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
        public AST derivative(String str) {
            return new AST.And(child.derivative(str), new AST.ZeroOrMore(child));
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitZeroOrMore(this);
        }
    }
    public static class Or extends AST{
        AST left, right;

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
        public AST derivative(String str) {
            AST leftDer = left == null ? null : left.derivative(str);
            AST rightDer = right == null ? null : right.derivative(str);
            return new AST.Or(leftDer, rightDer);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitOr(this);
        }
    }
    public static class Group extends AST{
        AST child;

        @Override
        public String toString() {
            return "Group{" +
                    "child=" + child +
                    '}';
        }

        Group(AST child){
            this.child = child;
        }

        @Override
        public AST derivative(String str) {
            return this.child.derivative(str);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitGroup(this);
        }
    }
    public static class And extends AST{
        AST left, right;

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
        public AST derivative(String str) {
            if(left.derivative(str) == null){
                return right;
            }
            return new AST.And(left.derivative(str), right);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitAnd(this);
        }
    }
    public static class Constant extends AST{
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
        public AST derivative(String str) {
            if(str.equals(value)){
                return null;
            }else{
                return this;
            }
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitConstant(this);
        }
    }
}
