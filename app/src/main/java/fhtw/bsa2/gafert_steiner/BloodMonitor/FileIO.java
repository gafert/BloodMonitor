package fhtw.bsa2.gafert_steiner.BloodMonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.ItemHolder;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.GET_URL_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.ITEMS_FILE;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.NOT_ON_SERVER_FILE;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.POST_URL_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.SETTINGS;

/**
 * Created by michi on 19.06.17.
 */

public class FileIO {
    private static final String TAG = "FileIO";

    private static FileIO ourInstance = null;

    private static File itemsFile;      // All items file
    private static File tmpFile;        // Not yet saved items file (tmp file); Will be deleted when all are saved
    private List<Item> queue;

    private Context context;
    private SharedPreferences settings;

    private FileIO(Context context) {
        this.context = context;
        this.settings = context.getSharedPreferences(SETTINGS, 0);
        this.queue = readTmpFile();
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

    private void getItemFile() throws IOException {
        // Get the directory for the app's private pictures directory.
        itemsFile = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), ITEMS_FILE);
    }

    private void getTmpFile() throws IOException {
        // Get the directory for the app's private pictures directory.
        tmpFile = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), NOT_ON_SERVER_FILE);
    }

    private void deleteItemFile() {
        if (itemsFile.exists()) {
            itemsFile.delete();
        }
    }

    private void deleteTmpFile() {
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
    }

    public void deleteFiles() {
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        if (itemsFile.exists()) {
            itemsFile.delete();
        }
        // Clear also the items which have not yet been
        // Uploaded to the server
        queue.clear();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * Reads all items from the save file
     *
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return (ArrayList<Item>) new Gson().fromJson(jsonString, GlobalShit.ITEM_LIST_TYPE_TOKEN);
    }

    /**
     * Writes all Items currently shown to the user to a file
     *
     * @param items ArrayList of Item to save
     * @return was the write successful
     */
    public boolean writeItemFile(List<Item> items) {
        String itemsJsonString = new Gson().toJson(items, GlobalShit.ITEM_LIST_TYPE_TOKEN);
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
        Toast.makeText(context, "Posting to: " + settings.getString(POST_URL_PREF, null), Toast.LENGTH_LONG).show();
        new AsyncPost().execute(item);
    }

    /**
     * Read the temporary file where items which are not yet saved on the server are saved
     *
     * @return Parsed ArrayList of Items not synced to server
     */
    private ArrayList<Item> readTmpFile() {
        String jsonString = null;
        if (isExternalStorageReadable()) {
            try {
                getTmpFile();
                if (tmpFile.exists() && tmpFile.length() != 0) {
                    BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
                    jsonString = reader.readLine();
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ArrayList<Item> _items = new Gson().fromJson(jsonString, GlobalShit.ITEM_LIST_TYPE_TOKEN);
        if (_items != null) {
            return _items;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Write temporary file to save Items which have not yet been uploaded to the server
     *
     * @param items A List of Items which have not been appended to the server
     * @return Was the creation/writing of the file successful
     */
    private boolean writeTmpFile(List<Item> items) {
        String itemsJsonString = new Gson().toJson(items, GlobalShit.ITEM_LIST_TYPE_TOKEN);
        if (isExternalStorageWritable()) {
            try {
                deleteTmpFile();
                getTmpFile();
                tmpFile.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile, true));
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
     * Call this to submit all local changes to a server
     * Is automatically called when the user added a value and a server connection was established
     * Posts all not yet synced items to the server
     * Gets all data from server and compares ids
     */
    public void sync() {
        Toasty.normal(context, "Synchronising", Toast.LENGTH_SHORT).show();

        // Create a new list of the not synced items
        // and clear the old one to prevent duplication
        List<Item> _tmp = new ArrayList<>();
        _tmp.addAll(queue);
        queue.clear();

        // Delete the file as there are no elements "not synced"
        deleteTmpFile();

        // Try to sync all of them
        for (Item _oldItem : _tmp) {
            new AsyncPost().execute(_oldItem);
        }

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
            String _itemString = gson.toJson(_item, GlobalShit.ITEM_TYPE_TOKEN);
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
                sync();

            } else {
                // Sync has failed
                Log.d(TAG, "Could not send to server, Item will be added to the queue");

                // Add the not synced item to the list and save it
                // Check if the item is already in the queue
                if (!queue.contains(_item)) {
                    queue.add(_item);
                    writeTmpFile(queue);
                }
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
                List<Item> serverItems = (ArrayList<Item>) gson.fromJson(result, GlobalShit.ITEM_LIST_TYPE_TOKEN);
                List<Item> localItems = ItemHolder.getInstance().getItems();

                if (!serverItems.isEmpty()) {
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
                        List<Item> localServer = new ArrayList<>();
                        for (Item _localItem : serverItems) {
                            boolean exits = false;
                            for (Item _serverItem : localItems) {
                                if (_serverItem.getId() == _localItem.getId()) {
                                    exits = true;
                                }
                            }
                            // If the server Item does no
                            if (!exits) {
                                localServer.add(_localItem);
                            }
                        }
                        // Write all files which are not on the server but in the list
                        // to the server
                        for (Item _item : localServer) {
                            writeToServer(_item);
                        }
                    }
                    Toasty.success(context, "Synchronised", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Could not sync
                Log.d(TAG, "Could not synchronise");
                Toasty.error(context, "Could not synchronise", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
