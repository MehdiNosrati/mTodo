## What's Changed in mTodo v3.0.0

### 🚀 100 Functional Offline Utilities Super App
mTodo has evolved into an all-in-one **Utilities Super App** with **100 functional, interactive tools** organized across 5 core domains:
- **Math & Finance (Tools 1–20)**: Everyday Calculator, Percentage Calculator, Tip & Bill Splitter, Discount & Sales Tax, Loan EMI, Compound Interest, Fuel Cost Planner, Unit Converters (Length, Weight, Temp, Area, Volume, Speed, Storage), Base Converter, Roman Numerals, Prime Factorizer & GCD/LCM, Quadratic Solver, Aspect Ratio Scaler, Profit Margin & Markup.
- **Text & Code Utilities (Tools 21–40)**: Text Statistics Analyzer, Case Converter, Base64 Encoder/Decoder, URL Encoder/Decoder, Cryptographic Hashes (MD5, SHA-1, SHA-256, SHA-512), Morse Code, ROT13/Caesar Cipher, Text Reverser, Binary/Hex Encoder, Lorem Ipsum Generator, Line Deduplicator & Sorter, URL Slugifier, Character Frequency, Palindrome Checker, UUID v4 Generator, JSON Formatter/Minifier, Password Entropy Meter, Whitespace Cleaner, Markdown Preview.
- **Time, Calendar & Productivity (Tools 41–60)**: Millisecond Stopwatch, Countdown Multi-Timer, Interval HIIT Timer, Date Difference, Age & Birthday Countdown, Unix Epoch Converter, World Clock Explorer, Event Countdown, Leap Year Checker, Work Hours & Overtime, Tap BPM Metronome, Sleep Cycle Calculator, Pomodoro Focus Manager, Week/Day of Year, Meeting Overlap Finder, Daylight & Solar Calculator, Moon Phase Calculator, Julian Date, Year/Day Progress, Running Pace Calculator.
- **Hardware & System Diagnostics (Tools 61–80)**: Screen Color Lantern, Flashlight LED Torch, Battery Health & Telemetry, Display Metrics Inspector, RAM & Memory Gauge, Device & OS Specs, Haptic Vibration Studio, Dead Pixel Screen Tester, Device Uptime, Storage Space Inspector, Display Refresh Rate, Network Connection Monitor, 440Hz Audio Pitch Pipe, Thermal Status, Clipboard Inspector, Screen Caliper, Bubble Level, Hardware Sensors Inventory, Audio Volume Streams, Theme Palette Inspector.
- **Everyday, Health & Decision Tools (Tools 81–100)**: Coin Flipper, Polyhedral Dice Roller, Random Number Generator, Secure Password Generator, QR Code Matrix Generator, Barcode Code-128 Generator, Decision Wheel Picker, Rock Paper Scissors, Tally Counter, BMI Calculator, BMR & Calorie Calculator, Daily Water Intake, Box Breathing Relaxation, WCAG Contrast Checker, Quick Scratchpad, Atbash Cipher, Playing Card Deck & Hand Draw, Magic 8-Ball, Habit Streak Tracker, Name Compatibility Matcher.

### 🛠 Sleek Vertical Edge-Anchored Toolbox Tab
- Added a vertical rectangular tab (`Column` layout, ~38dp wide by ~125dp tall) anchored flush to the right edge of the screen (`Alignment.CenterEnd`).
- Provides 1-tap navigation directly into the Super Toolbox without obscuring list content or action buttons.

### 🎯 Prominent Priority Filters Intact
- Core priority filters (`🔴 High`, `🟠 Med`, `🟢 Low`, `⚠️ Overdue`) remain prominent and distinct across task views.
- Removed cluttered category filters and sections for a cleaner, distraction-free task management experience.

### 📊 Clean, Professional Insights
- Completely emoji-free statistics and insights dashboard styled with elegant Material 3 vector iconography and clear typography.

### 🛡 Stability & Hardware Hardening
- Added defensive exception handling across all hardware sensors, audio synthesis, camera flashlight, and system broadcast receivers.
- Zero network required: 100% offline-first, private, and secure.

### 📦 Store Readiness
- Release bundle (`app-release.aab`) built with R8 minification, startup baseline profiles, and native symbol tables.
- Target SDK 36 (Android 15) and min SDK 24.
