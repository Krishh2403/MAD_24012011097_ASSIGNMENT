# FarmLog — Product Requirements Document

**Course:** 2CEIT5PE18 — Mobile Application Development (Semester V, CE/IT/CE-AI)  
**Faculty:** Prof. Hiten M Sadani, Ganpat University  
**Author:** Krish Patel  
**Presentation deadline:** On or before 21/09/2026  
**Submission deadline:** 21/09/2026, 8:00 AM (Google Form)

---

## 1. Overview

**One-line pitch:** A digital farm activity journal that lets farmers securely log, organize, and analyze crop activities such as sowing, harvesting, irrigation, and fertilizing, from their own verified account.

**Problem:** Farmers typically track field activity informally — on paper, in memory, or through scattered notes. These methods are hard to search, provide no way to see patterns across a season, and break down when more than one person uses the same device.

**Solution:** FarmLog provides a secure, per-user Android application where each farmer signs up with a verified email address, then records farm activities into a private, on-device log. The app surfaces quick-glance dashboard stats, category filters, and a Farm Insights screen with activity distribution and search.

---

## 2. Goals

- Provide secure account creation and login with verified email addresses
- Let users log a farm activity (crop, activity type, date, notes) in a few taps
- Keep each user's activity history private and separate on a shared device
- Show dashboard statistics: total logs and count of distinct active crops
- Provide category filters (Sowing / Harvesting / Irrigation / Fertilizer)
- Provide a Farm Insights screen with activity-type distribution and search
- Use Firebase Authentication with mandatory email verification for account security
- Deliver a functional Android application for the course

---

## 3. Non-Goals (out of scope)

- Cloud sync or backup of farm logs (logs are stored locally only, per device)
- Editing an existing farm log entry (only add/delete are supported)
- "Forgot password" / password reset flow
- Social login (Google/Apple/etc.)
- Multi-device data synchronization
- Push notifications
- Weather data integration, crop image attachments, irrigation/fertilizer reminders
- In-app messaging/chat
- Payments or financial transactions
- GPS/location-based features
- iOS or web application

---

## 4. Target Users

- Individual farmers logging their own day-to-day field activity
- Farm managers or agriculture students who want a simple, structured activity log
- Multiple farmers sharing one physical device, each keeping a private activity history

Every user plays the same single role — there is no separate "requester/responder" distinction; each account only ever manages its own farm activities.

---

## 5. Key Decisions Log

| Area | Decision | Rationale |
|---|---|---|
| Authentication | Firebase Email/Password with mandatory email verification | Ensures every account is tied to a real, reachable email address before it can be used |
| Data storage | Room (local SQLite database) | Keeps activity logging fully offline-capable; no cloud database needed for this scope |
| User identity for data | The authenticated user's email (lower-cased) used as a partition key | Simple per-user data isolation without maintaining a separate user-profile table |
| Network dependency | Only Firebase Authentication requires connectivity | Farm log CRUD works fully offline once signed in |
| Activity types | Fixed set: Sowing, Harvesting, Irrigation, Fertilizer | Covers the core activities in scope and keeps selection UI (chips) simple |
| Session handling | Firebase `currentUser` + `isEmailVerified` check on launch | Auto-resumes a verified session; blocks access for unverified accounts |
| Edit scope | Add and delete only, no edit | Keeps CRUD scope minimal and focused for the assignment |
| UI | Native Android Views (XML + ViewBinding) | Matches course requirements for native Android development |
| Security | Firebase manages all credentials; app never stores a password | Delegates credential security entirely to Firebase Authentication |

---

## 6. Tech Stack

- **Language:** Kotlin
- **UI:** Android Views (XML), ViewBinding
- **Layouts:** ConstraintLayout
- **Lists:** RecyclerView
- **UI Components:** Material Components (Material 3)
- **Authentication:** Firebase Authentication (Email/Password + email verification)
- **Local Database:** Room (SQLite)
- **Architecture:** MVVM (ViewModel + LiveData)
- **Async Operations:** Kotlin Coroutines
- **IDE:** Android Studio
- **Version Control:** Git & GitHub

---

## 7. Data Model

### Firebase Authentication (managed by Firebase, not app-defined)

| Field | Type | Description |
|---|---|---|
| uid | String | Firebase-generated unique user ID |
| email | String | User's login email address |
| displayName | String | User's name, set at sign-up |
| isEmailVerified | Boolean | Whether the user has confirmed their email link |

### `farm_logs` table (Room, on-device)

| Field | Type | Description |
|---|---|---|
| id | Int | Auto-generated primary key |
| owner_id | String | Lower-cased email of the record's owner |
| cropName | String | Crop name, e.g. Wheat, Rice, Tomato |
| activityType | String | Sowing / Harvesting / Irrigation / Fertilizer |
| date | String | Activity date (`dd MMM yyyy`) |
| notes | String | Optional free-text notes |

---

## 8. Screens

1. **Login / Sign Up** — name, email, and password entry; switches into a verification-pending state after account creation
2. **Verification Pending** — status message, resend-verification-email action, use-a-different-email action
3. **Dashboard** — welcome banner, stat tiles (total logs, active crops), category filter chips, activity list, add-activity FAB, logout menu
4. **Add Farm Activity** — crop name, activity-type chips, date picker, notes, save
5. **Farm Insights** — per-category count and progress bars, search across crop name / activity type / notes
6. **Log Detail** (dialog) — full note view with a delete option

---

## 9. Core Workflow

```text
User
  ↓
Sign Up (name, email, password)
  ↓
Firebase Authentication
  ↓
Verification Email Sent
  ↓
User Verifies Email
  ↓
Sign-In Confirmed
  ↓
Dashboard
  ↓
Add Farm Activity
  ↓
Room Database (local, scoped to owner email)
  ↓
Dashboard & Farm Insights
  ↓
Updated Stats and Records
```

Account lifecycle:

```text
Unverified → Verified
Verified   → Logged In   (auto-resumes on next launch)
Logged In  → Logged Out  (manual)
```

---

## 10. Security & Validation

- Firebase Authentication protects user accounts; the app never stores a password locally
- Sign-up requires a minimum 6-character password and a client-validated email format
- Dashboard access is blocked until `isEmailVerified` is `true` on the Firebase account
- Local farm logs are queried and inserted using the owner's email, so one user's data never appears under another user's session
- Only an authenticated, verified session can create or view farm logs; unauthenticated access redirects to Login
- Crop name and activity-type fields are validated before save, with inline error messages
- Leaving the verification-pending screen (via the back action) safely signs out instead of leaving an orphaned unverified session
- Logging out clears both the Firebase session and the locally cached session data

---

## 11. Success Criteria

- Users can successfully sign up and verify their email address
- Verified users can log in, and are auto-signed-in on return visits
- Users can add farm activities with crop, activity type, date, and notes
- Dashboard stats (total logs, active crops) update correctly and reactively
- Category filters correctly narrow the visible activity list
- Farm Insights distribution and search work correctly
- Logs can be deleted with a confirmation prompt
- Data is correctly isolated per signed-in user, verified across multiple accounts on one device
- Firebase correctly blocks unverified or invalid sign-in attempts

---

## 12. Future Work

- Cloud backup/sync (e.g. Firestore) for true multi-device access
- Edit existing farm log entries
- "Forgot password" flow
- Crop image attachments
- Weather integration and irrigation/fertilizer reminders
- Push notifications for reminders
- Dark mode
- Export reports as PDF/CSV
- Multi-language support
