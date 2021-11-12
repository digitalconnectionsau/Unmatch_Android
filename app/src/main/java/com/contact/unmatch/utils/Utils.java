package com.contact.unmatch.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.contact.unmatch.BuildConfig;
import com.contact.unmatch.UnmatchApp;

public class Utils {

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static SharedPreferences getPreferences() {
		return UnmatchApp.getAppContext().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
	}

	public static void savePreferences(String key, String value) {
		SharedPreferences sharedPreferences = getPreferences();
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public static void deletePreferences(String key) {
		SharedPreferences sharedPreferences = getPreferences();
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.apply();
	}

	public static String loadPreferences(String key) {
		SharedPreferences sharedPreferences = getPreferences();
		return sharedPreferences.getString(key, "");
	}

	public static String getAccessToken() {
		return loadPreferences("ACCESS_TOKEN");
	}

	public static void setAccessToken(String accessToken) {
		savePreferences("ACCESS_TOKEN", accessToken);
	}

	public static String getRefreshToken() {
		return loadPreferences("REFRESH_TOKEN");
	}

	public static void setRefreshToken(String refreshToken) {
		savePreferences("REFRESH_TOKEN", refreshToken);
	}

	public static void removeAccessToken() {
		deletePreferences("ACCESS_TOKEN");
	}

	public static void removeRefreshToken() {
		deletePreferences("REFRESH_TOKEN");
	}
}
