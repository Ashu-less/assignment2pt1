package assignment2;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public final class WordleRules implements GuessRules {
    private final WordleConfiguration configuration;
    private final Set<String> allowedWords;

    public WordleRules(WordleConfiguration configuration, Collection<String> words) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(words, "words");
        Set<String> normalized = new LinkedHashSet<>();
        for (String word : words) {
            if (word == null || word.length() != configuration.getWordLength()
                    || !word.matches("[A-Za-z]+")) {
                throw new IllegalArgumentException("Dictionary words must have the configured length and use A-Z.");
            }
            normalized.add(word.toUpperCase(Locale.ROOT));
        }
        if (normalized.isEmpty()) throw new IllegalArgumentException("Dictionary must not be empty.");
        allowedWords = Collections.unmodifiableSet(normalized);
    }

    public Set<String> getAllowedWords() {
        return allowedWords;
    }

    @Override
    public boolean isValidGuess(String guess) {
        return guess != null && guess.length() == configuration.getWordLength()
                && guess.matches("[A-Z]+") && allowedWords.contains(guess);
    }

    @Override
    public WordleFeedback evaluateGuess(String guess, String secret) {
        if (!isValidGuess(guess) || !isValidGuess(secret)) {
            throw new IllegalArgumentException("The guess and secret must both be allowed words.");
        }
        WordleFeedback.LetterStatus[] statuses = new WordleFeedback.LetterStatus[guess.length()];
        int[] remaining = new int[26];
        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == secret.charAt(i)) {
                statuses[i] = WordleFeedback.LetterStatus.CORRECT;
            } else {
                remaining[secret.charAt(i) - 'A']++;
            }
        }
        for (int i = 0; i < guess.length(); i++) {
            if (statuses[i] == WordleFeedback.LetterStatus.CORRECT) continue;
            int letter = guess.charAt(i) - 'A';
            if (remaining[letter] > 0) {
                statuses[i] = WordleFeedback.LetterStatus.PRESENT;
                remaining[letter]--;
            } else {
                statuses[i] = WordleFeedback.LetterStatus.ABSENT;
            }
        }
        return new WordleFeedback(guess, statuses);
    }
}
