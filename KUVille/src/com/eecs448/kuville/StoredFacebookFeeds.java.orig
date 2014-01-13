package com.eecs448.kuville;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @written Brent Mitchel
 * @tested Brent Mitchell
 * @debugged Brent Mitchell
 */
public class StoredFacebookFeeds {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_POSTID = "post_id";
	public static final String KEY_BOOLEAN = "read";

	private static final String DATABASE_NAME = "FacebookFeeds";
	private static final String DATABASE_TABLE = "feeds";
	private static final int DATABASE_VERSION = 1;

	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_POSTID
					+ " TEXT NOT NULL, " + KEY_BOOLEAN + " INTEGER" + ");");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);

		}
	}

	public StoredFacebookFeeds(Context c) {
		ourContext = c;
	}

	public StoredFacebookFeeds open() {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public boolean createEntry(String post_id) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_POSTID, post_id);
		cv.put(KEY_BOOLEAN, 0);

		if (!postidColExists(post_id)) {
			ourDatabase.insert(DATABASE_TABLE, null, cv);
			return false;
		}
		return true;
	}

	public String getData() {
		String[] columns = new String[] { KEY_ROWID, KEY_POSTID, KEY_BOOLEAN };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
				null, null);
		String result = "";

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			result += c.getString(0) + " " + c.getString(1) + " "
					+ c.getString(2) + "\n";
		c.close();
		return result;
	}

	public int getRowId(String post_id) {
		String[] columns = new String[] { KEY_ROWID, KEY_POSTID, KEY_BOOLEAN };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
				null, null);
		int postidCol = c.getColumnIndex(KEY_POSTID);
		int rowid = c.getColumnIndex(KEY_ROWID);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			if (post_id.equals(c.getString(postidCol))) {
				int returnInt = c.getInt(rowid);
				c.close();
				return returnInt;
			}
		c.close();
		return -1;

	}

	public boolean setRead(String post_id) {
		ContentValues update = new ContentValues();
		update.put(KEY_BOOLEAN, 1);

		ourDatabase.update(DATABASE_TABLE, update, KEY_ROWID + "="
				+ getRowId(post_id), null);
		return true;
	}

	public boolean isRead(String post_id) {
		String[] columns = new String[] { KEY_POSTID, KEY_BOOLEAN };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
				null, null);
		int postidCol = c.getColumnIndex(KEY_POSTID);
		int bool = c.getColumnIndex(KEY_BOOLEAN);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			if (post_id.equals(c.getString(postidCol)))
				if (c.getInt(bool) == 1) {
					c.close();
					return true;
				} else {
					c.close();
					return false;
				}
		}
		c.close();
		return false;

	}

	private boolean postidColExists(String post_id) {
		String[] columns = new String[] { KEY_POSTID };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
				null, null);
		int postidCol = c.getColumnIndex(KEY_POSTID);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			if (post_id.equals(c.getString(postidCol))) {
				c.close();
				return true;
			}
		}
		c.close();
		return false;
	}
}
