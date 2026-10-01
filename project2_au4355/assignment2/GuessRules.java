package assignment2;

public interface GuessRules {
    boolean isValidGuess(String guess);
    GuessFeedback evaluateGuess(String guess, String secret);
}
