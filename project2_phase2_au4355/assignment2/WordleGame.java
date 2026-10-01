package assignment2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public final class WordleGame extends GuessingGame {
    private final WordleConfiguration configuration;
    private final List<String> words;

    public WordleGame(WordleConfiguration configuration, Collection<String> words, boolean testMode) {
        this(configuration, new WordleRules(configuration, words), testMode,
                new Scanner(System.in), new Random());
    }

    WordleGame(WordleConfiguration configuration, Collection<String> words, boolean testMode,
            Scanner input, Random random) {
        this(configuration, new WordleRules(configuration, words), testMode, input, random);
    }

    private WordleGame(WordleConfiguration configuration, WordleRules rules, boolean testMode,
            Scanner input, Random random) {
        super(rules, testMode, input, random);
        this.configuration = configuration;
        this.words = new ArrayList<>(rules.getAllowedWords());
    }

    @Override
    protected int getMaxGuesses() {
        return configuration.getMaxGuesses();
    }

    @Override
    protected String createSecret() {
        return words.get(testMode ? 0 : random.nextInt(words.size()));
    }

    @Override
    protected String secretNoun() {
        return "word";
    }

    @Override
    protected String invalidGuessMessage() {
        return "Invalid guess. Enter an allowed " + configuration.getWordLength()
                + "-letter word using A-Z.";
    }

    @Override
    protected void printIntroduction() {
        System.out.println("Welcome to Wordle!");
        System.out.println("Guess a " + configuration.getWordLength() + "-letter word in "
                + configuration.getMaxGuesses() + " valid guesses.");
        System.out.println("C = correct position, P = present elsewhere, A = absent.");
    }
}
