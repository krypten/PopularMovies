package com.cyril.udacity.moviepop.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.cyril.udacity.moviepop.remote.TheMovieDbApi;

/**
 * Movies' Contract for SQLite db.
 */
public class MoviesContract {
	// The "Content authority" is a name for the entire content provider, similar to the
	// relationship between a domain name and its website.  A convenient string to use for the
	// content authority is the package name for the app, which is guaranteed to be unique on the
	// device.
	public static final String CONTENT_AUTHORITY = "com.cyril.udacity.moviepop";
	// Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
	// the content provider.
	public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	public static final String PATH_MOVIE = TheMovieDbApi.Config.MOVIE_PATH;
	public static final String PATH_TRAILER = TheMovieDbApi.Config.TRAILER_PATH;
	public static final String PATH_REVIEW = TheMovieDbApi.Config.REVIEWS_PATH;


	public static class TrailerEntry implements BaseColumns {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

		public static final String TABLE_NAME = "trailers";

		/**
		 * Type: INTEGER PRIMARY KEY AUTOINCREMENT
		 */
		public static String _ID = "_id";
		/**
		 * Type: TEXT UNIQUE
		 */
		public static String SERVER_ID = "server_id";
		/**
		 * Type: TEXT NOT NULL
		 */
		public static String TITLE = "title";
		/**
		 * Type: TEXT NOT NULL
		 */
		public static String VIDEO_KEY = "key";
		/**
		 * Type: TEXT NOT NULL
		 */
		public static String TYPE = "type";
		/**
		 * Type: TEXT NOT NULL
		 */
		public static String SITE = "site";
		/**
		 * Type: FOREIGN KEY
		 */
		public static String MOVIE_KEY = "movie_id";

		/**
		 * Matches: /video/[_id]/
		 */
		public static Uri buildTrailerUri(final long _id) {
			return BASE_URI.buildUpon().appendPath(PATH_TRAILER).appendPath(Long.toString(_id)).build();
		}
	}


	public static class ReviewEntry implements BaseColumns {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

		public static final String TABLE_NAME = "reviews";

		/**
		 * Type: INTEGER PRIMARY KEY AUTOINCREMENT
		 */
		public static String _ID = "_id";
		/**
		 * Type: TEXT NOT NULL UNIQUE
		 */
		public static String SERVER_ID = "server_id";
		/**
		 * Type: TEXT NOT NULL
		 */
		public static String AUTHOR = "author";
		/**
		 * Type: TEXT NOT NULL
		 */
		public static String CONTENT = "content";
		/**
		 * Type: FOREIGN KEY
		 */
		public static String MOVIE_KEY = "movie_id";

		/**
		 * Matches: /review/[_id]/
		 */
		public static Uri buildReviewUri(final long _id) {
			return BASE_URI.buildUpon().appendPath(PATH_REVIEW).appendPath(Long.toString(_id)).build();
		}
	}

	public static class MovieEntry implements BaseColumns {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

		public static final String TABLE_NAME = "movies";

		/**
		 * Type: INTEGER PRIMARY KEY AUTOINCREMENT
		 */
		public static String _ID = "_id";
		/**
		 * Type: TEXT UNIQUE
		 */
		public static String SERVER_ID = "server_id";
		/**
		 * Type: TEXT NOT NULL
		 */
		public static String TITLE = "title";
		/**
		 * Type: TEXT NOT NULL
		 */
		public static String OVERVIEW = "overview";
		/**
		 * Type: TEXT NOT NULL
		 */
		public static String RELEASE_DATE = "release_date";
		/**
		 * Type: TEXT NOT NULL
		 */
		public static String POSTER_URL = "poster_url";
		/**
		 * Type: REAL NOT NULL
		 */
		public static String RATING = "rating";
		/**
		 * Type: INTEGER NOT NULL DEFAULT 0
		 */
		public static String FAVORITE = "favorite";

		/**
		 * Matches: /movies/
		 */
		public static Uri buildDirUri() {
			return BASE_URI.buildUpon().appendPath(PATH_MOVIE).build();
		}

		/**
		 * Matches: /movies/[_id]/
		 */
		public static Uri buildMoviesUri(final String preferenceSetting) {
			return BASE_URI.buildUpon().appendPath(preferenceSetting).build();
		}

		/**
		 * Matches: /movies/[_id]/
		 */
		public static Uri buildMovieUri(final long _id) {
			return BASE_URI.buildUpon().appendPath(PATH_MOVIE).appendPath(Long.toString(_id)).build();
		}

		/**
		 * Matches: /movies/[_id]/videos
		 */
		public static Uri buildTrailerUri(final long _id) {
			return BASE_URI.buildUpon().appendPath(PATH_MOVIE).appendPath(Long.toString(_id)).appendPath(PATH_TRAILER).build();
		}

		/**
		 * Matches: /movies/[_id]/revies
		 */
		public static Uri buildReviewUri(final long _id) {
			return BASE_URI.buildUpon().appendPath(PATH_MOVIE).appendPath(Long.toString(_id)).appendPath(PATH_REVIEW).build();
		}

		/**
		 * Read movie ID item detail URI.
		 */
		public static long getMovieId(final Uri movieUri) {
			return Long.parseLong(movieUri.getPathSegments().get(1));
		}
	}
}