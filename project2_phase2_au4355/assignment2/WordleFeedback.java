package assignment2;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public final class WordleFeedback implements GuessFeedback {
    public enum LetterStatus {
        CORRECT("C"), PRESENT("P"), ABSENT("A");

        private final String symbol;

        LetterStatus(String symbol) {
            this.symbol = symbol;
        }
    }

    private final String guess;
    private final LetterStatus[] statuses;

    public WordleFeedback(String guess, LetterStatus[] statuses) {
        Objects.requireNonNull(guess, "guess");
        Objects.requireNonNull(statuses, "statuses");
        if (guess.isEmpty() || guess.length() != statuses.length) {
            throw new IllegalArgumentException("Feedback must have one status per letter.");
        }
        this.guess = guess;
        this.statuses = statuses.clone();
        for (LetterStatus status : this.statuses) Objects.requireNonNull(status, "status");
    }

    @Override
    public String getGuess() {
        return guess;
    }

    public LetterStatus[] getStatuses() {
        return statuses.clone();
    }

    @Override
    public String getFeedbackText() {
        return Arrays.stream(statuses).map(status -> status.symbol).collect(Collectors.joining(" "));
    }

    @Override
    public boolean isWinningGuess() {
        for (LetterStatus status : statuses) {
            if (status != LetterStatus.CORRECT) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return guess + " -> " + getFeedbackText();
    }
}
