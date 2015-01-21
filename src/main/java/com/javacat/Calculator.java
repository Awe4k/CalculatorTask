package com.javacat;

import com.javacat.Operators.Operator;
import com.javacat.Operators.OperatorPriority;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Deque;
import java.util.LinkedList;

/*
  * Калькулятор получает арифметическое выражение и вычисляет результат
  * Для вычисления не используются популярные алгоритмы (ОПН, сорт. станция)
  * Алгоритм придумал сам (хотя в некоторых аспектах похож на популярные)
  * Алгоритм рекурсивно находит самые вложенные скобки и вычисляет в них результат, который используется
  * для вычисления в следующих скобках
  *
 */
public class Calculator {

    private final Deque<Operand> arguments = new LinkedList<>();
    private final Deque<Operator> functions = new LinkedList<>();


    public static void startCalculations() {
        Calculator calculator = new Calculator();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String expression = bf.readLine();
                if (expression.equals("quit")) {
                    System.out.printf("Bye!");
                    return;
                }
                System.out.println(calculator.calculate(expression));
            }
        } catch (IOException io) {
            System.out.println("I/O issues, see stack trace");
            io.printStackTrace();
        } catch (Exception e) {
            System.out.println("Usage: correct arithmetic expression with brackets and simple operations (+-*/)");
        }
    }

    /*
     * Работа со скобками
     */
    private String calculate(String expression) {
        String result = expression;
        int firstClosingBracket;
        while ((firstClosingBracket = result.indexOf(')')) != -1) {
            String expressionBeforeFirstClosingBracket = result.substring(0, firstClosingBracket);
            int lastOpeningBracket = expressionBeforeFirstClosingBracket.lastIndexOf('(');
            result =
                    result.substring(0, lastOpeningBracket) +
                            calculate(expressionBeforeFirstClosingBracket.substring(lastOpeningBracket + 1)) +
                            result.substring(firstClosingBracket + 1);
        }
        parse(result);
        result = String.valueOf(calc());
        return result;
    }

    /*
     * Парсим выражение без скобок, разбиваем на токены (операторы/операнды)
     */
    private void parse(String expressionWithoutBrackets) {
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
                if (operator == Operator.SUB) {
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

    /*
     * Имея стеки токенов вычисляем значение вложенного выражения
     *
     * Мне не очень нравится как выглядит реализация, хотелось бы переделать
     */
    private double calc() {
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
            first.setValue(operator.executeOperation(first.getValue(), second.getValue()));
        }
        return arguments.pollFirst().getValue();
    }


    private Operator checkOperator(char c) {
        if (c == '+') return Operator.ADD;
        if (c == '-') return Operator.SUB;
        if (c == '/') return Operator.DIV;
        if (c == '*') return Operator.MUL;
        return null;
    }

    private class Operand {
        StringBuffer operand = new StringBuffer();

        public void add(char c) {
            operand.append(c);
        }

        public double getValue() {
            return Double.parseDouble(operand.toString());
        }

        public void setValue(double numericValue) {
            operand = new StringBuffer(String.valueOf(numericValue));
        }
    }


}


