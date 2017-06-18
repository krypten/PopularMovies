package com.cyril.udacity.moviepop.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
		db.execSQL(MoviesContract.MovieEntry.SQL_CREATE_TABLE);
		db.execSQL(MoviesContract.PopularMovies.SQL_CREATE_TABLE);
		db.execSQL(MoviesContract.TopRatedMovies.SQL_CREATE_TABLE);
		db.execSQL(MoviesContract.FavoriteMovies.SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.PopularMovies.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.TopRatedMovies.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoriteMovies.TABLE_NAME);
		onCreate(db);
	}
}
