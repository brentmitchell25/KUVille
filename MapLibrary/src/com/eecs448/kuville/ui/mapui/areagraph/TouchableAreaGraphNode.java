package com.eecs448.kuville.ui.mapui.areagraph;

import com.eecs448.kuville.ui.mapui.TouchableAreaInterface;

import android.graphics.RectF;
import android.util.Log;
/**
 * @written Jamie Robinson
 * @tested Jamie Robinson
 * @debugged Jamie Robinson
 */
abstract public class TouchableAreaGraphNode implements TouchableAreaInterface {
	
	private TouchableAreaGraphNode[] pointer = new TouchableAreaGraphNode[4];
	
	private RectF bounds;
	
	private Object visitor;
	
	public TouchableAreaGraphNode(float x_top, float y_top, float x_bot, float y_bot) {
		bounds = new RectF(x_top, y_top, x_bot, y_bot);
	}
	
	void entangle(TouchableAreaGraphNode[] pointers) {
		pointer = pointers;
	}
	
	/*boolean findNearest(TouchableAreaGraphNode nearest, Object visit_tag, float x, float y) {
		int returning_index = -1;
		boolean contained = false;
		
		if (visitor == null) {
			visitor = visit_tag;
		} else if (visitor == visit_tag) {
			return false;
		} else {
			do {
				synchronized(visit_tag) {
					try {
						visit_tag.wait();
					} catch (InterruptedException e) {}
				}
			} while(visitor == visit_tag);
		}
		
		if (bounds.contains(x, y)) {
			if (contains(x, y)) {
				nearest = this;
				return true;
			}
		}
		
		/*if (pointer[3] != null && x < bounds.centerX()) {
			if (pointer[0] != null && y < bounds.centerY()) {
				if (pointer_distances[0] < pointer_distances[3]) {
					contained = pointer[0].findNearest(nearest, visit_tag, x, y);
				} else {
					contained = pointer[3].findNearest(nearest, visit_tag, x, y);
				}
			} else if (pointer[2] != null) {
				if (pointer_distances[2] < pointer_distances[3]) {
					contained = pointer[2].findNearest(nearest, visit_tag, x, y);
				} else {
					contained = pointer[3].findNearest(nearest, visit_tag, x, y);
				}
			}
		} else if (pointer[1] != null){
			if (pointer[0] != null && y < bounds.centerY()) {
				if (pointer_distances[0] < pointer_distances[1]) {
					contained = pointer[0].findNearest(nearest, visit_tag, x, y);
				} else {
					contained = pointer[1].findNearest(nearest, visit_tag, x, y);
				}
			} else if (pointer[2] != null) {
				if (pointer_distances[1] < pointer_distances[2]) {
					contained = pointer[1].findNearest(nearest, visit_tag, x, y);
				} else {
					contained = pointer[2].findNearest(nearest, visit_tag, x, y);
				}
			}
		}
		
		if (x < bounds.centerX()) {
			if (pointer[0] != null && y < bounds.centerY()) {
				returning_index = 0;
				contained = pointer[returning_index].findNearest(nearest, visit_tag, x, y);
			} else if (pointer[1] != null) {
				returning_index = 1;
				contained = pointer[returning_index].findNearest(nearest, visit_tag, x, y);
			}
		} else {
			if (pointer[2] != null && y < bounds.centerY()) {
				returning_index = 2;
				contained = pointer[returning_index].findNearest(nearest, visit_tag, x, y);
			} else if (pointer[3] != null) {
				returning_index = 3;
				contained = pointer[returning_index].findNearest(nearest, visit_tag, x, y);
			}
		}
		
		if (returning_index == -1) {
			nearest = this;
		} else if (nearest == null) {
			if (measureDistance(x, y) < pointer[returning_index].measureDistance(x, y)) {
				nearest = this;
			} else {
				nearest = pointer[returning_index];
			}
		}
		
		visitor = null;
		visit_tag.notifyAll();
		
		return contained;
	}*/
	
	TouchableAreaGraphNode findContainer(Object visit_tag, float x, float y) {
		Log.v("Traversal", "Traversal - Entering: " + toString());
		TouchableAreaGraphNode container = null;
		
		if (visitor == null) {
			visitor = visit_tag;
		} else if (visitor == visit_tag) {
			Log.v("Traversal", "Traversal - Collision: " + toString());
			visitor = null;
			return null;
		} else {
			do {
				synchronized(visitor) {
					try {
						visitor.wait();
					} catch (InterruptedException e) {}
				}
			} while(visitor != null);
			visitor = visit_tag;
		}
		
		if (bounds.contains(x, y)) {
			if (contains(x, y)) {
				Log.v("Traversal", "Traversal - Found: " + toString());
				visitor = null;
				return this;
			}
		}
		
		// Check the other direction(s) if the first branch returns null and 
		// the point is on either center line
		if (x <= bounds.centerX()) {
			if (y <= bounds.centerY() && pointer[0] != null) {
				container = pointer[0].findContainer(visit_tag, x, y);
			}
			if (container == null && y >= bounds.centerY() && pointer[1] != null) {
				container = pointer[1].findContainer(visit_tag, x, y);
			}
		}
		if (container == null && x >= bounds.centerX()){
			if (y <= bounds.centerY() && pointer[2] != null) {
				container = pointer[2].findContainer(visit_tag, x, y);
			}
			if (container == null && y >= bounds.centerY() && pointer[3] != null) {
				container = pointer[3].findContainer(visit_tag, x, y);
			}
		}
		
		visitor = null;
		synchronized(visit_tag) {
			visit_tag.notifyAll();
		}
		
		return container;
	}
	
	float measureDistance(float x, float y) {
		float dx = bounds.centerX() - x;
		float dy = bounds.centerY() - y;
		return dx*dx + dy*dy;
	}
	
	protected RectF getRect() {
		return bounds;
	}
	
	/*abstract public boolean contains(float x, float y);
	abstract public void onClick();*/
}
