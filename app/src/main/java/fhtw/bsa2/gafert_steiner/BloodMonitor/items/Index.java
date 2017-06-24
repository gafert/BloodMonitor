package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.INDEX_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.SETTINGS;

public class Index {
    private static final String TAG = "Index";
    private static Index ourInstance = null;
    private Context context;
    private SharedPreferences settings;
    private int index;

    private Index(Context context) {
        this.context = context;
        this.settings = context.getSharedPreferences(SETTINGS, 0);
        this.index = settings.getInt(INDEX_PREF, 0);
    }

    public static Index getInstance() {
        if (ourInstance == null) {
            Log.e(TAG, "getInstance: Context not set");
        }
        return ourInstance;
    }

    public static Index getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new Index(context);
        }
        return ourInstance;
    }

    public int getNextID() {
        index = index + 1;

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(INDEX_PREF, index);
        editor.apply();

        Log.d(TAG, "getNextID: Index=" + index);
        return index;
    }
}
