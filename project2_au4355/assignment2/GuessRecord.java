package assignment2;

public class GuessRecord {
    private final String guess;
    private final int blackPegs;
    private final int whitePegs;

    public GuessRecord(String guess, int blackPegs, int whitePegs) {
        this.guess = guess;
        this.blackPegs = blackPegs;
        this.whitePegs = whitePegs;
    }

    public String getGuess() {
        return guess;
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
    public String toString() {
        return guess + " -> " + getFeedback();
    }
}

