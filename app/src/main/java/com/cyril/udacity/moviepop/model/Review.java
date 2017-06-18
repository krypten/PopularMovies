package com.cyril.udacity.moviepop.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.cyril.udacity.moviepop.remote.TheMovieDbApi;

import org.json.JSONException;
import org.json.JSONObject;

public class Review implements Parcelable {
	public static final String PARCELABLE_ID = "REVIEW";
	public static final String LIST_PARCELABLE_ID = "REVIEWS";
	private static final String TAG = Review.class.getSimpleName();

	private String mId;
	private String mAuthor;
	private String mContent;

	private Review(final Parcel in) {
		mId = in.readString();
		mAuthor = in.readString();
		mContent = in.readString();
	}

	public Review(final JSONObject jsonObject) throws JSONException {
		Log.i(TAG, jsonObject.toString(4));
		mId = jsonObject.getString(TheMovieDbApi.VERSION_3.RESPONSE.ID);
		mAuthor = jsonObject.getString(TheMovieDbApi.VERSION_3.RESPONSE.REVIEW_AUTHOR);
		mContent = jsonObject.getString(TheMovieDbApi.VERSION_3.RESPONSE.REVIEW_CONTENT);
	}

	Review(final String id, final String author, final String content) {
		mId = id;
		mAuthor = author;
		mContent = content;
	}

	public String getAuthor() {
		return mAuthor;
	}

	public String getContent() {
		return mContent;
	}

	// Parcelable implementation methods
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(mId);
		parcel.writeString(mAuthor);
		parcel.writeString(mContent);
	}

	// After implementing the `Parcelable` interface, we need to create the
	// `Parcelable.Creator<Movie> CREATOR` constant for our class;
	public static final Creator<Review> CREATOR = new Creator<Review>() {
		// This simply calls our new constructor (typically private) and
		// passes along the unmarshalled `Parcel`, and then returns the new object!
		@Override
		public Review createFromParcel(Parcel in) {
			return new Review(in);
		}

		// We just need to copy this and change the type to match our class.
		@Override
		public Review[] newArray(int size) {
			return new Review[size];
		}
	};
}
