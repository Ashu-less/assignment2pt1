package assignment2;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class TestHarness {
    private static int passed;

    public static void main(String[] args) {
        Random random = new Random(422);
        for (int t = 0; t < 200; t++) {
            GameConfiguration config = new GameConfiguration(1 + random.nextInt(10),
                    1 + random.nextInt(12), "BGOPRY0123".substring(0, 1 + random.nextInt(10)));
            String guess = code(config, random);
            String secret = code(config, random);
            int black = 0;
            int total = 0;
            for (int i = 0; i < guess.length(); i++) {
                if (guess.charAt(i) == secret.charAt(i)) black++;
            }
            for (char color : config.getLegalColors().toCharArray()) {
                int g = 0;
                int s = 0;
                for (int i = 0; i < guess.length(); i++) {
                    if (guess.charAt(i) == color) g++;
                    if (secret.charAt(i) == color) s++;
                }
                total += Math.min(g, s);
            }
            GuessRecord result = new MastermindRules(config).evaluateGuess(guess, secret);
            check(result.getBlackPegs() == black && result.getWhitePegs() == total - black
                    && result.isWinningGuess() == guess.equals(secret), "oracle scoring " + t);
        }
        GameConfiguration standard = GameConfiguration.defaultConfiguration();
        MastermindRules rules = new MastermindRules(standard);
        for (String invalid : new String[] {null, "", "BGO", "BGOPR", "BGOX", "bgop", "BG P"}) {
            check(!rules.isValidGuess(invalid), "invalid input " + invalid);
            rejects(() -> rules.evaluateGuess(invalid, "BGOP"));
            rejects(() -> rules.evaluateGuess("BGOP", invalid));
        }
        rejects(() -> new GameConfiguration(0, 12, "BG"));
        rejects(() -> new GameConfiguration(4, 0, "BG"));
        rejects(() -> new GameConfiguration(4, 12, null));
        rejects(() -> new GameConfiguration(4, 12, ""));
        rejects(() -> new GameConfiguration(4, 12, "ABCDEFGHIJK"));
        rejects(() -> new GameConfiguration(4, 12, "Bb"));
        rejects(() -> new GameConfiguration(4, 12, "B G"));
        rejects(() -> new GameConfiguration(4, 12, "ß"));
        String output = play(standard, true, "HISTORY\nX\nBBBB\nHISTORY\nbgop\nN\n");
        check(output.contains("No valid guesses"), "empty history");
        check(output.contains("Invalid guess"), "invalid message");
        check(output.contains("1. BBBB -> 1B_0W"), "history feedback");
        check(output.contains("(11 remaining)"), "valid guess consumes attempt");
        check(output.contains("4B_0W\nYou guessed"), "normalized winning input");
        check(output.contains("TEST MODE - secret code: BGOP"), "test mode reveal");
        GameConfiguration small = new GameConfiguration(2, 1, "BG");
        output = play(small, true, "X\nHISTORY\nGG\nN\n");
        check(output.contains("1B_0W") && output.contains("out of guesses"), "invalid and history preserve attempt");
        check(output.contains("secret code was BG"), "loss reveals secret");
        output = play(small, true, "BG\nmaybe\nYES\nHISTORY\nBG\nNO\n");
        check(output.split("You guessed", -1).length == 3, "replay win");
        check(output.contains("Please enter Y or N"), "invalid replay response");
        check(output.contains("No valid guesses"), "replay resets history");
        output = play(standard, true, "");
        check(!output.contains("Play again?") && output.contains("Thanks for playing"), "EOF exits cleanly");
        output = play(new GameConfiguration(5, 2, "RGB"), true, "RGBRG\nN\n");
        check(output.contains("5B_0W") && output.contains("You guessed"), "five peg game");
        output = play(standard, false, "");
        check(!output.contains("TEST MODE"), "normal mode hides secret");
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            check(new GameConfiguration(1, 1, "i").getLegalColors().equals("I"), "locale independent colors");
            check(play(standard, true, "history\nBGOP\nN\n").contains("No valid guesses"), "locale independent commands");
        } finally {
            Locale.setDefault(original);
        }
        System.out.println("All " + passed + " harness checks passed (200 seeded scoring cases).");
    }

    private static String code(GameConfiguration config, Random random) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < config.getCodeLength(); i++) {
            code.append(config.getLegalColors().charAt(random.nextInt(config.getLegalColors().length())));
        }
        return code.toString();
    }

    private static String play(GameConfiguration config, boolean testMode, String commands) {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(output)) {
            System.setOut(capture);
            new MastermindGame(config, testMode, new Scanner(commands), new Random(422)).play();
        } finally {
            System.setOut(original);
        }
        return output.toString().replace("\r\n", "\n");
    }

    private static void rejects(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            passed++;
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError(name);
        passed++;
    }
}
