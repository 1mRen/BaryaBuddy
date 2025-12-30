# Transaction Logic Documentation

## Overview
The app handles two types of transactions: **Expenses** (money going out) and **Income** (money coming in). Both affect the Daily Safe Spend (DSS) calculation differently.

---

## 1. Transaction Data Structure

```kotlin
data class Transaction(
    val id: Long = 0,                    // Auto-generated unique ID
    val amount: Double,                  // Transaction amount (always positive)
    val categoryId: Long,                // Category ID (0 for income, >0 for expenses)
    val description: String?,            // Optional description
    val date: Long,                      // Transaction date (timestamp in milliseconds)
    val createdAt: Long,                 // When record was created
    val transactionType: String          // "expense" or "income"
)
```

### Key Points:
- **Amount**: Always stored as a positive number
- **CategoryId**: 
  - `0` = Income transactions (no category)
  - `>0` = Expense transactions (links to a category)
- **TransactionType**: Determines how the amount affects the budget

---

## 2. Creating an Expense Transaction

### Flow:
1. User enters amount, selects category, optional description
2. Validation:
   - Amount must be > 0
   - Category must be selected
3. Transaction is created with `transactionType = "expense"`
4. Saved to database via `repository.addTransaction()`

### Code Logic:
```kotlin
// From AddExpenseViewModel.kt
suspend fun saveTransaction(): Boolean {
    val amount = state.amount.toDoubleOrNull() ?: return false
    val categoryId = state.selectedCategoryId ?: return false
    
    if (amount <= 0) return false
    
    val transaction = Transaction(
        amount = amount,
        categoryId = categoryId,
        description = state.description.takeIf { it.isNotBlank() },
        date = state.date,
        createdAt = System.currentTimeMillis(),
        transactionType = "expense"  // ← Marked as expense
    )
    
    return repository.addTransaction(transaction)
}
```

---

## 3. Creating an Income Transaction

### Flow:
1. User enters amount, optional description
2. Validation:
   - Amount must be > 0
   - No category needed
3. Transaction is created with `transactionType = "income"` and `categoryId = 0`
4. Saved to database via `repository.addTransaction()`

### Code Logic:
```kotlin
// From AddIncomeViewModel.kt
suspend fun saveIncome(): Boolean {
    val amount = state.amount.toDoubleOrNull() ?: return false
    
    if (amount <= 0) return false
    
    val transaction = Transaction(
        amount = amount,
        categoryId = 0,  // ← No category for income
        description = state.description.takeIf { it.isNotBlank() },
        date = state.date,
        createdAt = System.currentTimeMillis(),
        transactionType = "income"  // ← Marked as income
    )
    
    return repository.addTransaction(transaction)
}
```

---

## 4. Transaction Calculation in Daily Safe Spend (DSS)

### Step-by-Step Logic:

#### Step 1: Calculate Base Budget
```kotlin
disposableIncome = monthlyIncome - fixedBills - savingsGoal
```
This is the money available for daily spending after bills and savings.

#### Step 2: Calculate Ideal Daily Spend
```kotlin
idealDailySpend = disposableIncome / daysInMonth
```
The target amount you should spend per day to stay on budget.

#### Step 3: Filter Current Month Transactions
```kotlin
currentMonthTransactions = allTransactions.filter { 
    transactionDate >= monthStart && transactionDate < nextMonthStart 
}
```
Only transactions from the current month are considered.

#### Step 4: Calculate Net Spending
**This is the key logic:**

```kotlin
totalSpent = currentMonthTransactions.sumOf { transaction ->
    when (transaction.transactionType) {
        "income" -> -transaction.amount  // Income INCREASES budget (subtract from spent)
        else -> transaction.amount       // Expenses DECREASE budget (add to spent)
    }
}
```

**Example:**
- You have ₱10,000 disposable income
- You spend ₱2,000 (expense) → `totalSpent = +2000`
- You earn ₱500 (income) → `totalSpent = -500`
- Net: `totalSpent = 2000 - 500 = 1500`
- Remaining: `10000 - 1500 = ₱8,500`

#### Step 5: Calculate Remaining Budget
```kotlin
remainingBudget = disposableIncome - totalSpent
```

#### Step 6: Calculate Days Remaining
```kotlin
daysRemaining = daysInMonth - currentDay + 1  // Inclusive of today
```

#### Step 7: Calculate Real Daily Safe Spend
```kotlin
realDailySafeSpend = remainingBudget / daysRemaining
```

If `remainingBudget <= 0` or `daysRemaining <= 0`, return `0.0`.

#### Step 8: Determine Status Color
```kotlin
when {
    realDailySafeSpend >= (idealDailySpend * 0.7) -> GREEN   // 70%+ of ideal
    realDailySafeSpend >= (idealDailySpend * 0.3) -> YELLOW  // 30-70% of ideal
    else -> RED                                               // <30% of ideal
}
```

---

## 5. Transaction Storage & Retrieval

### Storage (Room Database):
- All transactions stored in `transactions` table
- Auto-incrementing ID for each transaction
- Indexed by date for fast queries

### Retrieval Methods:

1. **Get All Transactions** (for DSS calculation):
   ```kotlin
   repository.getAllTransactions()  // Returns Flow<List<Transaction>>
   ```

2. **Get Recent Transactions** (for home screen display):
   ```kotlin
   repository.getRecentTransactions(10)  // Returns Flow<List<Transaction>>
   ```

3. **Get Transactions by Month**:
   ```kotlin
   repository.getTransactionsByMonth(year, month)
   ```

---

## 6. Transaction Display Logic

### In Transaction List:
```kotlin
// From HomeScreen.kt - TransactionItem
val isIncome = transaction.transactionType == "income"

if (isIncome) {
    // Show green money icon
    // Display "Income" as label
    // Show "+₱amount" in green
} else {
    // Show category icon with category color
    // Display category name
    // Show "₱amount" in primary color
}
```

### Key Display Rules:
- **Income**: Green money icon, "+" prefix, green text
- **Expense**: Category icon, category name, no prefix, primary color

---

## 7. Complete Transaction Flow

```
User Action
    ↓
[Add Expense/Income Screen]
    ↓
[Validation: amount > 0, category selected (for expense)]
    ↓
[Create Transaction Object]
    ↓
[Save to Database via Repository]
    ↓
[Database emits Flow update]
    ↓
[HomeViewModel receives update]
    ↓
[Recalculate DSS with new transaction]
    ↓
[Update UI: Transaction list + DSS circle]
```

---

## 8. Example Scenarios

### Scenario 1: Adding Expenses
- **Monthly Income**: ₱50,000
- **Fixed Bills**: ₱10,000
- **Savings Goal**: ₱20,000
- **Disposable**: ₱20,000
- **Days in Month**: 30
- **Ideal DSS**: ₱666.67/day

**Day 1**: Add expense ₱500 (Food)
- `totalSpent = +500`
- `remainingBudget = 20000 - 500 = ₱19,500`
- `DSS = 19500 / 30 = ₱650/day` ✅ (Still GREEN)

**Day 5**: Add expense ₱2,000 (Shopping)
- `totalSpent = +2500` (500 + 2000)
- `remainingBudget = 20000 - 2500 = ₱17,500`
- `DSS = 17500 / 26 = ₱673/day` ✅ (Still GREEN)

### Scenario 2: Adding Income
**Day 10**: Add income ₱1,000 (Side job)
- Previous `totalSpent = +2500`
- `totalSpent = 2500 - 1000 = +1500` (income reduces spent amount)
- `remainingBudget = 20000 - 1500 = ₱18,500`
- `DSS = 18500 / 21 = ₱881/day` ✅ (Improved!)

### Scenario 3: Budget Exceeded
**Day 15**: Total expenses = ₱22,000, Income = ₱1,000
- `totalSpent = 22000 - 1000 = +21000`
- `remainingBudget = 20000 - 21000 = -₱1,000` ❌
- `DSS = 0` (Budget exceeded)
- Status: RED

---

## 9. Important Notes

1. **Income increases available budget** by reducing the "spent" amount
2. **Expenses decrease available budget** by increasing the "spent" amount
3. **Only current month transactions** affect DSS calculation
4. **Transaction amounts are always positive** - the type determines the effect
5. **DSS updates automatically** when transactions are added (via Flow)
6. **Category is required for expenses** but not for income

---

## 10. Database Queries

### Get Recent Transactions:
```sql
SELECT * FROM transactions 
ORDER BY date DESC, createdAt DESC 
LIMIT 10
```

### Get Current Month Transactions:
```sql
SELECT * FROM transactions 
WHERE date >= :monthStart AND date < :nextMonthStart
ORDER BY date DESC
```

### Calculate Total Spent (with income/expense logic):
```kotlin
// Done in Kotlin, not SQL
transactions.sumOf { transaction ->
    when (transaction.transactionType) {
        "income" -> -transaction.amount
        else -> transaction.amount
    }
}
```

---

## Summary

**Transaction Logic in One Sentence:**
*Income transactions reduce the "spent" amount (increasing budget), while expense transactions increase the "spent" amount (decreasing budget), and the DSS is calculated as remaining budget divided by days remaining in the month.*

