package com.eecs448.kuville;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

/**
 * @written Brent Mitchel
 * @tested Brent Mitchell
 * @debugged Brent Mitchell
 * @credit Facebook Developer Website
 */
public class FacebookFeeds {

	public static void facebookQuery(String sourceid,
			final LinearLayout layout, final Context c) {
		String fqlQuery = "{"
				+ "'query1': 'SELECT post_id,actor_id,message FROM stream WHERE source_id = "
				+ sourceid + " LIMIT 13'" + "}";
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);

		Session session = Session.getActiveSession();

		// Gets, parses, and sets Facebook feeds
		Request request = new Request(session, "/fql", params, HttpMethod.GET,
				new Request.Callback() {
					public void onCompleted(Response response) {
						GraphObject graphObject = response.getGraphObject();
						if (graphObject != null) {
							StringBuilder userInfo = new StringBuilder("");
							StoredFacebookFeeds db = new StoredFacebookFeeds(c);
							db.open();
							// Parses JSON object into string, then sets the
							// text to display it
							try {

								GraphObject go = response.getGraphObject();
								JSONObject jso = go.getInnerJSONObject();
								JSONArray data = jso.getJSONArray("data")
										.getJSONObject(0)
										.getJSONArray("fql_result_set");

								for (int i = 0; i < data.length(); i++) {
									JSONObject query1 = data.getJSONObject(i);
									if (!query1.getString("message").equals("")) {
										userInfo.append(query1
												.getString("message") + "\n\n");
										db.createEntry(query1
												.getString("post_id"));
										setView(userInfo, layout, c, db,
												query1.getString("post_id"));
										userInfo.delete(0, userInfo.length());
									}

								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							db.close();

						}
					}

				});

		Request.executeBatchAsync(request);

	}

	private static void setView(StringBuilder userInfo,
			final LinearLayout layout, Context c, StoredFacebookFeeds db,
			String post_id) {
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		CheckedTextView tv = new CheckedTextView(c);
		tv.setLayoutParams(lp);
		tv.setText(userInfo);
		// Instead of storing entire post, stores shorter post_id
		// to save storage (which is unique to each post)
		tv.setTag(post_id);
		if (db.isRead(post_id))
			tv.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
		else
			tv.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
		tv.setBackgroundResource(R.drawable.background);
		setListener(tv);

		layout.addView(tv);
		ImageView like = new ImageView(c);
		ImageView comment = new ImageView(c);
		ImageView share = new ImageView(c);

		like.setImageResource(R.drawable.like);
		comment.setImageResource(R.drawable.comment);
		share.setImageResource(R.drawable.share);

		LinearLayout llAlso = new LinearLayout(c);
		llAlso.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		llAlso.setOrientation(LinearLayout.HORIZONTAL);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
		like.setLayoutParams(params);
		comment.setLayoutParams(params);
		share.setLayoutParams(params);

		llAlso.addView(like);
		llAlso.addView(comment);
		llAlso.addView(share);
		layout.addView(llAlso);

	}

	private static void setListener(CheckedTextView tv) {
		tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				CheckedTextView tv = (CheckedTextView) view;

				tv.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
				ViewGroup parent = (ViewGroup) tv.getParent();
				int index = parent.indexOfChild(tv);
				parent.removeView(tv);
				parent.addView(tv, index);

				StoredFacebookFeeds db = new StoredFacebookFeeds(view
						.getContext());

				db.open();
				if (!db.isRead(tv.getTag().toString())) {
					db.setRead(tv.getTag().toString());

					Context context = view.getContext();
					SharedPreferences sharedPreferences = context
							.getApplicationContext()
							.getSharedPreferences("com.eecs448.kuville",
									Context.MODE_PRIVATE);

					SharedPreferences.Editor editor = sharedPreferences.edit();
					int gold = sharedPreferences.getInt("gold", 0);
					editor.putInt("gold", gold + 5);
					editor.commit();

					View topView = (View) view.getParent().getParent()
							.getParent().getParent().getParent();
					TextView goldTextview = (TextView) topView
							.findViewById(R.id.gold);

					goldTextview.setText(Integer.toString(gold + 5));
				}
				db.close();

			}
		});
	}
}
