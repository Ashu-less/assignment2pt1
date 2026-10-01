package assignment2;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
 
public abstract class GuessingGame {
 
    protected final boolean testMode;
    protected final Scanner input;
    protected final Random random;
    private final GuessRules rules;
 
    protected GuessingGame(GuessRules rules, boolean testMode, Scanner input, Random random) {
        this.rules = rules;
        this.testMode = testMode;
        this.input = input;
        this.random = random;
    }
 
    public final void play() {
        printIntroduction();
 
        boolean playAgain = true;
        while (playAgain) {
            playOneGame();
            playAgain = askToPlayAgain();
        }
 
        System.out.println("Thanks for playing!");
    }
 
    private void playOneGame() {
        String secret = createSecret();
        List<GuessFeedback> history = new ArrayList<>();
        int guessesUsed = 0;
 
        if (testMode) {
            System.out.println("TEST MODE - secret " + secretNoun() + ": " + secret);
        }
 
        while (guessesUsed < getMaxGuesses()) {
            int guessesLeft = getMaxGuesses() - guessesUsed;
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
                System.out.println(invalidGuessMessage());
                continue;
            }
 
            GuessFeedback result = rules.evaluateGuess(command, secret);
            history.add(result);
            guessesUsed++;
            System.out.println(result.getFeedbackText());
 
            if (result.isWinningGuess()) {
                System.out.println("You guessed the secret " + secretNoun() + "!");
                return;
            }
        }
 
        System.out.println("You are out of guesses. The secret " + secretNoun()
                + " was " + secret + ".");
    }
 
    private void printHistory(List<GuessFeedback> history) {
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
 
    protected abstract int getMaxGuesses();
    protected abstract String createSecret();
    protected abstract String secretNoun();
    protected abstract String invalidGuessMessage();
    protected abstract void printIntroduction();
}