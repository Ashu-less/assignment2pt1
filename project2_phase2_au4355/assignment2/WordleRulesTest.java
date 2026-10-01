package assignment2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class WordleRulesTest {
    private static int passed;

    public static void main(String[] args) throws Exception {
        WordleConfiguration config = WordleConfiguration.defaultConfiguration();
        List<String> words = WordListLoader.load(Paths.get("wordlist.txt"), config.getWordLength());
        WordleRules rules = new WordleRules(config, words);
        check(new GuessRecord("BGOP", 4, 0).isWinningGuess(), "Phase I constructor compatibility");
        check(config.getWordLength() == 5 && config.getMaxGuesses() == 6, "defaults");
        check(words.size() == 50 && words.get(0).equals("APPLE"), "provided dictionary");
        feedback(rules, "ALLEY", "APPLE", "C P A P A");
        feedback(rules, "BLESS", "CHESS", "A A C C C");
        feedback(rules, "BLEED", "BREED", "C A C C C");
        feedback(rules, "BLEED", "BREAD", "C A C A C");
        feedback(rules, "BROOM", "BLOOM", "C A C C C");
        feedback(rules, "CHESS", "SHORE", "A C P P A");
        feedback(rules, "APPLE", "APPLE", "C C C C C");
        feedback(rules, "BRICK", "HOUSE", "A A A A A");
        WordleRules fixtures = new WordleRules(config, Arrays.asList("APPLE", "PAPAL", "LLAMA", "LEVEL", "ELDER"));
        feedback(fixtures, "PAPAL", "APPLE", "P P C A P");
        feedback(fixtures, "LLAMA", "APPLE", "P A P A A");
        feedback(fixtures, "LEVEL", "ELDER", "P P A C A");
        for (String invalid : new String[] {null, "", "APPL", "APPLES", "APPL1", "AP LE", "ZZZZZ", "apple", "ÉTAGE"}) {
            check(!rules.isValidGuess(invalid), "reject input " + invalid);
            rejects(IllegalArgumentException.class, () -> rules.evaluateGuess(invalid, "APPLE"));
            rejects(IllegalArgumentException.class, () -> rules.evaluateGuess("APPLE", invalid));
        }
        rejects(IllegalArgumentException.class, () -> new WordleConfiguration(0, 6));
        rejects(IllegalArgumentException.class, () -> new WordleConfiguration(5, 0));
        rejects(IllegalArgumentException.class, () -> new WordleRules(config, new ArrayList<String>()));
        rejects(IllegalArgumentException.class, () -> new WordleRules(config, Arrays.asList("BAD")));
        rejects(IllegalArgumentException.class, () -> new WordleRules(config, Arrays.asList("12345")));
        List<String> mutable = new ArrayList<>(Arrays.asList("apple", "alley", "APPLE"));
        WordleRules copied = new WordleRules(config, mutable);
        mutable.clear();
        check(copied.getAllowedWords().size() == 2 && copied.isValidGuess("APPLE"), "dictionary copied and normalized");
        rejects(UnsupportedOperationException.class, () -> copied.getAllowedWords().clear());
        WordleFeedback result = rules.evaluateGuess("ALLEY", "APPLE");
        WordleFeedback.LetterStatus[] statuses = result.getStatuses();
        statuses[0] = WordleFeedback.LetterStatus.ABSENT;
        check(result.getFeedbackText().equals("C P A P A"), "feedback getter copy");
        WordleFeedback constructed = new WordleFeedback("ALLEY", statuses);
        statuses[0] = WordleFeedback.LetterStatus.CORRECT;
        check(constructed.getStatuses()[0] == WordleFeedback.LetterStatus.ABSENT, "feedback constructor copy");
        rejects(IllegalArgumentException.class, () -> new WordleFeedback("APPLE", new WordleFeedback.LetterStatus[4]));
        randomScoring();
        dictionaryLoading();
        consoleSessions(config, words);
        driverSessions();
        System.out.println("All " + passed + " Wordle and launch checks passed (200 seeded scoring cases).");
    }

    private static void randomScoring() {
        Random random = new Random(422);
        for (int trial = 0; trial < 200; trial++) {
            int length = 1 + random.nextInt(9);
            StringBuilder guess = new StringBuilder();
            StringBuilder secret = new StringBuilder();
            for (int i = 0; i < length; i++) {
                guess.append((char) ('A' + random.nextInt(3)));
                secret.append((char) ('A' + random.nextInt(3)));
            }
            WordleRules rules = new WordleRules(new WordleConfiguration(length, 6),
                    Arrays.asList(guess.toString(), secret.toString()));
            boolean[] used = new boolean[length];
            char[] expected = new char[length];
            Arrays.fill(expected, 'A');
            for (int i = 0; i < length; i++) {
                if (guess.charAt(i) == secret.charAt(i)) {
                    expected[i] = 'C';
                    used[i] = true;
                }
            }
            for (int i = 0; i < length; i++) {
                if (expected[i] == 'C') continue;
                for (int j = 0; j < length; j++) {
                    if (!used[j] && guess.charAt(i) == secret.charAt(j)) {
                        expected[i] = 'P';
                        used[j] = true;
                        break;
                    }
                }
            }
            WordleFeedback result = rules.evaluateGuess(guess.toString(), secret.toString());
            check(result.getFeedbackText().replace(" ", "").equals(new String(expected))
                    && result.isWinningGuess() == guess.toString().equals(secret.toString()), "oracle " + trial);
        }
    }

    private static void dictionaryLoading() throws Exception {
        Path file = Files.createTempFile("wordle-dictionary", ".txt");
        try {
            Files.write(file, "\uFEFFapple\nALLEY\napple\n\n# comment\nCAT\n".getBytes(StandardCharsets.UTF_8));
            check(WordListLoader.load(file, 5).equals(Arrays.asList("APPLE", "ALLEY")), "BOM, casing, duplicates, mixed lengths");
            check(WordListLoader.load(file, 3).equals(Arrays.asList("CAT")), "changed dictionary length");
            rejects(IllegalArgumentException.class, () -> WordListLoader.load(file, 0));
            rejects(java.io.IOException.class, () -> WordListLoader.load(file, 8));
            Files.write(file, "APPLE\nAPP1E\n".getBytes(StandardCharsets.UTF_8));
            rejects(java.io.IOException.class, () -> WordListLoader.load(file, 5));
            Files.write(file, new byte[0]);
            rejects(java.io.IOException.class, () -> WordListLoader.load(file, 5));
        } finally {
            Files.deleteIfExists(file);
        }
        rejects(java.io.IOException.class, () -> WordListLoader.load(file, 5));
    }

    private static void consoleSessions(WordleConfiguration config, List<String> words) {
        String output = play(config, words, true, "HISTORY\nZZZZZ\nALLEY\nGRAPE\nHISTORY\n apple \nN\n");
        check(output.contains("TEST MODE - secret word: APPLE"), "deterministic secret");
        check(output.contains("No valid guesses have been made yet."), "empty history");
        check(output.contains("Invalid guess."), "invalid message");
        check(occurrences(output, "(6 remaining)") == 3, "invalid and history preserve attempts");
        check(output.contains("(5 remaining)") && output.contains("(4 remaining)"), "valid guesses consume attempts");
        check(output.contains("1. ALLEY -> C P A P A\n2. GRAPE -> A A P P C"), "ordered history");
        check(output.contains("C C C C C\nYou guessed the secret word!"), "lowercase trimmed win");
        output = play(config, words, true, "ALLEY\nALLEY\nALLEY\nALLEY\nALLEY\nALLEY\nN\n");
        check(occurrences(output, "C P A P A") == 6 && output.contains("out of guesses. The secret word was APPLE."), "six guesses lose");
        output = play(config, words, true, "ALLEY\nALLEY\nALLEY\nALLEY\nALLEY\nAPPLE\nN\n");
        check(output.contains("You guessed") && !output.contains("out of guesses"), "last attempt wins");
        output = play(config, words, true, "APPLE\nmaybe\nYES\nHISTORY\nAPPLE\nNO\n");
        check(occurrences(output, "You guessed") == 2, "replay after win");
        check(output.contains("Please enter Y or N."), "invalid replay answer");
        check(output.contains("No valid guesses have been made yet."), "replay resets history");
        output = play(new WordleConfiguration(5, 1), words, true, "ALLEY\nY\nAPPLE\nN\n");
        check(output.contains("out of guesses") && output.contains("You guessed"), "replay after loss");
        output = play(new WordleConfiguration(3, 2), Arrays.asList("CAT", "ACT"), true, "ACT\nCAT\nN\n");
        check(output.contains("P P C") && output.contains("C C C\nYou guessed"), "three-letter configuration");
        output = play(config, words, true, "");
        check(!output.contains("Play again?") && output.contains("Thanks for playing!"), "EOF during game");
        check(play(config, words, true, "APPLE\n").contains("Thanks for playing!"), "EOF at replay");
        output = play(config, Arrays.asList("APPLE"), false, "APPLE\nN\n");
        check(!output.contains("TEST MODE") && output.contains("You guessed"), "normal mode dictionary secret");
        WordleGame game = new WordleGame(config, words, false, new Scanner(""), new Random(99));
        Random expected = new Random(99);
        for (int i = 0; i < 20; i++) {
            check(game.createSecret().equals(words.get(expected.nextInt(words.size()))), "seeded secret " + i);
        }
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            check(play(config, Arrays.asList("LIGHT"), true, "history\nlight\nN\n").contains("You guessed"), "Turkish locale");
        } finally {
            Locale.setDefault(original);
        }
    }

    private static void driverSessions() throws Exception {
        String previous = System.getProperty("wordle.dictionary");
        try {
            System.clearProperty("wordle.dictionary");
            check(launch("APPLE\nN\n", "wordle", "test").contains("You guessed the secret word!"), "Wordle launch");
            check(launch("BGOP\nN\n", "mastermind", "test").contains("4B_0W"), "Mastermind launch regression");
            for (String[] args : new String[][] {{}, {"chess"}, {"wordle", "bad"}, {"wordle", "test", "extra"}}) {
                check(launch("", args).contains("Usage:"), "invalid launch arguments");
            }
            Path dictionary = Files.createTempFile("wordle-driver", ".txt");
            try {
                Files.write(dictionary, "GRAPE\n".getBytes(StandardCharsets.UTF_8));
                System.setProperty("wordle.dictionary", dictionary.toString());
                check(launch("GRAPE\nN\n", "wordle", "test").contains("secret word: GRAPE"), "dictionary path override");
            } finally {
                Files.deleteIfExists(dictionary);
            }
            check(launch("", "wordle").contains("Unable to load Wordle dictionary:"), "missing dictionary");
            check(launch("BGOP\nN\n", "mastermind", "test").contains("You guessed"), "Mastermind does not need dictionary");
        } finally {
            if (previous == null) System.clearProperty("wordle.dictionary");
            else System.setProperty("wordle.dictionary", previous);
        }
    }

    private static String launch(String input, String... args) {
        InputStream originalInput = System.in;
        PrintStream originalError = System.err;
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            return capture(() -> {
                System.setErr(System.out);
                Driver.main(args);
            });
        } finally {
            System.setIn(originalInput);
            System.setErr(originalError);
        }
    }

    private static String play(WordleConfiguration config, List<String> words, boolean testMode, String input) {
        return capture(() -> new WordleGame(config, words, testMode, new Scanner(input), new Random(422)).play());
    }

    private static String capture(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(bytes)) {
            System.setOut(output);
            action.run();
        } finally {
            System.setOut(original);
        }
        return new String(bytes.toByteArray(), StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    private static int occurrences(String text, String fragment) {
        return text.split(java.util.regex.Pattern.quote(fragment), -1).length - 1;
    }

    private static void feedback(WordleRules rules, String guess, String secret, String expected) {
        WordleFeedback result = rules.evaluateGuess(guess, secret);
        check(result.getGuess().equals(guess) && result.getFeedbackText().equals(expected)
                && result.isWinningGuess() == guess.equals(secret), guess + " / " + secret + ": " + result);
    }

    private interface CheckedAction {
        void run() throws Exception;
    }

    private static void rejects(Class<? extends Exception> expected, CheckedAction action) {
        try {
            action.run();
        } catch (Exception exception) {
            if (expected.isInstance(exception)) {
                passed++;
                return;
            }
            throw new AssertionError("Unexpected exception", exception);
        }
        throw new AssertionError("Expected " + expected.getSimpleName());
    }

    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError(name);
        passed++;
    }
}
