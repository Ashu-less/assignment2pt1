package assignment2;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class WordListLoader {
    private WordListLoader() {
    }

    public static List<String> load(Path path, int wordLength) throws IOException {
        if (wordLength <= 0) {
            throw new IllegalArgumentException("Word length must be positive.");
        }
        Set<String> words = new LinkedHashSet<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean firstLine = true;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (firstLine && line.startsWith("\uFEFF")) line = line.substring(1);
                firstLine = false;
                String word = line.trim();
                if (word.isEmpty() || word.startsWith("#")) continue;
                if (!word.matches("[A-Za-z]+")) {
                    throw new IOException("Invalid dictionary entry at line " + lineNumber
                            + ": use one alphabetic word per line.");
                }
                if (word.length() == wordLength) words.add(word.toUpperCase(Locale.ROOT));
            }
        }
        if (words.isEmpty()) {
            throw new IOException("Dictionary contains no " + wordLength + "-letter words: " + path);
        }
        return new ArrayList<>(words);
    }
}
