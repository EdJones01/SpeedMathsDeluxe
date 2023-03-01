package speed.mathsdeluxe;

import java.util.LinkedList;
import java.util.Random;


public class EquationOld {
    private String equation;
    private String fractionalAnswer;
    private String decimalAnswer;
    private LinkedList<String> possibleAnswers = new LinkedList<>();

    public EquationOld(String equation, String stringAnswer) {
        this.equation = equation;
        this.fractionalAnswer = stringAnswer;

        calculatePossibleAnswers();
    }

    private void calculatePossibleAnswers() {
        possibleAnswers.add("" + fractionalAnswer);
        String[] split = fractionalAnswer.split(("/"));
        if (split.length > 1) {
            decimalAnswer = "" + (Float.parseFloat(split[0]) / Float.parseFloat(split[1]));
            possibleAnswers.add("" + decimalAnswer);
            if (split[1].contains("-"))
                possibleAnswers.add("-" + split[0] + "/" + split[1].substring(1));
        } else {
            decimalAnswer = fractionalAnswer;
        }
    }

    public String toString() {
        return equation;
    }

    public String getFractionalAnswerString() {
        return fractionalAnswer;
    }

    public String getDecimalAnswer() {
        return decimalAnswer;
    }

    public String[] getPossibleAnswers() {
        String[] arr = new String[possibleAnswers.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = possibleAnswers.get(i);
        }
        return arr;
    }

    public static EquationOld newEquation(int min, int max, int mode) {
        EquationOld equation = generateEquation(min, max);
        while (equation.getDecimalAnswer().equals("0"))
            equation = generateEquation(min, max);
        return equation;
    }

    private static EquationOld generateEquation(int minVariable, int maxVariable) {
        Random random = new Random();
        int a = random.nextInt(maxVariable - minVariable) + minVariable;
        int b = random.nextInt(maxVariable - minVariable) + minVariable;
        int c = 0;
        if (maxVariable - b > 0)
            c = random.nextInt(maxVariable - b) + b;

        int gcd = calculateGcd(c - b, a);
        String x;
        if (c - b == 0) {
            x = "0";
        } else if (a == 1 || a == gcd) {
            x = Integer.toString(c - b);
        } else {
            x = (c - b) / gcd + "/" + a / gcd;
        }

        String problem = a + "x + " + b + " = " + c;
        String solution = x;
        return new EquationOld(problem, solution);
    }

    private static int calculateGcd(int x, int y) {
        while (y != 0) {
            int temp = y;
            y = x % y;
            x = temp;
        }
        return x;
    }
}