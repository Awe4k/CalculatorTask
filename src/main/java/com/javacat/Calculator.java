package com.javacat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Deque;
import java.util.LinkedList;

public class Calculator {


    private Deque<Operand> arguments = new LinkedList<>();
    private Deque<Operator> functions = new LinkedList<>();


    public static void main(String[] args) throws IOException {
        Calculator calculator = new Calculator();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(System.in))) {
            String expression = bf.readLine();
            System.out.println(calculator.calculate(expression));
        } catch (NumberFormatException e) {
            System.out.println("Usage: correct arithmetic expression with brackets and basic operations (+-*/) ");
        }

    }


    private String calculate(String expression) {
        String result = expression;
        while (true) {
            int a = result.indexOf(')');

            if (a != -1) {
                String str = result.substring(0, a);
                int b = str.lastIndexOf('(');
                result = result.substring(0, b) + calculate(str.substring(b + 1)) + result.substring(a + 1);
            } else {
                parse(result);
                result = String.valueOf(calc());
                return result;
            }
        }

    }


    public void parse(String expressionWithoutBrackets) {
        Operand operand = new Operand();
        Operator operator;
        operand.add(expressionWithoutBrackets.charAt(0));
        for (int i = 1; i < expressionWithoutBrackets.length(); i++) {
            operator = checkOperator(expressionWithoutBrackets.charAt(i));
            if (operator == null) {
                operand.add(expressionWithoutBrackets.charAt(i));
                if (i == expressionWithoutBrackets.length() - 1) {
                    arguments.add(operand);
                }
            } else {
                // это условие нужно, чтобы стали возможны конструкции вида 3+-2 (==1)
                // где -2, например, результат вычисления 3+(1-3)
                if (operator instanceof Distract) {
                    if (checkOperator(expressionWithoutBrackets.charAt(i - 1)) != null) {
                        operand.add(expressionWithoutBrackets.charAt(i));
                        continue;
                    } else {
                        functions.add(operator);
                    }
                } else {
                    functions.add(operator);
                }
                arguments.add(operand);
                operand = new Operand();
            }
        }
    }


    private int calc() {
        while (arguments.size() > 1) {
            Operand first = arguments.pollFirst();
            Operand second = arguments.pollFirst();

            Operator operator = functions.pollFirst();

            if (functions.size() > 0) {
                if (operator.getPriority() == OperatorPriority.LOW) {
                    Operator nextOperator = functions.pollFirst();
                    if (nextOperator.getPriority() == OperatorPriority.HIGH) {
                        Operand third = arguments.pollFirst();
                        arguments.addFirst(second);
                        arguments.addFirst(first);

                        functions.addFirst(operator);
                        first = second;
                        second = third;
                        operator = nextOperator;
                    } else {
                        functions.addFirst(nextOperator);
                        arguments.addFirst(first);
                    }
                } else {
                    arguments.addFirst(first);
                }
            } else {
                arguments.addFirst(first);
            }
            first.setInt(operator.doOperation(first.getInt(), second.getInt()));
        }
        return arguments.pollFirst().getInt();
    }


    private Operator checkOperator(char c) {
        if (c == '+') return new Add();
        if (c == '-') return new Distract();
        if (c == '/') return new Division();
        if (c == '*') return new Multi();
        return null;
    }

    class Operand {
        StringBuffer operand = new StringBuffer();

        public void add(char c) {
            operand.append(c);
        }

        public int getInt() throws NumberFormatException {
            return Integer.parseInt(operand.toString());
        }

        public void setInt(int i) {
            operand = new StringBuffer(String.valueOf(i));
        }
    }

    interface Operator {
        public int doOperation(int firstOperand, int secondOperand);

        public OperatorPriority getPriority();
    }

    class Add implements Operator {

        @Override
        public int doOperation(int firstOperand, int secondOperand) {
            return firstOperand + secondOperand;
        }

        @Override
        public OperatorPriority getPriority() {
            return OperatorPriority.LOW;
        }
    }

    class Distract implements Operator {

        public int doOperation(int firstOperand, int secondOperand) {
            return firstOperand - secondOperand;
        }

        @Override
        public OperatorPriority getPriority() {
            return OperatorPriority.LOW;
        }
    }

    class Division implements Operator {

        @Override
        public int doOperation(int firstOperand, int secondOperand) {
            return firstOperand / secondOperand;
        }

        @Override
        public OperatorPriority getPriority() {
            return OperatorPriority.HIGH;
        }
    }

    class Multi implements Operator {

        @Override
        public int doOperation(int firstOperand, int secondOperand) {
            return firstOperand * secondOperand;
        }

        @Override
        public OperatorPriority getPriority() {
            return OperatorPriority.HIGH;
        }
    }

}