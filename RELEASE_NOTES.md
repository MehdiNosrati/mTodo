## What's Changed in mTodo v2.5.0

### 🔔 Notifications & Exact Alarms
- **Android 13+ & 16 Compatibility**: Added runtime permission requester for `POST_NOTIFICATIONS` and compliant exact alarm scheduling via `SCHEDULE_EXACT_ALARM`.
- **Google Play Compliance**: Removed `USE_EXACT_ALARM` to comply with Google Play's policy for task management apps.
- **Actionable Notification Actions**: Added interactive notification buttons in the tray: `[✓ Done]`, `[⏰ +15m]`, and `[⏰ +1h]`.
- **Notification Status Card**: Added a live status banner in Settings showing whether notifications are active or permission is needed.

### ⚡ Productivity & Interaction
- **Interactive Home Screen Widget**: Check off tasks directly from your home screen launcher without opening the app.
- **Recurring / Repeating Tasks**: Automatic recurrence scheduling (`Daily`, `Weekdays`, `Weekly`, `Monthly`) with auto-resetting checklist subtasks.
- **Voice-to-Text Input**: Speech recognition microphone button in the top bar for hands-free task creation.
- **Category System**: Organize tasks by category (`Personal`, `Work`, `Shopping`, `Health`, `Finance`, `Ideas`) with filter chips and badges.
- **Pinned Tasks**: Anchor urgent tasks to the top of the list.
- **Batch Multi-Select Mode**: Long-press any task to enter contextual selection mode to bulk complete or delete tasks in one tap.
- **30-Day Recycle Bin**: Non-destructive soft deletion with individual restore actions and automated 30-day purge.
- **Daily Goal Tracker & Streak Ring**: Set custom daily completion targets (1–10 tasks/day) and track your streak and progress ring in Insights.
- **Focus Mode (Pomodoro Timer)**: Built-in 25-minute Pomodoro timer in the task detail screen.

### 🛡 Performance & Architecture
- **Room Database v5**: Seamless migration (`MIGRATION_4_5`) with zero data loss.
- **Testing**: 64 automated unit, integration, and Roborazzi screenshot tests passing.
- **Lightweight**: APK footprint strictly under 6 MB with zero external cloud dependencies.
