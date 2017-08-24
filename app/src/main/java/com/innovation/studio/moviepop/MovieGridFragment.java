package com.innovation.studio.moviepop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innovation.studio.moviepop.data.MovieLoader;
import com.innovation.studio.moviepop.data.MovieService;
import com.innovation.studio.moviepop.data.MoviesContract;
import com.innovation.studio.moviepop.data.PrefUtils;
import com.innovation.studio.moviepop.model.MovieAdapter;

/**
 * A Movie Grid fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int MOVIE_LOADER = 0;
	private static final String SCROLL_OFFSET = "SCROLL_OFFSET";

	private View mErrorView;
	private RecyclerView mRecyclerView;
	private MovieAdapter mMovieAdapter;
	private MovieUpdateReceiver mRefreshingReceiver;

	private int mOffset;
	private int mLoadCount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mRefreshingReceiver = new MovieUpdateReceiver(new Handler(), this);
	}

	@Override
	public void onStart() {
		super.onStart();
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MovieService.BROADCAST_ACTION_ERROR);
		intentFilter.addAction(MovieService.BROADCAST_ACTION_NO_INTERNET);
		intentFilter.addAction(MovieService.BROADCAST_ACTION_STATE_CHANGE);
		getActivity().registerReceiver(mRefreshingReceiver, intentFilter);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(mRefreshingReceiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
		mErrorView = rootView.findViewById(R.id.error_view);
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
		mRecyclerView.setVisibility(View.VISIBLE);
		mErrorView.setVisibility(View.GONE);
	}

	@Override
	public void onLoaderReset(final Loader<Cursor> loader) {
		mRecyclerView.setAdapter(null);
	}

	private static class MovieUpdateReceiver extends BroadcastReceiver {
		private final MovieGridFragment mFragment;
		private final Handler mHandler; // Handler used to execute code on the UI thread

		public MovieUpdateReceiver(final Handler handler, final MovieGridFragment fragment) {
			this.mHandler = handler;
			this.mFragment = fragment;
		}

		@Override
		public void onReceive(final Context context, final Intent intent) {
			if (MovieService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
				if (mFragment.mMovieAdapter != null) {
					mFragment.mMovieAdapter.notifyDataSetChanged();
					mFragment.mRecyclerView.setVisibility(View.VISIBLE);
					mFragment.mErrorView.setVisibility(View.GONE);
				}
			} else {
				final String errorMsg;
				if (MovieService.BROADCAST_ACTION_NO_INTERNET.equals(intent.getAction())) {
					errorMsg = mFragment.getString(R.string.connectivity_error_message);
				} else {
					errorMsg = mFragment.getString(R.string.generic_error_message);
				}
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mFragment.mRecyclerView.setVisibility(View.GONE);
						final View errorView = mFragment.getActivity().findViewById(R.id.error_view);
						mFragment.mErrorView.setVisibility(View.VISIBLE);
						((TextView) mFragment.mErrorView.findViewById(R.id.error_message)).setText(errorMsg);
					}
				});
			}
		}
	}
}
