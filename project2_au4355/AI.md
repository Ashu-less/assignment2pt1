# AI Assistance Disclosure

ECE 422C - Project 2, Phase II | EID: au4355

## Tools used

OpenAI Codex assisted with implementation, testing, architecture explanation, and reports. Local tools included javac/java, Python, and Ghostscript. No external grader was accessed and no coursework was uploaded.

The user supplied the 50-word external dictionary. AI-generated synthetic word pairs are used only in tests.

## Up to three important interactions

1. Phase I repair: the user requested completion of the existing partial implementation. Codex connected the shared loop and interfaces, fixed the feedback constructor mismatch, and added scoring and interaction tests.

2. Phase II extension: the user supplied the Wordle specification and required architecture questions. Codex extended the same repository with Wordle-specific rules, feedback, configuration, dictionary loading, and game hooks. It reused the console runner and preserved a snapshot of the working Phase I source before Wordle changes.

3. Dictionary and validation: after Codex asked for the provided dictionary, the user supplied 50 words. Codex replaced its temporary sample list with exactly those entries, added repeated-letter and full-session tests, and prepared the Markdown reports, PDFs, and ZIP. All 561 local checks passed, including 400 seeded randomized scoring cases across both games.

## AI-generated suggestion changed or rejected

An earlier generic history implementation printed each result through toString(). This was changed to use GuessFeedback.getGuess() and getFeedbackText(), making history depend on the declared interface rather than requiring every future result type to format toString in a particular way. The runner therefore works with both peg-count and per-letter feedback without type checks.

## Review and responsibility

Codex drafted this disclosure from the recorded work. The student remains responsible for reviewing and explaining the submitted code: duplicate handling, shared contracts, configuration boundaries, and test oracles. Passing local tests does not establish a hidden-grader outcome.
