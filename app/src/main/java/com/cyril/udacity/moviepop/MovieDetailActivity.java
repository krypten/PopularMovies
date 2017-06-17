package com.cyril.udacity.moviepop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Movie Detail Activity for showing the details for Movie.
 */
public class MovieDetailActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
				.add(R.id.activity_detail, new MovieDetailFragment())
				.commit();
		}
	}
}
