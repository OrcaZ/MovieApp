package orcaz.mymovieapp.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import orcaz.mymovieapp.R;
import orcaz.mymovieapp.Util.Constants;
import orcaz.mymovieapp.data.MovieInfo;
import orcaz.mymovieapp.net.RetrieveDetailsTask;

public class DetailsActivityFragment extends Fragment {
    public static final String TAG = DetailsActivityFragment.class.getSimpleName();

    private MovieInfo mMovie;
    private TextView mTitleTextView, mReleaseDateTextView, mRatingTextView, mOverviewTextView;
    private ImageView mPosterImageView;

    public DetailsActivityFragment() {
    }

    public static DetailsActivityFragment newInstance(MovieInfo movie){
        Bundle movieBundle = new Bundle();
        movieBundle.putParcelable(Constants.MOVIE_DATA, movie);
        DetailsActivityFragment fragment = new DetailsActivityFragment();
        fragment.setArguments(movieBundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        mTitleTextView = (TextView) view.findViewById(R.id.movie_title_text_view);
        mReleaseDateTextView = (TextView) view.findViewById(R.id.release_date_text_view);
        mRatingTextView = (TextView) view.findViewById(R.id.rating_text_view);
        mOverviewTextView = (TextView) view.findViewById(R.id.overview_text_view);
        mPosterImageView = (ImageView) view.findViewById(R.id.poster_image_view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMovie = getArguments().getParcelable(Constants.MOVIE_DATA);
        if(mMovie!=null) {
            new RetrieveDetailsTask().execute(mMovie.mID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMovie != null){
            mTitleTextView.setText(mMovie.mTitle);
            mReleaseDateTextView.setText(mMovie.mReleaseDate);
            mRatingTextView.setText(mMovie.mRating+"/10");
            mOverviewTextView.setText(mMovie.mOverview);
            Picasso.with(getActivity()).load(mMovie.mPosterUri).into(mPosterImageView);
        }
    }
}
