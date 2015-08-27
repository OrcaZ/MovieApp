package orcaz.mymovieapp.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

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

    // Check network availability
    public static boolean IsInternetAvailable(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
