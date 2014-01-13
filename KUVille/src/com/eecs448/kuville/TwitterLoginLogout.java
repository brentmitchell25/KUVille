package com.eecs448.kuville;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Brent Mitchell
 * @credit Twitter-4j example
 */
public class TwitterLoginLogout {
	static String TWITTER_CONSUMER_KEY = "sT9517z5AY5NEaAF29yChQ";
	static String TWITTER_CONSUMER_SECRET = "nFDHQwTWN9IsOFXtT6SmDql3kEiRppiw4FSfHOtMv8";

	// Preference Constants
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";

	final public static String CALLBACK_SCHEME = "x-oauthflow-twitter";
	final public static String TWITTER_CALLBACK_URL = CALLBACK_SCHEME
			+ "://callback";

	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	private static Twitter twitter;
	private static RequestToken requestToken;

	public static void logoutFromTwitter(Context c) {
		SharedPreferences mSharedPreferences = c.getSharedPreferences("MyPref",
				0);
		// Clear the shared preferences
		Editor e = mSharedPreferences.edit();
		e.remove(PREF_KEY_OAUTH_TOKEN);
		e.remove(PREF_KEY_OAUTH_SECRET);
		e.remove(PREF_KEY_TWITTER_LOGIN);
		e.commit();
	}

	/**
	 * Function to login twitter
	 * */
	public static void loginToTwitter(Context c) {

		// Check if already logged in
		if (!isTwitterLoggedInAlready(c)) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
			builder.setOAuthAccessToken(PREF_KEY_OAUTH_TOKEN);
			// builder.setOAuthAccessTokenSecret(PREF_KEY_OAUTH_SECRET);
			Configuration configuration = builder.build();

			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try {
				requestToken = twitter
						.getOAuthRequestToken(TWITTER_CALLBACK_URL);
				if (requestToken != null)
					c.startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(requestToken.getAuthenticationURL())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// user already logged into twitter
			Toast.makeText(c, "Already Logged into twitter", Toast.LENGTH_LONG)
					.show();

		}

	}

	/**
	 * Check user already logged in your application using twitter Login flag is
	 * fetched from Shared Preferences
	 * */
	public static boolean isTwitterLoggedInAlready(Context c) {
		SharedPreferences mSharedPreferences = c.getSharedPreferences("MyPref",
				0);
		// return twitter login status from Shared Preferences
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	public static void parseURI(Context c) {
		/**
		 * This if conditions is tested once is redirected from twitter page.
		 * Parse the uri to get oAuth Verifier
		 * */
		if (!isTwitterLoggedInAlready(c)) {
			SharedPreferences mSharedPreferences = c.getSharedPreferences(
					"MyPref", 0);
			Uri uri = ((Activity) c).getIntent().getData();
			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				// oAuth verifier
				String verifier = uri
						.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

				try {
					// Get the access token
					AccessToken accessToken = twitter.getOAuthAccessToken(
							requestToken, verifier);

					// Shared Preferences
					Editor e = mSharedPreferences.edit();

					// After getting access token, access token secret
					// store them in application preferences
					e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
					e.putString(PREF_KEY_OAUTH_SECRET,
							accessToken.getTokenSecret());
					// Store login status - true
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
					e.commit(); // save changes

					Log.e("Twitter OAuth Token", "> " + accessToken.getToken());

				} catch (Exception e) {
					// Check log for login errors
					Log.e("Twitter Login Error", "> " + e.getStackTrace());
				}

			}
		}
	}

}
