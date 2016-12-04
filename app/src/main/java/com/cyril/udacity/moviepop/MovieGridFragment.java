package com.cyril.udacity.moviepop;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.cyril.udacity.moviepop.client.APIServiceCall;
import com.cyril.udacity.moviepop.client.TheMovieDbApi;
import com.cyril.udacity.moviepop.model.Movie;
import com.cyril.udacity.moviepop.model.MovieAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A Movie Grid fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment {
    private final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    MovieAdapter mMovieAdapter;

    private List<Movie> mMovies;

    public MovieGridFragment() {
        super();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState == null || !savedInstanceState.containsKey(Movie.LIST_PARCELABLE_ID)) {
            mMovies = new ArrayList<>();
        } else {
            mMovies = savedInstanceState.getParcelableArrayList(Movie.LIST_PARCELABLE_ID);
        }
        updateMovieGrid(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMovieAdapter = new MovieAdapter(
                getActivity(),
                R.layout.gridview_item_movie,
                new ArrayList<Movie>());
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Intent intent = new Intent(view.getContext(), MovieDetailActivity.class);
                intent.putExtra(Movie.PARCELABLE_ID, (Parcelable) mMovieAdapter.getItem(i));
                startActivity(intent);
            }
        });
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
        outState.putParcelableArrayList(Movie.LIST_PARCELABLE_ID, (ArrayList<? extends Parcelable>) mMovieAdapter.getMovieList());
        super.onSaveInstanceState(outState);
    }

    private void updateMovieGrid(final String featurePath) {
        final FetchMovieTask movieTask = new FetchMovieTask();
        Log.i(LOG_TAG, "Executing the Movie task...");
        movieTask.execute(featurePath);
    }

    private class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(String... params) {
            String featureType = TheMovieDbApi.Config.POPULAR_PATH;
            if (params != null && params[0] != null) {
                featureType = params[0];
            }
            return new APIServiceCall().call(featureType);
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            Log.i(LOG_TAG, String.valueOf(result.size()));
            Log.i(LOG_TAG, Arrays.toString(result.toArray()));
            if (mMovieAdapter != null) {
                mMovieAdapter.initialize(result);
            }
        }
    }
}
