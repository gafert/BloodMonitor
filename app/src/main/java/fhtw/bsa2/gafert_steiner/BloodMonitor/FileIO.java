package fhtw.bsa2.gafert_steiner.BloodMonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.activities.SettingsActivity.POSTURL_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.activities.SettingsActivity.SETTINGS;

/**
 * Created by michi on 19.06.17.
 */

public class FileIO {
    private static final String FILENAME = "documents.txt";
    private static FileIO ourInstance = null;
    private static File dataFile;
    private final Context context;
    private final String TAG = "FileIO";
    private SharedPreferences settings;

    private FileIO(Context context) {
        this.context = context;
        this.settings = context.getSharedPreferences(SETTINGS, 0);
    }

    public static FileIO getInstance() {
        return ourInstance;
    }

    public static FileIO getInstance(Context context) {
        if (ourInstance == null) {

            ourInstance = new FileIO(context);
        }
        return ourInstance;
    }

    private void getFile() throws IOException {
        // Get the directory for the app's private pictures directory.
        dataFile = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), FILENAME);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public void save(List<Item> items, Item newItem) {
        String itemsJsonString = new Gson().toJson(items, GlobalShit.ITEM_LIST_TYPE_TOKEN);
        String newItemjsonString = new Gson().toJson(newItem, GlobalShit.ITEM_TYPE_TOKEN);
        Log.d(TAG, "save: " + itemsJsonString);

        // Save to local file
        if (isExternalStorageWritable()) {
            try {
                deleteFile();
                getFile();
                dataFile.createNewFile();

                BufferedWriter bw = new BufferedWriter(new FileWriter(dataFile, true));
                PrintWriter writer = new PrintWriter(bw);

                writer.println(itemsJsonString);
                writer.flush();
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        new AsyncPost().execute(newItemjsonString);
    }

    public String readFile() {
        String emotionEntries = null;
        if (isExternalStorageReadable()) {
            try {
                getFile();
                if (dataFile.exists() && dataFile.length() != 0) {
                    BufferedReader reader = new BufferedReader(new FileReader(dataFile));
                    emotionEntries = reader.readLine();
                    reader.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return emotionEntries;
    }

    public void deleteFile() {
        if (dataFile.exists()) {
            dataFile.delete();
        }
    }

    public class AsyncPost extends AsyncTask<Object, String, String> {
        @Override
        protected String doInBackground(Object... object) {
            // Start REST and parse to json
            String jsonString = (String) object[0];

            return ServerIO.saveToServer(settings.getString(POSTURL_PREF, null), jsonString);
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

}
