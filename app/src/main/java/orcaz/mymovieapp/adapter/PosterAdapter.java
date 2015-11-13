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
public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {
    public static final String TAG = PosterAdapter.class.getSimpleName();

    private Context mContext;
    private List<Movie> mMovieList;

    public PosterAdapter(Context context) {
        setHasStableIds(true);
        mContext = context;
        mMovieList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    @Override
    public long getItemId(int position) {
        return mMovieList.get(position).id;
    }

    @Override
    public PosterAdapter.PosterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_view_poster, viewGroup, false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PosterAdapter.PosterViewHolder viewHolder, int i) {
        viewHolder.bindViewHolder(i);
    }

    public List<Movie> getMovieList() {
        return mMovieList;
    }

    public void update(List<Movie> movies) {
        if (movies != null) {
            mMovieList.clear();
            mMovieList.addAll(movies);
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
            Intent intent = new Intent(mContext, DetailsActivity.class);
            intent.putExtra(Constants.MOVIE_DATA, mMovieList.get(getAdapterPosition()));
            mContext.startActivity(intent);
        }
    }
}
