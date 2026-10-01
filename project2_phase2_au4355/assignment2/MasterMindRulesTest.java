package assignment2;

public class MasterMindRulesTest {
    private static int testsPassed = 0;

    public static void main(String[] args) {
        GameConfiguration normal = GameConfiguration.defaultConfiguration();
        MastermindRules rules = new MastermindRules(normal);

        checkFeedback(rules, "BGOP", "BGOP", 4, 0, "all exact matches");
        checkFeedback(rules, "GOPB", "BGOP", 0, 4, "all colors misplaced");
        checkFeedback(rules, "BBBB", "YYYY", 0, 0, "no matching colors");
        checkFeedback(rules, "RRRR", "RBGY", 1, 0, "extra duplicate guesses");
        checkFeedback(rules, "RBRB", "BRRR", 1, 2, "duplicates in both codes");
        checkFeedback(rules, "BBRR", "RRBB", 0, 4, "repeated misplaced colors");

        check(!rules.isValidGuess("BGO"), "short guesses are invalid");
        check(!rules.isValidGuess("BGOX"), "illegal colors are invalid");
        check(rules.isValidGuess("YYYY"), "repeated legal colors are valid");

        GameConfiguration changed = new GameConfiguration(5, 8, "RGB");
        MastermindRules changedRules = new MastermindRules(changed);
        check(changedRules.isValidGuess("RGBRG"), "a changed configuration is used");
        checkFeedback(changedRules, "RGBRG", "RGBGR", 3, 2,
                "feedback works with five pegs");

        System.out.println("All " + testsPassed + " tests passed.");
    }

    private static void checkFeedback(MastermindRules rules, String guess,
            String secret, int expectedBlack, int expectedWhite, String name) {
        GuessRecord result = rules.evaluateGuess(guess, secret);
        check(result.getBlackPegs() == expectedBlack
                        && result.getWhitePegs() == expectedWhite,
                name + " (expected " + expectedBlack + "B_" + expectedWhite
                        + "W, got " + result.getFeedback() + ")");
    }

    private static void check(boolean condition, String name) {
        if (!condition) {
            throw new AssertionError("Test failed: " + name);
        }
        testsPassed++;
        System.out.println("PASS: " + name);
    }
}

