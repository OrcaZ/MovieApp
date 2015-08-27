package orcaz.mymovieapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import orcaz.mymovieapp.R;
import orcaz.mymovieapp.Util.OnMoviesRetrievedListener;
import orcaz.mymovieapp.Util.Util;
import orcaz.mymovieapp.adapter.PosterAdapter;
import orcaz.mymovieapp.Util.Constants;
import orcaz.mymovieapp.data.MovieInfo;
import orcaz.mymovieapp.net.RetrieveMoviesTask;

public class MainActivityFragment extends Fragment implements OnMoviesRetrievedListener {
    public static final String TAG = MainActivityFragment.class.getSimpleName();

    private PosterAdapter mPosterAdapter;

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosterAdapter = new PosterAdapter(getActivity());
        if(savedInstanceState != null){
            mPosterAdapter.update(savedInstanceState.<MovieInfo>getParcelableArrayList(Constants.MOVIE_DATA));
        }else{
            retrieveMovies();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.poster_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(mPosterAdapter);
        return view;
    }

    private void retrieveMovies() {
        if(Util.IsInternetAvailable(getActivity()))
            new RetrieveMoviesTask(this).execute(Util.getSortBySetting(getActivity()));
        else
            Toast.makeText(getActivity(), "Internet connection not available.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
        switch (Util.getSortBySetting(getActivity())){
            case Constants.BY_POPULARITY:
                menu.findItem(R.id.by_popularity).setChecked(true);
                break;
            case Constants.BY_RATING:
                menu.findItem(R.id.by_rating).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.by_popularity:
                if(!item.isChecked()){
                    item.setChecked(true);
                    Util.setSortBySetting(getActivity(), Constants.BY_POPULARITY);
                    retrieveMovies();
                }
                break;
            case R.id.by_rating:
                if(!item.isChecked()){
                    item.setChecked(true);
                    Util.setSortBySetting(getActivity(), Constants.BY_RATING);
                    retrieveMovies();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.MOVIE_DATA, new ArrayList<>(mPosterAdapter.getMovieList()));
    }

    @Override
    public void onMoviesRetrieved(List<MovieInfo> movieList) {
        if(movieList != null && movieList.size() > 0)
            mPosterAdapter.update(movieList);
        else
            Toast.makeText(getActivity(), "Error retrieving movie data. Please try again later.", Toast.LENGTH_LONG).show();
    }
}
