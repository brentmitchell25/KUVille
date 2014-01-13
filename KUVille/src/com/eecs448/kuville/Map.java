package com.eecs448.kuville;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @written Brent Mitchel
 * @tested Brent Mitchell
 * @debugged Brent Mitchell
 * @credit Google Maps Example
 */

// This map implements Google Maps
// This can be replaced by the MapLibrary for custom image from KU
public class Map extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		// Get a handle to the Map Fragment
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		LatLng KU = new LatLng(38.9543833, -095.2516500);

		map.setMyLocationEnabled(true);
		// sets focus location and amount of zoom when starting Google maps
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(KU, 17));

		final String[] buildingName = getResources().getStringArray(
				R.array.buildingname);

		// Create markers from lat and long of buildings
		LatLng eaton = new LatLng(38.9576500, -095.2527833);
		map.addMarker(new MarkerOptions().title(buildingName[0])
				.position(eaton));

		LatLng allenFieldhouse = new LatLng(38.9545667, -095.2527000);
		map.addMarker(new MarkerOptions().title(buildingName[1]).position(
				allenFieldhouse));

		LatLng greenHall = new LatLng(38.9565000, -095.2541500);
		map.addMarker(new MarkerOptions().title(buildingName[2]).position(
				greenHall));

		LatLng union = new LatLng(38.9594833, -095.2433500);
		map.addMarker(new MarkerOptions().title(buildingName[3])
				.position(union));

		LatLng malott = new LatLng(38.9566, -095.2485);
		map.addMarker(new MarkerOptions().title(buildingName[4]).position(
				malott));

		LatLng strong = new LatLng(38.9585167, -095.2476333);
		map.addMarker(new MarkerOptions().title(buildingName[5]).position(
				strong));

		// Launches Building activity
		// Passes what building when marker is pressed
		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				StoredUpgrades db = new StoredUpgrades(getApplicationContext());
				db.open();
				if (db.isUpgraded(marker.getTitle()))
					if (marker.getTitle().equals(buildingName[0])) {
						Intent intent = new Intent(Map.this, Building.class);
						intent.putExtra("building_name", 0);
						db.close();
						startActivity(intent);
					} else if (marker.getTitle().equals(buildingName[1])) {
						Intent intent = new Intent(Map.this, Building.class);
						intent.putExtra("building_name", 1);
						db.close();
						startActivity(intent);
					} else if (marker.getTitle().equals(buildingName[2])) {
						Intent intent = new Intent(Map.this, Building.class);
						intent.putExtra("building_name", 2);
						db.close();
						startActivity(intent);
					} else if (marker.getTitle().equals(buildingName[3])) {
						Intent intent = new Intent(Map.this, Building.class);
						intent.putExtra("building_name", 3);
						db.close();
						startActivity(intent);
					} else if (marker.getTitle().equals(buildingName[4])) {
						Intent intent = new Intent(Map.this, Building.class);
						intent.putExtra("building_name", 4);
						db.close();
						startActivity(intent);
					} else if (marker.getTitle().equals(buildingName[5])) {
						Intent intent = new Intent(Map.this, Building.class);
						intent.putExtra("building_name", 5);
						db.close();
						startActivity(intent);
					}
				db.close();
				return false;

			}
		});
	}

	@Override
	public void onBackPressed() {
		// Prevents user from going back to login page
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
}
