package com.eecs448.kuville;

import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @written Brent Mitchel
 * @tested Brent Mitchell
 * @debugged Brent Mitchell
 * @credit Twitter-4j Example
 */
public class TwitterFeeds {
	private static final String TWITTER_CONSUMER_KEY = "sT9517z5AY5NEaAF29yChQ";
	private static final String TWITTER_CONSUMER_SECRET = "nFDHQwTWN9IsOFXtT6SmDql3kEiRppiw4FSfHOtMv8";
	static final String PREF_KEY_OAUTH_TOKEN = "45879105-M5MwhX10t33ff1fMYQmzlmNalrgxBC9wQ4mMebfxZ";
	static final String PREF_KEY_OAUTH_SECRET = "6zFJfb9UDsc4w3deL6HMNzRp0UJ4r5wLQS2rhFR4DlzQS";

	public static void twitterFeeds(String find, LinearLayout layout, Context c) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
		builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
		builder.setOAuthAccessToken(PREF_KEY_OAUTH_TOKEN);
		builder.setOAuthAccessTokenSecret(PREF_KEY_OAUTH_SECRET);
		TwitterFactory factory = new TwitterFactory(builder.build());
		Twitter twitter = factory.getInstance();
		StoredTwitterFeeds db = new StoredTwitterFeeds(c);
		db.open();
		try {
			// Gets tweets, parses, and displays tweets
			Query query = new Query(find);
			QueryResult result = twitter.search(query);
			List<twitter4j.Status> tweets = result.getTweets();
			for (twitter4j.Status tweet : tweets) {
				StringBuilder feeds = new StringBuilder();
				feeds.append("<b>" + tweet.getUser().getName() + "</b>" + " @"
						+ tweet.getUser().getScreenName() + "<br />"
						+ tweet.getText());

				db.createEntry(tweet.getText());
				setView(feeds, layout, c, db, tweet.getText());

			}

		} catch (Exception te) {
			te.printStackTrace();

		}
		db.close();
	}

	private static void setView(StringBuilder userInfo,
			final LinearLayout layout, Context c, StoredTwitterFeeds db,
			String tweet) {
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		CheckedTextView ctv = new CheckedTextView(c);
		TextView tv = new TextView(c);
		ctv.setLayoutParams(lp);
		tv.setLayoutParams(lp);
		ctv.setText(Html.fromHtml(userInfo.toString()));
		ctv.setTag(tweet);
		if (db.isRead(tweet))
			ctv.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
		else
			ctv.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
		ctv.setBackgroundResource(R.drawable.background);
		setListener(ctv);

		layout.addView(ctv);

	}

	private static void setListener(CheckedTextView ctv) {
		ctv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				CheckedTextView ctv = (CheckedTextView) view;
				ctv.setChecked(true);
				ctv.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
				ViewGroup parent = (ViewGroup) ctv.getParent();
				int index = parent.indexOfChild(ctv);
				parent.removeView(ctv);
				parent.addView(ctv, index);

				StoredTwitterFeeds db = new StoredTwitterFeeds(view
						.getContext());
				db.open();
				if (!db.isRead(ctv.getTag().toString())) {
					db.setRead(ctv.getTag().toString());

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
