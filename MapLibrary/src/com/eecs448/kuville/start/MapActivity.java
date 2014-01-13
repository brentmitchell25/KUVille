package com.eecs448.kuville.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eecs448.kuville.LogoutSocialMedia;
import com.eecs448.kuville.maplibrary.R;

/**
 * @written Jamie Robinson
 * @tested Jamie Robinson and Brent Mitchell
 * @debugged Jamie Robinson
 */
public class MapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		logout(item);
		return true;
	}

	private void logout(MenuItem item) {
		LogoutSocialMedia.logout(getApplicationContext());
		startActivity(new Intent(this, com.eecs448.kuville.MainActivity.class));
	}

	@Override
	public void onBackPressed() {
	}

}
