# UI Test Plan

Console UI test cases for Meowmeow, run by the `test-ui` skill
(`.claude/skills/test-ui/SKILL.md`).

## Format

Each test case is a `## Test: <name>` section with:

- `**Aim:**` — a one-line description of what the test case checks.
- a ```` ```input ```` code block — one console command per line, sent to
  the program's stdin in order.
- a ```` ```output ```` code block — the exact expected stdout, byte for
  byte (including the divider lines and leading spaces).

Add new test cases in this format; the runner script parses them
automatically.

## Test: Greeting and exit
**Aim:** The welcome banner appears on startup and "bye" prints the
farewell and ends the session.

```input
bye
```

```output
____________________________________________________________
(=^-ω-^=)  Meowmeow
Hello! I'm Meowmeow.
What can I do for you?
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: List with no tasks
**Aim:** "list" on an empty task list prints the header with no task lines.

```input
list
bye
```

```output
____________________________________________________________
(=^-ω-^=)  Meowmeow
Hello! I'm Meowmeow.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Add a task and list it
**Aim:** A plain line of text is added as a new (not-done) task, and shows
up in "list" with a 1-based index and an unchecked box.

```input
todo read book
list
bye
```

```output
____________________________________________________________
(=^-ω-^=)  Meowmeow
Hello! I'm Meowmeow.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [ ] todo read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[ ] todo read book
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Mark and unmark a task
**Aim:** "mark N" checks off task N and "unmark N" reverses it; both print
a confirmation showing the task's new state, and "list" reflects it.

```input
todo read book
mark 1
list
unmark 1
list
bye
```

```output
____________________________________________________________
(=^-ω-^=)  Meowmeow
Hello! I'm Meowmeow.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [ ] todo read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done, meow:
   [X] todo read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[X] todo read book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet, meow:
   [ ] todo read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[ ] todo read book
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Bare mark/unmark are guarded
**Aim:** "mark" and "unmark" with no number given print a friendly prompt
instead of being added as literal tasks named "mark"/"unmark".

```input
mark
unmark
bye
```

```output
____________________________________________________________
(=^-ω-^=)  Meowmeow
Hello! I'm Meowmeow.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Meow? Tell me which task number to mark.
____________________________________________________________
____________________________________________________________
 Meow? Tell me which task number to unmark.
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Invalid mark targets
**Aim:** A non-numeric or out-of-range argument to "mark" prints a friendly
error instead of crashing.

```input
mark abc
mark 5
bye
```

```output
____________________________________________________________
(=^-ω-^=)  Meowmeow
Hello! I'm Meowmeow.
What can I do for you?
____________________________________________________________
____________________________________________________________
 That's not a task number I recognise, meow?
____________________________________________________________
____________________________________________________________
 Meow? Task 5 doesn't exist in your list.
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```
