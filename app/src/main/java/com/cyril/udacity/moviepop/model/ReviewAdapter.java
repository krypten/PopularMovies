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
public class ReviewAdapter extends ArrayAdapter {
	private static final String TAG = ReviewAdapter.class.getSimpleName();

	private Context mContext;
	private List<Review> mReviewList;
	private LayoutInflater inflater;

	public ReviewAdapter(final Context context, final int resourceId, final List<Review> reviews) {
		super(context, resourceId, reviews);
		mContext = context;
		mReviewList = new ArrayList<>(reviews);
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (mReviewList != null) {
			return mReviewList.size();
		}
		return 0;
	}

	@Override
	public Review getItem(int i) {
		if (mReviewList != null) {
			return mReviewList.get(i);
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
			view = inflater.inflate(R.layout.listview_item_review, viewGroup, false);
		}
		final Review review = mReviewList.get(i);
		final TextView reviewAuthor = (TextView) view.findViewById(R.id.review_author);
		reviewAuthor.setText("Author : " + review.getAuthor());
		reviewAuthor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				final View reviewContent = ((ViewGroup) view.getParent()).findViewById(R.id.review_content);
				if (reviewContent != null) {
					reviewContent.setVisibility(reviewContent.isShown() ? View.GONE : View.VISIBLE);
				}
				view.invalidate();
				view.requestLayout();
			}
		});
		((TextView) view.findViewById(R.id.review_content)).setText(review.getContent());
		return view;
	}

	public void initialize(final List<Review> result) {
		if (result != null) {
			mReviewList.clear();
			for (final Review review : result) {
				mReviewList.add(review);
			}
			notifyDataSetChanged();
		} else {
			Toast.makeText(mContext, "INTERNET CONNECTION NOT PRESENT", Toast.LENGTH_LONG).show();
		}
	}
}
