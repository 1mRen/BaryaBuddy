package com.baryabuddy.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.baryabuddy.app.data.database.dao.CategoryDao
import com.baryabuddy.app.data.database.dao.TransactionDao
import com.baryabuddy.app.data.database.dao.UserProfileDao
import com.baryabuddy.app.data.database.entities.Category
import com.baryabuddy.app.data.database.entities.Transaction
import com.baryabuddy.app.data.database.entities.UserProfile

@Database(
    entities = [Transaction::class, Category::class, UserProfile::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add transactionType column with default value 'expense'
                database.execSQL("ALTER TABLE transactions ADD COLUMN transactionType TEXT NOT NULL DEFAULT 'expense'")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Step 1: Add new amountCentavos column
                database.execSQL("ALTER TABLE transactions ADD COLUMN amountCentavos INTEGER NOT NULL DEFAULT 0")
                
                // Step 2: Migrate existing amount data (convert Double to centavos)
                database.execSQL("UPDATE transactions SET amountCentavos = CAST(amount * 100 AS INTEGER)")
                
                // Step 3: Drop old amount column
                database.execSQL("CREATE TABLE transactions_new (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "amountCentavos INTEGER NOT NULL, " +
                        "categoryId INTEGER, " +
                        "description TEXT, " +
                        "date INTEGER NOT NULL, " +
                        "createdAt INTEGER NOT NULL)")
                
                database.execSQL("INSERT INTO transactions_new (id, amountCentavos, categoryId, description, date, createdAt) " +
                        "SELECT id, amountCentavos, " +
                        "CASE WHEN categoryId = 0 THEN NULL ELSE categoryId END, " +
                        "description, date, createdAt FROM transactions")
                
                database.execSQL("DROP TABLE transactions")
                database.execSQL("ALTER TABLE transactions_new RENAME TO transactions")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create new user_profile table with updated schema
                database.execSQL("CREATE TABLE user_profile_new (" +
                        "id INTEGER PRIMARY KEY NOT NULL, " +
                        "incomeAmount INTEGER NOT NULL DEFAULT 0, " +
                        "fixedBillsAmount INTEGER NOT NULL DEFAULT 0, " +
                        "savingsGoalAmount INTEGER NOT NULL DEFAULT 0, " +
                        "incomeFrequency TEXT NOT NULL DEFAULT 'MONTHLY', " +
                        "resetDay INTEGER NOT NULL DEFAULT 1, " +
                        "currency TEXT NOT NULL DEFAULT '₱', " +
                        "setupCompleted INTEGER NOT NULL DEFAULT 0)")
                
                // Migrate existing data: convert Double to Long (centavos)
                database.execSQL("INSERT INTO user_profile_new (id, incomeAmount, fixedBillsAmount, savingsGoalAmount, incomeFrequency, resetDay, currency, setupCompleted) " +
                        "SELECT id, " +
                        "CAST(monthlyIncome * 100 AS INTEGER), " +
                        "CAST(fixedBills * 100 AS INTEGER), " +
                        "CAST(savingsGoal * 100 AS INTEGER), " +
                        "'MONTHLY', " +
                        "1, " +
                        "currency, " +
                        "setupCompleted " +
                        "FROM user_profile")
                
                // Drop old table and rename new one
                database.execSQL("DROP TABLE user_profile")
                database.execSQL("ALTER TABLE user_profile_new RENAME TO user_profile")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "baryabuddy_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration() // For development - remove in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

