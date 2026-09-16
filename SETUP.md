# FarmLog — External Service Setup

You only have to do this once. It takes about 5–10 minutes.

FarmLog depends on one external service:

| Service | Used for | Cost |
|---|---|---|
| **Firebase** | Authentication (email/password + email verification) | Free (Spark plan) |

> Unlike some Firebase-based apps, FarmLog does **not** use Cloud Firestore. Farm activity records are stored entirely on-device in a local Room (SQLite) database, scoped to the signed-in user's email. Firebase is only responsible for the account itself (sign-up, sign-in, email verification).

Work through Part 1, then Part 2 to verify the setup.

---

## Part 1 — Firebase

### 1.1 Create the Firebase project

1. Go to <https://console.firebase.google.com> and sign in with a Google account.
2. Click **Create a project** (or **Add project**).
3. Project name: `FarmLog`.
4. On the Google Analytics step, Analytics can be turned off since FarmLog does not use it.
5. Click **Create project** and wait for the project to finish provisioning.
6. Click **Continue**.

---

### 1.2 Enable Email/Password Authentication

1. In the Firebase Console, open **Build → Authentication**.
2. Click **Get started**.
3. Open the **Sign-in method** tab.
4. Select **Email/Password**.
5. Enable **Email/Password**.
6. Leave **Email link (passwordless sign-in)** disabled — FarmLog doesn't use it.
7. Click **Save**.

FarmLog uses Firebase Authentication for:

- Account creation (sign-up)
- Login
- Email verification (dashboard access is blocked until the address is verified)
- Logout

---

### 1.3 Register the Android app

1. Open **Project settings** using the gear icon.
2. Scroll to **Your apps**.
3. Click the **Android** icon.
4. Enter the Android package name:

   ```text
   com.example.mad_24012011097_assignment
   ```

   This must exactly match the `applicationId` in `app/build.gradle.kts`.

5. App nickname: `FarmLog` (optional).
6. SHA-1 can be left blank — the current version uses Email/Password authentication only.
7. Click **Register app**.

---

### 1.4 Download `google-services.json`

1. Click **Download google-services.json**.
2. Place the downloaded file inside the `app/` folder.

   The final structure should be:

   ```text
   MAD_24012011097_Assignment/
   └── app/
       └── google-services.json
   ```

3. Open Android Studio.
4. Sync the project with Gradle.

> ⚠️ Check whether `google-services.json` should stay out of version control for your setup — if so, add `app/google-services.json` to `.gitignore` before committing. (This client key isn't a secret by itself, since Firebase access is controlled by security rules/restrictions rather than key secrecy, but keeping it out of a public repo is still good hygiene if you'd rather not publish it.)

---

## Part 2 — Verify It Worked

1. Open the FarmLog project in Android Studio.
2. Click **File → Sync Project with Gradle Files**.
3. Run the application on an Android device or emulator with internet access (and Google Play Services).
4. The app should open on the **Login / Sign Up** screen.
5. Create a test account using **Sign Up** (name, a real email you can access, password 6+ characters).
6. Check that inbox for the Firebase verification email and tap the link.
7. Back in the app, tap **"I've verified — Continue"**.
8. Check **Firebase Console → Authentication** to verify the account was created and shows as verified.
9. Add a farm activity from the Dashboard and confirm it appears there and in Farm Insights.
10. Log out, sign up a second test account, and confirm its dashboard starts empty (per-user data isolation).

The basic workflow should be:

```text
Sign Up
   ↓
Firebase Authentication
   ↓
Verification Email Sent
   ↓
Email Verified
   ↓
Dashboard
   ↓
Add Farm Activity
   ↓
Room Database (local, scoped to owner email)
   ↓
Dashboard & Farm Insights Updated
```

---

## Troubleshooting

**"File google-services.json is missing"**

Make sure the file is located exactly at:

```text
app/google-services.json
```

It should not be placed in the project root.

**"An internal error has occurred [CONFIGURATION_NOT_FOUND]" when signing up**

Check that Email/Password authentication is enabled under:

Firebase Console → Authentication → Sign-in method

**"Still not verified" after clicking the email link**

- Make sure you clicked the link from the same account's inbox (check spam/promotions too).
- Tap **Resend verification email** in the app and try the newest link.
- Tap **"I've verified — Continue"** again — the app re-checks Firebase's live status each time.

**Build fails with `kaptDebugAndroidTestKotlin` / `kaptDebugUnitTestKotlin` and `error.NonExistentClass`**

This is a stale kapt stub cache, not a real code error. Run a clean build once:

```bash
./gradlew.bat clean assembleDebug
```

**"Incorrect password for this email" / "No account found for this email"**

- Email matching is case-insensitive (the app lower-cases it automatically) — double-check the password instead.
- If you've never signed up with that address on this Firebase project, use **Sign Up** first, not sign-in.

**"Password is too weak"**

Firebase requires a minimum of 6 characters.

---

## Local Data Storage (Room)

FarmLog does not use Cloud Firestore. All farm activity records live in a single local Room table:

```text
farm_logs/
    id                (auto-generated)
    owner_id          (signed-in user's email, lower-cased)
    cropName
    activityType      (Sowing | Harvesting | Irrigation | Fertilizer)
    date
    notes
```

Data isolation between users is enforced at the query level — `FarmDao.getLogsForOwner(ownerId)` only ever returns rows matching the currently signed-in user's email — rather than by server-side security rules, since the data never leaves the device.

---

## Project Files Reference

| File | Responsibility |
|---|---|
| `LoginActivity.kt` | Firebase sign-up/sign-in, email verification gate |
| `MainActivity.kt` | Dashboard, stats, filters, logout |
| `AddLogActivity.kt` | Add a new farm activity |
| `SummaryActivity.kt` | Farm Insights: distribution + search |
| `FarmLog.kt` | Room entity for a farm activity record |
| `FarmDao.kt` | Room queries (per-owner fetch, insert, delete) |
| `FarmDatabase.kt` | Room database configuration + migrations |
| `FarmViewModel.kt` | Bridges Room data to the UI, scoped per signed-in owner |
| `FarmLogAdapter.kt` | RecyclerView adapter for activity lists |

For the full requirements and feature list, see [PRD.md](PRD.md) and [SRS.md](SRS.md).
