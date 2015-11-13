package orcaz.mymovieapp.net;

import orcaz.mymovieapp.data.MovieResponse;
import orcaz.mymovieapp.data.ReviewResponse;
import orcaz.mymovieapp.data.TrailerResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import static orcaz.mymovieapp.net.NetworkConstants.*;

/**
 * retrofit http api
 */
public interface TMDBApi {
    @GET(MOVIE_QUERY_PATH)
    void getMovies(@Query(API_KEY_QUERY_KEY) String key,
                            @Query(SORT_BY_QUERY_KEY) String sortBy,
                            @Query(RELEASE_DATE_MIN_QUERY_KEY) String releaseDate,
                            @Query(VOTE_COUNT_MIN_QUERY_KEY) Integer votes,
                            Callback<MovieResponse> cb);

    @GET(TRAILER_QUERY_PATH)
    void getTrailers(@Path(ID_PATH) Integer id, @Query(API_KEY_QUERY_KEY) String key, Callback<TrailerResponse> cb);

    @GET(REVIEWS_QUERY_PATH)
    void getReviews(@Path(ID_PATH) Integer id, @Query(API_KEY_QUERY_KEY) String key, Callback<ReviewResponse> cb);
}
