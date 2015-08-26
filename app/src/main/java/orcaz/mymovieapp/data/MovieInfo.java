package orcaz.mymovieapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by OrcaZ on 2015/08/26.
 */
public class MovieInfo implements Parcelable{
    public int mID;
    public String mTitle;
    public String mPosterUri;
    public String mOverview;
    public double mRating;
    public String mReleaseDate;

    public MovieInfo(int ID, String title, String posterUri, String overview, double rating, String releaseDate) {
        mID = ID;
        mTitle = title;
        mPosterUri = posterUri;
        mOverview = overview;
        mRating = rating;
        mReleaseDate = releaseDate;
    }

    protected MovieInfo(Parcel in) {
        mID = in.readInt();
        mTitle = in.readString();
        mPosterUri = in.readString();
        mOverview = in.readString();
        mRating = in.readDouble();
        mReleaseDate = in.readString();
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR = new Parcelable.Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mID);
        dest.writeString(mTitle);
        dest.writeString(mPosterUri);
        dest.writeString(mOverview);
        dest.writeDouble(mRating);
        dest.writeString(mReleaseDate);
    }
}
