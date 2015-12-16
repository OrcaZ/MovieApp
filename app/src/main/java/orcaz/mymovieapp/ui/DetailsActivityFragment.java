package orcaz.mymovieapp.ui;

import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import orcaz.mymovieapp.R;
import orcaz.mymovieapp.util.Constants;
import orcaz.mymovieapp.util.Util;
import orcaz.mymovieapp.data.Movie;
import orcaz.mymovieapp.data.MovieDBContract;
import orcaz.mymovieapp.data.MovieDBHelper;
import orcaz.mymovieapp.data.ReviewResponse;
import orcaz.mymovieapp.data.TrailerResponse;
import orcaz.mymovieapp.net.NetworkConstants;
import orcaz.mymovieapp.net.TMDBApi;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetailsActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String TAG = DetailsActivityFragment.class.getSimpleName();
    private static final int REVIEW_LOADER = 0;

    private Movie mMovie;
    private ReviewResponse mReviewResponse;
    private TrailerResponse mTrailerResponse;

    private ImageView mPosterImageView;
    private LinearLayout mReviewContainer, mReviewListItemContainer, mTrailerContainer, mTrailerListItemContainer;
    private ShareActionProvider mShareActionProvider;

    public DetailsActivityFragment() {
    }

    public static DetailsActivityFragment newInstance(Movie movie){
        Bundle movieBundle = new Bundle();
        movieBundle.putParcelable(Constants.MOVIE_DATA, movie);
        DetailsActivityFragment fragment = new DetailsActivityFragment();
        fragment.setArguments(movieBundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

                mMovie = getArguments().getParcelable(Constants.MOVIE_DATA);
        if (mMovie != null) {
            if(Util.IsInternetAvailable(getActivity())){
                Log.i(TAG, "Loading from tmdb");
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(NetworkConstants.BASE_URL)
                        .build();
                TMDBApi tmdbApi = restAdapter.create(TMDBApi.class);
                tmdbApi.getTrailers(mMovie.id, NetworkConstants.API_KEY, mTrailerResponseCallback);
                tmdbApi.getReviews(mMovie.id, NetworkConstants.API_KEY, mReviewResponseCallback);
            }
            else if (Util.getSortBySetting(getActivity()) == Constants.SHOW_FAVORITE) {
                Log.i(TAG, "Loading from local db");
                getLoaderManager().initLoader(REVIEW_LOADER, null, this);
                mTrailerListItemContainer.removeViewAt(0);
                TextView textView = new TextView(getActivity());
                textView.setText("Trailers not available in offline mode.");
                mTrailerListItemContainer.addView(textView);
            }else{
                Log.i(TAG, "Failed to load reviews and trailers.");
                mTrailerResponseCallback.failure(null);
                mReviewResponseCallback.failure(null);
            }
        }else{
            view.findViewById(R.id.movie_scroll_view).setVisibility(View.GONE);
            view.findViewById(R.id.detail_empty_text_view).setVisibility(View.VISIBLE);
            setHasOptionsMenu(false);
        }

        if (mMovie != null) {
            TextView titleTextView = (TextView) view.findViewById(R.id.movie_title_text_view);
            TextView releaseDateTextView = (TextView) view.findViewById(R.id.release_date_text_view);
            TextView ratingTextView = (TextView) view.findViewById(R.id.rating_text_view);
            TextView overviewTextView = (TextView) view.findViewById(R.id.overview_text_view);
            mPosterImageView = (ImageView) view.findViewById(R.id.poster_image_view);
            mPosterImageView.setVisibility(View.VISIBLE);
            mReviewContainer = (LinearLayout) view.findViewById(R.id.review_container);
            mReviewListItemContainer = (LinearLayout) mReviewContainer.findViewById(R.id.review_list_item_container);
            mTrailerContainer = (LinearLayout) view.findViewById(R.id.trailer_container);
            mTrailerListItemContainer = (LinearLayout) mTrailerContainer.findViewById(R.id.trailer_list_item_container);

            titleTextView.setText(mMovie.original_title);
            releaseDateTextView.setText(mMovie.release_date);
            ratingTextView.setText(mMovie.vote_average + "/10");
            overviewTextView.setText(mMovie.overview);
            Picasso.with(getActivity()).load(mMovie.poster_path).into(mPosterImageView);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_details_fragment, menu);
        if(Util.getFavoriteSetting(getActivity(), mMovie.id)){
            menu.findItem(R.id.action_set_favorite).setIcon(R.drawable.ic_favorite_white_24dp);
        }
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.action_share));
        if(mTrailerResponse != null && !mTrailerResponse.results.isEmpty()) {
            mShareActionProvider.setShareIntent(getShareIntent(mTrailerResponse.results.get(0).key));
        }
        Log.i(TAG, "In onCreateOptionsMenu, mShareACtionProvider is null: " + (mShareActionProvider == null));
    }

    private Intent getShareIntent(String key) {
        Log.i(TAG, "Creating share intent");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getYouTubeUri(key).toString());
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_set_favorite){
            if(Util.getFavoriteSetting(getActivity(), mMovie.id)) {
                Toast.makeText(getActivity(), "Removed from favorite.", Toast.LENGTH_SHORT).show();
                item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                Util.setFavoriteSetting(getActivity(), mMovie.id, false);
                removeMovieFromDB();
            }else{
                Toast.makeText(getActivity(), "Added to favorite.", Toast.LENGTH_SHORT).show();
                item.setIcon(R.drawable.ic_favorite_white_24dp);
                Util.setFavoriteSetting(getActivity(), mMovie.id, true);
                saveMovieToDB();
                Picasso.with(getActivity()).load(NetworkConstants.IMG_URL + mMovie.poster_path).into(new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    File posterFile = Util.GetPosterFile(getActivity(),mMovie.id);
                                    if(posterFile != null) {
                                        if (posterFile.createNewFile()){
                                            Log.i(TAG, "Saving poster image...");
                                            FileOutputStream ostream = new FileOutputStream(posterFile);
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                                            ostream.close();
                                        }else{
                                            Log.i(TAG, "Poster file already exists");
                                        }
                                    }else{
                                        Log.i(TAG, "Error creating poster file");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeMovieFromDB() {
        getActivity().getContentResolver().delete(MovieDBContract.MovieEntry.buildMovieUri(mMovie.id), null, null);
        getActivity().getContentResolver().delete(MovieDBContract.ReviewEntry.buildReviewUri(mMovie.id), null, null);
    }

    private void saveMovieToDB() {
        ContentValues movieEntry = new ContentValues();
        movieEntry.put(MovieDBContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.id);
        movieEntry.put(MovieDBContract.MovieEntry.COLUMN_OVERVIEW, mMovie.overview);
        movieEntry.put(MovieDBContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.release_date);
        movieEntry.put(MovieDBContract.MovieEntry.COLUMN_TITLE, mMovie.original_title);
        movieEntry.put(MovieDBContract.MovieEntry.COLUMN_VOTE_AVERAGE, mMovie.vote_average);
        getActivity().getContentResolver().insert(MovieDBContract.MovieEntry.CONTENT_URI, movieEntry);

        for (ReviewResponse.Review review : mReviewResponse.results) {
            ContentValues reviewEntry = new ContentValues();
            reviewEntry.put(MovieDBContract.ReviewEntry.COLUMN_MOVIE, mMovie.id);
            reviewEntry.put(MovieDBContract.ReviewEntry.COLUMN_AUTHOR, review.author);
            reviewEntry.put(MovieDBContract.ReviewEntry.COLUMN_REVIEW, review.content);
            getActivity().getContentResolver().insert(MovieDBContract.ReviewEntry.buildReviewUri(mMovie.id), reviewEntry);
            Log.i(TAG, "Writing review to DB");
        }
    }

    Callback<TrailerResponse> mTrailerResponseCallback = new Callback<TrailerResponse>() {
        int width, initialHeight, fullHeight;
        @Override
        public void success(final TrailerResponse trailerResponse, Response response) {
            mTrailerResponse = trailerResponse;
            mTrailerListItemContainer.removeViewAt(0);
            if(mTrailerResponse.results != null && mTrailerResponse.results.size() > 0){
                Log.i(TAG, "In success, mShareACtionProvider is null: " + (mShareActionProvider == null));
                if(mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(getShareIntent(mTrailerResponse.results.get(0).key));
                }
                loadTrailer(mTrailerResponse.results.get(0), new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (mTrailerResponse.results.size() > 1) {
                            mTrailerListItemContainer.post(new Runnable() {
                                @Override
                                public void run() {
                                    initialHeight = mTrailerListItemContainer.getHeight();
                                    width = mTrailerListItemContainer.getWidth();
                                    Log.i(TAG, "Initial Height = " + initialHeight);
                                    mTrailerListItemContainer.getLayoutParams().height = initialHeight;
                                    mTrailerListItemContainer.requestLayout();
                                    for (int index = 1; index < mTrailerResponse.results.size(); index++) {
                                        if (index != mTrailerResponse.results.size() - 1)
                                            loadTrailer(mTrailerResponse.results.get(index), null);
                                        else
                                            loadTrailer(mTrailerResponse.results.get(index), new com.squareup.picasso.Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    mTrailerListItemContainer.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mTrailerListItemContainer.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                                                            fullHeight = mTrailerListItemContainer.getMeasuredHeight();
                                                            Log.i(TAG, "Full Height = " + fullHeight);
                                                            View expandView = LayoutInflater.from(getActivity()).inflate(R.layout.item_view_expand, mTrailerContainer, false);
                                                            mTrailerContainer.addView(expandView);
                                                            expandView.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    if (mTrailerListItemContainer.getHeight() < fullHeight) {
                                                                        expandCollapse(mTrailerListItemContainer, fullHeight);
                                                                    } else {
                                                                        expandCollapse(mTrailerListItemContainer, initialHeight);
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onError() {

                                                }
                                            });
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
            }else{
                TextView textView = new TextView(getActivity());
                textView.setText("No trailer available.");
            }

        }

        @Override
        public void failure(RetrofitError error) {
            mTrailerListItemContainer.removeViewAt(0);
            TextView textView = new TextView(getActivity());
            textView.setText("Error retrieving trailers.");
            mTrailerListItemContainer.addView(textView);
        }
    };

    private void loadTrailer(final TrailerResponse.Trailer trailer, com.squareup.picasso.Callback cb) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_view_trailer, mTrailerListItemContainer, false);
        ((TextView) view.findViewById(R.id.trailer_text_view)).setText(trailer.name);
        if (trailer.site.equals("YouTube")) {
            Picasso.with(getActivity()).load(NetworkConstants.THUMBNAIL_URL + trailer.key + NetworkConstants.THUMBNAIL_PATH).into((ImageView) view.findViewById(R.id.trailer_image_view), cb);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Clicked");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(getYouTubeUri(trailer.key));
                    startActivity(intent);
                }
            });
        }

        mTrailerListItemContainer.addView(view);
    }

    private Uri getYouTubeUri(String key){
        return Uri.parse(NetworkConstants.YOUTUBE_URL)
                .buildUpon()
                .appendQueryParameter("v", key)
                .build();
    }

    Callback<ReviewResponse> mReviewResponseCallback = new Callback<ReviewResponse>() {
        int fullHeight;
        @Override
        public void success(ReviewResponse reviewResponse, Response response) {
            mReviewResponse = reviewResponse;
            mReviewListItemContainer.removeViewAt(0);
            if(mReviewResponse.results != null && mReviewResponse.results.size() > 0) {
                for(ReviewResponse.Review review : mReviewResponse.results) {
                    loadReview(review);
                }
                mReviewListItemContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        mReviewListItemContainer.measure(MeasureSpec.makeMeasureSpec(mReviewListItemContainer.getWidth(), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                        fullHeight = mReviewListItemContainer.getMeasuredHeight();
                        if(mReviewListItemContainer.getHeight() < fullHeight) {
                            View expandView = LayoutInflater.from(getActivity()).inflate(R.layout.item_view_expand, mReviewContainer, false);
                            mReviewContainer.addView(expandView);
                            expandView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mReviewListItemContainer.getHeight() < fullHeight) {
                                        expandCollapse(mReviewListItemContainer,
                                                fullHeight);
                                    } else {
                                        expandCollapse(mReviewListItemContainer,
                                                getResources().getDimensionPixelOffset(R.dimen.review_list_item_container_min_height));
                                    }
                                }
                            });
                        }else{
                            mReviewListItemContainer.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            mReviewListItemContainer.requestLayout();
                        }
                    }
                });
            }else{
                TextView textView = new TextView(getActivity());
                textView.setText("No review available.");
                mReviewListItemContainer.addView(textView);
                mReviewListItemContainer.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                mReviewListItemContainer.requestLayout();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            mReviewListItemContainer.removeViewAt(0);
            TextView textView = new TextView(getActivity());
            textView.setText("Error retrieving reviews.");
            mReviewListItemContainer.addView(textView);
            mReviewListItemContainer.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mReviewListItemContainer.requestLayout();
        }
    };

    private void loadReview(ReviewResponse.Review review) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_view_review, mReviewListItemContainer, false);
        ((TextView) view.findViewById(R.id.reviewer_text_view)).setText(review.author + ": ");
        ((TextView) view.findViewById(R.id.review_text_view)).setText(review.content);
        mReviewListItemContainer.addView(view);
    }

    private void expandCollapse(View view, int targetHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(view.getHeight(), targetHeight);
        animator.setDuration(300);
        animator.addUpdateListener(new ExpandCollapseListener(view));
        animator.start();
        toggleExpandView((View)view.getParent());
    }

    private void toggleExpandView(View parent){
        TextView textView = (TextView) parent.findViewById(R.id.expand_text_view);
        ImageView imageView = (ImageView) parent.findViewById(R.id.expand_button);
        if(textView.getText().equals(getString(R.string.show_more_label))){
            imageView.setImageResource(R.drawable.ic_expand_less_black_24dp);
            textView.setText(R.string.show_less_label);
        }else{
            imageView.setImageResource(R.drawable.ic_expand_more_black_24dp);
            textView.setText(R.string.show_more_label);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieDBContract.ReviewEntry.buildReviewUri(mMovie.id),
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ReviewResponse r = new ReviewResponse();
        r.id = mMovie.id;
        r.results = new ArrayList<>();
        if(data.moveToFirst()){
            do{
                r.AddReview(data.getString(data.getColumnIndex(MovieDBContract.ReviewEntry.COLUMN_AUTHOR)),
                        data.getString(data.getColumnIndex(MovieDBContract.ReviewEntry.COLUMN_REVIEW)));
            }while(data.moveToNext());
        }
        mReviewResponseCallback.success(r, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class ExpandCollapseListener implements ValueAnimator.AnimatorUpdateListener {
        private View mView;
        public ExpandCollapseListener (View view){
            mView = view;
        }
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mView.getLayoutParams().height = (Integer)animation.getAnimatedValue();
            mView.requestLayout();
        }
    }
}
