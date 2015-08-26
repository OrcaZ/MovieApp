package orcaz.mymovieapp.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import orcaz.mymovieapp.R;
import orcaz.mymovieapp.adapter.PosterAdapter;
import orcaz.mymovieapp.data.Constants;
import orcaz.mymovieapp.net.RetrieveMoviesTask;

public class MainActivityFragment extends Fragment {
    public static final String TAG = MainActivityFragment.class.getSimpleName();

    private PosterAdapter mPosterAdapter;

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.poster_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mPosterAdapter = new PosterAdapter(getActivity(), 6);
        recyclerView.setAdapter(mPosterAdapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
        int sortBy = getActivity().getPreferences(Context.MODE_PRIVATE).getInt(Constants.SORT_SETTINGS_KEY, Constants.BY_POPULARITY);
        switch (sortBy){
            case Constants.BY_POPULARITY:
                menu.findItem(R.id.by_popularity).setChecked(true);
                new RetrieveMoviesTask(mPosterAdapter).execute(RetrieveMoviesTask.BY_POPULARITY);
                break;
            case Constants.BY_RATING:
                menu.findItem(R.id.by_rating).setChecked(true);
                new RetrieveMoviesTask(mPosterAdapter).execute(RetrieveMoviesTask.BY_RATING);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.by_popularity:
                if(!item.isChecked()){
                    item.setChecked(true);
                    getActivity().getPreferences(Context.MODE_PRIVATE).edit().putInt(Constants.SORT_SETTINGS_KEY, Constants.BY_POPULARITY).commit();
                    new RetrieveMoviesTask(mPosterAdapter).execute(RetrieveMoviesTask.BY_POPULARITY);
                }
                break;
            case R.id.by_rating:
                if(!item.isChecked()){
                    item.setChecked(true);
                    getActivity().getPreferences(Context.MODE_PRIVATE).edit().putInt(Constants.SORT_SETTINGS_KEY, Constants.BY_RATING).commit();
                    new RetrieveMoviesTask(mPosterAdapter).execute(RetrieveMoviesTask.BY_RATING);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
