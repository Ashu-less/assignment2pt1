package assignment2;

import java.util.Random;
import java.util.Scanner;

public class MastermindGame extends GuessingGame {
    private final GameConfiguration configuration;

    public MastermindGame(GameConfiguration configuration, boolean testMode) {
        this(configuration, testMode, new Scanner(System.in), new Random());
    }

    MastermindGame(GameConfiguration configuration, boolean testMode,
            Scanner input, Random random) {
        super(new MastermindRules(configuration), testMode, input, random);
        this.configuration = configuration;
    }

    @Override
    protected int getMaxGuesses() {
        return configuration.getMaxGuesses();
    }

    @Override
    protected String secretNoun() {
        return "code";
    }

    @Override
    protected String invalidGuessMessage() {
        return "Invalid guess. Enter exactly " + configuration.getCodeLength()
                + " characters using only " + configuration.getLegalColors() + ".";
    }

    @Override
    protected String createSecret() {
        String colors = configuration.getLegalColors();
        StringBuilder secret = new StringBuilder();

        for (int i = 0; i < configuration.getCodeLength(); i++) {
            if (testMode) {
                secret.append(colors.charAt(i % colors.length()));
            } else {
                secret.append(colors.charAt(random.nextInt(colors.length())));
            }
        }
        return secret.toString();
    }

    @Override
    protected void printIntroduction() {
        System.out.println("Welcome to Mastermind!");
        System.out.println("Guess a " + configuration.getCodeLength()
                + "-peg code using: " + configuration.getLegalColors());
        System.out.println("A black peg is the right color in the right position.");
        System.out.println("A white peg is the right color in the wrong position.");
    }
}
