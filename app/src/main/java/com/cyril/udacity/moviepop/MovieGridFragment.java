package com.cyril.udacity.moviepop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cyril.udacity.moviepop.data.MovieLoader;
import com.cyril.udacity.moviepop.data.MovieService;
import com.cyril.udacity.moviepop.data.MoviesContract;
import com.cyril.udacity.moviepop.data.PrefUtils;
import com.cyril.udacity.moviepop.model.MovieAdapter;

/**
 * A Movie Grid fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int MOVIE_LOADER = 0;
	private static final String SCROLL_OFFSET = "SCROLL_OFFSET";

	private MovieAdapter mMovieAdapter;
	private RecyclerView mRecyclerView;
	private int mOffset;
	private int mLoadCount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		getActivity().registerReceiver(mRefreshingReceiver,
			new IntentFilter(MovieService.BROADCAST_ACTION_STATE_CHANGE));

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(mRefreshingReceiver);
		getLoaderManager().destroyLoader(MOVIE_LOADER);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().startService(new Intent(getActivity(), MovieService.class));
		getLoaderManager().initLoader(MOVIE_LOADER, null, this);
		if (savedInstanceState != null) {
			mOffset = savedInstanceState.getInt(SCROLL_OFFSET, 0);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.gridview_movie);
		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		state.putInt(SCROLL_OFFSET, mRecyclerView.computeVerticalScrollOffset());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_sort_by_popularity) {
			updatePreferences(MoviesContract.PATH_MOST_POPULAR);
			return true;
		} else if (id == R.id.action_sort_by_rating) {
			updatePreferences(MoviesContract.PATH_TOP_RATED);
			return true;
		} else if (id == R.id.action_favorites) {
			updatePreferences(MoviesContract.PATH_FAVORITES);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updatePreferences(final String path) {
		PrefUtils.updatePath(getActivity().getApplicationContext(), path);
		getLoaderManager().restartLoader(MOVIE_LOADER, null, MovieGridFragment.this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return MovieLoader.newMoviesInstance(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mMovieAdapter = new MovieAdapter(getActivity(), data);
		mRecyclerView.setAdapter(mMovieAdapter);
		mRecyclerView.post(new Runnable() {
			@Override
			public void run() {
				mLoadCount += 1;
				if (mLoadCount > 1) {
					mRecyclerView.smoothScrollBy(0, mOffset);
				}
			}
		});

		final int columnCount = getResources().getInteger(R.integer.grid_column_count);
		final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), columnCount);
		mRecyclerView.setLayoutManager(layoutManager);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mRecyclerView.setAdapter(null);
	}

	private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			if (MovieService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
				if (mMovieAdapter != null) {
					mMovieAdapter.notifyDataSetChanged();
				}
			}
		}
	};
}
