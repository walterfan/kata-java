package com.github.walterfan.twenty_four_points;


import java.util.Scanner;

public class GameApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameSolver solver = new GameSolver();

        System.out.println("请输入 4 个整数 (以空格分隔):");
        String input = scanner.nextLine();
        String[] tokens = input.split("\\s+");

        if (tokens.length != 4) {
            System.out.println("请输入正确的 4 个整数！");
            return;
        }

        int[] numbers = new int[4];
        try {
            for (int i = 0; i < 4; i++) {
                numbers[i] = Integer.parseInt(tokens[i]);
            }
        } catch (NumberFormatException e) {
            System.out.println("输入包含非数字内容，请重新输入！");
            return;
        }

        if (solver.canReach24(numbers)) {
            System.out.println("这组数字可以通过运算得到 24！");
        } else {
            System.out.println("遗憾！这组数字无法通过运算得到 24！");
        }
    }
}