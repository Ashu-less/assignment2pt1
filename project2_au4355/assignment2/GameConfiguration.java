package assignment2;

public class GameConfiguration {
    private final int codeLength;
    private final int maxGuesses;
    private final String legalColors;

    public GameConfiguration(int codeLength, int maxGuesses, String legalColors) {
        if (codeLength <= 0) {
            throw new IllegalArgumentException("Code length must be positive.");
        }
        if (maxGuesses <= 0) {
            throw new IllegalArgumentException("Maximum guesses must be positive.");
        }
        if (legalColors == null || legalColors.isEmpty() || legalColors.length() > 10) {
            throw new IllegalArgumentException("There must be between 1 and 10 legal colors.");
        }

        String normalizedColors = legalColors.toUpperCase();
        for (int i = 0; i < normalizedColors.length(); i++) {
            char color = normalizedColors.charAt(i);
            if (Character.isWhitespace(color)) {
                throw new IllegalArgumentException("Colors must be one-character symbols.");
            }
            if (normalizedColors.indexOf(color) != i) {
                throw new IllegalArgumentException("Legal colors must be distinct.");
            }
        }

        this.codeLength = codeLength;
        this.maxGuesses = maxGuesses;
        this.legalColors = normalizedColors;
    }

    public static GameConfiguration defaultConfiguration() {
        return new GameConfiguration(4, 12, "BGOPRY");
    }

    public int getCodeLength() {
        return codeLength;
    }

    public int getMaxGuesses() {
        return maxGuesses;
    }

    public String getLegalColors() {
        return legalColors;
    }
}

