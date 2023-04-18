package com.roomdata.myapp.data;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class UserDoaMyDao_Impl implements UserDoa.MyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<User> __insertionAdapterOfUser;

  private final EntityDeletionOrUpdateAdapter<User> __deletionAdapterOfUser;

  private final EntityDeletionOrUpdateAdapter<User> __updateAdapterOfUser;

  public UserDoaMyDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUser = new EntityInsertionAdapter<User>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `weatherData` (`id`,`temp`,`feeltemp`,`description`,`windspeed`) VALUES (?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, User value) {
        stmt.bindLong(1, value.getId());
        if (value.getTemperature() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getTemperature());
        }
        if (value.getFeeltemp() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getFeeltemp());
        }
        if (value.getDescription() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDescription());
        }
        if (value.getWindspeed() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getWindspeed());
        }
      }
    };
    this.__deletionAdapterOfUser = new EntityDeletionOrUpdateAdapter<User>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `weatherData` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, User value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfUser = new EntityDeletionOrUpdateAdapter<User>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `weatherData` SET `id` = ?,`temp` = ?,`feeltemp` = ?,`description` = ?,`windspeed` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, User value) {
        stmt.bindLong(1, value.getId());
        if (value.getTemperature() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getTemperature());
        }
        if (value.getFeeltemp() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getFeeltemp());
        }
        if (value.getDescription() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDescription());
        }
        if (value.getWindspeed() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getWindspeed());
        }
        stmt.bindLong(6, value.getId());
      }
    };
  }

  @Override
  public void insertObject(final User myObject) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfUser.insert(myObject);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final User user) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfUser.handle(user);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateUsers(final User... users) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfUser.handleMultiple(users);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<User> getAllObjects() {
    final String _sql = "SELECT * FROM weatherData";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "temp");
      final int _cursorIndexOfFeeltemp = CursorUtil.getColumnIndexOrThrow(_cursor, "feeltemp");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfWindspeed = CursorUtil.getColumnIndexOrThrow(_cursor, "windspeed");
      final List<User> _result = new ArrayList<User>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final User _item;
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final String _tmpTemperature;
        _tmpTemperature = _cursor.getString(_cursorIndexOfTemperature);
        final String _tmpFeeltemp;
        _tmpFeeltemp = _cursor.getString(_cursorIndexOfFeeltemp);
        final String _tmpDescription;
        _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
        final String _tmpWindspeed;
        _tmpWindspeed = _cursor.getString(_cursorIndexOfWindspeed);
        _item = new User(_tmpId,_tmpTemperature,_tmpFeeltemp,_tmpDescription,_tmpWindspeed);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
