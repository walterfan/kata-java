package com.github.walterfan.twenty_four_points;


import java.util.Scanner;

public class GameApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameSolver solver = new GameSolver();

        System.out.println("Please enter 4 integers (separated by spaces):");
        String input = scanner.nextLine();
        String[] tokens = input.split("\\s+");

        if (tokens.length != 4) {
            System.out.println("Please enter 4 correct integers！");
            return;
        }

        int[] numbers = new int[4];
        try {
            for (int i = 0; i < 4; i++) {
                numbers[i] = Integer.parseInt(tokens[i]);
            }
        } catch (NumberFormatException e) {
            System.out.println("The input contains non-numeric content, please re-enter!");
            return;
        }

        if (solver.canReach24(numbers)) {
            System.out.println("This set of numbers can be calculated to get 24!");
        } else {
            System.out.println("Sorry! This set of numbers cannot be calculated to get 24!");
        }
    }
}