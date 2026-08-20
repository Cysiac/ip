# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner - Intermediate
* IDE and level of expertise: Beginner

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
Commit subjects must follow the SE-EDU Git commit-message convention (required by the course).

## CS2103T iP course constraints

This repo is graded by an automated script as part of the CS2103T Individual Project, which imposes constraints beyond normal Java project practice:

* **Repo structure is load-bearing for grading** — never rename the fork away from `ip`, rename `master` to `main`, or move source out of `[project root]/src`.
* **Increments are tracked by git tags, not commit messages.** Each increment (e.g. `Level-7`, `A-Gradle`) needs a tag matching its exact ID string, and tags must be pushed explicitly (`git push --tags`) — a plain `git push` does not push them.
* **Branch-based increments (`Level-7` onward) must merge with `--no-ff`** — a fast-forward merge loses the branch structure the grading script checks for.
* **Java coding standard**: the SE-EDU standard's basic and intermediate rules are required, not optional style choices.
* **`A-Gradle`/JavaFX build setup**: follow the official SE-EDU JavaFX tutorial's sample `build.gradle` exactly — don't improvise a build config, mismatched setups break cross-platform builds.
* **Reused code must be credited** or it's treated as plagiarism by the course: inline comments for adapted snippets, `//@@author {username}-reused` tags for non-trivial reused blocks, and broad/pervasive AI use disclosed in the iP README's Acknowledgements section.
* **AI-use posture varies by task**: lean into AI for Week 6's optional increments (`A-BetterGui`, `A-Personality`, `A-MoreErrorHandling`, `A-MoreTesting`); lean toward coaching/review rather than producing the artifact for tasks meant as manual practice (peer PR review, full commit messages, PR descriptions).
* Final submission deadline: Fri 2026-09-18, 2359 (JAR + User Guide + released code — no separate Canvas upload).
* The full week-by-week task list and grading rubric live in `docs/CS2103T-iP-guide.md` (local reference, not committed to the repo).
