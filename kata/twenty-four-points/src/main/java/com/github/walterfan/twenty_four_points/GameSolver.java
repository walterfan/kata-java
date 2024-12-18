package com.github.walterfan.twenty_four_points;

import java.util.ArrayList;
import java.util.List;

public class GameSolver {
    private String solution = "";

	public boolean canReach24(int[] numbers) {
        List<Double> nums = new ArrayList<>();
        List<String> expressions = new ArrayList<>();
        for (int num : numbers) {
            nums.add((double) num);
            expressions.add(String.valueOf(num)); 
        }
        boolean result = solve(nums, expressions);
        if (result) {
            System.out.println("The formula found is: " + solution);
        }
        return result;
    }

    private boolean solve(List<Double> nums, List<String> expressions) {
        if (nums.size() == 1) {
            if (Math.abs(nums.get(0) - 24) < 1e-6) {
                solution = expressions.get(0); 
                return true;
            }
            return false;
        }

        for (int i = 0; i < nums.size(); i++) {
            for (int j = 0; j < nums.size(); j++) {
                if (i != j) {
                    List<Double> nextNums = new ArrayList<>();
                    List<String> nextExpressions = new ArrayList<>();

                    for (int k = 0; k < nums.size(); k++) {
                        if (k != i && k != j) {
                            nextNums.add(nums.get(k));
                            nextExpressions.add(expressions.get(k));
                        }
                    }

                    double a = nums.get(i), b = nums.get(j);
                    String exprA = expressions.get(i), exprB = expressions.get(j);

                    // try all possible formula
                    for (int op = 0; op < 6; op++) {
                        if (op == 0) { // add
                            nextNums.add(a + b);
                            nextExpressions.add("(" + exprA + " + " + exprB + ")");
                        } else if (op == 1) { // subtract
                            nextNums.add(a - b);
                            nextExpressions.add("(" + exprA + " - " + exprB + ")");
                        } else if (op == 2) { // reverse subtract
                            nextNums.add(b - a);
                            nextExpressions.add("(" + exprB + " - " + exprA + ")");
                        } else if (op == 3) { // multiply
                            nextNums.add(a * b);
                            nextExpressions.add("(" + exprA + " * " + exprB + ")");
                        } else if (op == 4 && b != 0) { // divide
                            nextNums.add(a / b);
                            nextExpressions.add("(" + exprA + " / " + exprB + ")");
                        } else if (op == 5 && a != 0) { // reverse divide
                            nextNums.add(b / a);
                            nextExpressions.add("(" + exprB + " / " + exprA + ")");
                        } else {
                            continue; // skip illegal operator
                        }

                        if (solve(nextNums, nextExpressions)) {
                            return true;
                        }

                        // back trace
                        nextNums.remove(nextNums.size() - 1);
                        nextExpressions.remove(nextExpressions.size() - 1);
                    }
                }
            }
        }
        return false;
    }
}