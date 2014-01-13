package com.eecs448.kuville.ui.mapui.areagraph;

import java.util.ArrayList;
import java.util.LinkedList;

import android.graphics.RectF;
import android.util.Log;
/**
 * @written Jamie Robinson
 * @tested Jamie Robinson
 * @debugged Jamie Robinson
 */
public class TouchableAreaGraph {
	private TouchableAreaGraphNode pivot;
	private LinkedList<TouchableAreaGraphNode> area_list;
	
	private boolean built;
	
	public TouchableAreaGraph() {
		built = false;
		pivot = null;
		area_list = new LinkedList<TouchableAreaGraphNode>();
	}
	
	public void addNode(TouchableAreaGraphNode node) {
		if (!built && node != null) {
			area_list.add(node);
		}
	}
	
	public synchronized void buildGraph() {
		if (!built && !area_list.isEmpty()) {
			LinkedList<Integer> pivot_scores = new LinkedList<Integer>();
			for (TouchableAreaGraphNode n : area_list) {
				
				@SuppressWarnings("unchecked")
				LinkedList<TouchableAreaGraphNode> exclusive = (LinkedList<TouchableAreaGraphNode>) area_list.clone();
				exclusive.remove(n);
				
				ArrayList<LinkedList<TouchableAreaGraphNode>> quadrants = 
						new ArrayList<LinkedList<TouchableAreaGraphNode>>(4);
				
				TouchableAreaGraphNode[] nearest_list = new TouchableAreaGraphNode[4];
				
				int min_quad_size = Integer.MAX_VALUE;
				int max_quad_size = 0;
				
				RectF n_rect = n.getRect();
				
				for (int i = 0; i < 4; i++) {
					quadrants.add(new LinkedList<TouchableAreaGraphNode>());
				}
				
				for (TouchableAreaGraphNode n2 : exclusive) {
					RectF n2_rect = n2.getRect();
					
					if (n_rect.centerX() > n2_rect.centerX()) {
						if (n_rect.centerY() > n2_rect.centerY()) {
							quadrants.get(0).add(n2);
						} else {
							quadrants.get(1).add(n2);
						}
					} else {
						if (n_rect.centerY() > n2_rect.centerY()) {
							quadrants.get(2).add(n2);
						} else {
							quadrants.get(3).add(n2);
						}
					}
				}
				
				for (int i = 0; i < 4; i++) {
					LinkedList<TouchableAreaGraphNode> working = quadrants.get(i);
					
					TouchableAreaGraphNode nearest = null;

					float near_dist = Float.MAX_VALUE;

					if (working.size() > max_quad_size) {
						max_quad_size = working.size();
					} else if (working.size() < min_quad_size) {
						min_quad_size = working.size();
					}

					for (TouchableAreaGraphNode n2 : working) {

						RectF n2_rect = n2.getRect();
						float dx = n2_rect.centerX() - n_rect.centerX();
						float dy = n2_rect.centerY() - n_rect.centerY();
						float n2_dist = dx*dx + dy*dy;
						
						if (near_dist > n2_dist) {
							near_dist = n2_dist;
							nearest = n2;
						}
					}
					
					nearest_list[i] = nearest;
				}
				
				n.entangle(nearest_list);
				
				pivot_scores.add(Integer.valueOf(max_quad_size - min_quad_size));
			}
			
			int min_score = Integer.MAX_VALUE;
			int min_score_index = 0;
			int index = 0;
			
			for (Integer i : pivot_scores) {
				if (i.intValue() < min_score) {
					min_score_index = index;
					min_score = i.intValue();
				}
				
				index++;
			}
			
			pivot = area_list.get(min_score_index);
			
			built = true;
		}
	}
	
	public TouchableAreaGraphNode findContainer(float x, float y) {
		if (built) {
			Log.v("Traversal", "Traversal - Pivot: " + pivot.toString());
			return pivot.findContainer(new Object(), x, y);
		} else {
			return null;
		}
	}
}
