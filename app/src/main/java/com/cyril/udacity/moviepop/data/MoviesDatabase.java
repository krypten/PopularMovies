package com.cyril.udacity.moviepop.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cyril.udacity.moviepop.data.MoviesContract.MovieEntry;
import com.cyril.udacity.moviepop.data.MoviesContract.ReviewEntry;
import com.cyril.udacity.moviepop.data.MoviesContract.TrailerEntry;

/**
 * Movie database class for creating db.
 */
public class MoviesDatabase extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "moviepop.db";
	private static final int DATABASE_VERSION = 1;

	public MoviesDatabase(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " ("
			+ MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ MovieEntry.SERVER_ID + " UNIQUE NOT NULL,"
			+ MovieEntry.TITLE + " TEXT NOT NULL,"
			+ MovieEntry.OVERVIEW + " TEXT NOT NULL,"
			+ MovieEntry.RELEASE_DATE + " TEXT NOT NULL,"
			+ MovieEntry.POSTER_URL + " TEXT NOT NULL,"
			+ MovieEntry.RATING + " REAL NOT NULL,"
			+ MovieEntry.FAVORITE + " INTEGER NOT NULL DEFAULT 0"
			+ ")";
		final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " ("
			+ TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ TrailerEntry.SERVER_ID + " NOT NULL UNIQUE,"
			+ TrailerEntry.TITLE + " TEXT NOT NULL,"
			+ TrailerEntry.VIDEO_KEY + " TEXT NOT NULL,"
			+ TrailerEntry.TYPE + " TEXT NOT NULL,"
			+ TrailerEntry.SITE + " TEXT NOT NULL,"

			// Set up the movie column as a foreign key to movie table.
			+ " FOREIGN KEY (" + TrailerEntry.MOVIE_KEY + ") REFERENCES "
			+ MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ")"
			+ " );";
		final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " ("
			+ ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ ReviewEntry.SERVER_ID + " UNIQUE NOT NULL,"
			+ ReviewEntry.AUTHOR + " TEXT NOT NULL,"
			+ ReviewEntry.CONTENT + " TEXT NOT NULL,"

			// Set up the movie column as a foreign key to movie table.
			+ " FOREIGN KEY (" + ReviewEntry.MOVIE_KEY + ") REFERENCES "
			+ MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ")"
			+ " );";
		db.execSQL(SQL_CREATE_MOVIE_TABLE);
		db.execSQL(SQL_CREATE_TRAILER_TABLE);
		db.execSQL(SQL_CREATE_REVIEW_TABLE);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
		onCreate(db);
	}
}
