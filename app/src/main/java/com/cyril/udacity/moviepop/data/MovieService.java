package com.cyril.udacity.moviepop.data;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.cyril.udacity.moviepop.model.Movie;
import com.cyril.udacity.moviepop.remote.APIServiceCall;

import java.util.ArrayList;
import java.util.List;

public class MovieService extends IntentService {
	private static final String TAG = MovieService.class.getSimpleName();

	public static final String BROADCAST_ACTION_STATE_CHANGE
		= "com.cyril.udacity.moviepop.intent.action.STATE_CHANGE";

	public MovieService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		// Check if the internet connection is available
		final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() == null
			|| !conMgr.getActiveNetworkInfo().isAvailable()
			|| !conMgr.getActiveNetworkInfo().isConnected()) {
			return;
		}

		final ArrayList<ContentProviderOperation> cpo = new ArrayList<>();
		final Uri dirUri = MoviesContract.MovieEntry.buildDirUri();

		cpo.add(ContentProviderOperation.newDelete(dirUri).build());

		try {
			List<Movie> movieList = new APIServiceCall().call(MoviesContract.PATH_MOST_POPULAR);

			for (final Movie movie : movieList) {
				final ContentValues values = new ContentValues();
				values.put(MoviesContract.MovieEntry._ID, movie.getId());
				values.put(MoviesContract.MovieEntry.TITLE, movie.getTitle());
				values.put(MoviesContract.MovieEntry.OVERVIEW, movie.getOverview());
				values.put(MoviesContract.MovieEntry.RELEASE_DATE, movie.getReleaseYear());
				values.put(MoviesContract.MovieEntry.POSTER_URL, movie.getPosterlUrl());
				values.put(MoviesContract.MovieEntry.RATING, movie.getRating());
				cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
			}
			getContentResolver().applyBatch(MoviesContract.CONTENT_AUTHORITY, cpo);
		} catch (RemoteException | OperationApplicationException e) {
			e.printStackTrace();
			Log.e(TAG, "Error updating content.", e);
		}
		sendStickyBroadcast(new Intent(BROADCAST_ACTION_STATE_CHANGE));
	}
}
