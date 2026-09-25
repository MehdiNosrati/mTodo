# mTodo

A modern, minimalist, and lightweight Todo app for Android built entirely with **Jetpack Compose** and **Material 3**.

<p align="center">
  <img alt="GitHub Workflow Status" src="https://img.shields.io/github/actions/workflow/status/MehdiNosrati/mTodo/gradle-wrapper-validation.yml?branch=master&style=for-the-badge">
  <img alt="GitHub top language" src="https://img.shields.io/github/languages/top/MehdiNosrati/mTodo?style=for-the-badge">
  <img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/MehdiNosrati/mTodo?style=for-the-badge">
  <img alt="Roborazzi Verified" src="https://img.shields.io/badge/Roborazzi-Screenshot%20Tests%20Verified-blue?style=for-the-badge">
</p>

---

## 📸 Screenshots

<p align="center">
  <img src="screens/home.png" width="270" alt="Home - Pinned Tasks, Categories, Voice Input & Multi-Select">
  &nbsp;&nbsp;
  <img src="screens/inline_add.png" width="270" alt="Inline Add Task UX & Quick Input">
  &nbsp;&nbsp;
  <img src="screens/todo_detail.png" width="270" alt="Task Details - Subtasks, Recurrence, Category & Focus Timer">
</p>

<p align="center">
  <img src="screens/done.png" width="270" alt="Done - Completed Tasks & Swipe Actions">
  &nbsp;&nbsp;
  <img src="screens/insights.png" width="270" alt="Insights - Daily Goal Ring, Productivity Trends & Priority Stats">
  &nbsp;&nbsp;
  <img src="screens/settings.png" width="270" alt="Settings - Notification Status, Recycle Bin & Backup/Restore">
</p>

---

## ✨ Features

- **Modern Jetpack Compose UI**: 100% declarative UI built with Material 3, dynamic cards, fluid spring physics, and soft indigo/violet accents.
- **Interactive Home Screen Widget**: Check off tasks directly from your home launcher via broadcast triggers without opening the app, plus a quick "Add" shortcut.
- **Recurring / Repeating Tasks**: Automatically schedule repeating tasks (**Daily**, **Weekdays**, **Weekly**, **Monthly**) upon completion with auto-resetting checklist subtasks.
- **Voice-to-Text Task Creation**: Dedicated speech recognizer microphone button in the top app bar for effortless hands-free task creation.
- **Category System**: Organize tasks by category (**Personal**, **Work**, **Shopping**, **Health**, **Finance**, **Ideas**) with horizontal filter chips and color-coded badges.
- **Pinned Tasks**: Anchor urgent or high-priority tasks to the top of your list with a dedicated pinned badge.
- **Batch Multi-Select Mode**: Long-press any task to enter contextual selection mode to batch complete or batch delete multiple items in one tap.
- **30-Day Recycle Bin & Trash**: Accidental deletions are protected with soft-deletes; view, restore individual items, or purge permanently, with automated cleanup after 30 days.
- **Daily Goal Tracker & Streak Ring**: Set custom daily completion targets (1–10 tasks/day) and track your daily progress ring and consistency streak in Insights.
- **Focus Mode (Pomodoro Timer)**: 25-minute distraction-free focus timer directly from the task detail screen to help power through demanding tasks.
- **Actionable Notification Reminders**: Android 13+ runtime permission guard and exact alarm scheduling (`USE_EXACT_ALARM`) with interactive notification action buttons: `[✓ Done]`, `[⏰ +15m]`, `[⏰ +1h]`.
- **Inline Task Creation**: Fast and non-intrusive inline drafting card at the top of the list — tap Enter or click away to save immediately.
- **Subtasks & Checklist System**: Break down complex tasks into subtasks with toggle checkboxes, interactive progress bar, and badge indicators on list rows.
- **Tag & Priority Filter Carousel + Search**: Instantly filter tasks by category, tag, priority (High, Medium, Low), or overdue status with horizontal filter chips and real-time search.
- **Swipe-to-Action with Undo Snackbar**: Smooth gestures on active tasks (swipe right to complete, swipe left to delete) and done tasks (swipe right to restore, swipe left to delete) with an instant Undo snackbar.
- **Flexible Task Sorting**: Sort tasks on the fly or choose a global default in settings: Creation Date, Priority (High &rarr; Low), Due Date, or Alphabetical.
- **Data Backup & Restore**: Full local JSON backup and restore via Android's Storage Access Framework (SAF) with merge or overwrite options.
- **Smart Date Segmentation**: Automatically groups active tasks into **Today**, **Yesterday**, and **Older** sections with sticky headers.
- **Task Statistics & Completion Insights**: Real-time productivity metrics, completion rates, daily consistency streaks, 7-day visual activity bar chart, and priority distribution breakdown.
- **Edge-to-Edge Dark & Light Mode**: Seamless theme switching with system-default and user-selected appearance options, with clear status bar contrast in both modes.
- **Multilingual Support**: Fully localized in English (`en`) and Persian (`fa`).
- **Offline-First Persistence**: Local Room SQLite storage (v5 migration) with reactive Kotlin `Flow` observables and zero cloud tracking.
- **Target SDK 36**: Optimized for Android 16 with security and performance improvements.
- **Minimal Download Footprint**: Zero external bloatware libraries, strictly under 6 MB APK size.

---

## 🛠 Tech Stack & Architecture

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) + [Material 3](https://m3.material.io/)
- **Architecture**: MVVM / Clean Architecture (UI &rarr; ViewModel &rarr; Repository &rarr; Room DAO)
- **Asynchronous / Reactive**: Kotlin Coroutines & `Flow` / `LiveData`
- **Dependency Injection**: [Koin](https://insert-koin.io/)
- **Local Storage**: [Room Database](https://developer.android.com/training/data-storage/room) (SQLite v5 with automated migration)
- **Screenshot & Regression Testing**: [Roborazzi](https://github.com/takahirom/roborazzi) + Robolectric Native Graphics
- **Build System**: Gradle 8.9, Android Gradle Plugin 8.7.3, Kotlin 2.0.21, and Gradle Version Catalog (`libs.versions.toml`)

---

## 🧪 Running Tests

Ensure your `JAVA_HOME` points to Java 17+:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
```

### Run Unit & Integration Tests:
```bash
./gradlew testDebugUnitTest
```

### Verify Roborazzi Screenshot Goldens:
```bash
./gradlew verifyRoborazziDebug
```

### Update Baseline Screenshot Goldens:
```bash
./gradlew recordRoborazziDebug
```

---

## 🗺 Roadmap

- [x] Modernize UI with Jetpack Compose & Material 3
- [x] Basic CRUD on tasks
- [x] Inline task drafting UX
- [x] Priority system (High / Med / Low / None)
- [x] Due dates, times, and tags metadata
- [x] Dedicated task detail and edit screen
- [x] Time period segmentation (Today / Yesterday / Older)
- [x] Task statistics & completion insights + priority distribution
- [x] Dark & Light themes with high-contrast status bars
- [x] Multilingual support: `en`, `fa`
- [x] Tag & Priority filter carousel + real-time search
- [x] Swipe-to-Action with Undo snackbars
- [x] Sorting (Date, Priority, Due Date, Alphabetical)
- [x] Subtasks / checklist support with progress badges
- [x] Due date reminders & system notifications
- [x] Native JSON backup & restore
- [x] Interactive home screen app widget (toggle done directly from launcher)
- [x] Recurring / repeating tasks (Daily, Weekdays, Weekly, Monthly)
- [x] Voice input via SpeechRecognizer
- [x] Category system (Personal, Work, Shopping, Health, Finance, Ideas)
- [x] Pinned tasks support
- [x] Batch multi-select actions (bulk complete/delete)
- [x] 30-day Recycle Bin / Trash with restore & auto-purge
- [x] Daily completion goal ring & streak tracking
- [x] Pomodoro focus timer (25 min)
- [x] Actionable notification buttons (`Done`, `+15m`, `+1h`) & Android 13+ runtime permissions
- [x] Automated unit, integration, and Room database test coverage
- [x] Roborazzi screenshot testing suite
- [x] Target SDK 36 compliance
