package com.eecs448.kuville.ui.mapui;

import android.view.View;

/**
 * @written Jamie Robinson
 * @tested Jamie Robinson
 * @debugged Jamie Robinson
 */
public interface TouchableAreaInterface {
	public boolean contains(float x, float y);

	public void onClick(View view);
}
