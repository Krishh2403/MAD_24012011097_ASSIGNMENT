

<h1 align="center">🌿 FarmLog</h1>

<p align="center">
  <strong>Smart Farm Activity Manager</strong>
</p>

<p align="center">
  A modern Android application for recording, organising, and analysing daily farm activities.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Database-Room-176B3A?style=for-the-badge&logo=sqlite&logoColor=white" alt="Room Database">
  <img src="https://img.shields.io/badge/Architecture-MVVM-F2A33A?style=for-the-badge" alt="MVVM">
</p>

---

## ✨ About FarmLog

FarmLog is an Android application developed for a **Mobile Application Development** assignment. It helps farmers maintain a digital journal for crop activities such as sowing, harvesting, irrigation, and fertiliser application.

The application provides secure Firebase-authenticated user accounts, email verification, per-user activity management, filters, search, Farm Insights, and a modern agriculture-inspired user interface.


---

## 🚀 Key Features

| Feature | Description |
|---|---|
| 🔐 Firebase Authentication | Sign up / sign in with name, email, and password via Firebase |
| ✉️ Email Verification | Dashboard access requires a verified email address, with resend and "use a different email" options |
| 👤 Multi-User Profiles | Each verified account has separate farm records |
| 💾 Session Persistence | Verified, logged-in users open directly to the dashboard |
| 🚪 Logout | Signs out of Firebase, clears the local session, and returns to Login |
| ➕ Add Activity | Save crop name, date, activity type, and notes |
| 🌾 Activity Types | Sowing, Harvesting, Irrigation, and Fertilizer |
| 🗂️ Smart Filters | Filter farm records by activity category |
| 📊 Farm Insights | View activity distribution and progress bars |
| 🔎 Search Records | Search using crop name, activity type, or notes |
| 🧹 Delete Activities | Remove unwanted farm activity records |
| 🎨 Premium UI | Green farming theme, animations, cards, and Material Design |
| 📱 Modern Layout | Handles device notch, status bar, and navigation bar safely |

---

## 📱 Application Flow

```text
Sign Up (name, email, password)
     ↓
Firebase Authentication
     ↓
Verification Email Sent
     ↓
User Verifies Email → Taps "I've verified — Continue"
     ↓
FarmLog Dashboard
     ↓
Add Farm Activity
     ↓
Save Activity in Room Database (scoped to owner email)
     ↓
Dashboard and Farm Insights Update Automatically
```

### Multi-User Profile Flow

```text
Sign up & verify farmer1@gmail.com
           ↓
Shows only farmer1@gmail.com activities

Logout
           ↓
Sign up & verify farmer2@gmail.com
           ↓
Shows only farmer2@gmail.com activities

Sign in again with farmer1@gmail.com
           ↓
Earlier saved farm activities load automatically
```

> Accounts are managed by Firebase Authentication; each farm record is saved with its owner's verified email, so users only ever see their own activities, even on a shared device.

---

## 📸 Application Screens

<details open>
<summary><strong>View FarmLog screenshots</strong></summary>

<br>

<table>
  <tr>
    <td align="center">
      <a href="SCREEN/Login.png">
        <img src="SCREEN/Login.png" width="190" alt="FarmLog Login Screen">
      </a>
      <br>
      <sub><b>Login Screen</b></sub>
    </td>
    <td align="center">
      <a href="SCREEN/SS1.png">
        <img src="SCREEN/SS1.png" width="190" alt="FarmLog Dashboard">
      </a>
      <br>
      <sub><b>Dashboard</b></sub>
    </td>
    <td align="center">
      <a href="SCREEN/SS2.png">
        <img src="SCREEN/SS2.png" width="190" alt="Add Farm Activity">
      </a>
      <br>
      <sub><b>Add Activity</b></sub>
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="SCREEN/SS3.png">
        <img src="SCREEN/SS3.png" width="190" alt="Farm Activity Filters">
      </a>
      <br>
      <sub><b>Select Crop</b></sub>
    </td>
    <td align="center">
      <a href="SCREEN/SS4.png">
        <img src="SCREEN/SS4.png" width="190" alt="Farm Insights">
      </a>
      <br>
      <sub><b>Activity Filters</b></sub>
    </td>
    <td align="center">
      <a href="SCREEN/SS5.png">
        <img src="SCREEN/SS5.png" width="190" alt="Farm Search and History">
      </a>
      <br>
      <sub><b>Farm Insights</b></sub>
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="SCREEN/SS6.png">
        <img src="SCREEN/SS6.png" width="190" alt="FarmLog Additional Screen">
      </a>
      <br>
      <sub><b>Search and Find</b></sub>
    </td>
  </tr>
</table>

</details>

> Click any screenshot to view it in full size.

---

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Kotlin | Main Android programming language |
| XML | User interface layouts |
| Android Studio | Development environment |
| Firebase Authentication | Email/password sign-up, sign-in, and email verification |
| Room Database | Local farm activity storage |
| SQLite | Database engine used by Room |
| MVVM | Application architecture pattern |
| ViewModel | Manages UI-related data |
| LiveData | Updates the UI when data changes |
| RecyclerView | Displays farm activity records |
| Material Components | Modern Android interface components |
| SharedPreferences | Caches the signed-in user's name/email for local queries |

---

## 🏗️ Application Architecture

FarmLog follows the **MVVM (Model–View–ViewModel)** architecture.

```text
Activities and XML Layouts
Login | Dashboard | Add Activity | Insights
        ↓                           ↓
Firebase Authentication      FarmViewModel
                                    ↓
                                FarmDao
                                    ↓
                             Room Database
```

---

## 📂 Important Project Files

| File | Responsibility |
|---|---|
| `FarmLog.kt` | Defines the Room entity for a farm activity |
| `FarmDao.kt` | Contains Room database queries |
| `FarmDatabase.kt` | Configures the Room database |
| `FarmViewModel.kt` | Connects database data with the UI |
| `FarmLogAdapter.kt` | Displays activity records in RecyclerViews |
| `LoginActivity.kt` | Handles Firebase sign-up/sign-in, email verification, and session persistence |
| `MainActivity.kt` | Displays the FarmLog dashboard |
| `AddLogActivity.kt` | Allows users to add farm activities |
| `SummaryActivity.kt` | Shows Farm Insights and statistics |

---

## ▶️ How to Run the Application

1. Open **Android Studio**.
2. Click **Open**.
3. Select the `MAD_24012011097_ASSIGNMENT` project folder.
4. Set up a Firebase project and drop your `google-services.json` into `app/` — full step-by-step instructions are in [SETUP.md](SETUP.md).
5. Wait for **Gradle Sync** to complete.
6. Start an Android Emulator (with Google Play Services) or connect an Android phone.
7. Click the green **Run ▶** button.
8. Sign up with your name, a real email address, and a password (6+ characters).
9. Open the verification email that Firebase sends, tap the link, then tap **"I've verified — Continue"** in the app.
10. Add farm activities and open Farm Insights.

---

## 🧪 Testing Multi-User Profiles

1. Sign up and verify:

   ```text
   farmer1@example.com
   ```

2. Add one or more farm activities.

3. Logout from the application (toolbar menu → **Log out**).

4. Sign up and verify a second address:

   ```text
   farmer2@example.com
   ```

5. The dashboard is empty because this is a new account with no saved records yet.

6. Logout and sign in again with:

   ```text
   farmer1@example.com
   ```

7. The earlier farm records load automatically.

> Each address needs a real inbox you can access to complete verification — use disposable/alias addresses if you don't want to use your primary email for testing.

---

## 🎯 Future Enhancements

- Firebase Firestore cloud backup / multi-device sync
- Edit existing farm records
- "Forgot password" flow
- Crop image upload
- Weather information integration
- Irrigation and fertiliser reminders
- Dark mode
- Export reports as PDF or CSV
- Multi-language support

---

## 🎓 Project Information

| Item | Details |
|---|---|
| Project Name | FarmLog – Smart Farm Activity Manager |
| Platform | Android |
| Programming Language | Kotlin |
| Authentication | Firebase Authentication (Email/Password + verification) |
| Database | Room Database (local, per-user) |
| Architecture | MVVM |
| Purpose | Mobile Application Development Assignment |

For the full requirement set and setup instructions, see [PRD.md](PRD.md), [SRS.md](SRS.md), and [SETUP.md](SETUP.md).

---

## 👨‍💻 Developer

**Name:** Krish Patel  
**Roll Number:** 24012011097  
**Course:** Mobile Application Development  

---

<p align="center">
  Made with 🌿 for smarter farm activity management.
</p>
