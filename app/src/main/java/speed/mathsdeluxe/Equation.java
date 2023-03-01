package speed.mathsdeluxe;

import java.util.Arrays;

public class Equation {
    private double y;
    private double m;
    private double c;
    private double x;

    public Equation(double y, double m, double c) {
        this.y = y;
        this.m = m;
        this.c = c;

        x = (y - c) / m;
    }

    public String toString() {
        return (int) y + " = " + (int) m + "x + " + (int) c;
    }

    public String[] getSimplifiedFractionalAnswerStrings() {
        return simplifyFraction((int) (y - c) + "/" + (int) m);
    }

    public static String[] simplifyFraction(String fraction) {
        System.out.println("Input: " + fraction);
        int numerator, denominator;
        String[] result;

        String[] parts = fraction.split("/");
        numerator = Integer.parseInt(parts[0]);
        denominator = Integer.parseInt(parts[1]);

        if (numerator == 0 || denominator == 0) {
            result = new String[]{"0"};
            return result;
        }

        int gcd = Math.abs(findGCD(numerator, denominator));

        numerator /= gcd;
        denominator /= gcd;

        if (denominator < 0) {
            numerator *= -1;
            denominator *= -1;
        }

        if (denominator == 1) {
            result = new String[]{Integer.toString(numerator)};
            return result;
        }

        result = new String[]{numerator + "/" + denominator};

        if (numerator < 0) {
            result = Arrays.copyOf(result, 2);
            result[1] = (-numerator) + "/" + (- denominator);
        }

        return result;
    }

    public static int findGCD(int num1, int num2) {
        if (num2 == 0) {
            return num1;
        }
        return findGCD(num2, num1 % num2);
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public double getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }
}