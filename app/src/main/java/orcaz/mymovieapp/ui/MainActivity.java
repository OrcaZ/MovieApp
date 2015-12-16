package orcaz.mymovieapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import orcaz.mymovieapp.R;
import orcaz.mymovieapp.adapter.OnMovieSelectedListener;
import orcaz.mymovieapp.data.Movie;
import orcaz.mymovieapp.util.Constants;

public class MainActivity extends AppCompatActivity implements OnMovieSelectedListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DETAILS_FRAG_TAG = "DETAILS_FRAG";
    private boolean mTwoPane = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.details_fragment_container) != null){
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.details_fragment_container, DetailsActivityFragment.newInstance(null), DETAILS_FRAG_TAG)
                        .commit();
            }
        }else{
            mTwoPane = false;
        }
        ((PosterFragment)getSupportFragmentManager().findFragmentById(R.id.forecast_fragment)).setPosterColumns(mTwoPane?3:2);
    }


    @Override
    public void onMovieSelected(Movie movie) {
        if (mTwoPane) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_fragment_container, DetailsActivityFragment.newInstance(movie), DETAILS_FRAG_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(Constants.MOVIE_DATA, movie);
            startActivity(intent);
        }
    }
}
