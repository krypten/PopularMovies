package com.cyril.udacity.moviepop.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import static android.content.Context.MODE_PRIVATE;

/**
 * Shared Preferences Utility Class.
 */
public class PrefUtils {
	private static final String NAME = "movie_pref";
	public static final String PATH_KEY = "path";

	public static void updatePath(final Context context, final String path) {
		final SharedPreferences.Editor editor = context.getSharedPreferences(NAME, MODE_PRIVATE).edit();
		editor.putString(PATH_KEY, path);
		editor.apply();
	}

	public static String getPath(final Context context) {
		final SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, MODE_PRIVATE);
		return sharedPreferences.getString(PATH_KEY, MoviesContract.PATH_MOST_POPULAR);
	}
}
