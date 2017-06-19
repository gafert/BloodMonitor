package fhtw.bsa2.gafert_steiner.BloodMonitor;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by michi on 19.06.17.
 */

public class FileIO {
    private static FileIO ourInstance = null;
    private static final String FILENAME = "documents.txt";
    private final Context context;
    private static File dataFile;
    private final String TAG = "FileIO";

    private FileIO(Context context) {
        this.context = context;
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

    public void writeFile(String jsonString) {
        if (isExternalStorageWritable()) {
            try {
                getFile();

                if (dataFile.exists()) {
                    dataFile.delete();
                }

                getFile();
                dataFile.createNewFile();

                Log.d(TAG, "writeToFile(): " + jsonString);

                BufferedWriter bw = new BufferedWriter(new FileWriter(dataFile, true));
                PrintWriter writer = new PrintWriter(bw);

                writer.println(jsonString);
                writer.flush();
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        File photoDirectory = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES).getPath());
        photoDirectory.delete();
    }
}
