# Mastermind and Wordle - Phase II

This extends the existing Phase I project. Both games use one `GuessingGame` console loop, `GuessRules` for validation/scoring, and `GuessFeedback` for history and winning results. Java 8 or newer is sufficient; no external Java libraries are required.

## Build and launch

Run from the `project2_au4355` directory:

```sh
mkdir -p build
javac -encoding UTF-8 -d build assignment2/*.java
java -cp build assignment2.Driver mastermind
java -cp build assignment2.Driver wordle
java -cp build assignment2.Driver mastermind test
java -cp build assignment2.Driver wordle test
```

Test mode reveals deterministic secrets: `BGOP` for default Mastermind and the first eligible dictionary word (`APPLE`) for Wordle. Normal mode selects each Mastermind peg independently and selects Wordle words uniformly from the distinct allowed entries.

Both games accept `HISTORY`, ignore invalid guesses without using an attempt, and offer replay after win/loss. Console guesses are trimmed and converted to uppercase. Rules APIs expect uppercase guesses. Wordle prints `C`, `P`, and `A` separated by spaces.

## Dictionary and configuration

`wordlist.txt` contains exactly the 50 words supplied by the user, in the supplied order. It remains external data and supplies both allowed guesses and secret words. No production Java source embeds the dictionary. The small dictionaries in tests are fixtures only.

By default Wordle loads `wordlist.txt` from the working directory. To run elsewhere or replace the dictionary:

```sh
java -Dwordle.dictionary=/absolute/path/to/wordlist.txt -cp build assignment2.Driver wordle
```

Files use UTF-8 and one English alphabetic word per line. The loader handles lowercase, duplicate entries, an initial BOM, blank lines, and whole-line `#` comments. It selects entries of the configured word length. Nonalphabetic entries, missing files, and files with no eligible words produce a clear startup error. Mastermind does not load the dictionary.

Default Wordle settings are five letters and six valid guesses. Change `WordleConfiguration` construction in Driver to configure them. A matching-length dictionary is required. Mastermind retains `GameConfiguration` for code length, attempts, and up to ten colors.

## Tests

```sh
sh run-tests.sh
```

All 561 checks pass: 11 original Mastermind checks, 245 Mastermind harness checks, and 305 Wordle/launch checks. The suites include 200 reproducible randomized scoring cases per game, independent scoring oracles, repeated-letter cases, dictionary errors, full console sessions, replay, and changed configurations. Assertions throw directly; `-ea` is unnecessary.

## Files and reports

- `ARCHITECTURE_CHANGE.md` answers all eight architecture questions and contains before/after diagrams. The PDF has the same substantive answers and drawn diagrams.
- `TESTS.md` / `TESTS.pdf` document validation.
- `AI.md` / `AI.pdf` disclose assistance and a rejected design choice.
- The final package is `Project2_Phase2_au4355.zip` at the repository root. EID is inferred from the existing project directory.
- The tracked original Phase I code and original ZIP are retained. `phase1_snapshot/Phase1_before_Wordle.zip` at the repository root preserves the working source immediately before this extension, with SHA-256 hashes.

For IDE package resolution, mark `project2_au4355` as the source root. The `assignment2` directory is the Java package. An old extensionless `assignment2/GuessingGame` draft and old `.class` files are not part of the Phase II ZIP; use `GuessingGame.java` and freshly compiled `build` classes.
