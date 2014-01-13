package com.eecs448.kuville.ui.mapui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * @written Jamie Robinson
 * @tested Jamie Robinson
 * @debugged Jamie Robinson
 */
public class Map {

	// Constant to determine how far we should zoom in past a 1:1
	// ratio between pixels and screen dimensions
	private static final int OVER_SCALE_ZOOM = 2;

	// Variables to identify the reference map image
	private Resources res;
	private int res_id;

	// A reference image to hold the full map.
	private Bitmap reference_map;

	// Options for decoding the reference_map
	private BitmapFactory.Options reference_options;

	// The cropped and resized image of the map drawn to the screen.
	private Bitmap map;

	// Reference_map image unscaled width and height.
	private int reference_default_width, reference_default_height;

	// Width and height of the view we are drawing to. Also the
	// size of the map Bitmap
	private int subset_width, subset_height;

	private float rx1, ry1, rx2, ry2;
	private int w, h;

	private TouchableAreaManager area_manager;

	// private float cx, cy;

	// Constructs a new map based on the resource and id
	public Map(Resources res, int id, int init_res_x_top, int init_res_y_top,
			int init_res_x_bot, int init_res_y_bot, int view_width,
			int view_height) {

		this.res = res;
		res_id = id;

		initRegion(init_res_x_top, init_res_y_top, init_res_x_bot,
				init_res_y_bot);

		subset_width = view_width;
		subset_height = view_height;

		initializeReferenceOptions();
		reloadReference(init_res_x_bot - init_res_x_top, init_res_y_bot
				- init_res_y_top);
		// reference_map = BitmapFactory.decodeResource(res, res_id,
		// reference_options);

		map = Bitmap
				.createBitmap(subset_width, subset_height, Config.ARGB_8888);

		area_manager = new TouchableAreaManager();
	}

	// Constructs a new map based on the resource and id. Initially centers the
	// view.
	public Map(Resources res, int id, int view_width, int view_height) {

		this.res = res;
		res_id = id;

		subset_width = view_width;
		subset_height = view_height;

		initializeReferenceOptions();

		initRegion((reference_default_width - subset_width) / 2,
				(reference_default_height - subset_height) / 2,
				(reference_default_width + subset_width) / 2,
				(reference_default_height + subset_height) / 2);

		reloadReference(w, h);
		// reference_map = BitmapFactory.decodeResource(res, res_id,
		// reference_options);

		map = Bitmap
				.createBitmap(subset_width, subset_height, Config.ARGB_8888);

		area_manager = new TouchableAreaManager();
	}

	public float transformScreenX(float x) {
		Log.v("Coord X", "Coord: " + (rx1 + (x * w) / subset_width));
		return rx1 + (x * w) / subset_width;
	}

	public float transformScreenY(float y) {
		Log.v("Coord Y", "Coord: " + (ry1 + (y * h) / subset_height));
		return ry1 + (y * h) / subset_height;
	}

	public float getAreaWidth() {
		return rx2 - rx1;
	}

	public float getAreaHeight() {
		return ry2 - ry1;
	}

	public TouchableAreaManager getTouchManager() {
		return area_manager;
	}

	// A setRegion function that is guaranteed to set the variables.
	// WARNING: Use with caution.
	private synchronized void initRegion(int x_top, int y_top, int x_bot,
			int y_bot) {
		rx1 = x_top;
		ry1 = y_top;
		rx2 = x_bot;
		ry2 = y_bot;

		/*
		 * x1 = x_top; y1 = y_top; x2 = x_bot; y2 = y_bot;
		 */

		w = (int) (rx2 - rx1);
		h = (int) (ry2 - ry1);

		/*
		 * cx = w/2f + rx1; cy = h/2f + ry1;
		 */
	}

	public synchronized void setRegion(float x_top, float y_top, float x_bot,
			float y_bot) {
		if (x_top >= 0 && x_bot <= reference_default_width) {
			rx1 = x_top;
			rx2 = x_bot;
			/*
			 * x1 = (int)x_top; x2 = (int)x_bot;
			 */
		}

		if (y_top >= 0 && y_bot <= reference_default_height) {
			ry1 = y_top;
			ry2 = y_bot;
			/*
			 * y1 = (int)y_top; y2 = (int)y_bot;
			 */
		}

		w = (int) (rx2 - rx1);
		h = (int) (ry2 - ry1);

		/*
		 * cx = w/2f + x1; cy = h/2f + y1;
		 */
	}

	public synchronized void moveRegion(float dx_top, float dy_top,
			float dx_bot, float dy_bot) {
		float new_x1 = dx_top + rx1;
		float new_y1 = dy_top + ry1;
		float new_x2 = dx_bot + rx2;
		float new_y2 = dy_bot + ry2;

		float new_width = new_x2 - new_x1;
		float new_height = new_y2 - new_y1;

		if (new_width > reference_default_width
				|| new_height > reference_default_height)
			return;

		if (new_width >= subset_width / OVER_SCALE_ZOOM) {
			if (new_x1 < 0) {
				new_x2 -= new_x1;
				new_x1 = 0;
			} else if (new_x2 >= reference_default_width) {
				double width = new_x2 - reference_default_width;
				new_x1 -= width;
				new_x2 -= width;
			}
		} else {
			new_x1 = rx1;
			new_x2 = rx1 + subset_width / OVER_SCALE_ZOOM;
		}

		if (new_height >= subset_height / OVER_SCALE_ZOOM) {
			if (new_y1 < 0) {
				new_y2 -= new_y1;
				new_y1 = 0;
			} else if (new_y2 >= reference_default_height) {
				double height = new_y2 - reference_default_height;
				new_y1 -= height;
				new_y2 -= height;
			}
		} else {
			new_y1 = ry1;
			new_y2 = ry1 + subset_height / OVER_SCALE_ZOOM;
		}

		setRegion(new_x1, new_y1, new_x2, new_y2);
	}

	/*
	 * public synchronized void rotate90() { int[] temp = {x1, y1, x2, y2};
	 * 
	 * /*x1 = (int)(cx - cy) + temp[1]; y1 = (int)(cy - cx) + temp[0]; x2 =
	 * (int)(cx - cy) + temp[3]; y2 = (int)(cy - cx) + temp[2];
	 */

	/*
	 * x1 = (int)(3*(temp[0]-cx) + 2*(cy-temp[1])); y1 = (int)(3*(temp[1]-cy) +
	 * 2*(cx-temp[0]));
	 * 
	 * x2 = (int)(3*(temp[2]-cx) + 2*(cy-temp[3])); y2 = (int)(3*(temp[3]-cy) +
	 * 2*(cx-temp[2]));
	 * 
	 * int cxmy = (int)(cx - cy); int cymx = (int)(cy - cx);
	 * 
	 * x1 = cxmy + temp[1]; y1 = cymx + temp[0]; x2 = cxmy + temp[3]; y2 = cymx
	 * + temp[2];
	 * 
	 * if (isLandscape()) { Log.v("center","rotation horiz -> vert"); x1 =
	 * (int)(cy + cx) - temp[3]; y1 = (int)(cy - cx) + temp[0]; x2 = (int)(cy +
	 * cx) - temp[1]; y2 = (int)(cy - cx) + temp[2]; } else {
	 * Log.v("center","rotation vert -> horiz");
	 * 
	 * x1 = (int)(cx - cy) + temp[1]; y1 = (int)(cx + cy) - temp[2]; x2 =
	 * (int)(cx - cy) + temp[3]; y2 = (int)(cx + cy) - temp[0]; }
	 * 
	 * int temp2 = subset_width; subset_width = subset_height; subset_height =
	 * temp2;
	 * 
	 * w = x2 - x1; h = y2 - y1; Log.v("center","rotation cent-x: " + cx + " | "
	 * + (w/2f + x1)); Log.v("center","rotation cent-y: " + cy + " | " + (h/2f +
	 * y1));
	 * 
	 * Log.v("rotate", "rotate top (" + temp[0] + ", " + temp[1] + ") -> (" + x1
	 * + ", " + y1 + ")"); Log.v("rotate", "rotate bot (" + temp[2] + ", " +
	 * temp[3] + ") -> (" + x2 + ", " + y2 + ")");
	 * 
	 * //Debug.Assert(cx == w/2f + x1, "Fail: " + cx + " | " + (w/2f + x1));
	 * 
	 * //reloadReference(w, h);
	 * 
	 * map = Bitmap.createBitmap(subset_width, subset_height, Config.ARGB_8888);
	 * 
	 * transformImage(); }
	 */

	public synchronized void transformImage() {
		reloadReference(w, h);

		// Multipliers to rescale the points to the current scale of
		// reference_map after we reload it
		final float multiplier1 = reference_options.outWidth
				/ ((float) reference_default_width);
		final float multiplier2 = reference_options.outHeight
				/ ((float) reference_default_height);

		// scale points store as a temporary integer
		final int tx1 = (int) (rx1 * multiplier1);
		final int ty1 = (int) (ry1 * multiplier2);
		final int tx2 = (int) (rx2 * multiplier1);
		final int ty2 = (int) (ry2 * multiplier2);

		// store scaled width and height in a temporary integer
		final int tw = (tx2 - tx1);
		final int th = (ty2 - ty1);

		int raw_x;
		int raw_y;

		// now we get the multipliers to map the raw x and y coordinates to fit
		// the screen
		final float scale_w = ((float) tw) / subset_width;
		final float scale_h = ((float) th) / subset_height;

		// Scale the image
		// Read from reference_map and write to map one row of pixels at a time
		// to save
		// memory to help prevent the OOM errors that plague Bitmaps.
		int[] pixels = new int[subset_width];
		int[] raw_pixels = new int[tw];

		// draw the bitmap
		for (int y = 0; y < subset_height; y++) {
			raw_y = (int) (y * scale_h);
			reference_map.getPixels(raw_pixels, 0, tw, tx1, ty1 + raw_y, tw, 1);

			for (int x = 0; x < subset_width; x++) {
				raw_x = (int) (x * scale_w);
				pixels[x] = raw_pixels[raw_x];
			}

			map.setPixels(pixels, 0, subset_width, 0, y, subset_width, 1);
		}
	}

	public final Bitmap getMapBitmap() {
		return map;
	}

	private void initializeReferenceOptions() {
		reference_options = new BitmapFactory.Options();

		reference_options.inInputShareable = true;
		reference_options.inPurgeable = true;
		reference_options.inScaled = false;

		// Set the outWidth and outHeight to the width and height of the
		// original image
		reference_options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, res_id, reference_options);
		reference_options.inJustDecodeBounds = false;

		// store them
		reference_default_width = reference_options.outWidth;
		reference_default_height = reference_options.outHeight;

		// Set impossible sample size to force reloadReference to load the image
		// on
		// its first pass
		reference_options.inSampleSize = -1;
	}

	private synchronized boolean reloadReference(int area_width, int area_height) {
		// TODO: Make the n added to the calculated sample size dependent on
		// screen pixel
		// density.
		int calculated_sample_size = calculateSampleSize(area_width,
				area_height) + 1;

		if (calculated_sample_size != reference_options.inSampleSize
				&& calculated_sample_size >= 1) {
			reference_options.inSampleSize = calculated_sample_size;
			reference_map = BitmapFactory.decodeResource(res, res_id,
					reference_options);
			return true;
		} else {
			return false;
		}
	}

	private int calculateSampleSize(int area_width, int area_height) {
		int ratio_width = area_width / subset_width;
		int ratio_height = area_height / subset_height;

		if (ratio_width < ratio_height) {
			return Integer.SIZE - Integer.numberOfLeadingZeros(ratio_width);
		} else {
			return Integer.SIZE - Integer.numberOfLeadingZeros(ratio_height);
		}
	}
}
