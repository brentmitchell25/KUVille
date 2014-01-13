package com.eecs448.kuville.testing;

import android.content.Context;
import android.util.Log;

import com.eecs448.kuville.StoredFacebookFeeds;
import com.eecs448.kuville.StoredTwitterFeeds;
import com.eecs448.kuville.StoredUpgrades;

/**
 * @written Brent Mitchel
 * @tested Brent Mitchell
 * @debugged Brent Mitchell
 */
public class Tester {
	public void logFacebookDatabase(Context c) {
		StoredFacebookFeeds db = new StoredFacebookFeeds(c);
		db.open();
		Log.i("Facebook Database", db.getData());
		db.close();
	}

	public void logTwitterDatabase(Context c) {
		StoredTwitterFeeds db = new StoredTwitterFeeds(c);
		db.open();
		Log.i("Twitter Database", db.getData());
		db.close();
	}

	public void logUpgradesDatabase(Context c) {
		StoredUpgrades db = new StoredUpgrades(c);
		db.open();
		Log.i("Upgrades Database", db.getData());
		db.close();
	}
}
