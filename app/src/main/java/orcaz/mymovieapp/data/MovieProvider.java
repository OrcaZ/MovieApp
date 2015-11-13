package orcaz.mymovieapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import orcaz.mymovieapp.data.MovieDBContract.MovieEntry;
import orcaz.mymovieapp.data.MovieDBContract.ReviewEntry;

/**
 * ContentProvider for accessing favorite movies saved in database
 */
public class MovieProvider extends ContentProvider {
    public static final String TAG = MovieProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mMovieDBHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int REVIEW = 200;

    public static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Uri for querying all favorite movies and inserting movies
        matcher.addURI(MovieDBContract.CONTENT_AUTHORITY, MovieDBContract.MOVIE_PATH , MOVIE);
        // Uri for removing specific movie
        matcher.addURI(MovieDBContract.CONTENT_AUTHORITY, MovieDBContract.MOVIE_PATH + "/#", MOVIE_WITH_ID);
        // Uri for querying, inserting and removing reviews for specific movie
        matcher.addURI(MovieDBContract.CONTENT_AUTHORITY, MovieDBContract.REVIEW_PATH + "/#", REVIEW);
        return matcher;
    }

    public boolean onCreate() {
        mMovieDBHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mMovieDBHelper.getReadableDatabase();
        Cursor result;
        switch (sUriMatcher.match(uri)){
            case MOVIE:
                Log.i(TAG, "Querying movie with uri: " + uri);
                result = db.query(MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case REVIEW:
                Log.i(TAG, "Querying review with uri: " + uri);
                String movieId = uri.getLastPathSegment();
                result = db.query(ReviewEntry.TABLE_NAME,
                        projection,
                        ReviewEntry.COLUMN_MOVIE + " = ? ",
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEW:
                return ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)){
            case MOVIE: {
                long _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW: {
                long _id = db.insert(ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int rowsDeleted;
        if( selection == null) selection = "1"; //delete all
        switch (sUriMatcher.match(uri)){
            case MOVIE:
                Log.i(TAG, "Deleting movie with uri: " + uri);
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
                Log.i(TAG, "Deleting movie with uri: " + uri);
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME,
                        MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{uri.getLastPathSegment()});
                break;
            case REVIEW:
                Log.i(TAG, "Deleting reviews with uri: " + uri);
                rowsDeleted = db.delete(ReviewEntry.TABLE_NAME,
                        ReviewEntry.COLUMN_MOVIE + " = ? ",
                        new String[]{uri.getLastPathSegment()});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int rowsUpdated;
        switch (sUriMatcher.match(uri)){
            case MOVIE:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if( rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
