package assignment2;

public class Driver {
    public static void main(String[] args) {
        if (args.length < 1 || !args[0].equalsIgnoreCase("mastermind")) {
            System.out.println("Usage: java assignment2.Driver mastermind [test]");
            return;
        }

        boolean testMode = args.length >= 2 && args[1].equalsIgnoreCase("test");
        if (args.length > 2 || (args.length == 2 && !testMode)) {
            System.out.println("Usage: java assignment2.Driver mastermind [test]");
            return;
        }

        GameConfiguration configuration = GameConfiguration.defaultConfiguration();
        MastermindGame game = new MastermindGame(configuration, testMode);
        game.play();
    }
}

