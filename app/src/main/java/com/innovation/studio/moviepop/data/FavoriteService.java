package com.innovation.studio.moviepop.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.innovation.studio.moviepop.model.Movie;

public class FavoriteService extends IntentService {
	private static final String TAG = FavoriteService.class.getSimpleName();

	public FavoriteService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		final long movieId = intent.getLongExtra(Movie.MOVIE_ID, 0);
		final boolean isFavorite = intent.getBooleanExtra(Movie.FAVORITE, false);

		if (isFavorite) {
			addFavoriteMovie(movieId);
		} else {
			removeFavoriteMovie(movieId);
		}
	}

	private void addFavoriteMovie(final long movieId) {
		final ContentValues value = new ContentValues();
		value.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieId);
		final Uri uri = getContentResolver().insert(MoviesContract.FavoriteMovies.buildFavoriteMoviesUri(), value);
		Log.d(TAG, "Uri : " + uri);
	}

	private void removeFavoriteMovie(final long movieId) {
		final int removeCount = getContentResolver().delete(
			MoviesContract.FavoriteMovies.buildFavoriteMoviesUri(),
			MoviesContract.COLUMN_MOVIE_ID_KEY + " = " + movieId,
			null
		);
		Log.d(TAG, "Removed " + removeCount + " with id " + movieId);
	}
}
