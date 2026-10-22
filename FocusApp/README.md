# FocusApp

FocusApp is an Android application designed to help eliminate distractions by completely blocking user-selected applications during customizable focus schedules. It includes a built-in Pomodoro timer to help structure your workflow.

## Features

*   **Schedule Management**: Create weekly, recurring time blocks where focus mode should be active. Click on a day to see the schedule, and set up blocks easily.
*   **App Blocking**: Select exactly which apps on your phone you want to block during focus hours. When you try to open a blocked app, the app will instantly show a full-screen overlay, forcing you back to work.
*   **15-Minute Bypass Mechanism**: If there is an emergency and you *really* need to use a blocked app, tap the override button 5 times on the block screen to unlock the app for exactly 15 minutes.
*   **Pomodoro Timer**: A flexible, built-in Pomodoro timer to set customized work and break intervals.
*   **Local Storage**: All of your schedules, app blocklists, and settings are stored entirely on-device using a secure Room database.

## Technical Details

*   **Language & UI**: Kotlin and Jetpack Compose.
*   **Database**: Room (Local SQLite).
*   **App Blocking Logic**: Uses Android's `AccessibilityService` (`TYPE_WINDOW_STATE_CHANGED`) to accurately detect when an app is launched and block it dynamically without heavy battery drain.

## Setup and Run Instructions

### Prerequisites

*   **Java**: OpenJDK 17 is required to build the project.
*   **Android Studio**: Recommended to open, build, and deploy the app.
*   **Android SDK**: Minimum API Level 26, Target API Level 34.

### Building via Command Line

1. Open a terminal and navigate to the project directory:
   ```bash
   cd FocusApp
   ```
2. Build the project using the included Gradle wrapper:
   ```bash
   ./gradlew assembleDebug
   ```
3. To install directly to a connected Android device or running emulator:
   ```bash
   ./gradlew installDebug
   ```

### First-Time Setup on Device

Due to Android security policies regarding app blocking, FocusApp requires explicit user permission to function properly:

1. Launch **FocusApp** on your Android device.
2. A prompt will appear asking you to enable **Accessibility Service**. Click **"Go to Settings"**.
3. In your Android device settings, find **FocusApp** under the "Downloaded Apps" or "Installed Services" accessibility menu, and toggle it to **ON**.
4. Go back to the app. You can now use the "Apps" screen to select which apps you want to block and the "Schedule" screen to set up your focus times!
