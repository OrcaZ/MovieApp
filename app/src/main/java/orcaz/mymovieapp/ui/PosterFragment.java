package orcaz.mymovieapp.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import orcaz.mymovieapp.R;
import orcaz.mymovieapp.adapter.OnMovieSelectedListener;
import orcaz.mymovieapp.util.Constants;
import orcaz.mymovieapp.util.Util;
import orcaz.mymovieapp.adapter.PosterAdapter;
import orcaz.mymovieapp.data.Movie;
import orcaz.mymovieapp.data.MovieDBContract;
import orcaz.mymovieapp.data.MovieResponse;
import orcaz.mymovieapp.net.NetworkConstants;
import orcaz.mymovieapp.net.TMDBApi;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PosterFragment extends Fragment implements Callback<MovieResponse>, LoaderManager.LoaderCallbacks<Cursor>{
    public static final String TAG = PosterFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 0;

    private PosterAdapter mPosterAdapter;
    private RecyclerView mPosterView;
    private TextView mEmptyView;
    private int mPosterColumns = 2;

    public PosterFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Util.IsInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "Internet Currently Not Available. Showing Favorite Movies Only.", Toast.LENGTH_LONG).show();
            Util.setSortBySetting(getActivity(), Constants.SHOW_FAVORITE);
        }
        mPosterAdapter = new PosterAdapter(getActivity(), (OnMovieSelectedListener)getActivity());
        if (savedInstanceState != null) {
            List<Movie> movieList = savedInstanceState.<Movie>getParcelableArrayList(Constants.MOVIE_DATA);
            if(movieList != null) {
                mPosterAdapter.update(movieList);
            }else {
                mPosterAdapter.update(null);
                mPosterView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            }
        } else {
            retrieveMovies();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poster, container, false);
        mPosterView = (RecyclerView) view.findViewById(R.id.poster_recycler_view);
        mPosterView.setLayoutManager(new GridLayoutManager(getActivity(), mPosterColumns));
        mPosterView.setAdapter(mPosterAdapter);
        mEmptyView = (TextView) view.findViewById(R.id.poster_empty_text_view);
        return view;
    }

    private void retrieveMovies() {
        if (Util.getSortBySetting(getActivity()) == Constants.SHOW_FAVORITE) {
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        } else if (Util.IsInternetAvailable(getActivity())) {
            getLoaderManager().destroyLoader(MOVIE_LOADER);
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(NetworkConstants.BASE_URL)
                    .build();
            TMDBApi tmdbApi = restAdapter.create(TMDBApi.class);
            switch (Util.getSortBySetting(getActivity())) {
                case Constants.BY_POPULARITY:
                    tmdbApi.getMovies(NetworkConstants.API_KEY, NetworkConstants.SORT_BY_POPULARITY, null, null, this);
                    break;
                case Constants.BY_RATING:
                    tmdbApi.getMovies(NetworkConstants.API_KEY, NetworkConstants.SORT_BY_RATING, Util.getDate(), 20, this);
                    break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
        switch (Util.getSortBySetting(getActivity())) {
            case Constants.BY_POPULARITY:
                menu.findItem(R.id.by_popularity).setChecked(true);
                break;
            case Constants.BY_RATING:
                menu.findItem(R.id.by_rating).setChecked(true);
                break;
            case Constants.SHOW_FAVORITE:
                menu.findItem(R.id.show_favorite).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.by_popularity:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    Util.setSortBySetting(getActivity(), Constants.BY_POPULARITY);
                }
                break;
            case R.id.by_rating:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    Util.setSortBySetting(getActivity(), Constants.BY_RATING);
                }
                break;
            case R.id.show_favorite:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    Util.setSortBySetting(getActivity(), Constants.SHOW_FAVORITE);
                }
                break;
        }
        retrieveMovies();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.MOVIE_DATA, new ArrayList<>(mPosterAdapter.getMovieList()));
    }

    @Override
    public void success(MovieResponse movieResponse, Response response) {
        response.getBody();
        if (movieResponse != null && movieResponse.results != null && movieResponse.results.size() > 0) {
            for (Movie movie : movieResponse.results) {
                movie.poster_path = NetworkConstants.IMG_URL + movie.poster_path;
            }
            if(mPosterView.getVisibility() != View.VISIBLE){
                mPosterView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
            mPosterAdapter.update(movieResponse.results);
        } else {
            mPosterAdapter.update(null);
            mPosterView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Log.e(TAG, "Error retrieving movie data", error.getCause());
        mPosterView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
    }

    private static final String[] projection = new String[]{
            MovieDBContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieDBContract.MovieEntry.COLUMN_TITLE,
            MovieDBContract.MovieEntry.COLUMN_OVERVIEW,
            MovieDBContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieDBContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    private static final int COLUMN_MOVIE_ID_INDEX = 0;
    private static final int COLUMN_TITLE_INDEX = 1;
    private static final int COLUMN_OVERVIEW_INDEX = 2;
    private static final int COLUMN_RELEASE_DATE_INDEX = 3;
    private static final int COLUMN_VOTE_AVERAGE_INDEX = 4;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "Creating Loader");
        return new CursorLoader(getActivity(),
                MovieDBContract.MovieEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            List<Movie> movieList = new ArrayList<>(data.getCount());
            do {
                File posterFile = Util.GetPosterFile(getActivity(), data.getInt(COLUMN_MOVIE_ID_INDEX));
                String posterUri = null;
                if (posterFile != null)
                    posterUri = "file:" + posterFile.getPath();
                movieList.add(new Movie(data.getInt(COLUMN_MOVIE_ID_INDEX),
                        data.getString(COLUMN_TITLE_INDEX),
                        data.getString(COLUMN_OVERVIEW_INDEX),
                        data.getString(COLUMN_RELEASE_DATE_INDEX),
                        posterUri,
                        data.getDouble(COLUMN_VOTE_AVERAGE_INDEX)));
            }while (data.moveToNext());
            mPosterAdapter.update(movieList);
        }else if(data.getCount() == 0) {
            mPosterAdapter.update(null);
            mPosterView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPosterAdapter.update(null);
    }

    public void setPosterColumns(int cols){
        mPosterColumns = cols;
        ((GridLayoutManager)mPosterView.getLayoutManager()).setSpanCount(cols);
    }
}
