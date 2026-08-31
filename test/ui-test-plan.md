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

## Test: Delete a task
**Aim:** "delete N" removes the N-th listed task, prints a confirmation
showing the removed task and the new total, and "list" afterwards shows
the remaining tasks renumbered with the gap closed.

```input
todo read book
todo return book
todo join sports club
delete 2
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
 Meow! I've added this task:
   [T][ ] return book
 Now you have 2 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] join sports club
 Now you have 3 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've removed this task:
   [T][ ] return book
 Now you have 2 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[T][ ] read book
 2.[T][ ] join sports club
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Bare delete is guarded
**Aim:** "delete" with no number given prints a friendly prompt instead of
being added as a literal task named "delete".

```input
delete
bye
```

```output
____________________________________________________________
(=^-ω-^=)  Meowmeow
Hello! I'm Meowmeow.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Meow? Tell me which task number to delete.
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Invalid delete targets are rejected without affecting existing tasks
**Aim:** A non-numeric or out-of-range argument to "delete" prints a
friendly error, matching "mark"/"unmark"'s behaviour, and leaves the
existing task list unchanged.

```input
todo read book
delete abc
delete 5
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
 Try: todo, deadline, event, list, find, mark, unmark, delete, bye
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Add a deadline and list it
**Aim:** "deadline <description> /by <when>" adds a task tagged [D] with
the "/by" date parsed and re-shown in "MMM d yyyy, h:mm a" form in
parentheses, and shows up in "list" the same way.

```input
deadline return book /by 2/12/2019 1800
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
   [D][ ] return book (by: Dec 2 2019, 6:00 pm)
 Now you have 1 task in the list, meow!
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[D][ ] return book (by: Dec 2 2019, 6:00 pm)
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
 "deadline return book /by 2/12/2019 1800".
____________________________________________________________
____________________________________________________________
 Meow? Use "deadline <description> /by <when>", e.g.
 "deadline return book /by 2/12/2019 1800".
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Add an event and list it
**Aim:** "event <description> /from <start> /to <end>" adds a task tagged
[E] with both endpoints parsed and re-shown in "MMM d yyyy, h:mm a" form
in parentheses, and shows up in "list" the same way.

```input
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
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
   [E][ ] project meeting (from: Dec 2 2019, 2:00 pm to: Dec 2 2019, 4:00 pm)
 Now you have 1 task in the list, meow!
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[E][ ] project meeting (from: Dec 2 2019, 2:00 pm to: Dec 2 2019, 4:00 pm)
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
 "event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600".
____________________________________________________________
____________________________________________________________
 Meow? Use "event <description> /from <start> /to <end>", e.g.
 "event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600".
____________________________________________________________
____________________________________________________________
 Meow? Use "event <description> /from <start> /to <end>", e.g.
 "event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600".
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
deadline return book /by 2/12/2019 1800
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
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
   [D][ ] return book (by: Dec 2 2019, 6:00 pm)
 Now you have 2 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [E][ ] project meeting (from: Dec 2 2019, 2:00 pm to: Dec 2 2019, 4:00 pm)
 Now you have 3 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list, meow:
 1.[T][ ] borrow book
 2.[D][ ] return book (by: Dec 2 2019, 6:00 pm)
 3.[E][ ] project meeting (from: Dec 2 2019, 2:00 pm to: Dec 2 2019, 4:00 pm)
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
deadline return book /BY 2/12/2019 1800
event project meeting /FROM 2/12/2019 1400 /TO 2/12/2019 1600
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
   [D][ ] return book (by: Dec 2 2019, 6:00 pm)
 Now you have 1 task in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [E][ ] project meeting (from: Dec 2 2019, 2:00 pm to: Dec 2 2019, 4:00 pm)
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
deadline reply /by email /by 2/12/2019
event remind team /to buy cake /from 2/12/2019 1400 /to 2/12/2019 1600
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
   [D][ ] reply /by email (by: Dec 2 2019)
 Now you have 1 task in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [E][ ] remind team /to buy cake (from: Dec 2 2019, 2:00 pm to: Dec 2 2019, 4:00 pm)
 Now you have 2 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Unrecognised or impossible dates are rejected
**Aim:** A "/by" value that is not a date ("tomorrow"), a date that does
not exist on the calendar ("30/2/2019"), and an impossible clock time
("2560") all print the date-format hint and add nothing.

```input
deadline return book /by tomorrow
deadline return book /by 30/2/2019
deadline return book /by 2/12/2019 2560
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
 Meow? I don't understand that date.
 Try: 2/12/2019 1800, 2/12/2019, 2019-12-02 1800, or 2019-12-02.
____________________________________________________________
____________________________________________________________
 Meow? I don't understand that date.
 Try: 2/12/2019 1800, 2/12/2019, 2019-12-02 1800, or 2019-12-02.
____________________________________________________________
____________________________________________________________
 Meow? I don't understand that date.
 Try: 2/12/2019 1800, 2/12/2019, 2019-12-02 1800, or 2019-12-02.
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

## Test: An event that ends before it starts is rejected
**Aim:** When "/to" is an earlier moment than "/from", the event is not
added and Meowmeow explains why.

```input
event trip /from 2/12/2019 1800 /to 2/12/2019 1400
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
 Meow? An event can't end before it starts.
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

## Test: List tasks on a specific date
**Aim:** "list <date>" shows only the deadline due that day and the event
whose span covers it (a multi-day event matches every day it spans), a
plain todo never matches, an empty result gets its own "free day" line,
and the filtered numbering restarts at 1.

```input
todo borrow book
deadline return book /by 2/12/2019 1800
event conf /from 1/12/2019 0900 /to 3/12/2019 1700
list 2/12/2019
list 2019-12-10
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
   [D][ ] return book (by: Dec 2 2019, 6:00 pm)
 Now you have 2 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [E][ ] conf (from: Dec 1 2019, 9:00 am to: Dec 3 2019, 5:00 pm)
 Now you have 3 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Here are the tasks on Dec 2 2019, meow:
 1.[D][ ] return book (by: Dec 2 2019, 6:00 pm)
 2.[E][ ] conf (from: Dec 1 2019, 9:00 am to: Dec 3 2019, 5:00 pm)
____________________________________________________________
____________________________________________________________
 Nothing on Dec 10 2019 - free day, meow!
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: List with an unrecognised date is rejected
**Aim:** "list <something that isn't a date>" prints the date-format hint
rather than falling back to listing everything.

```input
todo borrow book
list someday
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
 Meow? I don't understand that date.
 Try: 2/12/2019 1800, 2/12/2019, 2019-12-02 1800, or 2019-12-02.
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
 Try: todo, deadline, event, list, find, mark, unmark, delete, bye
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
**Aim:** "deadline /by 2/12/2019 1800" (no description) and "deadline
return book /by" (no text after "/by") both print the usage hint instead
of being added, even though the "/by" marker itself is present. The empty
part is caught before the date is ever parsed.

```input
deadline /by 2/12/2019 1800
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
 "deadline return book /by 2/12/2019 1800".
____________________________________________________________
____________________________________________________________
 Meow? Use "deadline <description> /by <when>", e.g.
 "deadline return book /by 2/12/2019 1800".
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Event with an empty description or reversed /from and /to markers is guarded
**Aim:** "event /from ... /to ..." (no description) and an event with "/to"
appearing before "/from" both print the usage hint instead of being
added. The structural problem is caught before any date is parsed.

```input
event /from 2/12/2019 1400 /to 2/12/2019 1600
event meeting /to 2/12/2019 1600 /from 2/12/2019 1400
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
 "event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600".
____________________________________________________________
____________________________________________________________
 Meow? Use "event <description> /from <start> /to <end>", e.g.
 "event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600".
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

## Test: Find tasks by keyword
**Aim:** "find <keyword>" lists every task whose description contains the
keyword (any task type), a plain non-matching task is left out, and the
filtered numbering restarts at 1.

```input
todo read book
deadline return book /by 2/12/2019
todo buy milk
find book
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
 Meow! I've added this task:
   [D][ ] return book (by: Dec 2 2019)
 Now you have 2 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] buy milk
 Now you have 3 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list, meow:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 2 2019)
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Find with no matches
**Aim:** "find <keyword>" with a keyword no task contains prints the "no
matching tasks" line rather than an empty list header.

```input
todo read book
todo buy milk
find xyzzy
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
 Meow! I've added this task:
   [T][ ] buy milk
 Now you have 2 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 No matching tasks, meow!
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Find is case-insensitive
**Aim:** "find BOOK" matches a task described as "Read Book" - the keyword
match ignores letter case, like the rest of Meowmeow's input handling.

```input
todo Read Book
todo buy milk
find BOOK
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
   [T][ ] Read Book
 Now you have 1 task in the list, meow!
____________________________________________________________
____________________________________________________________
 Meow! I've added this task:
   [T][ ] buy milk
 Now you have 2 tasks in the list, meow!
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list, meow:
 1.[T][ ] Read Book
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```

## Test: Bare find is guarded
**Aim:** "find" with no keyword prints a friendly prompt instead of
searching.

```input
find
bye
```

```output
____________________________________________________________
(=^-ω-^=)  Meowmeow
Hello! I'm Meowmeow.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Meow? Tell me what to search for, e.g. "find book".
____________________________________________________________
____________________________________________________________
 /\_/\
( ^.^ )  Meow! Bye bye~
 > ^ <
____________________________________________________________
```
