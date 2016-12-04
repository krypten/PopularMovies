package com.cyril.udacity.moviepop.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.cyril.udacity.moviepop.client.TheMovieDbApi;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie implements Parcelable {
    public static final String PARCELABLE_ID = "MOVIE";
    public static final String LIST_PARCELABLE_ID = "MOVIES";
    private static final String LOG_TAG = "MOVIE";

    private long mId;
    private String mTitle;
    private String mOverview;
    private String mReleaseDate;
    private String mPosterlUrl;
    private double mRating;

    private Movie(final Parcel in) {
        mId = in.readLong();
        mTitle = in.readString();
        mOverview = in.readString();
        mReleaseDate = in.readString();
        mPosterlUrl = in.readString();
        mRating = in.readDouble();
    }

    Movie(final long id, final String title, final String overview, final String releaseDate, final String posterUrl, final double rating) {
        mId = id;
        mTitle = title;
        mOverview = overview;
        mReleaseDate = releaseDate;
        mPosterlUrl = posterUrl;
        mRating = rating;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getPosterlUrl() {
        return mPosterlUrl;
    }

    /**
     * Return only the release year of the movie. That would be the first 4 characters of release date.
     *
     * @return release year of the movie
     */
    public String getReleaseYear() {
        return mReleaseDate.substring(0, 4);
    }

    /**
     * Since the rating are out of 10 hence appending string to tell the total number.
     *
     * @return rating
     */
    public String getRating() {
        return String.valueOf(mRating) + "/10";
    }


    public static Movie parse(final JSONObject jsonObject) throws JSONException {
        Log.i(LOG_TAG, jsonObject.toString(4));
        return new Movie(
                jsonObject.getLong(TheMovieDbApi.VERSION_3.RESPONSE.ID),
                jsonObject.getString(TheMovieDbApi.VERSION_3.RESPONSE.TITLE),
                jsonObject.getString(TheMovieDbApi.VERSION_3.RESPONSE.OVERVIEW),
                jsonObject.getString(TheMovieDbApi.VERSION_3.RESPONSE.RELEASE_DATE),
                jsonObject.getString(TheMovieDbApi.VERSION_3.RESPONSE.POSTER_PATH),
                jsonObject.getDouble(TheMovieDbApi.VERSION_3.RESPONSE.VOTE_AVERAGE)
        );
    }

    // Parcelable implementation methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mOverview);
        parcel.writeString(mReleaseDate);
        parcel.writeString(mPosterlUrl);
        parcel.writeDouble(mRating);
    }

    // After implementing the `Parcelable` interface, we need to create the
    // `Parcelable.Creator<Movie> CREATOR` constant for our class;
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
