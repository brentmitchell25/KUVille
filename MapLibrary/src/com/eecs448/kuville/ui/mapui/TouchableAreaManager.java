package com.eecs448.kuville.ui.mapui;

import android.view.View;

import com.eecs448.kuville.ui.mapui.areagraph.TouchableAreaGraph;
import com.eecs448.kuville.ui.mapui.areagraph.TouchableAreaGraphNode;
import com.eecs448.kuville.ui.mapui.touch.BuildingMapAreaBox;
import com.eecs448.kuville.ui.mapui.touch.DummyMapAreaBox;
/**
 * @written Jamie Robinson
 * @tested Jamie Robinson and Brent Mitchell
 * @debugged Jamie Robinson
 */
public class TouchableAreaManager {
	private static TouchableAreaGraph graph;

	public TouchableAreaManager() {
		graph = new TouchableAreaGraph();
		graph.addNode(new BuildingMapAreaBox("Eaton Hall", 0, 1645, 1132, 1923,
				1306));
		graph.addNode(new BuildingMapAreaBox("Allen Fieldhouse", 1, 1631, 1556,
				1935, 1761));
		graph.addNode(new BuildingMapAreaBox("Green Hall", 2, 1708, 1322, 1791,
				1438));
		graph.addNode(new BuildingMapAreaBox("Kansas Union", 3, 2697, 937,
				2810, 1078));
		graph.addNode(new BuildingMapAreaBox("Malott Hall", 4, 2196, 1303,
				2317, 1426));
		graph.addNode(new BuildingMapAreaBox("Strong Hall", 5, 2263, 1101,
				2411, 1233));

		// graph.addNode(new BuildingMapAreaBox("memorial_stadium", 2352, 452,
		// 2594, 751));
		// graph.addNode(new BuildingMapAreaBox("robinson", 2061, 1548, 2214,
		// 1692));
		// graph.addNode(new BuildingMapAeaBox("daisy", 1163, 1303, 1427,
		// 2043));
		// graph.addNode(new BuildingMapAreaBox("fraiser", 2698, 1234, 2752,
		// 1359));
		// graph.addNode(new BuildingMapAreaBox("watson", 2564, 1329, 2680,
		// 1414));
		// graph.addNode(new BuildingMapAreaBox("nichols", 740, 1855, 848,
		// 1945));
		graph.addNode(new DummyMapAreaBox(-5, -5, -3, -3));
		graph.addNode(new DummyMapAreaBox(3249, -4, 3259, -2));
		graph.addNode(new DummyMapAreaBox(-5, 2942, -3, 2944));
		graph.addNode(new DummyMapAreaBox(3249, 2942, 3259, 2944));

		graph.buildGraph();
	}

	public boolean dispatchClick(View ms, float x, float y) {
		TouchableAreaGraphNode area = graph.findContainer(x, y);
		if (area != null) {
			area.onClick(ms);
			return true;
		} else {
			return false;
		}
	}

}
