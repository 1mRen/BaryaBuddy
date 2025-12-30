package com.baryabuddy.app.data.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.baryabuddy.app.data.database.entities.UserProfile;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserProfileDao_Impl implements UserProfileDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserProfile> __insertionAdapterOfUserProfile;

  private final EntityDeletionOrUpdateAdapter<UserProfile> __updateAdapterOfUserProfile;

  public UserProfileDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserProfile = new EntityInsertionAdapter<UserProfile>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_profile` (`id`,`monthlyIncome`,`fixedBills`,`savingsGoal`,`currency`,`setupCompleted`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfile entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getMonthlyIncome());
        statement.bindDouble(3, entity.getFixedBills());
        statement.bindDouble(4, entity.getSavingsGoal());
        statement.bindString(5, entity.getCurrency());
        final int _tmp = entity.getSetupCompleted() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
    this.__updateAdapterOfUserProfile = new EntityDeletionOrUpdateAdapter<UserProfile>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `user_profile` SET `id` = ?,`monthlyIncome` = ?,`fixedBills` = ?,`savingsGoal` = ?,`currency` = ?,`setupCompleted` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfile entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getMonthlyIncome());
        statement.bindDouble(3, entity.getFixedBills());
        statement.bindDouble(4, entity.getSavingsGoal());
        statement.bindString(5, entity.getCurrency());
        final int _tmp = entity.getSetupCompleted() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final UserProfile profile, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserProfile.insert(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final UserProfile profile, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUserProfile.handle(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserProfile> getProfile() {
    final String _sql = "SELECT * FROM user_profile WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_profile"}, new Callable<UserProfile>() {
      @Override
      @Nullable
      public UserProfile call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMonthlyIncome = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyIncome");
          final int _cursorIndexOfFixedBills = CursorUtil.getColumnIndexOrThrow(_cursor, "fixedBills");
          final int _cursorIndexOfSavingsGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "savingsGoal");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfSetupCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "setupCompleted");
          final UserProfile _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpMonthlyIncome;
            _tmpMonthlyIncome = _cursor.getDouble(_cursorIndexOfMonthlyIncome);
            final double _tmpFixedBills;
            _tmpFixedBills = _cursor.getDouble(_cursorIndexOfFixedBills);
            final double _tmpSavingsGoal;
            _tmpSavingsGoal = _cursor.getDouble(_cursorIndexOfSavingsGoal);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final boolean _tmpSetupCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSetupCompleted);
            _tmpSetupCompleted = _tmp != 0;
            _result = new UserProfile(_tmpId,_tmpMonthlyIncome,_tmpFixedBills,_tmpSavingsGoal,_tmpCurrency,_tmpSetupCompleted);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getProfileOnce(final Continuation<? super UserProfile> $completion) {
    final String _sql = "SELECT * FROM user_profile WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserProfile>() {
      @Override
      @Nullable
      public UserProfile call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMonthlyIncome = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyIncome");
          final int _cursorIndexOfFixedBills = CursorUtil.getColumnIndexOrThrow(_cursor, "fixedBills");
          final int _cursorIndexOfSavingsGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "savingsGoal");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfSetupCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "setupCompleted");
          final UserProfile _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpMonthlyIncome;
            _tmpMonthlyIncome = _cursor.getDouble(_cursorIndexOfMonthlyIncome);
            final double _tmpFixedBills;
            _tmpFixedBills = _cursor.getDouble(_cursorIndexOfFixedBills);
            final double _tmpSavingsGoal;
            _tmpSavingsGoal = _cursor.getDouble(_cursorIndexOfSavingsGoal);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final boolean _tmpSetupCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSetupCompleted);
            _tmpSetupCompleted = _tmp != 0;
            _result = new UserProfile(_tmpId,_tmpMonthlyIncome,_tmpFixedBills,_tmpSavingsGoal,_tmpCurrency,_tmpSetupCompleted);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
