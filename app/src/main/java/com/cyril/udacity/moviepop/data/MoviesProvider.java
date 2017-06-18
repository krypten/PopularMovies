package com.cyril.udacity.moviepop.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.cyril.udacity.moviepop.data.MoviesContract.MovieEntry;
import com.cyril.udacity.moviepop.data.MoviesContract.ReviewEntry;
import com.cyril.udacity.moviepop.data.MoviesContract.TrailerEntry;

/**
 * Created by krypten on 6/17/17.
 */
public class MoviesProvider extends ContentProvider {
	private SQLiteOpenHelper mDBHelper;

	private static final int MOVIES = 100;
	private static final int MOVIES__ID = 101;
	private static final int MOVIE_REVIEWS = 102;
	private static final int MOVIE_TRAILERS = 103;
	private static final int REVIEWS = 200;
	private static final int TRAILERS = 300;

	/*
	private static final SQLiteQueryBuilder sTrailerByMovieQueryBuilder;
	private static final SQLiteQueryBuilder sReviewByMovieQueryBuilder;

	static {
		sTrailerByMovieQueryBuilder = new SQLiteQueryBuilder();
		sReviewByMovieQueryBuilder = new SQLiteQueryBuilder();

		//This is an inner join which looks like
		//movies INNER JOIN trailers ON movies._id = trailers.movie_id
		sTrailerByMovieQueryBuilder.setTables(
			MovieEntry.TABLE_NAME + " INNER JOIN " +
				TrailerEntry.TABLE_NAME +
				" ON " + TrailerEntry.TABLE_NAME +
				"." + TrailerEntry.MOVIE_KEY +
				" = " + MovieEntry.TABLE_NAME +
				"." + MovieEntry._ID);

		//This is an inner join which looks like
		//movies INNER JOIN reviews ON movies._id = review.movie_id
		sReviewByMovieQueryBuilder.setTables(
			MovieEntry.TABLE_NAME + " INNER JOIN " +
				ReviewEntry.TABLE_NAME +
				" ON " + ReviewEntry.TABLE_NAME +
				"." + ReviewEntry.MOVIE_KEY +
				" = " + MovieEntry.TABLE_NAME +
				"." + MovieEntry._ID);
	}
	*/

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = MoviesContract.CONTENT_AUTHORITY;
		matcher.addURI(authority, MoviesContract.PATH_MOVIE, MOVIES);
		matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/#", MOVIES__ID);
		matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/#/" + MoviesContract.PATH_TRAILER, MOVIE_TRAILERS);
		matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/#/" + MoviesContract.PATH_REVIEW, MOVIE_REVIEWS);
		matcher.addURI(authority, MoviesContract.PATH_TRAILER, TRAILERS);
		matcher.addURI(authority, MoviesContract.PATH_REVIEW, REVIEWS);
		return matcher;
	}

	@Override
	public boolean onCreate() {
		mDBHelper = new MoviesDatabase(getContext());
		return true;
	}

	private Cursor getMovieTrailers(SQLiteDatabase db, Uri uri, String[] projection, String sortOrder) {
		final long movieId = MovieEntry.getMovieId(uri);
		final String selection = TrailerEntry.MOVIE_KEY + " = ? ";
		final String[] selectionArgs = new String[]{String.valueOf(movieId)};
		return db.query(
			TrailerEntry.TABLE_NAME,
			projection,
			selection,
			selectionArgs,
			null,
			null,
			sortOrder
		);
	}

	private Cursor getMovieReviews(SQLiteDatabase db, Uri uri, String[] projection, String sortOrder) {
		final long movieId = MovieEntry.getMovieId(uri);
		final String selection = ReviewEntry.MOVIE_KEY + " = ? ";
		final String[] selectionArgs = new String[]{String.valueOf(movieId)};
		return db.query(
			ReviewEntry.TABLE_NAME,
			projection,
			selection,
			selectionArgs,
			null,
			null,
			sortOrder
		);
	}

	@Nullable
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		final SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor retCursor;
		switch (sUriMatcher.match(uri)) {
			case MOVIES:
			case MOVIES__ID:
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
			case MOVIE_TRAILERS:
				retCursor = getMovieTrailers(db, uri, projection, sortOrder);
				break;
			case MOVIE_REVIEWS:
				retCursor = getMovieReviews(db, uri, projection, sortOrder);
				break;
			case REVIEWS:
				retCursor = db.query(
					ReviewEntry.TABLE_NAME,
					projection,
					selection,
					selectionArgs,
					null,
					null,
					sortOrder
				);
				break;
			case TRAILERS:
				retCursor = db.query(
					TrailerEntry.TABLE_NAME,
					projection,
					selection,
					selectionArgs,
					null,
					null,
					sortOrder
				);
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
		final int match = sUriMatcher.match(uri);
		switch (match) {
			case MOVIES:
				return MovieEntry.CONTENT_TYPE;
			case MOVIES__ID:
				return MovieEntry.CONTENT_ITEM_TYPE;
			case TRAILERS:
				return TrailerEntry.CONTENT_TYPE;
			case REVIEWS:
				return ReviewEntry.CONTENT_TYPE;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Nullable
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mDBHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		Uri returnUri;

		switch (match) {
			case MOVIES: {
				long _id = db.insert(MovieEntry.TABLE_NAME, null, values);
				if ( _id > 0 )
					returnUri = MovieEntry.buildMovieUri(_id);
				else
					throw new android.database.SQLException("Failed to insert row into " + uri);
				break;
			}
			case REVIEWS: {
				long _id = db.insert(ReviewEntry.TABLE_NAME, null, values);
				if ( _id > 0 )
					returnUri = ReviewEntry.buildReviewUri(_id);
				else
					throw new android.database.SQLException("Failed to insert row into " + uri);
				break;
			}
			case TRAILERS: {
				long _id = db.insert(TrailerEntry.TABLE_NAME, null, values);
				if ( _id > 0 )
					returnUri = TrailerEntry.buildTrailerUri(_id);
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
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}
}
