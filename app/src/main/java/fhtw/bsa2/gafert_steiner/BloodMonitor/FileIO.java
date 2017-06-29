package fhtw.bsa2.gafert_steiner.BloodMonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.ItemHolder;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.GET_URL_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.ITEMS_FILE;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.POST_URL_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.SETTINGS;

/**
 * Saves items to a File with {@link #writeItemFile(List)} and to a server with {@link AsyncPost}
 * With the function {@link #sync(Boolean)} {@link FileIO} compares the Server files which it
 * got from {@link AsyncGet} to the local files. If there are changes post them or add them to
 * the local list.
 * <p>
 * Also reads from the local File with {@link #readItemFile()}
 */
public class FileIO {
    private static final String TAG = "FileIO";
    private static FileIO ourInstance = null;
    private static File itemsFile;      // All items file
    private final Context context;
    private final SharedPreferences settings;

    private boolean showToasts = false;
    private boolean isAdding = false;

    private FileIO(Context context) {
        this.context = context;
        this.settings = context.getSharedPreferences(SETTINGS, 0);
    }

    public static FileIO getInstance() {
        if (ourInstance == null) {
            Log.e(TAG, "getInstance: Context not set");
        }
        return ourInstance;
    }

    public static FileIO getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new FileIO(context);
        }
        return ourInstance;
    }

    private void getItemFile() {
        // Get the directory for the app's private pictures directory.
        itemsFile = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), ITEMS_FILE);
    }

    private void deleteItemFile() {
        if (itemsFile.exists()) {
            itemsFile.delete();
        }
    }

    public void deleteFiles() {
        if (itemsFile.exists()) {
            itemsFile.delete();
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * Reads all items from the save file
     * @return Parsed ArrayList of Item
     */
    public ArrayList<Item> readItemFile() {
        String jsonString = null;
        if (isExternalStorageReadable()) {
            try {
                getItemFile();
                if (itemsFile.exists() && itemsFile.length() != 0) {
                    BufferedReader reader = new BufferedReader(new FileReader(itemsFile));
                    jsonString = reader.readLine();
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Gson().fromJson(jsonString, Constants.ITEM_LIST_TYPE_TOKEN);
    }

    /**
     * Writes all Items currently shown to the user to a file
     *
     * @param items ArrayList of Item to save
     * @return was the write successful
     */
    public boolean writeItemFile(List<Item> items) {
        String itemsJsonString = new Gson().toJson(items, Constants.ITEM_LIST_TYPE_TOKEN);
        if (isExternalStorageWritable()) {
            try {
                deleteItemFile();           // Delete the old file
                getItemFile();              // Get the new file
                itemsFile.createNewFile();  // Create the new file
                BufferedWriter bw = new BufferedWriter(new FileWriter(itemsFile, true));
                PrintWriter writer = new PrintWriter(bw);
                writer.println(itemsJsonString);
                writer.flush();
                writer.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Public function to append items to the server
     * Saves items when server connection cannot be established and saves them in a local tmp file
     *
     * @param item Item to append to server
     */
    public void writeToServer(Item item) {
        this.isAdding = true;
        Toasty.info(context, "Trying to send Item to server", Toast.LENGTH_SHORT).show();
        new AsyncPost().execute(item);
    }

    /**
     * Call this to submit all local changes to a server
     * Is automatically called when the user added a value and a server connection was established
     * Posts all not yet synced items to the server
     * Gets all data from server and compares ids
     */
    public void sync(@Nullable Boolean showToasts) {
        isAdding = false;
        if (showToasts == null) {
            this.showToasts = false;
        } else {
            this.showToasts = showToasts;
        }

        if (this.showToasts)
            Toasty.info(context, "Synchronising", Toast.LENGTH_SHORT).show();

        // Load from Server and compare
        new AsyncGet().execute();
    }

    /**
     * Handles the posting to the server via Rest
     * Attribute is only the item to append
     * Call with AsyncPost.execute(item)
     */
    private class AsyncPost extends AsyncTask<Object, String, String> {
        private Item _item;

        @Override
        protected String doInBackground(Object... object) {
            // Start REST and parse to json
            _item = (Item) object[0];
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd_HH:mm").create();
            String _itemString = gson.toJson(_item, Constants.ITEM_TYPE_TOKEN);
            return ServerIO.saveToServer(settings.getString(POST_URL_PREF, null), _itemString);
        }

        /**
         * Was the sync successful?
         * Handle not synced items
         *
         * @param result Response of the Rest Class
         *               If null the item has not been synced
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                Log.d(TAG, "Sent Item to server");
                if (isAdding)
                    Toasty.success(context, "Synchronised", Toast.LENGTH_SHORT).show();
                isAdding = false;

                sync(false);

            } else {
                // Sync has failed
                if (isAdding)
                    Toasty.warning(context, "Could not send item to server\nThe Item will be uploaded later", Toast.LENGTH_LONG).show();
                isAdding = false;
            }
        }
    }

    /**
     * Gets the data from the server
     * Parses it and compares it to the local files
     * If there are items which are not locally saved add them
     * Also checks if all local files are on the server
     * If there are items not on the server call the post method and try to add the, to the server
     */
    private class AsyncGet extends AsyncTask<Object, String, String> {
        @Override
        protected String doInBackground(Object... params) {
            Log.d(TAG, "Syncing");
            return ServerIO.loadFromServer(settings.getString(GET_URL_PREF, null));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Log.d(TAG, "Synced");
                // Compare with local files and maybe add them
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd_HH:mm").create();
                List<Item> serverItems = gson.fromJson(result, Constants.ITEM_LIST_TYPE_TOKEN);
                List<Item> localItems = ItemHolder.getInstance().getItems();

                if (localItems.isEmpty()) {
                    ItemHolder.getInstance().merge(serverItems);
                } else {
                    // Write data to local
                    List<Item> serverLocal = new ArrayList<>();
                    for (Item _serverItem : serverItems) {
                        boolean exits = false;
                        for (Item _localItem : localItems) {
                            if (_serverItem.getId() == _localItem.getId()) {
                                exits = true;
                            }
                        }
                        // If the server Item does not exist locally add it
                        if (!exits) {
                            serverLocal.add(_serverItem);
                        }
                    }
                    ItemHolder.getInstance().merge(serverLocal);

                    //Write data to server if its a new server for example
                    for (Item _localItem : localItems) {
                        boolean exits = false;
                        for (Item _serverItem : serverItems) {
                            if (_localItem.getId() == _serverItem.getId()) {
                                exits = true;
                            }
                        }
                        // If the server Item does no
                        if (!exits) {
                            writeToServer(_localItem);
                        }
                    }

                }
                if (showToasts)
                    Toasty.success(context, "Synchronised", Toast.LENGTH_SHORT).show();
            } else {
                // Could not sync
                Log.d(TAG, "Could not synchronise");
                if (showToasts)
                    Toasty.error(context, "Could not synchronise", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
