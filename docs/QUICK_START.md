# Quick Start - Testing BaryaBuddy

## Fastest Way to Test

### 1. Open in Android Studio
```bash
# If you have Android Studio installed, just:
# 1. Open Android Studio
# 2. File > Open > Select "Budget App" folder
# 3. Wait for Gradle sync
# 4. Click Run (green play button)
```

### 2. Quick Build Commands

**Windows (PowerShell):**
```powershell
cd "Budget App"
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

**Mac/Linux:**
```bash
cd "Budget App"
./gradlew assembleDebug
./gradlew installDebug
```

### 3. What to Test First (5-Minute Test)

1. **Launch App** → Should show onboarding
2. **Complete Setup:**
   - Income: `50000`
   - Bills: `10000`  
   - Goal: `20000`
3. **Check Home Screen:**
   - See Pulse circle with DSS amount
   - Should be Green (fresh start)
4. **Add Expense:**
   - Tap FAB (+ button)
   - Amount: `500`
   - Select "Food" category
   - Tap "Save Expense"
5. **Verify:**
   - Transaction appears in list
   - DSS amount decreased
   - Pulse might change color if spending is high

### 4. Expected Results

✅ **Onboarding:** 3 steps, validation works  
✅ **Home Screen:** Pulse circle shows daily safe spend  
✅ **Add Expense:** Bottom sheet opens, saves successfully  
✅ **Persistence:** Close app, reopen → data still there  

### 5. If Something Breaks

**Check Logcat in Android Studio:**
- Filter by: `com.baryabuddy`
- Look for red errors
- Common fixes in TESTING_GUIDE.md

**Common First-Time Issues:**
- Gradle sync fails → Check internet connection
- Build errors → Clean project: `Build > Clean Project`
- App crashes → Check Logcat for stack trace

### 6. Test Data for Quick Verification

**Scenario: Monthly Budget Test**
```
Income: 30000
Bills: 5000
Goal: 10000
Disposable: 15000
Ideal DSS (30-day month): 500/day

Day 1: Add 200 expense
Expected DSS: (15000 - 200) / 30 = 493.33
Status: Green (493.33 >= 350 = 70% of 500)

Day 15: Add 5000 more
Total spent: 5200
Expected DSS: (15000 - 5200) / 16 = 612.5
Status: Still Green

Day 20: Add 4000 more  
Total spent: 9200
Expected DSS: (15000 - 9200) / 11 = 527.27
Status: Green (527.27 >= 350)

Day 25: Add 5000 more
Total spent: 14200
Expected DSS: (15000 - 14200) / 6 = 133.33
Status: Yellow (133.33 is 26.7% of 500, between 30-70% threshold)
```

---

**Need detailed testing?** See `TESTING_GUIDE.md` for comprehensive checklist.

