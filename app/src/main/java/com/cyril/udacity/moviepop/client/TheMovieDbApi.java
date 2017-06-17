package com.cyril.udacity.moviepop.client;

/**
 * Interface Class for themoviewdb.org API.
 */
public class TheMovieDbApi {
	public static final class Config {
		public static final String URL = "https://api.themoviedb.org/";
		public static final String POSTER_URL = "http://image.tmdb.org/t/p/";
		public static final String MOVIE_PATH = "movie";
		public static final String POPULAR_PATH = "popular";
		public static final String TOP_RATED_PATH = "top_rated";
		public static final String TRAILER_PATH = "videos";
		public static final String REVIEWS_PATH = "reviews";
		public static final String API_KEY_PARAM = "api_key";
	}

	public static final class VERSION_3 {
		public static final String VERSION_ID = "3";

		public static final class RESPONSE {
			public static final String ID = "id";
			public static final String LIST = "results";

			public static final String OVERVIEW = "overview";
			public static final String TITLE = "title";
			public static final String RELEASE_DATE = "release_date";
			public static final String POSTER_PATH = "poster_path";
			public static final String VOTE_AVERAGE = "vote_average";

			public static final String VIDEO_TITLE = "name";
			public static final String VIDEO_KEY = "key";
			public static final String VIDEO_SITE = "site";
			public static final String VIDEO_TYPE = "type";
		}
	}

	public static final class SIZE {
		public static final String SMALL = "w185";
		public static final String MEDIUM = "w342";
	}

	public static String getPosterUrl(final String posterUrl, final String size) {
		return Config.POSTER_URL + size + posterUrl;
	}
}
