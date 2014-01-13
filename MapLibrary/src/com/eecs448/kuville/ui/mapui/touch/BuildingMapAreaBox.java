package com.eecs448.kuville.ui.mapui.touch;

import android.content.Intent;
import android.graphics.RectF;
import android.view.View;

import com.eecs448.kuville.ui.mapui.areagraph.TouchableAreaGraphNode;

/**
 * @written Jamie Robinson
 * @tested Jamie Robinson and Brent Mitchell
 * @debugged Jamie Robinson
 */
public class BuildingMapAreaBox extends TouchableAreaGraphNode {
	private String building;
	private RectF area;
	private int building_index;

	public BuildingMapAreaBox(String building_name, float x_top, float y_top,
			float x_bot, float y_bot) {
		super(x_top, y_top, x_bot, y_bot);
		this.building_index = -1;
		building = building_name;
		area = new RectF(x_top, y_top, x_bot, y_bot);
	}

	public BuildingMapAreaBox(String building_name, int buildingIndex,
			float x_top, float y_top, float x_bot, float y_bot) {
		super(x_top, y_top, x_bot, y_bot);
		this.building_index = buildingIndex;
		building = building_name;
		area = new RectF(x_top, y_top, x_bot, y_bot);
	}

	public BuildingMapAreaBox(int building_index, float x_top, float y_top,
			float x_bot, float y_bot) {
		super(x_top, y_top, x_bot, y_bot);
		this.building_index = building_index;
		building = null;
		area = new RectF(x_top, y_top, x_bot, y_bot);
	}

	@Override
	public void onClick(View ms) {

		com.eecs448.kuville.StoredUpgrades db = new com.eecs448.kuville.StoredUpgrades(
				ms.getContext());
		db.open();
		// Do not open building if it has not been upgraded
		if (db.isUpgraded(building)) {
			Intent intent = new Intent(ms.getContext(),
					com.eecs448.kuville.Building.class);
			intent.putExtra("building_name", building_index);
			db.close();
			ms.getContext().startActivity(intent);
		}
		db.close();
	}

	@Override
	public boolean contains(float x, float y) {
		return x > area.left && x < area.right && y > area.top
				&& y < area.bottom;
	}

	@Override
	public String toString() {
		return building;
	}
}

// Later on when we don't need to demo this we should make the BuildingMapAreas
// polygons
// but for now we will settle with rectangular regions for simplicity.
/*
 * class Area { private ArrayList<Point> point_list;
 * 
 * Area() { point_list = new ArrayList<Point>(); }
 * 
 * int }
 * 
 * class Point { private int x, y;
 * 
 * private Point(int x, int y) { this.x = x; this.y = y; }
 * 
 * int getX() { return x; }
 * 
 * int getY() { return y; } }
 */