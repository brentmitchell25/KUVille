package com.eecs448.kuville;

import android.content.Context;

import com.facebook.Session;

/**
 * @written Brent Mitchel
 * @tested Brent Mitchell
 * @debugged Brent Mitchell
 */
public class LogoutSocialMedia {
	public static void logout(Context c) {
		if (TwitterLoginLogout.isTwitterLoggedInAlready(c))
			TwitterLoginLogout.logoutFromTwitter(c);
		if (Session.getActiveSession() != null)
			Session.getActiveSession().closeAndClearTokenInformation();
	}
}
