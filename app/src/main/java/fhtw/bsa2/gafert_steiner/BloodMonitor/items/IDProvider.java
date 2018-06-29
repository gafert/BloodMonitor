package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.INDEX_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.SETTINGS;

/**
 * This class generates an unique ID
 * The function {@link #getNextID()} gives back an ID which is saved in
 * the {@link SharedPreferences} and +1s it and saves it again
 * <p>
 * The function {@link #setLastID(int)} can be used to set the ID in the {@link SharedPreferences}
 * This may be used if the ID was changed somewhere else and not directly with this instance
 */
public class IDProvider {
    private static final String TAG = "IDProvider";
    private static IDProvider ourInstance;
    private final Context context;
    private final SharedPreferences settings;
    private int index;

    private IDProvider(Context context) {
        this.context = context;
        this.settings = context.getSharedPreferences(SETTINGS, 0);
        this.index = settings.getInt(INDEX_PREF, 0);
    }

    public static IDProvider getInstance() {
        if (ourInstance == null) {
            Log.e(TAG, "getInstance: Context not set");
        }
        return ourInstance;
    }

    public static IDProvider getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new IDProvider(context);
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

        Log.d(TAG, "getNextID: IDProvider=" + index);
        return index;
    }

    /**
     * Sets an ID which shall be incremented if it was somewhere else changed
     *
     * @param id The ID which shall be saved in the {@link SharedPreferences}
     */
    public void setLastID(int id) {
        index = id;
    }
}
