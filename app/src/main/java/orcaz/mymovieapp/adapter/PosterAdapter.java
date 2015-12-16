package orcaz.mymovieapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import orcaz.mymovieapp.R;
import orcaz.mymovieapp.data.Movie;
import orcaz.mymovieapp.ui.DetailsActivity;
import orcaz.mymovieapp.util.Constants;

/**
 * Adapter for MainActivity recycler view showing posters.
 */
public class PosterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = PosterAdapter.class.getSimpleName();

    private Context mContext;
    private List<Movie> mMovieList;
    private OnMovieSelectedListener mListener;

    public PosterAdapter(Context context, OnMovieSelectedListener listener) {
        setHasStableIds(true);
        mContext = context;
        mListener = listener;
        mMovieList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    @Override
    public long getItemId(int position) {
        if(mMovieList.isEmpty())
            return -1;
        else
            return mMovieList.get(position).id;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_view_poster, viewGroup, false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ((PosterViewHolder)viewHolder).bindViewHolder(i);
    }

    public List<Movie> getMovieList() {
        return mMovieList;
    }

    public void update(List<Movie> movies) {
        if (movies != null) {
            mMovieList.clear();
            mMovieList.addAll(movies);
            notifyDataSetChanged();
        }else{
            mMovieList.clear();
            notifyDataSetChanged();
        }
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;

        public PosterViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.poster_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindViewHolder(int i) {
            Picasso.with(mContext).load(mMovieList.get(i).poster_path).into(mImageView);
        }

        @Override
        public void onClick(View v) {
            mListener.onMovieSelected(mMovieList.get(getAdapterPosition()));
        }
    }
}
