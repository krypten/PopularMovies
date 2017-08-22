package com.innovation.studio.moviepop.data;

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

import com.innovation.studio.moviepop.model.Movie;
import com.innovation.studio.moviepop.remote.APIServiceCall;

import java.util.ArrayList;
import java.util.List;

public class MovieService extends IntentService {
	private static final String TAG = MovieService.class.getSimpleName();

	public static final String BROADCAST_ACTION_STATE_CHANGE
		= "com.innovation.studio.moviepop.intent.action.STATE_CHANGE";

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
		final String path = PrefUtils.getPath(getApplicationContext());
		final Uri pathUri;
		if (MoviesContract.PATH_FAVORITES.equals(path)) {
			return;
		} else {
			pathUri = MoviesContract.getUriFromPath(path);
		}

		final ArrayList<ContentProviderOperation> cpo = new ArrayList<>();
		final Uri dirUri = MoviesContract.MovieEntry.buildDirUri();

		// cpo.add(ContentProviderOperation.newDelete(dirUri).build());
		cpo.add(ContentProviderOperation.newDelete(pathUri).build());

		try {
			List<Movie> movieList = new APIServiceCall().call(path);

			for (final Movie movie : movieList) {
				final ContentValues values = new ContentValues();
				values.put(MoviesContract.MovieEntry._ID, movie.getId());
				values.put(MoviesContract.MovieEntry.TITLE, movie.getTitle());
				values.put(MoviesContract.MovieEntry.OVERVIEW, movie.getOverview());
				values.put(MoviesContract.MovieEntry.RELEASE_DATE, movie.getReleaseYear());
				values.put(MoviesContract.MovieEntry.POSTER_URL, movie.getPosterlUrl());
				values.put(MoviesContract.MovieEntry.RATING, movie.getRating());
				cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());

				final ContentValues pathValue = new ContentValues();
				pathValue.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movie.getId());
				cpo.add(ContentProviderOperation.newInsert(pathUri).withValues(pathValue).build());
			}
			getContentResolver().applyBatch(MoviesContract.CONTENT_AUTHORITY, cpo);
		} catch (RemoteException | OperationApplicationException e) {
			e.printStackTrace();
			Log.e(TAG, "Error updating content.", e);
		}
		// sendStickyBroadcast(new Intent(BROADCAST_ACTION_STATE_CHANGE));
	}
}
