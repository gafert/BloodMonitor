package fhtw.bsa2.gafert_steiner.BloodMonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

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

import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.POST_URL_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.SETTINGS;

/**
 * Created by michi on 19.06.17.
 */

public class FileIO {
    private static final String TAG = "FileIO";
    private static final String ITEMS_FILE = "documents.txt";
    private static final String NOT_ON_SERVER_FILE = "Not_saved_.txt";

    private static FileIO ourInstance = null;
    private static File listFile;
    private static File notSavedFile;
    private Context context;
    private SharedPreferences settings;
    private List<Item> notSavedItems;

    private FileIO(Context context) {
        this.context = context;
        this.settings = context.getSharedPreferences(SETTINGS, 0);
        this.notSavedItems = readNotSavedFile();
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

    private void getListFile() throws IOException {
        // Get the directory for the app's private pictures directory.
        listFile = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), ITEMS_FILE);
    }

    private void getNotSavedFile() throws IOException {
        // Get the directory for the app's private pictures directory.
        notSavedFile = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), NOT_ON_SERVER_FILE);
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

    public ArrayList<Item> readListFile() {
        String jsonString = null;
        if (isExternalStorageReadable()) {
            try {
                getListFile();
                if (listFile.exists() && listFile.length() != 0) {
                    BufferedReader reader = new BufferedReader(new FileReader(listFile));
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

    public boolean saveListFile(List<Item> items) {
        String itemsJsonString = new Gson().toJson(items, GlobalShit.ITEM_LIST_TYPE_TOKEN);
        Log.d(TAG, "saveListFile: " + itemsJsonString);

        // Save to local file
        if (isExternalStorageWritable()) {
            try {
                deleteListFile();
                getListFile();
                listFile.createNewFile();

                BufferedWriter bw = new BufferedWriter(new FileWriter(listFile, true));
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

    public void deleteListFile() {
        if (listFile.exists()) {
            listFile.delete();
        }
    }

    public void deleteNotSavedFile() {
        if (notSavedFile.exists()) {
            notSavedFile.delete();
        }
    }

    public void deleteFiles() {
        if (notSavedFile.exists()) {
            notSavedFile.delete();
        }
        if (listFile.exists()) {
            listFile.delete();
        }
    }

    public ArrayList<Item> readNotSavedFile() {
        String jsonString = null;
        if (isExternalStorageReadable()) {
            try {
                getNotSavedFile();
                if (notSavedFile.exists() && notSavedFile.length() != 0) {
                    BufferedReader reader = new BufferedReader(new FileReader(notSavedFile));
                    jsonString = reader.readLine();
                    reader.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ArrayList<Item> _items = (ArrayList<Item>) new Gson().fromJson(jsonString, GlobalShit.ITEM_LIST_TYPE_TOKEN);
        if (_items != null) {
            return _items;
        } else {
            return new ArrayList<>();
        }
    }

    public boolean saveNotSavedFile(List<Item> items) {
        String itemsJsonString = new Gson().toJson(items, GlobalShit.ITEM_LIST_TYPE_TOKEN);
        Log.d(TAG, "saveListFile: " + itemsJsonString);

        // Save to local file
        if (isExternalStorageWritable()) {
            try {
                deleteNotSavedFile();
                getNotSavedFile();
                notSavedFile.createNewFile();

                BufferedWriter bw = new BufferedWriter(new FileWriter(notSavedFile, true));
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

    public void saveToServer(Item item) {
        Toast.makeText(context, settings.getString(POST_URL_PREF, null), Toast.LENGTH_LONG).show();
        Toast.makeText(context, item.toString(), Toast.LENGTH_LONG).show();
        new AsyncPost().execute(item);
    }

    private class AsyncPost extends AsyncTask<Object, String, String> {
        private Item _item;

        @Override
        protected String doInBackground(Object... object) {
            // Start REST and parse to json
            _item = (Item) object[0];
            String _itemString = new Gson().toJson(_item, GlobalShit.ITEM_TYPE_TOKEN);
            return ServerIO.saveToServer(settings.getString(POST_URL_PREF, null), _itemString);
        }

        @Override
        protected void onPostExecute(String result) {
            //TODO: Save not server side saved entries in a file
            if (result != null) {
                Log.d(TAG, "onPostExecute: Server said=" + result);
                //Toast.makeText(context, "Server said:\n" + result, Toast.LENGTH_SHORT).show();

                List<Item> _tmp = new ArrayList<>();
                _tmp.addAll(notSavedItems);
                for (Item _oldItem : _tmp) {
                    notSavedItems.remove(_oldItem); // remove to prevent loop
                    new AsyncPost().execute(_oldItem);
                }
                deleteNotSavedFile();
            } else {
                Log.d(TAG, "onPostExecute: Could not send to server");
                //Toast.makeText(context, "Could not send to server", Toast.LENGTH_SHORT).show();

                notSavedItems.add(_item);
                saveNotSavedFile(notSavedItems);
            }
        }
    }
}
