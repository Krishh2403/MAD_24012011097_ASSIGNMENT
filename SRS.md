# Software Requirements Specification (SRS)

## FarmLog – Smart Farm Activity Manager

| | |
|---|---|
| **Project** | FarmLog |
| **Prepared for** | Mobile Application Development Assignment |
| **Developer** | Krish Patel (Roll No. 24012011097) |
| **Document Version** | 1.0 |

---

## 1. Introduction

### 1.1 Purpose
This document specifies the functional and non-functional requirements for FarmLog, an Android application for recording and analyzing farm activities. It is intended for the developer and evaluator as a reference for what the system is required to do.

### 1.2 Scope
FarmLog is a single-module native Android application (Kotlin) that:
- Authenticates users via Firebase Authentication (email/password with mandatory email verification).
- Stores each user's farm activity records locally on-device using Room (SQLite), scoped per user.
- Presents a dashboard, an "add activity" form, and an insights/search screen.

### 1.3 Definitions, Acronyms, Abbreviations

| Term | Definition |
|---|---|
| MVVM | Model–View–ViewModel architectural pattern |
| DAO | Data Access Object (Room's interface for SQL operations) |
| LiveData | Android's lifecycle-aware observable data holder |
| Owner ID | The email address used as the per-user partition key for farm logs |
| SDK | Software Development Kit |

### 1.4 References
- Android developer documentation (developer.android.com)
- Firebase Authentication documentation (firebase.google.com/docs/auth)
- Room persistence library documentation

### 1.5 Overview
Section 2 describes the product at a high level. Section 3 lists detailed functional and non-functional requirements. Section 4 describes the data model and system architecture.

---

## 2. Overall Description

### 2.1 Product Perspective
FarmLog is a standalone mobile application, not part of a larger system. It integrates one external service — **Firebase Authentication** — for identity management; all other data (the farm logs themselves) is stored locally via Room and never leaves the device.

### 2.2 Product Functions (Summary)
- Account creation and sign-in with email verification.
- Session persistence and logout.
- Create and delete farm activity records.
- Filter and search farm activity records.
- View aggregate statistics and per-category distribution.

### 2.3 User Characteristics
End users are assumed to have basic smartphone literacy and access to a working email inbox (required to complete the verification step) but no technical background.

### 2.4 Constraints
- Minimum Android version: API 24 (Android 7.0 Nougat).
- Requires network connectivity for all authentication operations (sign up, sign in, resend/verify email); farm-log CRUD operations work offline.
- Firebase project must have the **Email/Password** sign-in provider enabled; the app does not implement any other provider.

### 2.5 Assumptions & Dependencies
- The device has Google Play Services available (required by the Firebase Auth SDK).
- The app's `google-services.json` correctly matches the registered package name `com.example.mad_24012011097_assignment`.
- A single Android device is used by one active user session at a time.

---

## 3. Specific Requirements

### 3.1 External Interface Requirements

#### 3.1.1 User Interfaces
- **Login/Sign-up screen** — name, email, password fields; a status message area; a "resend verification email" and "use a different email" action, shown conditionally.
- **Dashboard screen** — toolbar with a logout menu, welcome banner, stat tiles (total logs, active crops), category filter chips, a scrollable list of logs, and a floating action button to add a new log.
- **Add Activity screen** — crop name input (with suggestions), activity-type chip group, date picker field, notes field, save button.
- **Farm Insights screen** — search field, per-category counts with progress bars, and a filtered/searchable list of logs.

#### 3.1.2 Hardware Interfaces
None beyond standard Android touchscreen input. No camera, sensor, or peripheral access is required.

#### 3.1.3 Software Interfaces
- **Firebase Authentication SDK** (`com.google.firebase:firebase-auth`, via `firebase-bom:33.7.0`) — account creation, sign-in, email verification, session management.
- **Room** (`androidx.room`, v2.8.4) — local persistence for farm activity records.
- **Android Jetpack**: `ViewModel`, `LiveData`, `ViewBinding`, `ConstraintLayout`, Material Components.

#### 3.1.4 Communications Interfaces
HTTPS calls from the Firebase Auth SDK to Google's Identity Platform backend. No other network communication occurs in the app.

---

### 3.2 Functional Requirements

#### Authentication (FR-1.x)
- **FR-1.1** The system shall allow a new user to create an account with name, email, and password (minimum 6 characters).
- **FR-1.2** The system shall reject email addresses that do not match a valid email format (checked client-side) before attempting account creation.
- **FR-1.3** On successful account creation, the system shall send a verification email to the provided address and shall not grant dashboard access until the address is verified.
- **FR-1.4** The system shall provide a "resend verification email" action.
- **FR-1.5** The system shall provide a "use a different email" action that signs out the pending account and returns the user to the entry form with the email/password fields cleared.
- **FR-1.6** If a returning user enters credentials matching an existing, verified account, the system shall sign them in and open the dashboard directly.
- **FR-1.7** If a returning user's account exists but is not yet verified, the system shall re-send a verification email and show the pending-verification screen.
- **FR-1.8** If an already-authenticated, verified session exists at app launch, the system shall skip the login form and open the dashboard automatically.
- **FR-1.9** The system shall allow the user to log out, which clears both the Firebase session and the locally cached session data.
- **FR-1.10** The Android system back button/gesture, while on the pending-verification screen, shall return the user to the entry form instead of closing the app.

#### Farm Activity Management (FR-2.x)
- **FR-2.1** The system shall allow an authenticated user to create a farm activity record consisting of: crop name, activity type (Sowing, Harvesting, Irrigation, or Fertilizer), date, and optional notes.
- **FR-2.2** The system shall require a non-empty crop name and a selected activity type before saving a record; missing fields shall be flagged inline.
- **FR-2.3** Every saved record shall be tagged with the currently authenticated user's email as its owner.
- **FR-2.4** The system shall display only the records owned by the currently authenticated user.
- **FR-2.5** The system shall allow the user to delete a record, after a confirmation prompt, from either the dashboard or the insights screen.
- **FR-2.6** The system shall default the date field to the current date and allow selection of any date via a date picker.

#### Dashboard & Filtering (FR-3.x)
- **FR-3.1** The dashboard shall display the total number of saved records for the current user.
- **FR-3.2** The dashboard shall display the number of distinct crop names among the current user's records.
- **FR-3.3** The dashboard shall provide filter chips (All, Sowing, Harvesting, Irrigation, Fertilizer) that restrict the visible list to the selected category.
- **FR-3.4** The dashboard shall show an empty-state message when the filtered list has no records.

#### Insights & Search (FR-4.x)
- **FR-4.1** The insights screen shall display, for each activity type, the count and the percentage of the user's total records.
- **FR-4.2** The insights screen shall provide a text search that filters records by crop name, activity type, or notes, matched case-insensitively.

---

### 3.3 Non-Functional Requirements

| ID | Category | Requirement |
|---|---|---|
| NFR-1 | Security | The app shall never persist a user's password locally; all credential handling is delegated to Firebase Authentication. |
| NFR-2 | Security | Access to the dashboard shall require a verified email address (`isEmailVerified == true`). |
| NFR-3 | Data isolation | Farm logs shall be queried and inserted using the owner's email as a partition key (`owner_id`), preventing one user from seeing another's records on a shared device. |
| NFR-4 | Performance | The dashboard and insights lists shall update reactively (via `LiveData`) within one UI frame of an underlying data change, for typical data volumes. |
| NFR-5 | Reliability | Farm log data shall persist across app restarts and device reboots via Room's on-disk SQLite storage. |
| NFR-6 | Availability | All farm-log CRUD operations shall function without network connectivity. |
| NFR-7 | Compatibility | The app shall run on Android API 24 through API 35. |
| NFR-8 | Usability | Form validation errors shall be shown inline, next to the relevant field, in plain language. |
| NFR-9 | Maintainability | The app shall follow the MVVM pattern, separating UI (Activities/XML), state (ViewModel/LiveData), and persistence (DAO/Room) concerns. |

---

## 4. Data Model & Architecture

### 4.1 Data Model — `farm_logs` table (Room entity: `FarmLog`)

| Column | Type | Notes |
|---|---|---|
| `id` | Int | Primary key, auto-generated |
| `owner_id` | String | Lower-cased email of the record's owner |
| `cropName` | String | e.g. "Wheat", "Tomato" |
| `activityType` | String | One of: Sowing, Harvesting, Irrigation, Fertilizer |
| `date` | String | Formatted as `dd MMM yyyy` |
| `notes` | String | Optional free text |

Schema version 2 (migrated from version 1 via `MIGRATION_1_2`, which added the `owner_id` column).

### 4.2 Authentication Model
Managed entirely by Firebase Authentication; the app does not define its own user table. Locally, the app caches only `user_name` and `user_email` in `SharedPreferences` (`farmlog_preferences`) purely for display and as the Room query key — these are not the source of truth for authentication state, which is always re-derived from `FirebaseAuth.getInstance().currentUser`.

### 4.3 System Architecture

```
 Activities / XML Layouts (View)
 LoginActivity | MainActivity | AddLogActivity | SummaryActivity
                        |
                        v
                  FarmViewModel  (per-owner LiveData via MediatorLiveData)
                        |
                        v
                    FarmDao
                        |
                        v
                 Room Database (SQLite, "farm_log_database")

 LoginActivity  <---->  FirebaseAuth (external: Firebase Authentication)
```

### 4.4 Non-Functional Design Notes
- `FarmViewModel.setCurrentOwner(ownerId)` re-binds the `LiveData` source whenever the active user changes, ensuring the dashboard/insights screens never show stale or cross-user data.
- Insets (status bar / navigation bar / notch) are handled explicitly per screen via `WindowInsetsCompat` listeners rather than relying on default system behavior.

---

## 5. Other Requirements

- **Regulatory/compliance**: None specific; standard Firebase Authentication terms apply to account data handled by Google.
- **Localization**: Not currently supported; UI strings are English-only and largely hardcoded (not extracted to `strings.xml`) in the current version.
