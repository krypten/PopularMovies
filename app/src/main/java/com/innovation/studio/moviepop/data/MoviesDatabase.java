package com.innovation.studio.moviepop.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.innovation.studio.moviepop.data.MoviesContract.FavoriteMovies;
import com.innovation.studio.moviepop.data.MoviesContract.MovieEntry;
import com.innovation.studio.moviepop.data.MoviesContract.PopularMovies;
import com.innovation.studio.moviepop.data.MoviesContract.TopRatedMovies;

/**
 * Movie database class for creating db.
 */
public class MoviesDatabase extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "moviepop.db";
	private static final int DATABASE_VERSION = 1;

	private static final String ALTER = "ALTER TABLE ";
	private static final String RENAME_TO = " RENAME TO ";

	public MoviesDatabase(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL(MovieEntry.SQL_CREATE_TABLE);
		db.execSQL(PopularMovies.SQL_CREATE_TABLE);
		db.execSQL(TopRatedMovies.SQL_CREATE_TABLE);
		db.execSQL(FavoriteMovies.SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		db.execSQL(ALTER + MovieEntry.TABLE_NAME + RENAME_TO + MovieEntry.TABLE_NAME + "_" + DATABASE_VERSION);
		db.execSQL(ALTER + PopularMovies.TABLE_NAME + RENAME_TO + PopularMovies.TABLE_NAME + "_" + DATABASE_VERSION);
		db.execSQL(ALTER + TopRatedMovies.TABLE_NAME + RENAME_TO + TopRatedMovies.TABLE_NAME + "_" + DATABASE_VERSION);
		db.execSQL(ALTER + FavoriteMovies.TABLE_NAME + RENAME_TO + FavoriteMovies.TABLE_NAME + "_" + DATABASE_VERSION);
		onCreate(db);
	}
}
