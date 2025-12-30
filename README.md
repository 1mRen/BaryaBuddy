# BaryaBuddy 💰

A modern Android budget tracking app that helps you manage your finances with a smart Daily Safe Spend (DSS) feature. Track your income, expenses, and stay on budget with real-time spending insights.

## 📱 Features

- **Daily Safe Spend (DSS) Calculator**: Automatically calculates how much you can safely spend each day based on your monthly budget
- **Income & Expense Tracking**: Record both income and expenses with categories
- **Visual Budget Indicator**: Color-coded pulse circle (Green/Yellow/Red) showing your budget health
- **Transaction History**: View recent transactions with category icons and descriptions
- **Category Management**: Organize expenses by categories (Food, Shopping, Bills, etc.)
- **Onboarding Flow**: Easy setup wizard for first-time users
- **Settings Management**: Update your budget parameters anytime
- **Offline-First**: All data stored locally using Room Database

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room Database
- **Navigation**: Navigation Compose
- **Async Operations**: Kotlin Coroutines & Flow
- **Build System**: Gradle with Kotlin DSL

## 📋 Requirements

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 24 (Android 7.0) or higher
- Target SDK: 34 (Android 14)

## 🚀 Getting Started

### Prerequisites

1. Install [Android Studio](https://developer.android.com/studio)
2. Ensure you have JDK 17 installed
3. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/budget-app.git
   cd budget-app
   ```

### Installation

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Connect an Android device or start an emulator
4. Click the Run button (▶️) or press `Shift + F10`

### Building from Command Line

**Windows:**
```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

**Mac/Linux:**
```bash
./gradlew assembleDebug
./gradlew installDebug
```

## 📖 Usage

### First Time Setup

1. Launch the app - you'll see the onboarding screen
2. Enter your **Monthly Income**
3. Enter your **Fixed Bills** (rent, utilities, etc.)
4. Set your **Savings Goal**
5. Complete the setup

### Using the App

- **Home Screen**: View your Daily Safe Spend in the pulse circle
  - 🟢 Green: You're on track (70%+ of ideal spend)
  - 🟡 Yellow: Be cautious (30-70% of ideal spend)
  - 🔴 Red: Over budget (<30% of ideal spend)

- **Add Expense**: Tap the + button → Select category → Enter amount → Save
- **Add Income**: Tap the + button → Select "Income" → Enter amount → Save
- **View Transactions**: Scroll down on the home screen to see recent transactions
- **Settings**: Access from the home screen to update your budget parameters

## 🏗️ Architecture

The app follows **MVVM (Model-View-ViewModel)** architecture:

```
┌─────────────────┐
│   UI Layer      │  (Compose Screens)
│  (Presentation) │
└────────┬────────┘
         │
┌────────▼────────┐
│  ViewModel      │  (State Management)
└────────┬────────┘
         │
┌────────▼────────┐
│  Use Cases      │  (Business Logic)
│   (Domain)      │
└────────┬────────┘
         │
┌────────▼────────┐
│  Repository     │  (Data Abstraction)
└────────┬────────┘
         │
┌────────▼────────┐
│  Room Database  │  (Local Storage)
└─────────────────┘
```

## 📁 Project Structure

```
app/src/main/java/com/baryabuddy/app/
├── data/
│   ├── database/          # Room database setup
│   │   ├── entities/      # Database entities
│   │   └── dao/           # Data Access Objects
│   └── repository/         # Data repository
├── domain/
│   ├── model/             # Domain models
│   └── usecase/           # Business logic use cases
├── presentation/
│   ├── home/              # Home screen
│   ├── onboarding/        # Onboarding flow
│   ├── addexpense/        # Add expense screen
│   ├── addincome/         # Add income screen
│   ├── settings/          # Settings screen
│   ├── components/        # Reusable UI components
│   └── navigation/        # Navigation setup
└── ui/
    └── theme/             # App theme and styling
```

## 💡 Key Features Explained

### Daily Safe Spend (DSS) Calculation

The DSS is calculated using this formula:

```
Disposable Income = Monthly Income - Fixed Bills - Savings Goal
Ideal Daily Spend = Disposable Income / Days in Month
Total Spent = Sum of Expenses - Sum of Income (for current month)
Remaining Budget = Disposable Income - Total Spent
DSS = Remaining Budget / Days Remaining
```

Income transactions **increase** your available budget, while expenses **decrease** it. The app automatically recalculates your DSS as you add transactions.

For detailed transaction logic, see [TRANSACTION_LOGIC.md](docs/TRANSACTION_LOGIC.md)

## 🧪 Testing

See [QUICK_START.md](docs/QUICK_START.md) for a quick testing guide and sample test scenarios.

## 📚 Documentation

- [Transaction Logic](docs/TRANSACTION_LOGIC.md) - Detailed explanation of how transactions affect the budget
- [Quick Start Guide](docs/QUICK_START.md) - Fast testing and verification guide

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## 👤 Author

Marc Lawrence Magadan- [@1mRen](https://github.com/1mRen)

## 🙏 Acknowledgments

- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose)
- Icons and UI components from Material Design 3

---

**Note**: This app is currently in development. Some features may be subject to change.

