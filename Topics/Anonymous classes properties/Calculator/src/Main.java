
class CalculatorWrapper {
    static Calculator anonymousCalculator = new Calculator() {
        public long sum(long val1, long val2) {
            return val1 + val2;
        }

        public long subtraction(long val1, long val2) {
            return val1 - val2;
        }
    };
}

abstract class Calculator {

    public abstract long sum(long val1, long val2);

    public abstract long subtraction(long val1, long val2);
}
