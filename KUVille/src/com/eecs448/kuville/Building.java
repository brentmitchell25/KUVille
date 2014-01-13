package com.eecs448.kuville;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.eecs448.kuville.testing.Tester;

/**
 * @written Brent Mitchel
 * @tested Brent Mitchell
 * @debugged Brent Mitchell
 */
public class Building extends Activity {
	private TabHost tabHost;
	private ImageView building;
	private LinearLayout facebookFeeds;
	private LinearLayout twitterFeeds;
	private LinearLayout upgrades;
	private TextView gold;
	private SharedPreferences sharedPreferences;
	private int buildingIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buildings);

		// Initialize variables
		buildingIndex = getIntent().getExtras().getInt("building_name");

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		building = (ImageView) findViewById(R.id.image);
		facebookFeeds = (LinearLayout) findViewById(R.id.facebook);
		twitterFeeds = (LinearLayout) findViewById(R.id.twitter);
		upgrades = (LinearLayout) findViewById(R.id.upgrades);
		gold = (TextView) findViewById(R.id.gold);
		sharedPreferences = this.getApplicationContext().getSharedPreferences(
				"com.eecs448.kuville", Context.MODE_PRIVATE);

		// For first run
		SharedPreferences settings = getSharedPreferences("MyPrefs",
				MODE_PRIVATE);
		Boolean firstRun = settings.getBoolean("firstRun", true);

		if (firstRun) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("firstRun", false);
			editor.putInt("gold", 0);
			editor.commit();
		} else {
			gold.setText(Integer.toString(sharedPreferences.getInt("gold", 0)));
		}

		String[] buildingName = getResources().getStringArray(
				R.array.buildingname);

		// Sets the tabs on the screen
		tabHost.setup();
		TabSpec spec1 = tabHost.newTabSpec(buildingName[buildingIndex]);
		spec1.setIndicator(buildingName[buildingIndex]);
		spec1.setContent(R.id.building);

		TabSpec spec2 = tabHost.newTabSpec("Facebook");
		spec2.setIndicator("Facebook");
		spec2.setContent(R.id.facebook);

		TabSpec spec3 = tabHost.newTabSpec("Twitter");
		spec3.setContent(R.id.twitter);
		spec3.setIndicator("Twitter");

		TabSpec spec4 = tabHost.newTabSpec("Upgrades");
		spec4.setContent(R.id.upgrades);
		spec4.setIndicator("Upgrades");

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
		tabHost.addTab(spec4);

		setImage();
		// setFacebookFeeds();
		// setTwitterFeeds();
		// setUpgrading();

		TextView tv = (TextView) tabHost.getTabWidget().getChildAt(0)
				.findViewById(android.R.id.title);
		tv.setTextSize(8);

		tv = (TextView) tabHost.getTabWidget().getChildAt(1)
				.findViewById(android.R.id.title);
		tv.setTextSize(7);

		tv = (TextView) tabHost.getTabWidget().getChildAt(2)
				.findViewById(android.R.id.title);
		tv.setTextSize(7);

		tv = (TextView) tabHost.getTabWidget().getChildAt(3)
				.findViewById(android.R.id.title);
		tv.setTextSize(7);

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			public void onTabChanged(String tabId) {
				Tester test = new Tester();
				switch (tabHost.getCurrentTab()) {
				case 0:
					setImage();
					break;
				case 1:
					setFacebookFeeds();
					test.logFacebookDatabase(getApplicationContext());
					break;
				case 2:
					setTwitterFeeds();
					test.logTwitterDatabase(getApplicationContext());
					break;
				case 3:
					setUpgrading();
					test.logUpgradesDatabase(getApplicationContext());
					break;
				default:
					break;
				}

			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		gold.setText(Integer.toString((sharedPreferences.getInt("gold", 0))));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.logout:
			logout(item);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void logout(MenuItem item) {
		LogoutSocialMedia.logout(getApplicationContext());
		startActivity(new Intent(this, MainActivity.class));
	}

	// Sets the image when the activity is first diplayed
	private void setImage() {
		int drawableResourceId = this
				.getResources()
				.getIdentifier(
						getResources().getStringArray(R.array.buildings)[buildingIndex],
						"drawable", this.getPackageName());

		building.setImageResource(drawableResourceId);
		facebookFeeds.setVisibility(View.GONE);
		twitterFeeds.setVisibility(View.GONE);
		building.setVisibility(View.VISIBLE);
	}

	// Important to clear all views before displaying feeds
	private void setFacebookFeeds() {
		facebookFeeds.removeAllViews();
		facebookFeeds.setVisibility(View.VISIBLE);
		twitterFeeds.setVisibility(View.GONE);
		building.setVisibility(View.GONE);
		FacebookFeeds.facebookQuery(
				getResources().getStringArray(R.array.facebook)[buildingIndex],
				facebookFeeds, Building.this);

	}

	private void setTwitterFeeds() {
		twitterFeeds.removeAllViews();
		facebookFeeds.setVisibility(View.GONE);
		twitterFeeds.setVisibility(View.VISIBLE);
		building.setVisibility(View.GONE);
		TwitterFeeds.twitterFeeds(getResources()
				.getStringArray(R.array.twitter)[buildingIndex], twitterFeeds,
				Building.this);
	}

	private void setUpgrading() {
		upgrades.removeAllViews();
		facebookFeeds.setVisibility(View.GONE);
		twitterFeeds.setVisibility(View.GONE);
		building.setVisibility(View.GONE);
		Upgrades up = new Upgrades(this);
		up.displayUpgrades(upgrades);
	}
}
