package com.cyril.udacity.moviepop.model;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cyril.udacity.moviepop.MovieDetailActivity;
import com.cyril.udacity.moviepop.R;
import com.cyril.udacity.moviepop.data.MoviesContract;
import com.cyril.udacity.moviepop.remote.TheMovieDbApi;
import com.squareup.picasso.Picasso;

/**
 * Created by agrawac on 12/3/16.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
	private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

	private Cursor mCursor;
	private Context mContext;
	private LayoutInflater inflater;

	public MovieAdapter(final Context context, final Cursor cursor) {
		mContext = context;
		mCursor = cursor;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public MovieAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		final View layoutView = inflater.inflate(R.layout.gridview_item_movie, parent, false);
		final ViewHolder vh = new ViewHolder(layoutView);
		layoutView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				mContext.startActivity(
					new Intent(
						Intent.ACTION_VIEW,
						MoviesContract.MovieEntry.buildMovieUri(getItemId(vh.getAdapterPosition())),
						view.getContext(),
						MovieDetailActivity.class
					)
				);
			}
		});
		return vh;
	}

	@Override
	public void onBindViewHolder(final MovieAdapter.ViewHolder holder, final int position) {
		mCursor.moveToPosition(position);
		final String posterUrl = TheMovieDbApi.getPosterUrl(mCursor.getString(MoviesContract.MovieEntry.Query.POSTER_URL), TheMovieDbApi.SIZE.MEDIUM);
		Log.i(LOG_TAG, " Poster Url : " + posterUrl);
		Picasso.with(mContext).load(posterUrl).into(holder.mPosterView);
	}

	@Override
	public long getItemId(final int position) {
		mCursor.moveToPosition(position);
		return mCursor.getLong(MoviesContract.MovieEntry.Query._ID);
	}

	@Override
	public int getItemCount() {
		if (mCursor != null) {
			return mCursor.getCount();
		}
		return 0;
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public ImageView mPosterView;

		public ViewHolder(final View view) {
			super(view);
			mPosterView = (ImageView) view.findViewById(R.id.gridview_item_poster);
		}
	}
}
