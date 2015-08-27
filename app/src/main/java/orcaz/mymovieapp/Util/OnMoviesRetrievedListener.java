package orcaz.mymovieapp.Util;

import java.util.List;

import orcaz.mymovieapp.data.MovieInfo;

public interface OnMoviesRetrievedListener {
    void onMoviesRetrieved(List<MovieInfo> movieList);
}
