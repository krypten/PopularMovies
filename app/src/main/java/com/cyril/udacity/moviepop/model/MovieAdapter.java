package com.cyril.udacity.moviepop.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cyril.udacity.moviepop.MovieDetailActivity;
import com.cyril.udacity.moviepop.R;
import com.cyril.udacity.moviepop.client.TheMovieDbApi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agrawac on 12/3/16.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
	private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

	private Context mContext;
	private List<Movie> mMovieList;
	private LayoutInflater inflater;

	public MovieAdapter(final Context context, final List<Movie> movies) {
		mContext = context;
		mMovieList = new ArrayList<>(movies);
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View layoutView = inflater.inflate(R.layout.gridview_item_movie, parent, false);
		final ViewHolder vh = new ViewHolder(layoutView);
		layoutView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				final Intent intent = new Intent(view.getContext(), MovieDetailActivity.class);
				intent.putExtra(Movie.PARCELABLE_ID, mMovieList.get(vh.getAdapterPosition()));
				mContext.startActivity(intent);
			}
		});
		return vh;
	}

	@Override
	public void onBindViewHolder(MovieAdapter.ViewHolder holder, int i) {
		final String posterUrl = TheMovieDbApi.getPosterUrl(mMovieList.get(i).getPosterlUrl(), TheMovieDbApi.SIZE.MEDIUM);
		Log.i(LOG_TAG, " Poster Url : " + posterUrl);
		Picasso.with(mContext).load(posterUrl).into(holder.mPosterView);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public int getItemCount() {
		if (mMovieList != null) {
			return mMovieList.size();
		}
		return 0;
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
			notifyDataSetChanged();
		} else {
			Toast.makeText(mContext, "INTERNET CONNECTION NOT PRESENT", Toast.LENGTH_LONG).show();
		}
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public ImageView mPosterView;

		public ViewHolder(final View view) {
			super(view);
			mPosterView = (ImageView) view.findViewById(R.id.gridview_item_poster);
		}
	}
}
