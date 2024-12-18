/**
 * 
 */
package com.fanyamin.kata.demo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Point2D {
    private double x;
    private double y;

    double distance(double x1, double y1) {
        return Math.abs(x1 - x) + Math.abs(y1 - y);
    }
}

/**
 * convert list of number to class Point
 */
public class PointAvgDistance {

    public static double oldCalc(List<Integer> numbers) {
        List<Point2D> points = new ArrayList<>(numbers.size());
        for (int number : numbers) {
            points.add(new Point2D((double) number % 3, (double) number / 3));
        }

        int count = 0;
        double sum = 0.0;
        double avg = 0.0;
        for (Point2D point : points) {
            if (point.getY() > 1) {
                double distance = point.distance(0, 0);
                sum += distance;
                count++;
            }
        }

        if (count > 0) {
            return sum / count;
        }

        return avg;
    }

    public static double newCalc(List<Integer> numbers) {
        return numbers.stream()
                .map(x -> new Point2D((double) x % 3, (double) x / 3))
                .filter(x -> x.getY() > 1)
                .mapToDouble(x -> x.distance(0.0, 0.0))
                .average()
                .orElse(0.0);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8);
        var oldRet = oldCalc(numbers);
        var newRet = newCalc(numbers);
        System.out.println("average distance is %f or %f".formatted(oldRet, newRet));

    }

}
