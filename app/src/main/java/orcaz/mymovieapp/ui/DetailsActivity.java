package orcaz.mymovieapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import orcaz.mymovieapp.R;
import orcaz.mymovieapp.Util.Constants;
import orcaz.mymovieapp.data.MovieInfo;

public class DetailsActivity extends AppCompatActivity {
    public static final String TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {
            MovieInfo movie = getIntent().getParcelableExtra(Constants.MOVIE_DATA);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.details_fragment_container, DetailsActivityFragment.newInstance(movie))
                    .commit();
        }
    }
}
