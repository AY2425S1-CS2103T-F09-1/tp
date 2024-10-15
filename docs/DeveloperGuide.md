---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# ClubConnect Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
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

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**: 

Computing Club Committee members

* Tech-savvy leaders who organize events, manage activities, and foster community engagement.
* Connect members, sponsors, and industry partners, driving innovation and learning.

**Value proposition**: Streamline computing club's communication and organization with our address book app. Effortlessly manage member details, sponsor contacts, and event participants in one place. Enhance collaboration, boost engagement, and ensure seamless planning, all while saving time and reducing administrative hassle.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`, Exists - `EXISTS`, Not possible - `N.A.`

| Priority | As a …​                            | I want to …​                                                                        | So that I can…​                                                                     |
|----------|------------------------------------|-------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| `* * *`  | Committee president                | Search contacts by multiple criteria (e.g., job title, tags)                        | Find the right contacts even if I don’t remember their names                        |
| `* * *`  | Committee president                | Filter the contacts to different types of events                                    | Easily know who to contact for specific purposes, even with multiple ongoing events |
| `* * *`  | Committee member                   | Detect and merge duplicate contacts easily                                          | Keep my address book clean and well-organized                                       |
| `* * *`  | Committee president                | Mass delete contacts                                                                | Easily remove all contacts related to one event after it's over                     |
| `* *`    | Committee president                | Assign tasks and responsibilities to committee members                              | Ensure all activities are covered without confusion                                 |
| `* *`    | Committee member                   | Receive notifications for upcoming meetings and events                              | Stay informed and participate on time                                               |
| `* *`    | Events coordinator                 | Send out event reminders and notifications to members                               | Keep everyone informed and boost engagement                                         |
| `* *`    | Committee member                   | Import contacts from a CSV file                                                     | Quickly populate the address book                                                   |
| `* *`    | Committee member                   | Export contacts to a CSV file                                                       | Share the contact list with others                                                  |
| `* *`    | Committee member                   | Customize the app's interface                                                       | Tailor the app to my preferences                                                    |
| `* *`    | Club member                        | View a list of upcoming events                                                      | Stay informed about club activities                                                 |
| `* *`    | Committee member                   | Add a new event to the calendar                                                     | Plan club activities                                                                |
| `*`      | Committee president                | Have a blacklist of participants                                                    | Keep track of people who are not allowed to join future events                      |
| `*`      | Committee member                   | Track event attendance                                                              | See who participated                                                                |
| `*`      | Secretary                          | Track meeting attendance                                                            | Maintain records of who participated in club activities                             |
| `*`      | Committee member                   | View a member's participation history                                               | Recognize active members                                                            |
| `EXISTS` | Committee member organizing events | Label each of my contacts                                                           | I can easily mass contact sponsors / participants / organizing committee, etc       |
| `EXISTS` | Committee member                   | Add a new member to the address book                                                | Keep track of all members in the club                                               |
| `EXISTS` | Committee president                | Delete contacts                                                                     | Avoid contacting people no longer involved with the committee                       |
| `EXISTS` | Committee president                | Keep track of every member’s contact information, e.g., phone number, email address | Contact them during an emergency                                                    |
| `N.A.`   | Committee member                   | Password-protect sensitive contact information                                      | Ensure my contacts remain private and secure                                        |
| `N.A.`   | Communication committee member     | Log all interactions with sponsors and partners                                     | Reference past conversations and ensure nothing is overlooked                       |
| `N.A.`   | Committee member                   | Send a group email to all members                                                   | Communicate important information quickly                                           |
| `N.A.`   | Committee member                   | Set reminders for upcoming events                                                   | Ensure I don’t miss important activities                                            |
| `N.A.`   | Committee member                   | Integrate the app with my calendar                                                  | Automatically sync important events and reminders                                   |

### Use cases

(For all use cases below, the **System** is the `ClubConnect` and the **Actor** is the `User`, unless specified otherwise)

---

**Use case: UC01 - Add contact**  
**Actor:** User  
**MSS:**
1. User requests to add a contact.
2. App adds the contact.  
   Use case ends.

**Extensions:**
* 1a. The given name is invalid (i.e., name is empty or does not start with an alphabet).
    * 1a1. App shows an error message to tell the user that the given name is invalid.  
      Use case ends.
* 1b. The given phone number is invalid (i.e., phone number is not an 8-digit number and/or does not start with 6, 8, or 9).
    * 1b1. App shows an error message to tell the user that the given phone number is invalid.  
      Use case ends.
* 1c. The given email is invalid (i.e., email does not follow normal email address format).
    * 1c1. App shows an error message to tell the user that the given email is invalid.  
      Use case ends.
* 1d. The given contact is a duplicate of another contact in the list.
    * 1d1. App shows an error message to tell the user that the contact already exists in the list.  
      Use case ends.

---

**Use case: UC02 - Edit contact**  
**Actor:** User  
**MSS:**
1. User requests to edit a contact by providing the index and the parameters to be changed.
2. App changes the contact.  
   Use case ends.

**Extensions:**
* 1a. User provides an invalid contact index (i.e., negative index or index exceeding size of list).
    * 1a1. App shows an error message to tell the user that the contact does not exist.  
      Use case ends.
* 1b. User provides an invalid name (i.e., name does not start with an alphabet).
    * 1b1. App shows an error message to tell the user that the contact name is not valid.  
      Use case ends.
* 1c. User provides an invalid phone number (i.e., phone number is not numerical).
    * 1c1. App shows an error message that the phone number is not valid.  
      Use case ends.
* 1d. User provides an invalid email address (i.e., email address does not have a domain).
    * 1d1. App shows an error message that the email address is not valid.  
      Use case ends.

---

**Use case: UC03 - Delete contact by index**  
**Actor:** User  
**MSS:**
1. User requests to list contacts.
2. App shows a list of contacts.
3. User requests to delete a specific contact by index in the list.
4. App deletes the contact at the specified index.  
   Use case ends.

**Extensions:**
* 2a. The list is empty.
    * 2a1. App shows an error message to tell the user the list is empty.  
      Use case ends.
* 3a. The given index is invalid (i.e., index does not exist or is not a positive integer).
    * 3a1. App shows an error message to tell the user that the given index is invalid.  
      Use case ends.

---

**Use case: UC04 - Delete contact by name**  
**Actor:** User  
**MSS:**
1. User requests to delete a specific contact by name in the list.
2. App deletes the contact with the specified name.  
   Use case ends.

**Extensions:**
* 1a. The given name does not exist.
    * 1a1. App shows an error message to tell the user that the given name does not exist.  
      Use case ends.
* 1b. There are multiple contacts with the same name.
    * 1b1. App shows an error message to tell the user that there are multiple contacts with the same name and to delete by index instead.  
      Use case ends.

---

**Use case: UC05 - Search for contact by criteria**  
**Actor:** User  
**MSS:**
1. User specifies criteria and keywords.
2. App shows a list of contacts that match the provided criteria and keywords.  
   Use case ends.

**Extensions:**
* 1a. No criteria is provided.
    * 1a1. App shows an error message to tell the user that no criteria has been provided.  
      Use case ends.
* 1b. Criteria provided does not exist.
    * 1b1. App shows an error message to tell the user that the criteria does not exist.  
      Use case ends.
* 1c. No keywords are provided.
    * 1c1. App shows an error message to tell the user that no keywords have been provided.  
      Use case ends.
  
---

**Use case: UC06 - Label a contact**  
**Actor:** User  
**MSS:**
1. User requests to label a contact with a specified tag by name or ID in the list.
2. App labels the specified contact with the specified tag.  
   Use case ends.

**Extensions:**
* 1a. The given name does not exist.
    * 1a1. App shows an error message to tell the user that the given name does not exist.  
      Use case ends.
* 1b. The given ID does not exist.
    * 1b1. App shows an error message to tell the user that the given ID does not exist.  
      Use case ends.
* 1c. The user inputs a negative integer as the ID.
    * 1c1. App shows an error message to tell the user to input a valid ID.  
      Use case ends.
* 1d. The user inputs a tag that has already been added to the specified contact.
    * 1d1. App shows an error message to tell the user that the new tag is a duplicate and would not be added to the contact.  
      Use case ends.
* 1e. There are multiple contacts with the same name.
    * 1e1. App shows an error message to tell the user that there are multiple contacts with the same name and to label by index instead.  
      Use case ends.

---

**Use case: UC07 - Mass Delete**  
**Actor:** User  
**MSS:**
1. User requests to mass delete contacts by providing a list of contact IDs.
2. App validates the provided contact IDs.
3. App deletes the valid contacts.
4. App logs the success message indicating the number of contacts deleted.  
   Use case ends.

**Extensions:**
* 2a. No contact IDs provided.  
    * 1a1. App shows an error message to tell the user that the given name does not exist.  
      Use case ends.
* 2b. Invalid contact ID(s) provided.
    * 2b1. App shows an error message to tell the user that the contact is invalid and ask the user to provide valid contact IDs  
      Use case ends.
* 2c. Duplicate contact IDs provided.
    * 2c1. App handles duplicates internally, ensuring each ID is processed once.
    * 2c2. Logs the message "Successfully deleted [number] contacts."  
      Use case resumes at step 2.

---

**Use case: UC08 - Filter content by type**  
**Actor:** User  
**MSS:**
1. User requests to filter contacts by specifying an event type.
2. App validates the provided event type.
3. App retrieves and returns the list of contacts associated with the specified event type.
4. App logs the message indicating the number of contacts filtered.  
   Use case ends.

**Extensions:**
* 2a. Invalid event type provided.
    * 2a1. App tells the user that the event is invalid and asks the user to provide a valid event type.  
      Use case ends.
* 2b. No contacts associated with the specified event type.
    * 2b1. App returns an empty list.
    * 2b2. Logs the message "Filtered 0 contacts for event type: [eventType]."  
      Use case ends.

---

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2. Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4. System should respond within two seconds.
5. System should be usable by a novice who has not used a command line interface before.
6. Final product should be a result of evolving/enhancing/morphing the given codebase.
7. Should be for a single user i.e. (not a multi-user product).
8. Needs to be developed in a breadth-first incremental manner over the project duration.
9. Should be stored locally and should be in a human editable text file.
10. Should follow the Object-oriented paradigm primarily.
11. Software should work without requiring an installer.
12. Software should not depend on a remote server.
13. The GUI should work well (i.e., should not cause any resolution-related inconveniences to the user) for,
    - standard screen resolutions 1920x1080 and higher, and,
    - for screen scales 100% and 125%.
14. In addition, the GUI should be usable (i.e., all functions can be used even if the user experience is not optimal) for,
    - resolutions 1280x720 and higher, and,
    - for screen scales 150%.
15. JAR / ZIP file should not exceed 100MB.
16. Documents, such as PDF Files, should not exceed 15MB/file.
17. DG and UG should be PDF-friendly. Don't use expandable panels, embedded videos, animated GIFs etc.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
