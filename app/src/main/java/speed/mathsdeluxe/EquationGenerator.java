package speed.mathsdeluxe;

import java.util.LinkedList;
import java.util.Random;


public class EquationGenerator {
    private int maxY;
    private int maxM;
    private int maxC;
    private int minY;
    private int minM;
    private int minC;

    public EquationGenerator() {
        maxY = 10;
        maxM = 10;
        maxC = 10;
        minY = 1;
        minM = 2;
        minC = 1;
    }

    public EquationGenerator(int min, int max) {
        this(min,min, min, max, max, max);
    }

    public EquationGenerator(int minY, int minM, int minC, int maxY, int maxM, int maxC) {
        this.maxY = maxY;
        this.maxM = maxM;
        this.maxC = maxC;
        this.minY = minY;
        this.minM = minM;
        this.minC = minC;
    }

    public Equation generateEquation() {
        int y = randomIntBetween(minY, maxY);
        int m = randomIntBetween(minM, maxM);
        int c = randomIntBetween(minC, maxC);

        return new Equation(y, m, c);
    }

    /**
     * Returns a random Integer between 2 values.
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @return A random value between the two provided Integers.
     */
    private int randomIntBetween(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }
}

