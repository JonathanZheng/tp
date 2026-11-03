---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).
* The offline User Guide generator uses [Python-Markdown 3.7](https://python-markdown.github.io/) to convert Markdown to HTML. It is a documentation-generation dependency and is not required at application runtime.

Notable uses of AI:

* OpenAI Codex and Claude Code were used to assist with implementation, tests, debugging and documentation.
* All AI-generated output was reviewed and appropriately adapted before use.
* @JonathanZheng used OpenAI Codex for preliminary PR reviews and to implement and test the offline User Guide.
* @BenjaminWong33 used OpenAI Codex to explain the codebase, generate ideas, assist with debugging and testing, and suggest implementation approaches.
* @denzelgoo used OpenAI Codex to assist with idea generation, as well as planning and drafting implementation changes. OpenAI Codex was also used to review the accuracy of documentation against the actual implementation and behaviours of the application.
* @firebean850 used OpenAI Codex to assist with generating ideas, assisting with debugging and testing, and planning feature changes.
* @BurpZes used OpenAI Codex to assist with debugging and testing, finding bugs and analyzing the codebase and documentation for discrepancies.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in `docs/diagrams`. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2627S1-CS2103T-W09-4/tp/tree/master/src/main/java/seedu/insureconnect/Main.java) and [`MainApp`](https://github.com/AY2627S1-CS2103T-W09-4/tp/tree/master/src/main/java/seedu/insureconnect/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103T-W09-4/tp/tree/master/src/main/java/seedu/insureconnect/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `CustomerDetailsPanel`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts. `CommandHistory`, `FocusNavigation`, and `ColourSchemeStyles` are helper classes that are not GUI parts, so they do not inherit from `UiPart`.

The sequence diagram below illustrates how the customer list and details panel remain synchronized when the selected customer changes.

<img src="images/CustomerSelectionSequenceDiagram.png" width="650" />

When a customer is selected, `MainWindow` receives the selection change and asks `CustomerDetailsPanel` to display that customer's information. If the displayed list becomes empty, the selection becomes `null` and the details panel displays its empty state. If the selected customer is removed while other customers remain, `PersonListPanel` selects the nearest remaining customer.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-W09-4/tp/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103T-W09-4/tp/tree/master/src/main/java/seedu/insureconnect/ui/MainWindow.java).

`MainWindow` opens the `HelpWindow` when the user enters `help`, selects the Help menu item, or presses F1.
The window shows the available command formats and a link to the User Guide with a **Copy URL** button.

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2627S1-CS2103T-W09-4/tp/tree/master/src/main/java/seedu/insureconnect/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` followed by a confirmation response such as `execute("y")` as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `InsureConnectParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
2. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
3. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
4. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `InsureConnectParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `InsureConnectParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

`CommandFormats` defines one format string per command, such as `ADD_COMMAND_FORMAT`.
Commands with usage messages include their format strings; `HelpWindow` combines all the strings into the list shown to the user.

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103T-W09-4/tp/tree/master/src/main/java/seedu/insureconnect/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores InsureConnect data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** In the current design, each `Person` owns one or more `Tag` values, which represent that customer's insurance policy numbers. `UniquePersonList` prevents the same policy number from being assigned to different customers.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103T-W09-4/tp/tree/master/src/main/java/seedu/insureconnect/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both InsureConnect data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonInsureConnectStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.insureconnect.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Editing a customer's remark

`InsureConnectParser` sends an `edit` command to `EditCommandParser`. The parser reads the displayed-list index and the supplied prefixes, including `r/`, then creates an `EditPersonDescriptor`. A missing `r/` leaves the descriptor's remark as `null`, meaning that the existing remark should be kept. If `r/` is present without text, `ParserUtil.parseRemark` creates an empty `Remark`, meaning that the existing remark should be cleared. Remark input is trimmed, repeated whitespace is collapsed, and values longer than 500 characters after normalization are rejected. Duplicate `r/` prefixes are rejected.

`EditCommand` selects the customer by index from the current filtered list. It creates a replacement `Person` using the supplied descriptor values and retaining existing values for omitted fields, including the remark. The original creation time is retained. After checking for duplicate customers and policy numbers, the command replaces the person in the model and shows the full list. `LogicManager` saves the data when it has changed.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md) — after changing the User Guide or its screenshots, run `python3 tools/generate_offline_guide.py` and commit the updated files in `docs/offline/`.
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* is an insurance agent (or financial adviser) based in Singapore
* manages a large portfolio of customers (up to about 1000), each holding one or more insurance policies
* handles many policies, claims, renewals and follow-ups daily
* needs to pull up a customer's contact details and policy numbers quickly, for example while the customer is on the line
* finds it hard to remember customers' policy numbers, but remembers their names
* does most of their administrative work on a laptop or desktop computer
* can type fast and prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: InsureConnect gives an insurance agent fast access to customer contact details and associated policy numbers through typed commands, so that a customer can be found by name instead of by a policy number that is hard to remember. It is faster than a spreadsheet or a mouse-driven CRM, keeps all data on the agent's own computer and works without an internet connection.

**Policy scope**: Policy numbers are identifiers attached to customer records. Searching by policy number retrieves matching customer records. InsureConnect does not store policy coverage, premiums, renewal dates or policy status, and remarks apply to a customer rather than an individual policy.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

This table records user needs, including future work; a priority does not indicate implementation status.

For policy-related stories, US06 and US07 support customer lookup by policy number and viewing a customer's policy numbers only. US25 supports deleting customer records; policy numbers can be removed by editing a customer, provided at least one remains.

Detailed policy information (US08), policy renewal and status features (US16-US18), navigation to separate policy records (US22), and the task and renewal overview (US28) are future work and are not implemented.

| Priority | Story | As an …​                          | I want to …​                                                | So that I can…​                                              |
|--------|-------|-----------------------------------|-------------------------------------------------------------|--------------------------------------------------------------|
| `* * *` | US01  | insurance agent (regular user)    | add a customer record                                       | track customer contact information in the system             |
| `* *`  | US02  | insurance agent (first time user) | view a help guide                                           | learn the available commands                                 |
| `* * *` | US03  | insurance agent (regular user)    | search for a customer by name                               | quickly find the customer's record                           |
| `* *`  | US04  | insurance agent (beginner user)   | search for a customer by customer ID                        | retrieve the correct record efficiently                      |
| `* *`  | US05  | insurance agent (beginner user)   | search for a customer by phone number                       | identify the customer using information they provide         |
| `* *`  | US06  | insurance agent (beginner user)   | search for a customer by policy number                      | retrieve the associated customer record                      |
| `* *`  | US07  | insurance agent (beginner user)   | view all policy numbers belonging to a customer             | identify the customer's policies                             |
| `* *`  | US08  | insurance agent (beginner user)   | view detailed information about a policy                    | answer customer enquiries                                    |
| `* *`  | US09  | insurance agent (beginner user)   | add notes to a customer record                              | keep track of previous interactions                          |
| `* *`  | US10  | insurance agent (beginner user)   | update customer contact information                         | keep records accurate                                        |
| `* *`  | US11  | insurance agent (beginner user)   | create a follow-up task                                     | not forget required actions                                  |
| `* * ` | US12  | insurance agent (beginner user)   | specify a due date for a follow-up task                     | know when it needs to be completed                           |
| `* *`  | US13  | insurance agent (beginner user)   | view outstanding tasks                                      | know what work remains                                       |
| `* *`  | US14  | insurance agent  (regular user)   | view overdue tasks                                          | prioritise missed follow-ups                                 |
| `* *`  | US15  | insurance agent (regular user)    | mark tasks as completed                                     | keep my task list up to date                                 |
| `* *`  | US16  | insurance agent (regular user)    | view policies approaching renewal                           | follow up with customers in time                             |
| `* *`  | US17  | insurance agent  (regular user)   | filter policies by status                                   | focus on relevant records                                    |
| `* *`  | US18  | insurance agent  (regular user)   | filter policies by renewal date                             | identify policies requiring attention soon                   |
| `* *`  | US19  | insurance agent with many clients | filter and sort large lists                                 | quickly locate relevant information                          |
| `*`    | US20  | insurance agent (regular user)    | use command autocomplete                                    | enter commands faster and avoid syntax errors                |
| `* *`  | US22  | insurance agent (regular user)    | navigate between related customer and policy records        | handle enquiries efficiently                                 |
| `*`    | US21  | insurance agent  (regular user)   | access command history                                      | quickly repeat frequently used commands                      |
| `*`    | US23  | insurance agent (expert user)     | perform batch operations                                    | process multiple records efficiently                         |
| `*`    | US24  | insurance agent   (expert user)   | archive old or completed records                            | prevent unnecessary information from cluttering my workspace |
| `* * *` | US25  | insurance agent  (regular user)   | delete customer or policy records that are no longer needed | keep the system organised                                    |
| `* *`  | US26  | insurance agent (expert user)     | receive confirmation before destructive operations          | avoid accidentally removing important information            |
| `* *`  | US27  | insurance agent (regular user)    | receive clear success and error messages                    | know whether my command was executed correctly               |
| `* *`  | US28  | insurance agent  (regular user)   | view upcoming tasks and renewals at the start of my shift   | plan my workload                                             |
| `*`    | US29  | insurance agent (expert user)     | review remaining tasks before ending my shift               | avoid forgetting important work                              |
| `* * *` | US30  | insurance agent  (regular user)   | list all records in the system                              | view all records at once                                     |
| `* * *` | US31  | insurance agent  (regular user)   | edit all records in the system                              | avoid deleting and creating a new record                     |



### Use cases

(For all use cases below, the **System** is `InsureConnect` and the **Actor** is the `agent`, unless specified otherwise)

**Use case: UC01 - Add a customer**

**MSS**

1.  Agent requests to add a customer, providing the customer's details.
2.  InsureConnect adds the customer and shows the details of the added customer.

    Use case ends.

**Extensions**

* 1a. A compulsory detail (name, phone, email, address or at least one policy number) is missing.

    * 1a1. InsureConnect shows an error message stating the expected format.

      Use case resumes at step 1.

* 1b. A detail is in an invalid format (e.g. the phone number contains letters).

    * 1b1. InsureConnect shows an error message stating which detail is invalid and what values are accepted.

      Use case resumes at step 1.

* 1c. A customer with the same name and phone number already exists.

    * 1c1. InsureConnect explains that matching name and phone number make the entry a duplicate, clears any active
      filter, and identifies the existing customer by name, phone number, and 1-based index. The customer is not added.

      Use case ends.

* 1d. A given policy number already belongs to an existing customer.

    * 1d1. InsureConnect informs the agent which customer the policy number belongs to and does not add the customer.

      Use case resumes at step 1.

**Use case: UC02 - Find a customer**

**MSS**

1.  Agent requests to find customers, providing one or more keywords.
2.  InsureConnect shows the customers who have a word in their name starting with any of the keywords, together with the number of matches.

    Use case ends.

**Extensions**

* 1a. No keyword is given.

    * 1a1. InsureConnect shows an error message stating the expected format.

      Use case resumes at step 1.

* 2a. No customer matches the keywords.

    * 2a1. InsureConnect shows an empty list and informs the agent that 0 customers were found.

      Use case ends.

**Use case: UC03 - Delete a customer**

**MSS**

1.  Agent <u>finds the customer (UC02)</u>.
2.  Agent requests to delete a specific customer in the displayed list.
3.  InsureConnect displays the selected customer and requests confirmation.
4.  Agent confirms the deletion.
5.  InsureConnect deletes the customer and shows the details of the deleted customer.

    Use case ends.

**Extensions**

* 1a. The customer is not found.

  Use case ends.

* 2a. The given index is invalid.

    * 2a1. InsureConnect shows an error message.

      Use case resumes at step 2.

**Use case: UC04 - Edit a customer's details**

**MSS**

1.  Agent <u>finds the customer (UC02)</u>.
2.  Agent requests to edit a specific customer in the displayed list, providing the new details. The agent may provide `r/REMARK` to replace the remark or `r/` to clear it.
3.  InsureConnect updates the customer and shows the updated details.

    Use case ends.

**Extensions**

* 1a. The customer is not found.

  Use case ends.

* 2a. The given index is invalid.

    * 2a1. InsureConnect shows an error message.

      Use case resumes at step 2.

* 2b. No new details are given, or a new detail is in an invalid format, such as a remark longer than 500 characters after normalization.

    * 2b1. InsureConnect shows an error message stating which detail is missing or invalid.

      Use case resumes at step 2.

* 2c. The edit would make the customer a duplicate of another existing customer, or a new policy number already belongs to another customer.

    * 2c1. InsureConnect informs the agent that the customer already exists and does not apply the edit.

      Use case ends.

**Use case: UC05 - Start the app with an unreadable data file**

**MSS**

1.  Agent launches InsureConnect.
2.  InsureConnect detects that the data file cannot be read and warns the agent that the existing data was not loaded.
3.  InsureConnect starts with an empty customer list and leaves the existing data file untouched.
4.  Agent closes InsureConnect and repairs or replaces the data file.

    Use case ends.

**Extensions**

* 2a. The data file does not exist.

    * 2a1. InsureConnect starts without showing a warning and creates a new data file when the first change is saved.

      Use case ends.

* 3a. Agent makes a change to the customer list before closing InsureConnect.

    * 3a1. InsureConnect does not overwrite the unreadable data file and warns the agent that the change was not saved.

      Use case resumes at step 4.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2.  Should be able to hold up to 1000 customers without noticeable sluggishness in performance for typical usage.
3.  Should respond to any command within 2 seconds when holding up to 1000 customers.
4.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
5.  Should be usable by a single user only; data is not shared between users or computers.
6.  Should store data locally in a human editable text file, so that an advanced user can inspect and correct the data without the app.
7.  Should not depend on a database management system or a remote server, and should work fully without an internet connection.
8.  Should save data to the hard disk after every command that changes data, so that no change is lost if the app is closed unexpectedly. If a save fails, the user should be told so in the result of that same command.
9.  Should never overwrite an existing data file that it failed to read.
10. Should be packaged as a single JAR file of no more than 100MB that runs without an installer.
11. The GUI should work well (i.e. no resolution related inconvenience) for screen resolutions 1920x1080 and higher, and screen scales 100% and 125%.
12. The GUI should remain usable (i.e. all functions can be used even if the experience is not optimal) for screen resolutions 1280x720 and higher, and screen scales 150%.
13. Every error message for an invalid command should state the expected command format or the accepted values, so that the user can correct the command without consulting the User Guide.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Agent**: An insurance agent or financial adviser who sells insurance policies and uses InsureConnect to manage their customers
* **Customer**: A person whose details are recorded in InsureConnect, who holds at least one insurance policy sold by the agent
* **Policy**: An insurance contract held by a customer, such as a life, health, motor or travel insurance plan
* **Policy number**: The unique code that identifies a policy, made up of 1 to 30 characters with at least one letter or digit; hyphens and slashes are also allowed (e.g. `LIFE-2026/SG`). Every customer has at least one, and no two customers can share the same policy number. Entered with the `t/` prefix
* **Remark**: An optional free text note attached to a customer, used for follow-ups and preferences (e.g. `Prefers WhatsApp after 6pm`). The `r/` prefix sets it, and `edit INDEX r/` clears it. An omitted remark is stored as an empty string when adding a customer
* **Duplicate customer**: A customer whose name and phone number match those of an existing customer. Name matching ignores case, extra spaces, and canonical Unicode differences (for example, precomposed and decomposed accents). Phone matching ignores a leading `+`; for Singapore numbers, the `65` country code is also ignored when comparing it with an 8-digit local number
* **Prefix matching**: The way `find` compares keywords with names: a keyword matches a name if any word in the name starts with the keyword, ignoring case (e.g. `Jane` matches `Mary-Jane Tan` and `wei` matches `Tan Wei Ming`, but `ei` does not)
* **Index**: The position number of a customer in the currently displayed list, used to identify the customer in commands such as `delete`. Indices start from 1 and change when the list is filtered by `find` or a customer is deleted
* **Prefix**: The short marker that comes before a value in a command, such as `n/` for name or `p/` for phone
* **Escape character**: A character (`!`) used within the `n/` name, `a/` address, and `r/` remark fields to prevent patterns (such as ` a/` or ` t/`) from being misinterpreted as command prefixes
* **Data file**: The JSON file in which InsureConnect stores all customer records on the user's computer
* **MSS**: Main Success Scenario, the most common sequence of steps in a use case when nothing goes wrong

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

The walkthrough below exercises the main features together. It complements the [User Guide](UserGuide.md), which describes command formats and field constraints in detail. Continue with exploratory testing after completing the path.

### Prepare an isolated working folder

1. Create a new, empty folder for this test run and copy `insureconnect.jar` into it. Open a terminal in that folder and launch the app with `java -jar insureconnect.jar`. Use this same folder for every relaunch; InsureConnect stores `data/addressbook.json` and `preferences.json` relative to the launch folder.

   **Expected:** The app opens with no customers. A missing data file is treated as an empty list.

2. Add three customers using the following commands:

   ```text
   add n/Alice Tan p/91234567 e/alice.tan@example.com a/10 Test Street t/LIFE20001 t/HEALTH20001 r/Call after 6pm
   add n/Bob Lim p/81234567 e/bob.lim@example.com a/20 Sample Road t/MOTOR20002
   add n/Caroline Ng p/98765432 e/caroline.ng@example.com a/30 Sample Crescent t/TRAVEL20003
   ```

   **Expected:** Each customer appears once in the list. Press F6 twice to focus the customer list. Selecting a customer with Up/Down updates the details panel. Alice has two policies and a remark; the other remarks are empty.

### Exercise commands and customer records

1. Try `list` and `list n/`. While the name sort is active, add one more customer, then run `list n/`, `list c/`, and `list`:

   ```text
   add n/Aaron Goh p/87776666 e/aaron.goh@example.com a/40 Example Walk t/LIFE20004
   ```

   **Expected:** `list` restores insertion order, `list n/` sorts by name, and `list c/` sorts by creation time. Aaron appears first in name order and last in insertion and creation-time order. Adding a customer preserves the active sort.

2. Try `find Ali`, `find p/912`, `find t/life20001`, and `find n/Alice p/912 t/LIFE20001`, then run `list`.

   **Expected:** The searches find Alice by name prefix, phone prefix, and case-insensitive exact policy number. The combined search returns Alice only. A new search replaces the previous filter; `list` restores all four customers.

3. Try this duplicate, then edit Alice (the first customer after `list`) and check the details panel:

   ```text
   add n/Alice Tan p/91234567 e/another@example.com a/40 Test Avenue t/LIFE20999
   edit 1 r/Follow up next week
   edit 1 e/alice.updated@example.com
   edit 1 r/
   ```

   **Expected:** The duplicate is rejected and the list is unchanged. The remark is set, remains after editing the email, and is then cleared. The policy numbers remain Alice's original two because they were omitted from the edits.

4. Try `delete 2`, then enter `n`. Repeat `delete 2`, then enter `y`.

   **Expected:** The first response cancels deletion and Bob remains. The second confirms deletion and Bob is removed. Confirmation responses are `y` or `n`; any other response cancels.

5. Try `clear`, then enter `n`.

   **Expected:** The clear is cancelled and all remaining customers stay in the list.

### Exercise the interface

1. Enter `help` and check that the Help window shows the command formats and instructions for opening the offline User Guide. Click **Copy URL**, open a browser, and paste the URL to view the online User Guide. Return to InsureConnect and close the Help window with **Esc** or **F1**.

2. Enter `userguide` to open the bundled User Guide in your browser. If it cannot be opened, check that InsureConnect displays the file path so you can open the guide manually.

3. In the command box, type `lis` and press **Tab**.

   **Expected:** The suggestion completes the command word to `list`; press Enter to run it. Submit a few different commands, type an unfinished draft, then use Up/Down to recall earlier commands and return to the draft. History lasts for the current app session.

4. Press **F6** repeatedly to cycle through the command box, output box, and customer list; use **Shift+F6** to cycle backward. Check that a partially typed command is retained. In the output box, select feedback text and use the system copy shortcut. In the customer list, use Up/Down to change the selected customer. If the list is longer than the visible area, use Page Up/Page Down to browse it.

5. Switch between light and dark with **View → Colour scheme** and F2, including while Help is open. Try `resize p/min`, `resize p/med`, `resize p/max`, and `resize s/1000 700` if that size fits your screen; otherwise use a custom size that fits.

   **Expected:** The selected colour scheme applies to the main and Help windows. Resize commands change the main window and keep it within the usable screen area; a custom size must fit the current screen.

### Check persistence after relaunch

1. Before the final `clear`, note the customer list, choose a colour scheme, resize and move the window, then enter `exit`. Relaunch from the same working folder.

   **Expected:** The customer records, window size and location, and colour scheme are restored. Customer records are saved after successful changes; the app does not save them at a confirmation prompt or after a cancelled operation.

### Optional data-file checks

These checks involve editing files directly. Perform them only in the isolated test folder, and copy `data/addressbook.json` to a backup before changing it.

1. **Legacy record:** With the app closed, edit the saved JSON and remove the `createdAt` property from one customer object without changing its other fields. Relaunch the app and run `list c/`.

   **Expected:** The customer loads. A missing creation time is treated as `1970-01-01T00:00:00Z`, so that customer appears before records with current creation times. Loading alone does not rewrite the file. Edit that customer and check the saved JSON; the creation time is then written as the epoch timestamp. Close the app and restore the valid backup.

2. **Corrupted file:** Make `data/addressbook.json` invalid JSON (for example, replace its contents with `{ invalid`) and relaunch the app.

   **Expected:** A warning appears and the app starts with an empty customer list. Try `add n/Test User p/87654321 e/test.user@example.com a/50 Sample Street t/LIFE20999`; the save fails, the list remains empty, and the invalid file is not overwritten. Close the app and restore the valid backup.

### Finish the walkthrough

1. Relaunch with the restored customer data, run `clear`, and enter `y`.

   **Expected:** All customers are removed and `list` shows an empty list. The working folder is disposable, so clearing its records is safe.
