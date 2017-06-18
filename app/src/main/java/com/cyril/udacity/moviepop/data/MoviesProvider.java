package com.cyril.udacity.moviepop.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cyril.udacity.moviepop.data.MoviesContract.FavoriteMovies;
import com.cyril.udacity.moviepop.data.MoviesContract.MovieEntry;
import com.cyril.udacity.moviepop.data.MoviesContract.PopularMovies;
import com.cyril.udacity.moviepop.data.MoviesContract.TopRatedMovies;

import static com.cyril.udacity.moviepop.data.MoviesContract.PATH_FAVORITES;
import static com.cyril.udacity.moviepop.data.MoviesContract.PATH_MOST_POPULAR;
import static com.cyril.udacity.moviepop.data.MoviesContract.PATH_MOVIE;
import static com.cyril.udacity.moviepop.data.MoviesContract.PATH_TOP_RATED;

/**
 * Created by krypten on 6/17/17.
 */
public class MoviesProvider extends ContentProvider {
	private static final String MOVIE_ID_SELECTION = MovieEntry.TABLE_NAME + "." + MovieEntry._ID + " = ? ";

	private static final int MOVIES = 100;
	private static final int MOVIES__ID = 101;
	private static final int POPULAR_MOVIES = 200;
	private static final int TOP_RATED_MOVIES = 300;
	private static final int FAVORITE_MOVIES = 400;

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = MoviesContract.CONTENT_AUTHORITY;
		matcher.addURI(authority, PATH_MOVIE, MOVIES);
		matcher.addURI(authority, PATH_MOVIE + "/#", MOVIES__ID);
		matcher.addURI(authority, PATH_MOVIE + "/" + PATH_MOST_POPULAR, POPULAR_MOVIES);
		matcher.addURI(authority, PATH_MOVIE + "/" + PATH_TOP_RATED, TOP_RATED_MOVIES);
		matcher.addURI(authority, PATH_MOVIE + "/" + PATH_FAVORITES, FAVORITE_MOVIES);
		return matcher;
	}

	private SQLiteOpenHelper mDBHelper;

	@Override
	public boolean onCreate() {
		mDBHelper = new MoviesDatabase(getContext());
		return true;
	}

	@Nullable
	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
		final SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor retCursor;
		switch (sUriMatcher.match(uri)) {
			case MOVIES:
				retCursor = db.query(
					MovieEntry.TABLE_NAME,
					projection,
					selection,
					selectionArgs,
					null,
					null,
					sortOrder
				);
				break;
			case MOVIES__ID:
				retCursor = getMovieById(db, uri, projection, sortOrder);
				break;
			case POPULAR_MOVIES:
				retCursor = getMoviesByReference(db, PopularMovies.TABLE_NAME, selection, selectionArgs, projection, sortOrder);
				break;
			case TOP_RATED_MOVIES:
				retCursor = getMoviesByReference(db, TopRatedMovies.TABLE_NAME, selection, selectionArgs, projection, sortOrder);
				break;
			case FAVORITE_MOVIES:
				retCursor = getMoviesByReference(db, FavoriteMovies.TABLE_NAME, selection, selectionArgs, projection, sortOrder);
				break;
			default:
				throw new UnsupportedOperationException("Query for unknown uri: " + uri);
		}
		retCursor.setNotificationUri(getContext().getContentResolver(), uri);
		return retCursor;
	}

	@Nullable
	@Override
	public String getType(final Uri uri) {
		switch (sUriMatcher.match(uri)) {
			case MOVIES:
				return MovieEntry.CONTENT_TYPE;
			case MOVIES__ID:
				return MovieEntry.CONTENT_ITEM_TYPE;
			case POPULAR_MOVIES:
				return PopularMovies.CONTENT_TYPE;
			case TOP_RATED_MOVIES:
				return TopRatedMovies.CONTENT_TYPE;
			case FAVORITE_MOVIES:
				return FavoriteMovies.CONTENT_TYPE;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Nullable
	@Override
	public Uri insert(final Uri uri, final ContentValues values) {
		final SQLiteDatabase db = mDBHelper.getWritableDatabase();
		Uri returnUri;
		switch (sUriMatcher.match(uri)) {
			case MOVIES: {
				long _id = db.insertWithOnConflict(MovieEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (_id > 0)
					returnUri = MovieEntry.buildMovieUri(_id);
				else
					throw new android.database.SQLException("Failed to insert row into " + uri);
				break;
			}
			case POPULAR_MOVIES: {
				long _id = db.insertWithOnConflict(PopularMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (_id > 0)
					returnUri = PopularMovies.buildPopularMoviesUri();
				else
					throw new android.database.SQLException("Failed to insert row into " + uri);
				break;
			}
			case TOP_RATED_MOVIES: {
				long _id = db.insertWithOnConflict(TopRatedMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (_id > 0)
					returnUri = TopRatedMovies.buildTopRatedMoviesUri();
				else
					throw new android.database.SQLException("Failed to insert row into " + uri);
				break;
			}

			case FAVORITE_MOVIES: {
				long _id = db.insertWithOnConflict(FavoriteMovies.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (_id > 0)
					returnUri = FavoriteMovies.buildFavoriteMoviesUri();
				else
					throw new android.database.SQLException("Failed to insert row into " + uri);
				break;
			}
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return returnUri;
	}

	@Override
	public int bulkInsert(@NonNull final Uri uri, @NonNull final ContentValues[] values) {
		final SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int insertCount = 0;
		;
		switch (sUriMatcher.match(uri)) {
			case MOVIES:
				db.beginTransaction();
				try {
					for (final ContentValues value : values) {
						final long id = db.insertWithOnConflict(MovieEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
						if (id != -1) {
							insertCount++;
						}
					}
				} finally {
					db.endTransaction();
				}
				getContext().getContentResolver().notifyChange(uri, null);
				return insertCount;
			default:
				insertCount = super.bulkInsert(uri, values);
		}
		return insertCount;
	}

	@Override
	public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
		final SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int rowsDeleted;
		switch (sUriMatcher.match(uri)) {
			case MOVIES:
				rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
				break;
			case MOVIES__ID:
				final long id = MovieEntry.getMovieId(uri);
				rowsDeleted = db.delete(MovieEntry.TABLE_NAME,
					MOVIE_ID_SELECTION, new String[]{Long.toString(id)});
				break;
			case POPULAR_MOVIES:
				rowsDeleted = db.delete(PopularMovies.TABLE_NAME, selection, selectionArgs);
				break;
			case TOP_RATED_MOVIES:
				rowsDeleted = db.delete(TopRatedMovies.TABLE_NAME, selection, selectionArgs);
				break;
			case FAVORITE_MOVIES:
				rowsDeleted = db.delete(FavoriteMovies.TABLE_NAME, selection, selectionArgs);
				break;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		if (rowsDeleted != 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsDeleted;
	}

	@Override
	public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
		return 0;
	}

	@Override
	public void shutdown() {
		mDBHelper.close();
		super.shutdown();
	}

	/* QUERY UTILITY METHODS */

	private Cursor getMovieById(final SQLiteDatabase db, final Uri uri, final String[] projection, final String sortOrder) {
		final String[] selectionArgs = new String[]{Long.toString(MovieEntry.getMovieId(uri))};
		return db.query(
			MovieEntry.TABLE_NAME,
			projection,
			MOVIE_ID_SELECTION,
			selectionArgs,
			null,
			null,
			sortOrder
		);
	}

	private Cursor getMoviesByReference(final SQLiteDatabase db, final String tableName, final String selection, final String[] selectionArgs, final String[] projection, final String sortOrder) {
		final SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
		// tableName INNER JOIN movies ON tableName.movie_id = movies._id
		sqLiteQueryBuilder.setTables(tableName + " INNER JOIN " + MovieEntry.TABLE_NAME
			+ " ON " + tableName + "." + MoviesContract.COLUMN_MOVIE_ID_KEY
			+ " = " + MovieEntry.TABLE_NAME + "." + MovieEntry._ID);
		return sqLiteQueryBuilder.query(
			db,
			projection,
			selection,
			selectionArgs,
			null,
			null,
			sortOrder
		);
	}
}
