package orcaz.mymovieapp.net;

public class NetworkConstants {
    // Please insert TMDB api key here
    public static final String API_KEY = "1e5ad415e9df0a99db1ab71d6d84d7e4";
    public static final String BASE_URL = "http://api.themoviedb.org/3";
    public static final String MOVIE_QUERY_PATH = "/discover/movie";
    public static final String ID_PATH = "id";
    public static final String TRAILER_QUERY_PATH = "/movie/{" + ID_PATH + "}/videos";
    public static final String REVIEWS_QUERY_PATH = "/movie/{" + ID_PATH + "}/reviews";
    public static final String API_KEY_QUERY_KEY = "api_key";
    public static final String SORT_BY_QUERY_KEY = "sort_by";
    public static final String SORT_BY_POPULARITY = "popularity.desc";
    public static final String SORT_BY_RATING = "vote_average.desc";
    public static final String RELEASE_DATE_MIN_QUERY_KEY = "primary_release_date.gte";
    public static final String VOTE_COUNT_MIN_QUERY_KEY = "vote_count.gte";

    public static final String IMG_URL = "http://image.tmdb.org/t/p/w185";

    public static final String THUMBNAIL_URL = "http://img.youtube.com/vi/";
    public static final String THUMBNAIL_PATH = "/mqdefault.jpg";
    public static final String YOUTUBE_URL = "http://www.youtube.com/watch";
}
