package com.innovation.studio.moviepop.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Helper for loading a list of movies or a single movie.
 */
public class MovieLoader extends CursorLoader {
	public static MovieLoader newMoviesInstance(final Context context) {
		final String path = PrefUtils.getPath(context.getApplicationContext());
		return new MovieLoader(context, MoviesContract.getUriFromPath(path));
	}

	public static MovieLoader newMovieInstance(final Context context, final long movieId) {
		return new MovieLoader(context, MoviesContract.MovieEntry.buildMovieUri(movieId));
	}

	public MovieLoader(Context context, Uri uri) {
		super(context, uri, MoviesContract.MovieEntry.Query.PROJECTION, null, null, null);
	}
}
