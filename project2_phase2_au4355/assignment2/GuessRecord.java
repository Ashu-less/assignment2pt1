package assignment2;
public class GuessRecord implements GuessFeedback {
    private final String guess;
    private final int blackPegs;
    private final int whitePegs;
    private final int codeLength;

    public GuessRecord(String guess, int blackPegs, int whitePegs) {
        this(guess, blackPegs, whitePegs, guess.length());
    }

    public GuessRecord(String guess, int blackPegs, int whitePegs, int codeLength) {
        this.guess = guess;
        this.blackPegs = blackPegs;
        this.whitePegs = whitePegs;
        this.codeLength = codeLength;
    }

    public int getBlackPegs() {
        return blackPegs;
    }

    public int getWhitePegs() {
        return whitePegs;
    }

    public boolean isWinningGuess(int codeLength) {
        return blackPegs == codeLength;
    }

    public String getFeedback() {
        return blackPegs + "B_" + whitePegs + "W";
    }

    @Override
    public String getGuess() {
        return guess;
    }

    @Override
    public String getFeedbackText() {
        return getFeedback();
    }

    @Override
    public boolean isWinningGuess() {
        return blackPegs == codeLength;
    }

    @Override
    public String toString() {
        return guess + " -> " + getFeedback();
    }
}
