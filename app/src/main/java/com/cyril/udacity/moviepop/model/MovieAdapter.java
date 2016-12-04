package com.cyril.udacity.moviepop.model;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cyril.udacity.moviepop.R;
import com.cyril.udacity.moviepop.client.TheMovieDbApi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by agrawac on 12/3/16.
 */
public class MovieAdapter extends ArrayAdapter {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private Context mContext;
    private List<Movie> mMovieList;
    private LayoutInflater inflater;

    public MovieAdapter(final Context context, final int resourceId, final List<Movie> movies) {
        super(context, resourceId, movies);
        mContext = context;
        mMovieList = new ArrayList<>(movies);
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mMovieList != null) {
            return mMovieList.size();
        }
        return 0;
    }

    @Override
    public Movie getItem(int i) {
        if (mMovieList != null) {
            return mMovieList.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.gridview_item_movie, viewGroup, false);
        }
        final ImageView posterView = (ImageView) view.findViewById(R.id.gridview_item_poster);
        final String posterUrl = TheMovieDbApi.getPosterUrl(mMovieList.get(i).getPosterlUrl(), TheMovieDbApi.SIZE.MEDIUM);
        Log.i(LOG_TAG, " Poster Url : " + posterUrl);
        Picasso.with(mContext).load(posterUrl).into(posterView);
        return view;
    }

    public List<Movie> getMovieList() {
        return mMovieList;
    }

    public void initialize(final List<Movie> result) {
        if (result != null) {
            mMovieList.clear();
            for (final Movie movie : result) {
                mMovieList.add(movie);
            }
        }
        notifyDataSetChanged();
    }
}
