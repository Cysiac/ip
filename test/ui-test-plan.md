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

## Test: Deadline and event markers are case-insensitive
**Aim:** "/BY", "/FROM", and "/TO" are recognized the same as their
lowercase forms, matching how the command names themselves are already
case-insensitive.

```input
deadline return book /BY Sunday
event project meeting /FROM Mon 2pm /TO 4pm
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
 Meow! I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 2 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Marker closest to the end wins when it also appears in the description
**Aim:** If the description text happens to contain something that looks
like a marker (e.g. "/by" or "/to"), the flag nearest the end of the line
is treated as the real one, not the first match.

```input
deadline reply /by email /by Friday
event remind team /to buy cake /from 2pm /to 4pm
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
   [D][ ] reply /by email (by: Friday)
 Now you have 1 task in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [E][ ] remind team /to buy cake (from: 2pm to: 4pm)
 Now you have 2 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Errors interleaved with valid commands do not corrupt task count or list
**Aim:** An unknown command and two out-of-range "mark"/"unmark" calls are
rejected without being stored or changing the task count, and a
subsequent valid "todo" and "list" show only the two real tasks.

```input
todo read book
notacommand
mark 99
unmark 99
todo write essay
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
 Meow? I don't know what that means.
 Try: todo, deadline, event, list, mark, unmark, bye
____________________________________________________________
____________________________________________________________
 Meow? Task 99 doesn't exist in your list.
____________________________________________________________
____________________________________________________________
 Meow? Task 99 doesn't exist in your list.
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] write essay
 Now you have 2 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[T][ ] read book
 2.[T][ ] write essay
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Invalid unmark targets are rejected without affecting existing tasks
**Aim:** A non-numeric or out-of-range argument to "unmark" prints a friendly
error, matching "mark"'s behaviour, and leaves the existing task list
unchanged.

```input
todo read book
unmark abc
unmark 5
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
 That's not a task number I recognise, meow?
____________________________________________________________
____________________________________________________________
 Meow? Task 5 doesn't exist in your list.
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

## Test: Zero, negative, and non-integer mark targets are rejected
**Aim:** "mark 0", "mark -1", and "mark 1.5" are all out-of-range or
unparseable and are rejected with a friendly error, without disturbing
the task list; a valid "mark 1" afterwards still succeeds.

```input
todo read book
mark 0
mark -1
mark 1.5
mark 1
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
 Meow? Task 0 doesn't exist in your list.
____________________________________________________________
____________________________________________________________
 Meow? Task -1 doesn't exist in your list.
____________________________________________________________
____________________________________________________________
 That's not a task number I recognise, meow?
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
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Marking an already-done task or unmarking an already-not-done task still succeeds
**Aim:** Repeating "mark N" on an already-done task, or "unmark N" on an
already-not-done task, is a no-op on the task's state but still prints
the usual confirmation rather than erroring.

```input
todo read book
mark 1
mark 1
unmark 1
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
 Nice! I've marked this task as done, meow:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet, meow:
   [T][ ] read book
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

## Test: Deadline with an empty description or empty by-value is guarded
**Aim:** "deadline /by Sunday" (no description) and "deadline return book /by"
(no text after "/by") both print the usage hint instead of being
added, even though the "/by" marker itself is present.

```input
deadline /by Sunday
deadline return book /by
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

## Test: Event with an empty description or reversed /from and /to markers is guarded
**Aim:** "event /from Mon /to Tue" (no description) and an event with "/to"
appearing before "/from" both print the usage hint instead of being
added.

```input
event /from Mon /to Tue
event meeting /to 4pm /from 2pm
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
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Blank lines are ignored without affecting task count
**Aim:** A blank line entered between two commands produces no output and does
not affect the stored task list.

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

## Test: Adding a 101st task is rejected and the 100 stored tasks are unaffected
**Aim:** Once the 100-task capacity (the current version's fixed-size store) is
reached, one more "todo" prints the capacity error instead of adding
it, and "list" afterwards still shows exactly the 100 stored tasks,
unaffected by the rejected attempt.

```input
todo task 1
todo task 2
todo task 3
todo task 4
todo task 5
todo task 6
todo task 7
todo task 8
todo task 9
todo task 10
todo task 11
todo task 12
todo task 13
todo task 14
todo task 15
todo task 16
todo task 17
todo task 18
todo task 19
todo task 20
todo task 21
todo task 22
todo task 23
todo task 24
todo task 25
todo task 26
todo task 27
todo task 28
todo task 29
todo task 30
todo task 31
todo task 32
todo task 33
todo task 34
todo task 35
todo task 36
todo task 37
todo task 38
todo task 39
todo task 40
todo task 41
todo task 42
todo task 43
todo task 44
todo task 45
todo task 46
todo task 47
todo task 48
todo task 49
todo task 50
todo task 51
todo task 52
todo task 53
todo task 54
todo task 55
todo task 56
todo task 57
todo task 58
todo task 59
todo task 60
todo task 61
todo task 62
todo task 63
todo task 64
todo task 65
todo task 66
todo task 67
todo task 68
todo task 69
todo task 70
todo task 71
todo task 72
todo task 73
todo task 74
todo task 75
todo task 76
todo task 77
todo task 78
todo task 79
todo task 80
todo task 81
todo task 82
todo task 83
todo task 84
todo task 85
todo task 86
todo task 87
todo task 88
todo task 89
todo task 90
todo task 91
todo task 92
todo task 93
todo task 94
todo task 95
todo task 96
todo task 97
todo task 98
todo task 99
todo task 100
todo task 101
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
   [T][ ] task 1
 Now you have 1 task in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 2
 Now you have 2 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 3
 Now you have 3 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 4
 Now you have 4 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 5
 Now you have 5 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 6
 Now you have 6 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 7
 Now you have 7 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 8
 Now you have 8 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 9
 Now you have 9 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 10
 Now you have 10 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 11
 Now you have 11 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 12
 Now you have 12 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 13
 Now you have 13 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 14
 Now you have 14 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 15
 Now you have 15 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 16
 Now you have 16 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 17
 Now you have 17 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 18
 Now you have 18 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 19
 Now you have 19 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 20
 Now you have 20 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 21
 Now you have 21 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 22
 Now you have 22 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 23
 Now you have 23 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 24
 Now you have 24 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 25
 Now you have 25 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 26
 Now you have 26 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 27
 Now you have 27 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 28
 Now you have 28 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 29
 Now you have 29 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 30
 Now you have 30 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 31
 Now you have 31 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 32
 Now you have 32 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 33
 Now you have 33 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 34
 Now you have 34 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 35
 Now you have 35 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 36
 Now you have 36 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 37
 Now you have 37 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 38
 Now you have 38 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 39
 Now you have 39 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 40
 Now you have 40 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 41
 Now you have 41 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 42
 Now you have 42 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 43
 Now you have 43 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 44
 Now you have 44 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 45
 Now you have 45 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 46
 Now you have 46 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 47
 Now you have 47 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 48
 Now you have 48 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 49
 Now you have 49 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 50
 Now you have 50 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 51
 Now you have 51 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 52
 Now you have 52 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 53
 Now you have 53 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 54
 Now you have 54 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 55
 Now you have 55 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 56
 Now you have 56 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 57
 Now you have 57 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 58
 Now you have 58 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 59
 Now you have 59 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 60
 Now you have 60 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 61
 Now you have 61 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 62
 Now you have 62 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 63
 Now you have 63 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 64
 Now you have 64 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 65
 Now you have 65 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 66
 Now you have 66 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 67
 Now you have 67 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 68
 Now you have 68 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 69
 Now you have 69 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 70
 Now you have 70 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 71
 Now you have 71 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 72
 Now you have 72 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 73
 Now you have 73 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 74
 Now you have 74 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 75
 Now you have 75 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 76
 Now you have 76 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 77
 Now you have 77 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 78
 Now you have 78 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 79
 Now you have 79 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 80
 Now you have 80 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 81
 Now you have 81 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 82
 Now you have 82 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 83
 Now you have 83 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 84
 Now you have 84 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 85
 Now you have 85 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 86
 Now you have 86 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 87
 Now you have 87 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 88
 Now you have 88 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 89
 Now you have 89 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 90
 Now you have 90 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 91
 Now you have 91 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 92
 Now you have 92 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 93
 Now you have 93 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 94
 Now you have 94 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 95
 Now you have 95 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 96
 Now you have 96 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 97
 Now you have 97 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 98
 Now you have 98 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 99
 Now you have 99 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] task 100
 Now you have 100 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Sorry, I can't remember any more than 100 things! Meow?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[T][ ] task 1
 2.[T][ ] task 2
 3.[T][ ] task 3
 4.[T][ ] task 4
 5.[T][ ] task 5
 6.[T][ ] task 6
 7.[T][ ] task 7
 8.[T][ ] task 8
 9.[T][ ] task 9
 10.[T][ ] task 10
 11.[T][ ] task 11
 12.[T][ ] task 12
 13.[T][ ] task 13
 14.[T][ ] task 14
 15.[T][ ] task 15
 16.[T][ ] task 16
 17.[T][ ] task 17
 18.[T][ ] task 18
 19.[T][ ] task 19
 20.[T][ ] task 20
 21.[T][ ] task 21
 22.[T][ ] task 22
 23.[T][ ] task 23
 24.[T][ ] task 24
 25.[T][ ] task 25
 26.[T][ ] task 26
 27.[T][ ] task 27
 28.[T][ ] task 28
 29.[T][ ] task 29
 30.[T][ ] task 30
 31.[T][ ] task 31
 32.[T][ ] task 32
 33.[T][ ] task 33
 34.[T][ ] task 34
 35.[T][ ] task 35
 36.[T][ ] task 36
 37.[T][ ] task 37
 38.[T][ ] task 38
 39.[T][ ] task 39
 40.[T][ ] task 40
 41.[T][ ] task 41
 42.[T][ ] task 42
 43.[T][ ] task 43
 44.[T][ ] task 44
 45.[T][ ] task 45
 46.[T][ ] task 46
 47.[T][ ] task 47
 48.[T][ ] task 48
 49.[T][ ] task 49
 50.[T][ ] task 50
 51.[T][ ] task 51
 52.[T][ ] task 52
 53.[T][ ] task 53
 54.[T][ ] task 54
 55.[T][ ] task 55
 56.[T][ ] task 56
 57.[T][ ] task 57
 58.[T][ ] task 58
 59.[T][ ] task 59
 60.[T][ ] task 60
 61.[T][ ] task 61
 62.[T][ ] task 62
 63.[T][ ] task 63
 64.[T][ ] task 64
 65.[T][ ] task 65
 66.[T][ ] task 66
 67.[T][ ] task 67
 68.[T][ ] task 68
 69.[T][ ] task 69
 70.[T][ ] task 70
 71.[T][ ] task 71
 72.[T][ ] task 72
 73.[T][ ] task 73
 74.[T][ ] task 74
 75.[T][ ] task 75
 76.[T][ ] task 76
 77.[T][ ] task 77
 78.[T][ ] task 78
 79.[T][ ] task 79
 80.[T][ ] task 80
 81.[T][ ] task 81
 82.[T][ ] task 82
 83.[T][ ] task 83
 84.[T][ ] task 84
 85.[T][ ] task 85
 86.[T][ ] task 86
 87.[T][ ] task 87
 88.[T][ ] task 88
 89.[T][ ] task 89
 90.[T][ ] task 90
 91.[T][ ] task 91
 92.[T][ ] task 92
 93.[T][ ] task 93
 94.[T][ ] task 94
 95.[T][ ] task 95
 96.[T][ ] task 96
 97.[T][ ] task 97
 98.[T][ ] task 98
 99.[T][ ] task 99
 100.[T][ ] task 100
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```
