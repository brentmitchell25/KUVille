package com.eecs448.kuville.ui.mapui.touch;

import android.view.View;

import com.eecs448.kuville.ui.mapui.areagraph.TouchableAreaGraphNode;
/**
 * @written Jamie Robinson
 * @tested Jamie Robinson
 * @debugged Jamie Robinson
 */
public class DummyMapAreaBox extends TouchableAreaGraphNode {

	public DummyMapAreaBox(float x_top, float y_top, float x_bot, float y_bot) {
		super(x_top, y_top, x_bot, y_bot);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean contains(float x, float y) {
		return false;
	}

	@Override
	public void onClick(View view) {}

}
