package com.persiandesigners.hyperalborz;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class NetworkAvailable {

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
}
