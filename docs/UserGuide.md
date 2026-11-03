---
layout: page
title: User Guide
---

InsureConnect is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, InsureConnect can help you manage contacts faster than traditional GUI applications.

InsureConnect stores customer contact details and associated policy numbers. You can use a policy number to find its customer record. It does not store policy coverage, premiums, renewal dates or policy status; consult your insurer's records for these details. Remarks belong to the customer record, not to an individual policy.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

2. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-W09-4/tp/releases).

3. Copy the file to the folder you want to use as InsureConnect's _working directory_. InsureConnect stores its data and preferences relative to this folder.

4. Open a terminal, `cd` to that folder, and run `java -jar insureconnect.jar`.<br>
   Launch InsureConnect from this same directory each time to keep using the same records and preferences.
   A GUI similar to the one below should appear in a few seconds. <br>
   ![Ui](images/Ui.png)

5. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>

   As you type a command prefix, InsureConnect suggests the remaining command text in grey. Press **Tab** to accept the suggestion.

   ![Command box showing a ghost-text command suggestion](images/CommandBoxGhostText.png)

   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01, t/life123` : Adds a contact named `John Doe` to InsureConnect with policy number life123.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

6. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Keyboard navigation

Press **F2** to switch between the light and dark colour schemes.
This shortcut works from both the main window and the Help window.

Press **F6** to move focus between the **command box → output box → contacts list → command box**.
Press **Shift+F6** to move in the reverse direction. A thin outline marks the focused area.
These shortcuts work while typing commands or selecting output text, and preserve the command you are editing.
If focus is outside these areas, F6 enters the command box and Shift+F6 enters the contacts list.

Press **F1** to open the Help window. While the Help window is focused, press **F1** again or **Esc** to close it.

In the contacts list, use **Up/Down** to move between contacts and **Page Up/Page Down** to browse longer lists.
In the output box, use the arrow keys to move through feedback, **Shift+arrow keys** to select text,
and your system's copy shortcut (**Ctrl+C** on Windows/Linux, **Command+C** on macOS) to copy it.
The output is read-only. Press F6 twice from the output box to return to command entry.

On keyboards that reserve function keys for hardware controls, hold **Fn** as needed to send F1, F2 or F6.

### Command history

In the command box, press **Up** to recall older commands and **Down** to move toward newer commands.
After the newest command, Down restores your unfinished draft, including its caret and selection.
Recalled commands are editable; press Enter to run them. Navigating again discards edits to a recalled command.
History includes successful and failed submissions, skips blank commands and consecutive identical submissions,
and lasts until you close the application. Navigation stops at the oldest command without wrapping.
Modified arrow shortcuts and arrow keys in other areas retain their usual behavior.

### Command suggestions

As you type the start of a command word, the rest of the word appears as faded text after the caret.
For example, typing `ed` shows `edit`. Press **Tab** to fill in the suggested word, then continue typing as usual.
Pressing Tab only fills in the word; press Enter to run the command.
When several commands match, the first in alphabetical order is suggested, so `c` suggests `clear`. Type more letters to narrow it down, such as `ex` for `exit`.
Suggestions appear only for command words, only while the caret is at the end of the text, and match lowercase input exactly.

## Changing the colour scheme

Choose **View → Colour scheme → Light** or **Dark** from the menu bar. The
selected option is marked in the menu and takes effect immediately in the main
window and Help window. Command errors remain highlighted in both schemes.

Alternatively, press **F2** to switch between the light and dark colour schemes.

Dark is the default. Your choice is saved with your preferences when you exit
normally, and restored the next time you launch InsureConnect. Switching schemes
preserves your command text, output, and selected contact.

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `edit INDEX [n/NAME]` can be used as `edit 1 n/John Doe` or as `edit 1 p/98765432`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/POLICY_NUMBER]…​` in `edit` may be omitted, or written as `t/LIFE20481` or `t/LIFE20481 t/HEALTH20482`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* **Name constraints**: `NAME` must start with a Latin-script letter or digit. It may contain Latin-script letters (including accented letters such as `é` and `ö`), combining accent marks, digits, spaces, and the characters `-`, `'`, `’`, `.`, `/`, and `!`. Names written in other scripts are not supported, and names cannot consist solely of punctuation (e.g. `---`, `...`, `///` are invalid).

* **Policy number constraints**: Each `t/POLICY_NUMBER` must be 1 to 30 characters long, contain at least one letter or digit, and may include hyphens (`-`) or slashes (`/`). Spaces and other punctuation are not allowed. Every customer needs at least one policy number, and a policy number can belong to only one customer. Policy numbers are case-insensitive and are displayed in uppercase. For example, life20481 and LIFE20481 refer to the same policy number.

* **Escape character (`!`) for name, address, and remark prefix collisions**:
  Command prefixes are identified by a space followed by a letter and a slash (e.g. ` a/` for address, ` t/` for policy number). Names, addresses, or remarks that contain these patterns can conflict with command prefixes if typed directly.<br>
  To avoid prefix collisions, prefix the collision point with an exclamation mark `!` within the name, address, or remark field:
  * `n/Mohd !a/l Kassim` or `n/Mohd a!/l Kassim` will be saved as `Mohd a/l Kassim`.
  * Indian patronymics such as `s/o` or `d/o` (e.g. `n/Ravi s/o Muthu`) do not conflict with existing prefixes and do not require escaping (though `!s/o` is also supported).
  * In a remark, `r/dietary restrictions: !n/a` will be saved as `dietary restrictions: n/a`.
  * In an address, `a/Blk 123 !t/4` will be saved as `Blk 123 t/4`.
  * If you need a literal exclamation mark in a name, address, or remark, type `!!` (for example, `a/Block!! A` is saved as `Block! A`).
  * **Warning:** A single `!` before any character is treated as an escape marker and removed. For example, `r/Great! Thanks` is saved as `Great Thanks`.
  * **Note:** `!` is treated as an escape character only in the name (`n/`), address (`a/`), and remark (`r/`) fields. Other fields (such as `e/` email) do not use `!` as an escape character. Their own validation rules still apply; for example, email addresses containing `!` are not accepted.

* Extraneous parameters for commands that take no parameters, such as `help`, `exit`, `clear`, `userguide`, and `ug`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Customer field requirements

These rules apply when adding a customer or changing a field with `edit`:

| Field | Requirements |
| --- | --- |
| `n/NAME` | Must start with a letter or digit. The remaining characters can be letters, digits, spaces, `-`, `'`, `.`, `/`, or `!`. A name cannot be blank or consist only of punctuation. There is no fixed length limit. See the escape character note above for typing `!` and command prefixes in a name. |
| `p/PHONE_NUMBER` | At least 3 digits, with an optional single `+` at the beginning. Spaces, letters, and other punctuation are not allowed. There is no fixed maximum length. |
| `e/EMAIL` | Format: `local-part@domain`. The local part must start and end with a letter or digit; between them, it may also contain `+`, `_`, `.`, or `-`, each followed by a letter or digit. Domain labels may contain letters, digits, and internal hyphens, but must start and end with a letter or digit. The final domain label must contain at least 2 characters. There is no fixed length limit. For example, `jane.doe+work@example.com` is accepted, but `jane!doe@example.com` is not. |
| `a/ADDRESS` | Must not be blank. Any nonblank text is accepted, up to 300 characters after leading and trailing whitespace is removed. |
| `t/POLICY_NUMBER` | 1 to 30 characters, containing at least one letter or digit. Hyphens (`-`) and slashes (`/`) are allowed; spaces and other punctuation are not. Every customer must have at least one policy number. A policy number can belong to only one customer; matching ignores case (for example, `life20481` and `LIFE20481` are the same number). |
| `r/REMARK` | Optional and may be empty. At most 500 characters after leading and trailing whitespace is removed and repeated whitespace is collapsed. See the escape character note above for typing `!` and command prefixes in a remark. |

### Viewing help: `help`

Opens the Help window. It lists the formats for `add`, `edit`, `delete`, `find`, `list`, `resize`, `clear`, `help`, `userguide` (`ug`), and `exit`.
The window explains how to open the offline guide using `userguide`. Click **Copy URL** to copy the online guide link to your clipboard.

You can open the same window from the **Help** menu or by pressing **F1**.
Press **F1** again or **Esc** to close the Help window while the Help window is focused.

![User Guide link and Copy URL button in the Help window](images/helpMessageWindow.png)

Format: `help`


### Opening the User Guide: `userguide`

Opens the complete bundled User Guide in your default web browser. The guide and its screenshots work without an internet connection. External links, such as downloads, still require internet access. For faster command-line use, `ug` can be used as a short form of `userguide`.

If the browser cannot be opened automatically, the error message gives the local guide file path. Open that file in a browser before closing InsureConnect; the temporary copy is removed when the app exits.

Format: `userguide` or `ug`

### Adding a person: `add`

Adds a person to InsureConnect.

A person is a duplicate only when both their name and phone number match an existing person.
Name matching ignores case, leading or trailing spaces, and repeated spaces between words.
Phone number matching ignores a leading `+`. For Singapore numbers, an 8-digit local
number is also treated as equivalent to the same number prefixed with the country code `65`.
For example, `+1800988989898989` matches `1800988989898989`, while `+6591110101`,
`6591110101`, and `91110101` are treated as the same phone number.
Customers with the same name but different phone numbers, or different names with the same
phone number, are allowed.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS t/POLICY_NUMBER [t/POLICY_NUMBER]…​ [r/REMARK]`

The [customer field requirements](#customer-field-requirements) above apply to every supplied field.

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A customer must have at least one policy number. An existing customer's policy number cannot be reused.
</div>

* `r/REMARK` is optional. If omitted, the customer's remark is empty.

Examples:
* `add n/John Doe p/+6598765432 e/johnd@example.com a/John street, block 123, #01-01 t/LIFE20481`
* `add n/Betsy Crowe t/LIFE20482 e/betsycrowe@example.com a/Newgate Prison p/1234567 t/HEALTH20483`
* `add n/Ravi s/o Muthu p/91234567 e/ravi@example.com a/Blk 123 Jurong West t/LIFE20484`
* `add n/Mohd !a/l Kassim p/81234567 e/mohd@example.com a/Blk 456 Clementi Ave 3 t/LIFE20485`

### Listing all persons: `list`

Shows a list of all persons in InsureConnect. `list` uses the stored order, `list n/` sorts by name from A to Z (case-insensitive), and `list c/` sorts by creation time from oldest to newest. Sorting changes the displayed order only; the stored order is unchanged. The chosen sort stays in effect when you add a customer, so the new customer appears in its sorted position. A search replaces the sort.

Format: `list [n/|c/]`

Use either `n/` or `c/`, without a value. Other arguments are invalid.

### Editing a customer: `edit`

Edits an existing customer in InsureConnect.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [r/REMARK] [t/POLICY_NUMBER]…​`

* Edits the customer at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* After a successful edit, the active search filter is cleared and the full customer list is shown. This also happens if the edit changes a field used by the search, which ensures that the customer being edited is always visible in the customer list even after edits.
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* Fields that are omitted keep their existing values. To replace a remark, use `r/REMARK`; to clear it, use `r/` by itself. An empty `r/` counts as a field to edit.
* Supplied fields must follow the [customer field requirements](#customer-field-requirements) above.
* An edit is rejected if the resulting name and phone number match another person, using the same duplicate rule as `add`.
* When editing policy numbers, all of the customer's existing policy numbers are replaced by those supplied; the change is not cumulative.
* At least one policy number must remain. `t/` without a value is rejected.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/LIFE20486` Edits the name and replaces the policy numbers of the 2nd person.
*  `edit 1 n/Mohd !a/l Kassim` Edits the name of the 1st person to be `Mohd a/l Kassim`.
*  `edit 1 r/Prefers WhatsApp after 6pm` Replaces the 1st person's remark.
*  `edit 1 r/` Clears the 1st person's remark.

### Locating customers by name, phone or policy number: `find`

Finds customers by name keywords, phone prefix or exact policy number, optionally combined.

Format: `find KEYWORD [MORE_KEYWORDS]` or `find [n/KEYWORDS] [p/PHONE_PREFIX] [t/POLICY_NUMBER]`

* Name search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* Unprefixed keywords search names only, including when a keyword contains digits.
* Keywords match the start of any name word; for example, `Han` matches `Hans` and `Bo Hans`.
* Hyphens separate searchable name components; for example, `find Jane` matches `Mary-Jane Tan`.
* Customers with an exact name-word match appear before prefix-only matches. For example, `find John` shows
  `John Tan` before `Johnny Lim`. Matching is case-insensitive and considers any name word or keyword.
* Customers tied in match quality retain their stored order. Searching only changes the displayed order;
  `list` restores the full list in stored order, while `list n/` and `list c/` apply their respective sorts.
* Full words also match, but middle-of-word and suffix matches do not; for example, `ans` does not match `Hans`.
* Customers matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find John` returns `john` and `John Doe`
* `find jo` returns `John Doe` and `Mary Jones`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavid.png)

To search by phone number, use `find p/PHONE_PREFIX`, for example `find p/9876` or `find p/+65`.

* Phone searches match the beginning of the phone number, including the complete number.
  For example, `find p/9876` matches `98765432`, while `find p/+65` matches `+6591234567`.
* Supply an optional leading `+` followed by one or more digits (`0-9`). A single digit is allowed, and leading zeros
  are preserved. Spaces around the prefix are allowed; spaces within it, letters, hyphens, parentheses, other
  punctuation, and a `+` anywhere except the beginning are rejected.
* To combine phone and name criteria, use an explicit `n/` field as described below.
  Supplying multiple phone prefixes is not supported and produces an error.
* All customers with matching phone numbers are shown in stored order, including customers sharing a number.
  A phone-only search replaces the previous search and display sort. Use the displayed indexes for `edit` and `delete`.

#### Combining search criteria

Use `n/` for name keywords, `p/` for a phone prefix, and `t/` for an exact policy number.
Supply at least one field, once each, in any order. A customer must match **every supplied field (AND)**;
within `n/`, matching **any keyword (OR)** is sufficient. Policy searches match any policy belonging to the customer.

* `find n/Alice Bob p/912` finds customers matching Alice or Bob whose phone numbers start with 912.
* `find n/Alice p/912 t/LIFE20001` also requires the policy number LIFE20001, ignoring case.
* `find t/LIFE20001` searches by policy number alone.

Name matching remains case-insensitive and ranks exact name words before prefix-only matches.
Phone prefixes contain digits only with an optional leading `+`. Policy numbers match exactly, ignoring case, and contain 1–30 characters with at least one letter or digit; hyphens and slashes are allowed.
Phone and policy searches without a name criterion retain stored order.
Each new search replaces the previous filter; it does not narrow the previous results.

Empty values and repeated fields are rejected. Use `find n/Alice p/912`, not `find Alice p/912`.
Only `n/`, `p/`, and `t/` are supported in prefixed searches; `e/`, `a/`, and `r/` are rejected.
As in `add`, escape those prefixes with `!` when they are part of a name, for example `find n/Mohd !a/l`.
Other slashes need no escaping, for example `find n/Ravi s/o p/912`.
Existing unprefixed name searches and `find p/DIGITS` remain available.

To search by policy number, use `find t/POLICY_NUMBER`, for example `find t/LIFE-2026/SG`.

* Policy-number searches use an exact match and are case-insensitive. For example,
  `find t/life-2026/sg` finds a customer with policy number `LIFE-2026/SG`.
* Supply exactly one policy number containing 1 to 30 characters with at least one letter or digit.
  Hyphens and slashes are allowed; spaces, other punctuation, and multiple policy numbers are rejected.
* Combine `t/` with explicit `n/` and `p/` fields as described above.
* A policy-number search replaces the previous search and display sort. Use the displayed
  indexes for `edit` and `delete`.

### Deleting a person: `delete`

Requests deletion of the specified person from InsureConnect. The person is deleted only after confirmation.

Format: `delete INDEX`

Confirmation response: `y`

* `delete INDEX` shows the customer at the specified `INDEX` and requests confirmation.
* When prompted, enter `y` to delete the customer. The response is case-insensitive, so `y` and `Y` are equivalent. Enter `n`, press Enter without a response, or enter anything else to cancel the deletion. The default response is `n`.
* The index refers to the index number shown in the displayed customer list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:

* `list` followed by `delete 2`, then `y`, deletes the 2nd customer in InsureConnect.
* `find Betsy` followed by `delete 1`, then `y`, deletes the 1st customer in the results of the `find` command.

### Clearing all entries: `clear`

Clears all entries from InsureConnect after confirmation.
If there are no customers to clear, InsureConnect displays a message and does not request confirmation.
When prompted, enter `y` to clear the records. The response is case-insensitive (i.e. `y` and `Y` are equivalent).
Enter `n`, press Enter without a response, or enter anything else to cancel. The default response is `n`.

Format: `clear`

### Resizing the window: `resize`

Resizes the main window using either a preset or an explicit width and height. The two forms cannot be combined.

Format: `resize p/min|p/med|p/max` or `resize s/WIDTH HEIGHT`

* `p/min` sets the app minimum of 880 by 600 pixels.
* `p/med` sets each dimension to two thirds of the current screen's usable area, raised to the app minimum if needed.
* `p/max` fills the current screen's usable area while leaving a 12-pixel margin on every edge.
* `s/` accepts whole-number width and height in pixels, in that order. Neither dimension may be below the app minimum or exceed the current screen's usable area after the edge margins.

The window stays within the usable area of the screen it currently occupies. If that screen cannot fit the app minimum with the edge margins, resizing is rejected. The new size is saved with your preferences when you exit.

Examples: `resize p/med`, `resize s/1000 700`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

InsureConnect automatically saves customer records after a successful `add`, `edit`, `delete`, or `clear` command that changes the records. `delete` and `clear` save only after you confirm them. Failed or cancelled commands, confirmation prompts, and commands that do not change customer records do not save the records. You do not need to save manually.

### Editing the data file

InsureConnect saves its files relative to the working directory (the directory from which you launch it): customer records are saved to `data/addressbook.json`, and preferences are saved to `preferences.json`. For example, if you run the JAR by its full path while your terminal is in `/home`, the files are `/home/data/addressbook.json` and `/home/preferences.json`, regardless of where the JAR is stored. Launching InsureConnect from different directories uses different records and preferences files. Launch it from the same directory each time to keep using the same records and preferences. The legacy data filename is retained so existing installations continue to load their records. Advanced users are welcome to update data directly by editing `data/addressbook.json`.

Addresses already saved with more than 300 characters are loaded and preserved as-is. The 300-character limit applies to addresses entered with the `add` and `edit` commands.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, InsureConnect displays a warning and starts with an empty customer list. The invalid file remains unchanged and cannot be overwritten by add, edit, delete, or clear; commands that attempt to save customer records will fail. To resume saving, close InsureConnect and repair, move, rename, or delete the invalid file before restarting. We recommend making a backup before editing the file.<br>

Furthermore, certain edits can cause InsureConnect to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

--------------------------------------------------------------------------------------------------------------------

### Loading contact files from older versions

Contact files without `createdAt` timestamps load automatically. Their existing
contact details are preserved, and unknown creation dates use
`1970-01-01T00:00:00Z` as a stable fallback for creation-date sorting. This is a
migration value, not the actual creation date. The timestamp is written on the
next successful save; loading alone does not rewrite the file. Existing valid
timestamps are retained. Malformed timestamps and other invalid contact data
still produce a loading warning. Old labels are not converted into policy
numbers: policy numbers must still be valid and unique across contacts.


## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the `data/addressbook.json` file from your previous InsureConnect working directory.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS t/POLICY_NUMBER [t/POLICY_NUMBER]…​ [r/REMARK]` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/LIFE20481 t/HEALTH20482` <br> e.g. escaping `a/`: `add n/Mohd !a/l Kassim p/81234567 e/mohd@example.com a/Blk 456 Clementi Ave 3 t/LIFE20483`
**Clear** | `clear`, then `y` to confirm
**Delete** | `delete INDEX`, then `y` to confirm<br> e.g., `delete 3`, then `y`
**Edit** | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [r/REMARK] [t/POLICY_NUMBER]…​`<br> e.g., `edit 2 n/James Lee e/jameslee@example.com`<br> e.g., `edit 1 r/Follow up next week`<br> e.g. escaping `a/`: `edit 1 n/Mohd !a/l Kassim`
**Exit** | `exit`
**Find** | `find KEYWORD [MORE_KEYWORDS]` or `find [n/KEYWORDS] [p/PHONE_PREFIX] [t/POLICY_NUMBER]`<br> e.g., `find James Jake`, `find n/Alice p/912 t/LIFE20001`
**List** | `list [n/|c/]`<br> e.g., `list n/`, `list c/`
**Resize** | `resize p/min\|p/med\|p/max` or `resize s/WIDTH HEIGHT`<br> e.g., `resize p/med`, `resize s/1000 700`
**Help** | `help`
**User Guide** | `userguide` or `ug`
