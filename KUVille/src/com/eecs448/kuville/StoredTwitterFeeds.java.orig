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
public class StoredTwitterFeeds {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_TWEET = "message";
	public static final String KEY_BOOLEAN = "read";

	private static final String DATABASE_NAME = "TwitterFeeds";
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
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TWEET
					+ " TEXT NOT NULL, " + KEY_BOOLEAN + " INTEGER" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);

		}
	}

	public StoredTwitterFeeds(Context c) {
		ourContext = c;
	}

	public StoredTwitterFeeds open() {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public boolean createEntry(String tweet) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_TWEET, tweet);
		cv.put(KEY_BOOLEAN, 0);

		if (!tweetColExists(tweet)) {
			ourDatabase.insert(DATABASE_TABLE, null, cv);
			return false;
		}
		return true;
	}

	public String getData() {
		String[] columns = new String[] { KEY_ROWID, KEY_TWEET, KEY_BOOLEAN };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
				null, null);
		String result = "";

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			result += c.getString(0) + " " + c.getString(1) + " "
					+ c.getString(2) + "\n";
		c.close();
		return result;
	}

	public int getRowId(String tweet) {
		String[] columns = new String[] { KEY_ROWID, KEY_TWEET, KEY_BOOLEAN };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
				null, null);
		int tweetCol = c.getColumnIndex(KEY_TWEET);
		int rowid = c.getColumnIndex(KEY_ROWID);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			if (tweet.equals(c.getString(tweetCol))) {
				int returnInt = c.getInt(rowid);
				c.close();
				return returnInt;
			}
		c.close();
		return -1;

	}

	public boolean setRead(String tweet) {
		ContentValues update = new ContentValues();
		update.put(KEY_BOOLEAN, 1);

		ourDatabase.update(DATABASE_TABLE, update, KEY_ROWID + "="
				+ getRowId(tweet), null);
		return true;
	}

	public boolean isRead(String tweet) {
		String[] columns = new String[] { KEY_TWEET, KEY_BOOLEAN };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
				null, null);
		int tweetCol = c.getColumnIndex(KEY_TWEET);
		int bool = c.getColumnIndex(KEY_BOOLEAN);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			if (tweet.equals(c.getString(tweetCol)))
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

	private boolean tweetColExists(String tweet) {
		String[] columns = new String[] { KEY_TWEET };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
				null, null);
		int tweetCol = c.getColumnIndex(KEY_TWEET);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			if (tweet.equals(c.getString(tweetCol))) {
				c.close();
				return true;
			}
		}
		c.close();
		return false;
	}
}
