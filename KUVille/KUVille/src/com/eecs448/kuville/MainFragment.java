package com.eecs448.kuville;

import java.util.Arrays;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.eecs448.kuville.start.MapActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

/**
 * @written Brent Mitchel
 * @tested Brent Mitchell
 * @debugged Brent Mitchell
 * @credit Facebook Developers
 */
public class MainFragment extends Fragment {

	private UiLifecycleHelper uiHelper;
	private Button next;
	private ImageView twitterLogin;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.login, container, false);

		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.authButton);
		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays.asList("user_likes",
				"user_status", "user_groups", "friends_groups"));
		next = (Button) view.findViewById(R.id.next);

		twitterLogin = (ImageView) view.findViewById(R.id.btnLoginTwitter);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		SharedPreferences settings = getActivity().getSharedPreferences(
				"MyPrefs", 0);
		Boolean firstRun = settings.getBoolean("firstRunUpgrades", true);

		if (firstRun) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("firstRunUpgrades", false);
			editor.commit();
			Upgrades upgrades = new Upgrades(getActivity());
			upgrades.createUpgrades();
		}

		setTwitterImage();

		goToMap(true);

		/**
		 * Twitter login button click event will call loginToTwitter() function
		 * */
		twitterLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (TwitterLoginLogout.isTwitterLoggedInAlready(getActivity()))
					TwitterLoginLogout.logoutFromTwitter(getActivity());
				else
					TwitterLoginLogout.loginToTwitter(getActivity());
				setTwitterImage();
			}
		});

		next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				goToMap(false);

			}
		});
		TwitterLoginLogout.parseURI(getActivity());
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
		goToMap(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
		setTwitterImage();
		goToMap(true);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onStop() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onStart() {
		super.onPause();
		setTwitterImage();
		goToMap(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			next.setVisibility(View.VISIBLE);
		} else if (state.isClosed()) {
			next.setVisibility(View.VISIBLE);
		}
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void goToMap(boolean automaticLogin) {
		Session session = Session.getActiveSession();
		// Allows users to skip zero or more logins
		if (session != null && session.getState().isOpened()
				&& TwitterLoginLogout.isTwitterLoggedInAlready(getActivity())
				&& automaticLogin) {
			startActivity(new Intent(getActivity(), MapActivity.class));
			// startActivity(new Intent(getActivity(), Map.class));
		} else if (!automaticLogin) { // Skips the login page if both Twitter
										// and Facebook are logged in
			startActivity(new Intent(getActivity(), MapActivity.class));
			// startActivity(new Intent(getActivity(), Map.class));
		}
	}

	private void setTwitterImage() {
		twitterLogin.setVisibility(View.VISIBLE);
		if (!TwitterLoginLogout.isTwitterLoggedInAlready(getActivity()))
			twitterLogin.setImageResource(R.drawable.twitter_login);
		else
			twitterLogin.setImageResource(R.drawable.twitter_logout);
	}
}
