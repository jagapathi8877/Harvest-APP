# Grainly

Grainly is a native offline-first Android application designed specifically for harvesting machine owners in rural India. It serves as a digital replacement for their physical notebooks, making it incredibly simple to record daily work, calculate complex billing variations based on runtime and breaks, and accurately track pending payments.

## 🎯 Core Philosophy
The app is built with a "dead simple" user experience in mind, catering to users with basic digital skills. It prioritizes fast data entry, offline reliability, and clear financial summaries without overwhelming the user with complex analytics or dashboards. 

## ✨ Key Features

### 1. Zero-Friction Setup
* **No Login Required:** The app opens directly to the Home screen for immediate access. No phone numbers, OTPs, or PINs required.
* **First Launch Automation:** Automatically creates a default "Machine" and a "Default Season" (e.g., ₹2600/hr) upon initialization.

### 2. Home Screen (Dashboard)
* **Quick Stats:** Side-by-side view of *Today's Earnings* and *Total Pending Amount*.
* **Daily Summary:** Shows the number of records logged today and total hours operated.
* **Fast Action:** A prominent Saffron-colored "New Record" button for immediate data entry.
* **Today's Log:** A clear, scrollable list of all work completed on the current day.

### 3. Smart Calculator
* **Dedicated Tool:** The second tab offers a standalone calculator for quick, commitment-free estimates.
* **Rapid Input:** Large numeric keypad-style input for custom runtimes (e.g., "HHMM").
* **Instant Calculation:** Displays a beautiful, easy-to-read result card calculating the total amount based on the current season's hourly rate.

### 4. New Record Entry (Core Flow)
* **Smart Form:** Captures Customer Name, Phone, Village (Optional), Date, and Machine.
* **Intuitive Machine Selector:** Quick-tap chip row to easily select which machine was used.
* **Custom Time Pad:** Tapping Start or End time opens a custom-built, large-button numeric keypad with AM/PM toggles, entirely replacing clunky scrolling time pickers.
* **Quick Breaks:** Tap-to-add break buttons (+5, +10, +15 min) to effortlessly subtract machine downtime.
* **Live Calculation Card:** Instantly updates to show total Runtime, applied Rate, and Final Amount as time values are adjusted.
* **Payment Status:** Colored toggle chips for `PAID`, `PARTIAL`, and `UNPAID`. Selecting `PARTIAL` smartly opens an input for the collected amount.

### 5. Records Management & PDF Export
* **Universal Search:** Quickly find previous work by searching Customer Name or Village.
* **Advanced Filters:** Slide-out filter sheet to narrow down records by Village (text search), Machine (chips), Payment Status, and custom Date Ranges.
* **PDF Export:** Generate and share professional PDF reports of work records directly from the app.
* **Detailed View:** Tap any card to view exhaustive details including clear breakdowns of paid vs. pending balances.

### 6. Pending Payments Hub
* **Collection Dashboard:** A dedicated screen strictly for managing debts.
* **Categorized Tabs:** Split into `UNPAID` (zero collected) and `PARTIAL` (some amount pending).
* **Payment Collection:** Tap any pending record to update its payment status, marking full or partial collections.

### 7. Settings & Localization
* **Bilingual Support:** Full interface translations for English and everyday spoken Telugu (తెలుగు).
* **Business Configuration:** Quickly adjust the active Season's hourly rate via a bottom sheet.
* **Machine Management:** Add, edit, or delete harvesting machines from your fleet.

## 🛠️ Technical Stack
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material Design 3)
* **Architecture:** MVVM (Model-View-ViewModel)
* **Local Database:** Room Database for absolute offline-first reliability.
* **Theme & Colors:** Custom deep dark-mode aesthetic with Saffron (`#F5A800`), Deep Violet (`#2C1A4D`), and vibrant state colors (Success Green, Danger Red, Warning Amber).

## 📱 Offline-First Architecture
Grainly operates with Room Database as its single source of truth. Users are never blocked by poor network connectivity.
