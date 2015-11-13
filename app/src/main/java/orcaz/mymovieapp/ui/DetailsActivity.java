package orcaz.mymovieapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import orcaz.mymovieapp.R;
import orcaz.mymovieapp.data.Movie;
import orcaz.mymovieapp.util.Constants;

public class DetailsActivity extends AppCompatActivity {
    public static final String TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Movie movie = getIntent().getParcelableExtra(Constants.MOVIE_DATA);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.details_fragment_container, DetailsActivityFragment.newInstance(movie))
                    .commit();
        }
    }
}
