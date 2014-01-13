package com.eecs448.kuville.ui;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

import com.eecs448.kuville.ui.mapui.Map;

/**
 * @written Jamie Robinson
 * @tested Jamie Robinson and Brent Mitchell
 * @debugged Jamie Robinson
 */
public class MapTouchHandler {
	public static final byte STATE_RESET = (byte) 0xff;
	public static final byte STATE_BIT_ACTIVE = 0x1;
	public static final byte STATE_BIT_MULTI_ACTIVE = 0x2;
	public static final byte STATE_BIT_MULTI_PAST = 0x4;
	public static final byte STATE_BIT_MOVED = 0x8;

	private static byte state = ~STATE_RESET;

	private static ReusableCoord touch_center = new ReusableCoord();
	private static ReusableCoord distance_measure = new ReusableCoord();
	private static int last_touches;

	public static boolean handleTouch(MapSurface s, Map m, MotionEvent e) {
		int action = MotionEventCompat.getActionMasked(e);
		int touches = MotionEventCompat.getPointerCount(e);

		switch (action) {
		case (MotionEvent.ACTION_POINTER_DOWN): {
			setStateBit(STATE_BIT_MULTI_ACTIVE);
			setStateBit(STATE_BIT_MULTI_PAST);
		}
		case (MotionEvent.ACTION_DOWN): {
			setStateBit(STATE_BIT_ACTIVE);

			// active_list.add(new TouchCoord((int)MotionEventCompat.getX(e,
			// index), (int)MotionEventCompat.getY(e, index)));
			updateCenter(e, true);

			return true;
		}
		case (MotionEvent.ACTION_POINTER_UP): {
			if (touches == 2) {
				unSetStateBit(STATE_BIT_MULTI_ACTIVE);
			}
		}
		case (MotionEvent.ACTION_UP): {
			if (touches == 1) {
				if (!(checkState(STATE_BIT_MULTI_PAST) || checkState(STATE_BIT_MOVED))) {
					m.getTouchManager().dispatchClick(s,
							m.transformScreenX(e.getX()),
							m.transformScreenY(e.getY()));
					Log.v("Touch info", "Clicked!");
				}
				unSetStateBit(STATE_RESET);
				last_touches = 0;
			}
			// active_list.remove(index);
			touch_center.resetCoord();
			distance_measure.resetCoord();
			updateCenter(e, true);

			return true;
		}
		case (MotionEvent.ACTION_MOVE): {
			// active_list.get(index).update((int) MotionEventCompat.getX(e,
			// index), (int) MotionEventCompat.getY(e, index));
			if (last_touches == touches) {
				updateCenter(e, false);
			} else {
				updateCenter(e, true);
			}

			last_touches = touches;

			if (touch_center.getDX() != 0 || touch_center.getDY() != 0
					|| distance_measure.getDX() != 0
					|| distance_measure.getDY() != 0) {
				setStateBit(STATE_BIT_MOVED);

				float sx = m.getAreaHeight() / m.getAreaWidth();
				float sy = m.getAreaWidth() / m.getAreaHeight();

				float dx = (float) (1 / Math.sqrt(1 + sx * sx));
				float dy = (float) (1 / Math.sqrt(1 + sy * sy));

				m.moveRegion(
						distance_measure.getDX() * dx - touch_center.getDX(),
						distance_measure.getDX() * dy - touch_center.getDY(),
						-(distance_measure.getDX() * dx + touch_center.getDX()),
						-(distance_measure.getDX() * dy + touch_center.getDY()));

				if (MapSurface.getInstance().getState() == MapSurface.RenderSurfaceThread.THREAD_IDLE) {
					MapSurface.getInstance().requestRedraw();
				}
			}
			// m.moveRegion(distance_measure.getDX(), distance_measure.getDX(),
			// -distance_measure.getDX(), -distance_measure.getDX());
			return true;
		}
		case (MotionEvent.ACTION_CANCEL): {
			return true;
		}
		default: {
			Log.w("Touch Event", "Touch event not handled. Action Mask: "
					+ action);
		}
		}

		return false;
	}

	// Used for testing. Reads
	private static String decompose(byte b) {
		String decomp = new String();

		for (int i = Byte.SIZE - 1; i >= 0; i--) {
			decomp += (b >> i) & 1;
		}

		return decomp;
	}

	private static boolean checkState(byte bit) {
		return (state & bit) > 0;
	}

	private static void setStateBit(byte bit) {
		byte temp_state = state;
		state |= bit;
		Log.v("State Change", "state added: " + decompose(bit) + " | "
				+ decompose(temp_state) + "->" + decompose(state));
	}

	private static void unSetStateBit(byte bit) {
		byte temp_state = state;
		state &= ~bit;
		Log.v("State Change", "state removed: " + decompose(bit) + " | "
				+ decompose(temp_state) + "->" + decompose(state));
	}

	private static void updateCenter(MotionEvent e, boolean double_update) {
		int sumx = 0;
		int sumy = 0;
		// int list_size = active_list.size();

		final int touches = e.getPointerCount();
		if (touches > 0) {
			for (int i = 0; i < touches; i++) {
				sumx += MotionEventCompat.getX(e, i);
				sumy += MotionEventCompat.getY(e, i);
			}

			// Log.d("thing", "check: " + touch_center.update(sumx/touches,
			// sumy/touches));
			if (double_update) {
				touch_center.updateFull(sumx / touches, sumy / touches);
			} else {
				touch_center.update(sumx / touches, sumy / touches);
			}
			if (touches > 1) {
				// Log.v("Reach", "Reached");
				final int cx = touch_center.getX();
				final int cy = touch_center.getY();

				double sum = 0;

				for (int i = 0; i < touches; i++) {
					int x = cx - (int) MotionEventCompat.getX(e, i);
					int y = cy - (int) MotionEventCompat.getY(e, i);

					sum += Math.sqrt(x * x + y * y);
				}

				if (double_update) {
					distance_measure.updateFull((int) (sum / touches), 0);
				} else {
					distance_measure.update((int) (sum / touches), 0);
				}
			}
		} else {
			touch_center.resetCoord();
			distance_measure.resetCoord();
		}
	}

	private static class TouchCoord {
		protected int x, y, prev_x, prev_y;

		public TouchCoord(int x, int y) {
			this.x = x;
			this.y = y;

			prev_x = -1;
			prev_y = -1;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getDX() {
			if (prev_x != -1) {
				return x - prev_x;
			} else {
				return 0;
			}
		}

		public int getDY() {
			if (prev_y != -1) {
				return y - prev_y;
			} else {
				return 0;
			}
		}

		public boolean update(int new_x, int new_y) {
			if (new_x >= 0 && new_y >= 0) {
				prev_x = x;
				x = new_x;

				prev_y = y;
				y = new_y;

				return true;
			} else {
				return false;
			}
		}

		public boolean updateFull(int new_x, int new_y) {
			if (new_x >= 0 && new_y >= 0) {
				prev_x = new_x;
				x = new_x;

				prev_y = new_y;
				y = new_y;

				return true;
			} else {
				return false;
			}
		}

	}

	private static class ReusableCoord extends TouchCoord {
		public ReusableCoord() {
			super(-1, -1);
		}

		public void resetCoord() {
			x = -1;
			y = -1;
			prev_x = -1;
			prev_y = -1;
		}
	}
}
