package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.INDEX_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.SETTINGS;

public class IdentificationGenerator {
    private static final String TAG = "IdentificationGenerator";
    private static IdentificationGenerator ourInstance = null;
    private Context context;
    private SharedPreferences settings;
    private int index;

    private IdentificationGenerator(Context context) {
        this.context = context;
        this.settings = context.getSharedPreferences(SETTINGS, 0);
        this.index = settings.getInt(INDEX_PREF, 0);
    }

    public static IdentificationGenerator getInstance() {
        if (ourInstance == null) {
            Log.e(TAG, "getInstance: Context not set");
        }
        return ourInstance;
    }

    public static IdentificationGenerator getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new IdentificationGenerator(context);
        }
        return ourInstance;
    }

    /**
     * Generate a new unique ID
     *
     * @return Returns the new ID
     */
    public int getNextID() {
        index = index + 1;

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(INDEX_PREF, index);
        editor.apply();

        Log.d(TAG, "getNextID: IdentificationGenerator=" + index);
        return index;
    }
}
