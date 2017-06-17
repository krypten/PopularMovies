package com.cyril.udacity.moviepop;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cyril.udacity.moviepop.client.APIServiceCall;
import com.cyril.udacity.moviepop.client.TheMovieDbApi;
import com.cyril.udacity.moviepop.model.Movie;
import com.cyril.udacity.moviepop.model.MovieAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A Movie Grid fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment {
	private final String LOG_TAG = MovieGridFragment.class.getSimpleName();

	MovieAdapter mMovieAdapter;

	public MovieGridFragment() {
		super();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		updateMovieGrid(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		List<Movie> movies;
		if (savedInstanceState == null || !savedInstanceState.containsKey(Movie.LIST_PARCELABLE_ID)) {
			movies = new ArrayList<>();
		} else {
			movies = savedInstanceState.getParcelableArrayList(Movie.LIST_PARCELABLE_ID);
		}
		final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		final RecyclerView rView = (RecyclerView) rootView.findViewById(R.id.gridview_movie);

		final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
		rView.setLayoutManager(layoutManager);

		mMovieAdapter = new MovieAdapter(getActivity(), movies);
		rView.setAdapter(mMovieAdapter);
		return rootView;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_sort_by_popularity) {
			updateMovieGrid(TheMovieDbApi.Config.POPULAR_PATH);
		} else if (id == R.id.action_sort_by_rating) {
			updateMovieGrid(TheMovieDbApi.Config.TOP_RATED_PATH);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (mMovieAdapter != null) {
			outState.putParcelableArrayList(Movie.LIST_PARCELABLE_ID, (ArrayList<? extends Parcelable>) mMovieAdapter.getMovieList());
		}
		super.onSaveInstanceState(outState);
	}

	private void updateMovieGrid(final String featurePath) {
		final FetchMovieTask movieTask = new FetchMovieTask();
		Log.i(LOG_TAG, "Executing the Movie task...");
		movieTask.execute(featurePath);
	}

	private class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {
		@Override
		protected List<Movie> doInBackground(String... params) {
			String featureType = TheMovieDbApi.Config.POPULAR_PATH;
			if (params != null && params[0] != null) {
				featureType = params[0];
			}
			return new APIServiceCall().call(getActivity(), featureType);
		}

		@Override
		protected void onPostExecute(List<Movie> result) {
			if (mMovieAdapter != null) {
				mMovieAdapter.initialize(result);
			}
		}
	}
}
