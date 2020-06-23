package Model;

public enum Operator {
    ADDITION() {
       @Override public double apply(double a, double b) {
           return a + b;
       }
    },
    SUBTRACTION() {
        @Override
        public double apply(double a, double b) {
            return a - b;
        }
    },
    UNARY() {
        @Override
        public double apply(double a, double b) {
            return a;
        }
    };

    Operator() {
    }

    public abstract double apply(double a, double b);
}
