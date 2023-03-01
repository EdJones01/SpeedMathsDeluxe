package speed.mathsdeluxe;

import java.util.LinkedList;

public class Game {
    private int minimumNumber;

    private int maximumNumber;

    private int numberOfQuestions;

    private LinkedList<Equation> questions;

    private EquationGenerator equationGenerator;

    private String savecode;

    public Game(int minimumNumber, int maximumNumber, int numberOfQuestions) {
        this.minimumNumber = minimumNumber;
        this.maximumNumber = maximumNumber;
        this.numberOfQuestions = numberOfQuestions;

        equationGenerator = new EquationGenerator(minimumNumber, maximumNumber);

        questions = new LinkedList<>();
        generateSavecode();
        generateQuestions();

    }

    public void generateQuestions() {
        for (int i = 0; i < numberOfQuestions; i++) {
                questions.push(equationGenerator.generateEquation());
        }
    }

    private void generateSavecode() {
        savecode = numberOfQuestions + "_" + minimumNumber + "-" + maximumNumber;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public String getSavecode() {
        return savecode;
    }

    public LinkedList<Equation> getQuestions() {
        return questions;
    }


}
