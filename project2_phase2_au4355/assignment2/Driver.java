package assignment2;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Driver {
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2
                || (!args[0].equalsIgnoreCase("mastermind") && !args[0].equalsIgnoreCase("wordle"))
                || (args.length == 2 && !args[1].equalsIgnoreCase("test"))) {
            System.out.println("Usage: java assignment2.Driver <mastermind|wordle> [test]");
            return;
        }

        boolean testMode = args.length == 2;
        GuessingGame game;
        if (args[0].equalsIgnoreCase("mastermind")) {
            game = new MastermindGame(GameConfiguration.defaultConfiguration(), testMode);
        } else {
            WordleConfiguration configuration = WordleConfiguration.defaultConfiguration();
            try {
                Path dictionary = Paths.get(System.getProperty("wordle.dictionary", "wordlist.txt"));
                List<String> words = WordListLoader.load(dictionary, configuration.getWordLength());
                game = new WordleGame(configuration, words, testMode);
            } catch (IOException | IllegalArgumentException exception) {
                System.err.println("Unable to load Wordle dictionary: " + exception.getMessage());
                System.err.println("Use -Dwordle.dictionary=/path/to/wordlist.txt before assignment2.Driver.");
                return;
            }
        }
        game.play();
    }
}
