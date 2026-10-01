package assignment2;

public class MastermindRules implements GuessRules {
    private final GameConfiguration configuration;

    public MastermindRules(GameConfiguration configuration) {
        this.configuration = configuration;
    }

    public boolean isValidGuess(String guess) {
        if (guess == null || guess.length() != configuration.getCodeLength()) {
            return false;
        }

        for (int i = 0; i < guess.length(); i++) {
            if (configuration.getLegalColors().indexOf(guess.charAt(i)) == -1) {
                return false;
            }
        }
        return true;
    }

    public GuessRecord evaluateGuess(String guess, String secret) {
        if (!isValidGuess(guess) || !isValidGuess(secret)) {
            throw new IllegalArgumentException("The guess and secret must both be valid codes.");
        }

        boolean[] usedGuessPegs = new boolean[configuration.getCodeLength()];
        boolean[] usedSecretPegs = new boolean[configuration.getCodeLength()];
        int blackPegs = 0;
        int whitePegs = 0;

        // Exact matches must be counted first.
        for (int i = 0; i < configuration.getCodeLength(); i++) {
            if (guess.charAt(i) == secret.charAt(i)) {
                blackPegs++;
                usedGuessPegs[i] = true;
                usedSecretPegs[i] = true;
            }
        }

        // Match the remaining guess pegs to unused secret pegs.
        for (int guessIndex = 0; guessIndex < configuration.getCodeLength(); guessIndex++) {
            if (usedGuessPegs[guessIndex]) {
                continue;
            }

            for (int secretIndex = 0; secretIndex < configuration.getCodeLength(); secretIndex++) {
                if (!usedSecretPegs[secretIndex]
                        && guess.charAt(guessIndex) == secret.charAt(secretIndex)) {
                    whitePegs++;
                    usedGuessPegs[guessIndex] = true;
                    usedSecretPegs[secretIndex] = true;
                    break;
                }
            }
        }

        return new GuessRecord(guess, blackPegs, whitePegs, configuration.getCodeLength());
    }
}

