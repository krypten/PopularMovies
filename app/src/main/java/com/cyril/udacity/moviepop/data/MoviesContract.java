package com.cyril.udacity.moviepop.data;

import android.content.ContentResolver;
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
	public static final String PATH_MOST_POPULAR = TheMovieDbApi.Config.POPULAR_PATH;
	public static final String PATH_TOP_RATED = TheMovieDbApi.Config.TOP_RATED_PATH;
	public static final String PATH_FAVORITES = "favorites";

	public static final String COLUMN_MOVIE_ID_KEY = "movie_id";

	public static Uri getUriFromPath(final String path) {
		if (PATH_MOST_POPULAR.equals(path)) {
			return PopularMovies.buildPopularMoviesUri();
		} else if (PATH_TOP_RATED.equals(path)) {
			return TopRatedMovies.buildTopRatedMoviesUri();
		}
		return FavoriteMovies.buildFavoriteMoviesUri();
	}

	public static class MovieEntry implements BaseColumns {
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

		public static final String TABLE_NAME = "movies";

		// COLUMNS
		public static final String _ID = "_id";
		public static final String SERVER_ID = "server_id";
		public static final String TITLE = "title";
		public static final String OVERVIEW = "overview";
		public static final String RELEASE_DATE = "release_date";
		public static final String POSTER_URL = "poster_url";
		public static final String RATING = "rating";
		public static final String FAVORITE = "favorite";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " ("
			+ _ID + " INTEGER PRIMARY KEY,"
			+ TITLE + " TEXT NOT NULL,"
			+ OVERVIEW + " TEXT NOT NULL,"
			+ RELEASE_DATE + " TEXT NOT NULL,"
			+ POSTER_URL + " TEXT NOT NULL,"
			+ RATING + " REAL NOT NULL"
			+ ")";

		public interface Query {
			String[] PROJECTION = {
				MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
				MovieEntry.TITLE,
				MovieEntry.OVERVIEW,
				MovieEntry.RELEASE_DATE,
				MovieEntry.POSTER_URL,
				MovieEntry.RATING,
			};
			int _ID = 0;
			int TITLE = 1;
			int OVERVIEW = 2;
			int RELEASE_DATE = 3;
			int POSTER_URL = 4;
			int RATING = 5;
		}

		/**
		 * Matches: /movies/
		 */
		public static Uri buildDirUri() {
			return BASE_URI.buildUpon().appendPath(PATH_MOVIE).build();
		}

		/**
		 * Matches: /movies/[_id]/
		 */
		public static Uri buildMovieUri(final long _id) {
			return BASE_URI.buildUpon().appendPath(PATH_MOVIE).appendPath(Long.toString(_id)).build();
		}

		/**
		 * Read movie ID item detail URI.
		 */
		public static long getMovieId(final Uri movieUri) {
			return Long.parseLong(movieUri.getPathSegments().get(1));
		}
	}

	public static class PopularMovies implements BaseColumns {
		public static final String CONTENT_TYPE = MovieEntry.CONTENT_TYPE + "/" + PATH_MOST_POPULAR;

		public static final String TABLE_NAME = "popular_movies";

		// COLUMNS
		public static final String _ID = "_id";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE " + PopularMovies.TABLE_NAME + " ("
			+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_MOVIE_ID_KEY + " INTEGER NOT NULL, "
			// Set up the movie column as a foreign key to table.
			+ " FOREIGN KEY (" + COLUMN_MOVIE_ID_KEY + ") REFERENCES "
			+ MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ")"
			+ " );";

		/**
		 * Matches: /movie/popular
		 */
		public static Uri buildPopularMoviesUri() {
			return MovieEntry.buildDirUri().buildUpon().appendPath(PATH_MOST_POPULAR).build();
		}
	}

	public static class TopRatedMovies implements BaseColumns {
		public static final String CONTENT_TYPE = MovieEntry.CONTENT_TYPE + "/" + PATH_TOP_RATED;

		public static final String TABLE_NAME = "top_rated_movies";

		// COLUMNS
		public static final String _ID = "_id";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TopRatedMovies.TABLE_NAME + " ("
			+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_MOVIE_ID_KEY + " INTEGER NOT NULL, "
			// Set up the movie column as a foreign key to table.
			+ " FOREIGN KEY (" + COLUMN_MOVIE_ID_KEY + ") REFERENCES "
			+ MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ")"
			+ " );";

		/**
		 * Matches: /movie/top_rated
		 */
		public static Uri buildTopRatedMoviesUri() {
			return MovieEntry.buildDirUri().buildUpon().appendPath(PATH_TOP_RATED).build();
		}
	}

	public static class FavoriteMovies implements BaseColumns {
		public static final String CONTENT_TYPE = MovieEntry.CONTENT_TYPE + "/" + PATH_FAVORITES;

		public static final String TABLE_NAME = "favorite_movies";

		// COLUMNS
		public static final String _ID = "_id";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE " + FavoriteMovies.TABLE_NAME + " ("
			+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_MOVIE_ID_KEY + " INTEGER NOT NULL, "
			// Set up the movie column as a foreign key to table.
			+ " FOREIGN KEY (" + COLUMN_MOVIE_ID_KEY + ") REFERENCES "
			+ MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ")"
			+ " );";

		/**
		 * Matches: /movie/favorite
		 */
		public static Uri buildFavoriteMoviesUri() {
			return MovieEntry.buildDirUri().buildUpon().appendPath(PATH_FAVORITES).build();
		}
	}
}