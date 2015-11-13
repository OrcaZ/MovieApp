package orcaz.mymovieapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import orcaz.mymovieapp.R;

public class Util {
    public static int getSortBySetting(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(Constants.SORT_SETTINGS_KEY, Constants.BY_POPULARITY);
    }
    public static void setSortBySetting(Context context, int sortBySetting){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(Constants.SORT_SETTINGS_KEY, sortBySetting)
                .commit();
    }

    public static boolean getFavoriteSetting(Context context, int id){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Constants.FAVORITE_SETTINGS_BASE_KEY+id, false);
    }
    public static void setFavoriteSetting(Context context, int id, boolean favoriteSetting){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(Constants.FAVORITE_SETTINGS_BASE_KEY+id, favoriteSetting)
                .commit();
    }

    // Check network availability
    public static boolean IsInternetAvailable(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    // Check external storage availability
    public static boolean IsExternalStorageAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File GetPosterFile(Context context, int movieId){
        File posterStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name));
        if (!posterStorageDir.exists()) {
            if (!posterStorageDir.mkdirs()) {
                return null;
            }
        }
        return new File(posterStorageDir.getPath() + File.separator + "POSTER_" + movieId + ".jpg");
    }

    // Get the date of 1 year before current date in yyyy-MM-dd format to limit query by rating to last year
    public static String getDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(calendar.getTime());
    }
}
