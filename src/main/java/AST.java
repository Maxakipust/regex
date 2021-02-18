public abstract class AST {
    public boolean accepting = false;
    public abstract AST derivative(String str);
    public abstract String accept(String str) throws Exception;

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
        public String accept(String str) throws Exception{
            try {
                return child.accept(str);
            }catch (Exception ex){
                return str;
            }
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
        public String accept(String str) throws Exception{
            String s = str;
            s = child.accept(s);
            while(true){
                try{
                    s = child.accept(s);
                }catch (Exception ex){
                    break;
                }
            }
            return s;
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
        public String accept(String str) throws Exception{
            String s = str;
            while(true){
                try{
                    s = child.accept(s);
                }catch (Exception ex){
                    break;
                }
            }
            return s;
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
        public String accept(String str) throws Exception{
            try{
                return left.accept(str);
            }catch (Exception ex){
                return right.accept(str);
            }
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
        public String accept(String str) throws Exception{
            return child.accept(str);
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
        public String accept(String str) throws Exception{
            String afterLeft = left.accept(str);
            return right.accept(afterLeft);
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
        public String accept(String str) throws Exception {
            if(str.startsWith(value)){
                return str.replace(value, "");
            }else {
                throw new Exception("nope");
            }
        }
    }
}
