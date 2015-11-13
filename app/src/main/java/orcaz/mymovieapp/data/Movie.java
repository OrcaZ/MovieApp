package orcaz.mymovieapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie Object for retrofit MovieResponse
 */
public class Movie implements Parcelable {
    public int id;
    public String original_title;

    public Movie(int id, String original_title, String overview, String release_date, String poster_path, double vote_average) {
        this.id = id;
        this.original_title = original_title;
        this.overview = overview;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
    }

    public String overview;
    public String release_date;
    public String poster_path;
    public double vote_average;

    protected Movie(Parcel in) {
        id = in.readInt();
        original_title = in.readString();
        overview = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        vote_average = in.readDouble();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeDouble(vote_average);
    }
}