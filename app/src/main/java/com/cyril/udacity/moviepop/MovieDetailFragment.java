package com.cyril.udacity.moviepop;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cyril.udacity.moviepop.remote.APIServiceCall;
import com.cyril.udacity.moviepop.remote.TheMovieDbApi;
import com.cyril.udacity.moviepop.model.Movie;
import com.cyril.udacity.moviepop.model.Review;
import com.cyril.udacity.moviepop.model.ReviewAdapter;
import com.cyril.udacity.moviepop.model.Trailer;
import com.cyril.udacity.moviepop.model.TrailerAdapter;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Movie Detail Activity for showing the details for Movie.
 */
public class MovieDetailFragment extends Fragment {
	private final String TAG = MovieDetailFragment.class.getSimpleName();

	private ReviewAdapter mReviewAdapter;
	private TrailerAdapter mTrailerAdapter;
	private Movie mMovie;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		final Intent intent = getActivity().getIntent();
		// Movie Object
		if (intent != null) {
			mMovie = intent.getParcelableExtra(Movie.PARCELABLE_ID);
		} else if (savedInstanceState != null && savedInstanceState.containsKey(Movie.PARCELABLE_ID)) {
			mMovie = savedInstanceState.getParcelable(Movie.PARCELABLE_ID);
		}
		// Populate the movie view
		final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
		if (mMovie != null) {
			((TextView) rootView.findViewById(R.id.movie_title)).setText(mMovie.getTitle());
			((TextView) rootView.findViewById(R.id.movie_rating)).setText(mMovie.getRating());
			((TextView) rootView.findViewById(R.id.movie_release_date)).setText(mMovie.getReleaseYear());
			final ImageView posterView = (ImageView) rootView.findViewById(R.id.movie_poster);
			final String posterUrl = TheMovieDbApi.getPosterUrl(mMovie.getPosterlUrl(), TheMovieDbApi.SIZE.SMALL);
			Picasso.with(getActivity()).load(posterUrl).into(posterView);
			((TextView) rootView.findViewById(R.id.movie_overview)).setText(mMovie.getOverview());
			rootView.findViewById(R.id.movie_mark_favorite_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View view) {
					mMovie.setFavorite(((ToggleButton) view).isChecked());
				}
			});

			// Movie trailers
			mTrailerAdapter = new TrailerAdapter(
				getActivity(),
				R.layout.listview_item_trailer,
				new ArrayList<Trailer>());
			final ExpandableHeightListView trailersList = (ExpandableHeightListView) rootView.findViewById(R.id.trailer_list);
			trailersList.setAdapter(mTrailerAdapter);
			trailersList.setExpanded(true);

			trailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
					Log.i(TAG, "Starting the YouTubeStandalonePlayer ...");
					Intent intent = YouTubeStandalonePlayer.createVideoIntent(
						getActivity(),
						BuildConfig.YOUTUBE_API_KEY,
						mTrailerAdapter.getItem(i).getVideoKey());
					startActivity(intent);
				}
			});

			final FetchTrailerTask trailerTask = new FetchTrailerTask();
			Log.i(TAG, "Executing the trailer task...");
			trailerTask.execute(getTrailersPath(mMovie));

			// Movie reviews
			mReviewAdapter = new ReviewAdapter(
				getActivity(),
				R.layout.listview_item_review,
				new ArrayList<Review>());
			final ExpandableHeightListView reviewsList = (ExpandableHeightListView) rootView.findViewById(R.id.reviews_list);
			reviewsList.setAdapter(mReviewAdapter);
			reviewsList.setExpanded(true);

			final FetchReviewTask reviewTask = new FetchReviewTask();
			Log.i(TAG, "Executing the review task...");
			reviewTask.execute(getReviewsPath(mMovie));
		} else {
			Log.d(TAG, "Movie data not found");
		}
		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(Movie.PARCELABLE_ID, mMovie);
	}

	private String getTrailersPath(final Movie movie) {
		return movie.getId() + "/" + TheMovieDbApi.Config.TRAILER_PATH;
	}

	private String getReviewsPath(final Movie movie) {
		return movie.getId() + "/" + TheMovieDbApi.Config.REVIEWS_PATH;
	}

	private class FetchTrailerTask extends AsyncTask<String, Void, List<Trailer>> {
		@Override
		protected List<Trailer> doInBackground(String... params) {
			if (params != null && params[0] != null) {
				return new APIServiceCall().call(getActivity(), params[0]);
			}
			return new ArrayList<>();
		}

		@Override
		protected void onPostExecute(List<Trailer> result) {
			if (mTrailerAdapter != null) {
				mTrailerAdapter.initialize(result);
			}
		}
	}

	private class FetchReviewTask extends AsyncTask<String, Void, List<Review>> {
		@Override
		protected List<Review> doInBackground(String... params) {
			if (params != null && params[0] != null) {
				return new APIServiceCall().call(getActivity(), params[0]);
			}
			return new ArrayList<>();
		}

		@Override
		protected void onPostExecute(List<Review> result) {
			if (mReviewAdapter != null) {
				mReviewAdapter.initialize(result);
			}
		}
	}
}
