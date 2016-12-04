package com.cyril.udacity.moviepop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyril.udacity.moviepop.client.TheMovieDbApi;
import com.cyril.udacity.moviepop.model.Movie;
import com.squareup.picasso.Picasso;

/**
 * Movie Detail Activity for showing the details for Movie.
 */
public class MovieDetailActivity extends ActionBarActivity {
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        final Intent intent = getIntent();
        // Movie Object
        if (intent != null) {
            mMovie = intent.getParcelableExtra(Movie.PARCELABLE_ID);
        } else if (savedInstanceState != null && savedInstanceState.containsKey(Movie.PARCELABLE_ID)) {
            mMovie = savedInstanceState.getParcelable(Movie.PARCELABLE_ID);
        }
        // Populate the movie view
        if (mMovie != null) {
            ((TextView) findViewById(R.id.movie_title)).setText(mMovie.getTitle());
            ((TextView) findViewById(R.id.movie_rating)).setText(mMovie.getRating());
            ((TextView) findViewById(R.id.movie_release_date)).setText(mMovie.getReleaseYear());
            final ImageView posterView = (ImageView) findViewById(R.id.movie_poster);
            final String posterUrl = TheMovieDbApi.getPosterUrl(mMovie.getPosterlUrl(), TheMovieDbApi.SIZE.SMALL);
            Picasso.with(this).load(posterUrl).into(posterView);
            ((TextView) findViewById(R.id.movie_overview)).setText(mMovie.getOverview());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Movie.PARCELABLE_ID, mMovie);
    }
}
