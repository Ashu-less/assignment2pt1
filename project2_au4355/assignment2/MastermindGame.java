package assignment2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MastermindGame {
    private final GameConfiguration configuration;
    private final MastermindRules rules;
    private final boolean testMode;
    private final Scanner input;
    private final Random random;

    public MastermindGame(GameConfiguration configuration, boolean testMode) {
        this(configuration, testMode, new Scanner(System.in), new Random());
    }

    MastermindGame(GameConfiguration configuration, boolean testMode,
            Scanner input, Random random) {
        this.configuration = configuration;
        this.rules = new MastermindRules(configuration);
        this.testMode = testMode;
        this.input = input;
        this.random = random;
    }

    public void play() {
        printIntroduction();

        boolean playAgain = true;
        while (playAgain) {
            playOneGame();
            playAgain = askToPlayAgain();
        }

        System.out.println("Thanks for playing!");
    }

    private void playOneGame() {
        String secret = createSecretCode();
        List<GuessRecord> history = new ArrayList<>();
        int guessesUsed = 0;

        if (testMode) {
            System.out.println("TEST MODE - secret code: " + secret);
        }

        while (guessesUsed < configuration.getMaxGuesses()) {
            int guessesLeft = configuration.getMaxGuesses() - guessesUsed;
            System.out.print("Enter a guess (" + guessesLeft + " remaining) or HISTORY: ");

            if (!input.hasNextLine()) {
                System.out.println();
                return;
            }

            String command = input.nextLine().trim().toUpperCase();
            if (command.equals("HISTORY")) {
                printHistory(history);
                continue;
            }

            if (!rules.isValidGuess(command)) {
                System.out.println("Invalid guess. Enter exactly "
                        + configuration.getCodeLength() + " characters using only "
                        + configuration.getLegalColors() + ".");
                continue;
            }

            GuessRecord result = rules.evaluateGuess(command, secret);
            history.add(result);
            guessesUsed++;
            System.out.println(result.getFeedback());

            if (result.isWinningGuess(configuration.getCodeLength())) {
                System.out.println("You guessed the secret code!");
                return;
            }
        }

        System.out.println("You are out of guesses. The secret code was " + secret + ".");
    }

    private String createSecretCode() {
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

    private void printHistory(List<GuessRecord> history) {
        if (history.isEmpty()) {
            System.out.println("No valid guesses have been made yet.");
            return;
        }

        System.out.println("Guess history:");
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i));
        }
    }

    private boolean askToPlayAgain() {
        while (true) {
            System.out.print("Play again? (Y/N): ");
            if (!input.hasNextLine()) {
                System.out.println();
                return false;
            }

            String answer = input.nextLine().trim();
            if (answer.equalsIgnoreCase("Y") || answer.equalsIgnoreCase("YES")) {
                return true;
            }
            if (answer.equalsIgnoreCase("N") || answer.equalsIgnoreCase("NO")) {
                return false;
            }
            System.out.println("Please enter Y or N.");
        }
    }

    private void printIntroduction() {
        System.out.println("Welcome to Mastermind!");
        System.out.println("Guess a " + configuration.getCodeLength()
                + "-peg code using: " + configuration.getLegalColors());
        System.out.println("A black peg is the right color in the right position.");
        System.out.println("A white peg is the right color in the wrong position.");
    }
}
