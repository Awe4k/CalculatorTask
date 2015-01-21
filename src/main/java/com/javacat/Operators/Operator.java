package com.javacat.Operators;

public enum Operator {
    ADD(new Operation() {
        public double doOperation(double a, double b) {
            return a + b;
        }
    }, OperatorPriority.LOW),

    SUB(new Operation() {
        public double doOperation(double a, double b) {
            return a - b;
        }
    }, OperatorPriority.LOW),

    MUL(new Operation() {
        public double doOperation(double a, double b) {
            return a * b;
        }
    }, OperatorPriority.HIGH),
    DIV(new Operation() {
        public double doOperation(double a, double b) {
            return a / b;
        }
    }, OperatorPriority.HIGH);

    final Operation operation;
    final OperatorPriority priority;

    Operator(Operation op, OperatorPriority priority) {
        operation = op;
        this.priority = priority;
    }

    public double executeOperation(double a, double b) {
        return operation.doOperation(a, b);
    }

    public OperatorPriority getPriority() {
        return priority;
    }
}
