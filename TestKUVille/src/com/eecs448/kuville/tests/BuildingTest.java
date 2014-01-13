package com.eecs448.kuville.tests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.eecs448.kuville.Building;
import com.eecs448.kuville.R;

public class BuildingTest extends ActivityInstrumentationTestCase2<Building> {
	private Activity activity;
	private Context c;
	private Intent intent;

	public BuildingTest() {
		super(Building.class);
	}

	public void setUp() {
		c = getInstrumentation().getTargetContext().getApplicationContext();
		intent = new Intent(c, Building.class);
		intent.putExtra("Eaton Hall", 0);
	}

	public void testImage() {
		setActivityIntent(intent);
		activity = this.getActivity();

		ImageView image = (ImageView) activity.findViewById(R.id.image);
		assertNotNull("Image of building is invisible", image);
	}

	public void testFacebookFeeds() {

		setActivityIntent(intent);
		activity = this.getActivity();

		LinearLayout ll = (LinearLayout) activity.findViewById(R.id.facebook);
		// assertEquals(ll.getChildCount(), 0);
		assertTrue(ll.getChildCount() == 0); // Always 0 because not logged in
	}

	public void testTwitterFeeds() {
		setActivityIntent(intent);
		activity = this.getActivity();

		LinearLayout ll = (LinearLayout) activity.findViewById(R.id.twitter);
		// assertEquals(ll.getChildCount(), 0);
		assertTrue(ll.getChildCount() != 0);
	}

	public void testUpgrades() {

		setActivityIntent(intent);
		activity = this.getActivity();

		LinearLayout ll = (LinearLayout) activity.findViewById(R.id.upgrades);
		assertEquals(ll.getChildCount(), 0);
		// assertTrue(ll.getChildCount() != 0);
	}
}