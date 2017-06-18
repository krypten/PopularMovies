package com.cyril.udacity.moviepop.data;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.net.Uri;

/**
 * Helper for loading a list of movies or a single movie.
 */
public class MovieLoader extends CursorLoader {
	public static MovieLoader newMoviesInstance(final Context context) {
		return new MovieLoader(context, MoviesContract.MovieEntry.buildDirUri());
	}

	public static MovieLoader newMovieInstance(final Context context, final long movieId) {
		return new MovieLoader(context, MoviesContract.MovieEntry.buildMovieUri(movieId));
	}

	public MovieLoader(Context context, Uri uri) {
		super(context, uri, MoviesContract.MovieEntry.Query.PROJECTION, null, null, null);
	}
}
