package com.cyril.udacity.moviepop.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cyril.udacity.moviepop.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agrawac on 12/3/16.
 */
public class TrailerAdapter extends ArrayAdapter {
	private static final String TAG = TrailerAdapter.class.getSimpleName();

	private Context mContext;
	private List<Trailer> mTrailerList;
	private LayoutInflater inflater;

	public TrailerAdapter(final Context context, final int resourceId, final List<Trailer> trailers) {
		super(context, resourceId, trailers);
		mContext = context;
		mTrailerList = new ArrayList<>(trailers);
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (mTrailerList != null) {
			return mTrailerList.size();
		}
		return 0;
	}

	@Override
	public Trailer getItem(int i) {
		if (mTrailerList != null) {
			return mTrailerList.get(i);
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
			view = inflater.inflate(R.layout.listview_item_trailer, viewGroup, false);
		}
		final Trailer trailer = mTrailerList.get(i);
		((TextView) view.findViewById(R.id.trailer_type)).setText(trailer.getType());
		((TextView) view.findViewById(R.id.trailer_title)).setText(trailer.getTitle());
		view.invalidate();
		view.requestLayout();
		return view;
	}

	public List<Trailer> getTrailerList() {
		return mTrailerList;
	}

	public void initialize(final List<Trailer> result) {
		if (result != null) {
			mTrailerList.clear();
			for (final Trailer trailer : result) {
				mTrailerList.add(trailer);
			}
			notifyDataSetChanged();
		} else {
			Toast.makeText(mContext, "INTERNET CONNECTION NOT PRESENT", Toast.LENGTH_LONG).show();
		}
	}
}
