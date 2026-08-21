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
**Aim:** "todo <description>" adds a new (not-done) todo task, tagged [T],
and shows up in "list" with a 1-based index and an unchecked box.

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
   [T][ ] read book
 Now you have 1 task in the list, meow!
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[T][ ] read book
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
   [T][ ] read book
 Now you have 1 task in the list, meow!
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done, meow:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet, meow:
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[T][ ] read book
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

## Test: Bare todo and unknown commands are guarded
**Aim:** "todo" with no description prints a friendly prompt instead of
being added, and a line that isn't any known command is rejected rather
than being stored as a task.

```input
todo
read book
bye
```

```output
____________________________________________________________
(=^-ω-^=)  Meowmeow
Hello! I'm Meowmeow.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Meow? Tell me what to add, e.g. "todo borrow book".
____________________________________________________________
____________________________________________________________
 Meow? I don't know what that means.
 Try: todo, deadline, event, list, mark, unmark, bye
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Add a deadline and list it
**Aim:** "deadline <description> /by <when>" adds a task tagged [D] with
the "by" text shown in parentheses, and shows up in "list" the same way.

```input
deadline return book /by Sunday
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
   [D][ ] return book (by: Sunday)
 Now you have 1 task in the list, meow!
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[D][ ] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Malformed deadline commands are guarded
**Aim:** A "deadline" missing the "/by" marker (or with nothing after it)
prints a usage hint instead of being added, whether or not a description
was given.

```input
deadline return book
deadline
bye
```

```output
____________________________________________________________
(=^-ω-^=)  Meowmeow
Hello! I'm Meowmeow.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Meow? Use "deadline <description> /by <when>", e.g.
 "deadline return book /by Sunday".
____________________________________________________________
____________________________________________________________
 Meow? Use "deadline <description> /by <when>", e.g.
 "deadline return book /by Sunday".
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Add an event and list it
**Aim:** "event <description> /from <start> /to <end>" adds a task tagged
[E] with the "from"/"to" text shown in parentheses, and shows up in "list"
the same way.

```input
event project meeting /from Mon 2pm /to 4pm
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
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 1 task in the list, meow!
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Malformed event commands are guarded
**Aim:** An "event" missing "/from", missing "/to", or with no description
at all prints a usage hint instead of being added.

```input
event meeting
event meeting /from Mon
event
bye
```

```output
____________________________________________________________
(=^-ω-^=)  Meowmeow
Hello! I'm Meowmeow.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Meow? Use "event <description> /from <start> /to <end>", e.g.
 "event project meeting /from Mon 2pm /to 4pm".
____________________________________________________________
____________________________________________________________
 Meow? Use "event <description> /from <start> /to <end>", e.g.
 "event project meeting /from Mon 2pm /to 4pm".
____________________________________________________________
____________________________________________________________
 Meow? Use "event <description> /from <start> /to <end>", e.g.
 "event project meeting /from Mon 2pm /to 4pm".
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Mixed task list

**Aim:** Todos, a deadline, and an event can all be added together and
each renders with its own type tag and detail suffix in "list".

```input
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
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
   [T][ ] borrow book
 Now you have 1 task in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[T][ ] borrow book
 2.[D][ ] return book (by: Sunday)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```
