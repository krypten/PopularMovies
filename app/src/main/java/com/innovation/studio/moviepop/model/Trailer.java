package com.innovation.studio.moviepop.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.innovation.studio.moviepop.remote.TheMovieDbApi;

import org.json.JSONException;
import org.json.JSONObject;

public class Trailer implements Parcelable {
	public static final String PARCELABLE_ID = "TRAILER";
	public static final String LIST_PARCELABLE_ID = "TRAILERS";
	private static final String TAG = Trailer.class.getSimpleName();

	private String mId;
	private String mTitle;
	private String mVideoKey;
	private String mType;
	private String mSite;

	private Trailer(final Parcel in) {
		mId = in.readString();
		mTitle = in.readString();
		mVideoKey = in.readString();
		mType = in.readString();
		mSite = in.readString();
	}

	public Trailer(final JSONObject jsonObject) throws JSONException {
		Log.i(TAG, jsonObject.toString(4));
		mId = jsonObject.getString(TheMovieDbApi.VERSION_3.RESPONSE.ID);
		mTitle = jsonObject.getString(TheMovieDbApi.VERSION_3.RESPONSE.VIDEO_TITLE);
		mVideoKey = jsonObject.getString(TheMovieDbApi.VERSION_3.RESPONSE.VIDEO_KEY);
		mType = jsonObject.getString(TheMovieDbApi.VERSION_3.RESPONSE.VIDEO_TYPE);
		mSite = jsonObject.getString(TheMovieDbApi.VERSION_3.RESPONSE.VIDEO_SITE);
	}

	Trailer(final String id, final String title, final String key, final String type, final String site) {
		mId = id;
		mTitle = title;
		mVideoKey = key;
		mType = type;
		mSite = site;
	}

	public String getVideoKey() {
		return mVideoKey;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getType() {
		return mType;
	}

	// Parcelable implementation methods
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(mId);
		parcel.writeString(mTitle);
		parcel.writeString(mVideoKey);
		parcel.writeString(mType);
		parcel.writeString(mSite);
	}

	// After implementing the `Parcelable` interface, we need to create the
	// `Parcelable.Creator<Movie> CREATOR` constant for our class;
	public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
		// This simply calls our new constructor (typically private) and
		// passes along the unmarshalled `Parcel`, and then returns the new object!
		@Override
		public Trailer createFromParcel(Parcel in) {
			return new Trailer(in);
		}

		// We just need to copy this and change the type to match our class.
		@Override
		public Trailer[] newArray(int size) {
			return new Trailer[size];
		}
	};
}
