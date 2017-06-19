package com.cyril.udacity.moviepop;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cyril.udacity.moviepop.data.FavoriteService;
import com.cyril.udacity.moviepop.data.MovieLoader;
import com.cyril.udacity.moviepop.data.MoviesContract;
import com.cyril.udacity.moviepop.data.MoviesContract.MovieEntry.Query;
import com.cyril.udacity.moviepop.model.Movie;
import com.cyril.udacity.moviepop.model.Review;
import com.cyril.udacity.moviepop.model.ReviewAdapter;
import com.cyril.udacity.moviepop.model.Trailer;
import com.cyril.udacity.moviepop.model.TrailerAdapter;
import com.cyril.udacity.moviepop.remote.APIServiceCall;
import com.cyril.udacity.moviepop.remote.TheMovieDbApi;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Movie Detail Activity for showing the details for Movie.
 */
public class MovieDetailFragment extends Fragment implements
	LoaderManager.LoaderCallbacks<Cursor> {
	private final String TAG = MovieDetailFragment.class.getSimpleName();

	private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

	private ReviewAdapter mReviewAdapter;
	private TrailerAdapter mTrailerAdapter;
	private Cursor mCursor;
	private View mRootView;
	private long mMovieId;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = getActivity().getIntent();
		if (intent != null && intent.getData() != null) {
			mMovieId = MoviesContract.MovieEntry.getMovieId(intent.getData());
		}
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
		// the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
		// fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
		// we do this in onActivityCreated.
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Populate the movie view
		mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
		bindViews();
		return mRootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putLong(Movie.MOVIE_ID, mMovieId);
		super.onSaveInstanceState(outState);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return MovieLoader.newMovieInstance(getActivity(), mMovieId);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mCursor = data;
		if (mCursor != null && !mCursor.moveToFirst()) {
			Log.e(TAG, "Error reading item detail cursor");
			mCursor.close();
			mCursor = null;
		}
		bindViews();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mCursor = null;
		bindViews();
	}

	private void bindViews() {
		if (mRootView == null) {
			return;
		}

		if (mCursor != null) {
			final ImageView posterView = (ImageView) mRootView.findViewById(R.id.movie_poster);
			final String posterUrl = TheMovieDbApi.getPosterUrl(mCursor.getString(Query.POSTER_URL), TheMovieDbApi.SIZE.SMALL);
			Picasso.with(getActivity()).load(posterUrl).into(posterView);

			((TextView) mRootView.findViewById(R.id.movie_title)).setText(mCursor.getString(Query.TITLE));
			((TextView) mRootView.findViewById(R.id.movie_overview)).setText(mCursor.getString(Query.OVERVIEW));
			((TextView) mRootView.findViewById(R.id.movie_rating)).setText(mCursor.getString(Query.RATING));
			((TextView) mRootView.findViewById(R.id.movie_release_date)).setText(mCursor.getString(Query.RELEASE_DATE));

			final ToggleButton toggleButton = (ToggleButton) mRootView.findViewById(R.id.movie_mark_favorite_btn);
			toggleButton.setChecked(isFavorite(Long.toString(mMovieId)));
			toggleButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View view) {
					final Intent intent = new Intent(getActivity(), FavoriteService.class);
					intent.putExtra(Movie.MOVIE_ID, mMovieId);
					intent.putExtra(Movie.FAVORITE, ((ToggleButton) view).isChecked());
					getActivity().startService(intent);
				}
			});

			// Movie trailers
			mTrailerAdapter = new TrailerAdapter(
				getActivity(),
				R.layout.listview_item_trailer,
				new ArrayList<Trailer>());
			final ExpandableHeightListView trailersList = (ExpandableHeightListView) mRootView.findViewById(R.id.trailer_list);
			trailersList.setAdapter(mTrailerAdapter);
			trailersList.setExpanded(true);

			trailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
					Log.i(TAG, "Starting the video in YouTube ...");
					startActivity(
						new Intent(
							Intent.ACTION_VIEW,
							Uri.parse(YOUTUBE_BASE_URL + mTrailerAdapter.getItem(i).getVideoKey())
						)
					);
				}
			});
			final long id = mCursor.getLong(Query._ID);
			final FetchTrailerTask trailerTask = new FetchTrailerTask();
			Log.i(TAG, "Executing the trailer task...");
			trailerTask.execute(getTrailersPath(id));

			// Movie reviews
			mReviewAdapter = new ReviewAdapter(
				getActivity(),
				R.layout.listview_item_review,
				new ArrayList<Review>());
			final ExpandableHeightListView reviewsList = (ExpandableHeightListView) mRootView.findViewById(R.id.reviews_list);
			reviewsList.setAdapter(mReviewAdapter);
			reviewsList.setExpanded(true);

			final FetchReviewTask reviewTask = new FetchReviewTask();
			Log.i(TAG, "Executing the review task...");
			reviewTask.execute(getReviewsPath(id));
		} else {
			Log.d(TAG, "Movie data not found");
		}
	}

	private boolean isFavorite(final String id) {
		boolean isFavorite = false;
		final Cursor cursor = getContext().getContentResolver().query(
			MoviesContract.FavoriteMovies.buildFavoriteMoviesUri(),
			null,
			MoviesContract.COLUMN_MOVIE_ID_KEY + " = " + id,
			null,
			null
		);
		if (cursor != null) {
			isFavorite = cursor.getCount() != 0;
			cursor.close();
		}
		return isFavorite;
	}

	private String getTrailersPath(final long movieId) {
		return movieId + "/" + TheMovieDbApi.Config.TRAILER_PATH;
	}

	private String getReviewsPath(final long movieId) {
		return movieId + "/" + TheMovieDbApi.Config.REVIEWS_PATH;
	}

	/* ASYNC TASK CLASSES */

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
