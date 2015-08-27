package orcaz.mymovieapp.net;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import orcaz.mymovieapp.Util.Constants;
import orcaz.mymovieapp.Util.OnMoviesRetrievedListener;
import orcaz.mymovieapp.data.MovieInfo;

public class RetrieveMoviesTask extends AsyncTask<Integer, Void, List<MovieInfo>> {
    public static final String TAG = RetrieveMoviesTask.class.getSimpleName();

    private OnMoviesRetrievedListener mListener;

    public RetrieveMoviesTask(OnMoviesRetrievedListener listener){
        mListener = listener;
    }

    @Override
    protected List<MovieInfo> doInBackground(Integer... sortBy) {
        Uri.Builder builder = Uri.parse(NetworkConstants.BASE_URL)
                .buildUpon()
                .appendQueryParameter(NetworkConstants.API_KEY_QUERY_KEY, NetworkConstants.API_KEY);
        switch (sortBy[0]){
            case Constants.BY_POPULARITY:
                builder.appendPath(NetworkConstants.DISCOVER_PATH)
                        .appendPath(NetworkConstants.MOVIE_PATH)
                        .appendQueryParameter(NetworkConstants.SORT_BY_KEY, NetworkConstants.SORT_BY_POPULARITY);
                break;
            case Constants.BY_RATING:
                builder.appendPath(NetworkConstants.DISCOVER_PATH)
                        .appendPath(NetworkConstants.MOVIE_PATH)
                        .appendQueryParameter(NetworkConstants.SORT_BY_KEY, NetworkConstants.SORT_BY_RATING)
                                // Set minimum vote count
                        .appendQueryParameter(NetworkConstants.VOTE_COUNT_MIN_KEY, "20")
                                // Limit query to last year
                        .appendQueryParameter(NetworkConstants.RELEASE_DATE_MIN_KEY, getDate());
                break;
            default:
                return null;
        }
        Uri uri = builder.build();
        Log.d(TAG, "Uri Created: " + uri.toString());
        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        String JSONString = null;

        try {
            URL url = new URL(uri.toString());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            InputStream in = httpURLConnection.getInputStream();
            if(in == null) return null;
            StringBuilder strBuilder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(in, Charset.forName("utf-8")));
            String line;
            while((line = reader.readLine()) != null) strBuilder.append(line);
            if(strBuilder.length() == 0) return null;
            JSONString = strBuilder.toString();

        } catch (MalformedURLException e) {
            Log.e(TAG, "Invalid URL: " + uri.toString(), e);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        // To deal with random network problem
        if(JSONString == null) return null;

        try {
            JSONObject responseData = new JSONObject(JSONString);
            JSONArray moviesJSONArray = responseData.getJSONArray("results");
            List<MovieInfo> movieList = new ArrayList<>();
            for (int index = 0; index < moviesJSONArray.length(); index++ ){
                JSONObject movieObject = moviesJSONArray.getJSONObject(index);
                movieList.add(new MovieInfo(
                        movieObject.getInt(NetworkConstants.MOVIE_ID_JSON_KEY),
                        movieObject.getString(NetworkConstants.ORIGINAL_TITLE_JSON_KEY),
                        NetworkConstants.IMG_URL + movieObject.getString(NetworkConstants.POSTER_PATH_JSON_KEY),
                        movieObject.getString(NetworkConstants.OVERVIEW_JSON_KEY),
                        movieObject.getDouble(NetworkConstants.RATING_JSON_KEY),
                        movieObject.getString(NetworkConstants.RELEASE_DATE_JSON_KEY)));
            }
            return movieList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<MovieInfo> movieList) {
        super.onPostExecute(movieList);
        mListener.onMoviesRetrieved(movieList);
    }

    // Get the date of 1 year before current date in yyyy-MM-dd format to limit query by rating to last year
    private String getDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(calendar.getTime());
    }
}
