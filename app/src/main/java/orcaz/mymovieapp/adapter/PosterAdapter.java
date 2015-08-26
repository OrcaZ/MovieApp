package orcaz.mymovieapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import orcaz.mymovieapp.R;
import orcaz.mymovieapp.data.Constants;
import orcaz.mymovieapp.data.MovieInfo;
import orcaz.mymovieapp.ui.DetailsActivity;

/**
 * Created by OrcaZ on 2015/08/26.
 */
public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {
    public static final String TAG = PosterAdapter.class.getSimpleName();

    private Context mContext;
    private int mNum;
    private List<MovieInfo> mMovieList;

    public PosterAdapter(Context context, int num){
        mContext = context;
        mNum = num;
        mMovieList = new ArrayList<>();
    }

    @Override
    public PosterAdapter.PosterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.item_view_poster, viewGroup, false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PosterAdapter.PosterViewHolder viewHolder, int i) {
        viewHolder.bindViewHolder(i);
    }

    public void update(List<MovieInfo> movies){
        mMovieList.clear();
        mMovieList.addAll(movies);
        notifyDataSetChanged();
    }

    public void showUpdateErrorMessage(){
        Toast.makeText(mContext, "Error acquiring movie data, please try again later.", Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mImageView;
        public PosterViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView)itemView.findViewById(R.id.poster_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindViewHolder(int i){
            Picasso.with(mContext).load(mMovieList.get(i).mPosterUri).into(mImageView);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, DetailsActivity.class);
            intent.putExtra(Constants.MOVIE_DATA, mMovieList.get(getAdapterPosition()));
            mContext.startActivity(intent);
        }
    }
}
