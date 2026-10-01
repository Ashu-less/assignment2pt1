package assignment2;

public final class WordleConfiguration {
    private final int wordLength;
    private final int maxGuesses;

    public WordleConfiguration(int wordLength, int maxGuesses) {
        if (wordLength <= 0 || maxGuesses <= 0) {
            throw new IllegalArgumentException("Word length and maximum guesses must be positive.");
        }
        this.wordLength = wordLength;
        this.maxGuesses = maxGuesses;
    }

    public static WordleConfiguration defaultConfiguration() {
        return new WordleConfiguration(5, 6);
    }

    public int getWordLength() {
        return wordLength;
    }

    public int getMaxGuesses() {
        return maxGuesses;
    }
}
