package Model;

public enum Operator {
    ADDITION('+') {
       @Override public double apply(double a, double b) {
           return a + b;
       }
    },
    SUBTRACTION('-') {
        @Override
        public double apply(double a, double b) {
            return a - b;
        }
    },
    UNARY('U') {
        @Override
        public double apply(double a, double b) {
            return a;
        }
    };

    private final char op;

    Operator(char op) {
        this.op = op;
    }

    public abstract double apply(double a, double b);
}
