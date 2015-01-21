package com.javacat;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainClass {
    public static void main(String[] args) throws IOException {
        Calculator calculator = new Calculator();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(System.in))) {
            String expression = bf.readLine();
            System.out.println(calculator.calculate(expression));
        } catch (NumberFormatException e) {
            System.out.println("Usage: correct arithmetic expression with brackets and basic operations (+-*/) ");
        }
    }
}
