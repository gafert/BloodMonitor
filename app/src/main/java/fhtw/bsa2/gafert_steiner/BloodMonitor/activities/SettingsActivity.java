package fhtw.bsa2.gafert_steiner.BloodMonitor.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import fhtw.bsa2.gafert_steiner.BloodMonitor.R;

public class SettingsActivity extends AppCompatActivity {

    // Static sharedSettings identifiers
    public static String SETTINGS = "IpPrefs";
    public static String IP_PREF = "postIp";
    public static String PORT_PREF = "port";
    public static String POSTDIRECTORY_PREF = "postDirectory";
    public static String GETDIRECTORY_PREF = "getDirectory";
    public static String GETURL_PREF = "getUrl";
    public static String POSTURL_PREF = "postUrl";
    private static String TAG = "SettingsFragment";
    EditText ip;
    EditText port;
    EditText getDirectory;
    EditText postDirectory;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seetings);

        // Get all texts
        ip = (EditText) findViewById(R.id.postIP);
        port = (EditText) findViewById(R.id.postPort);
        getDirectory = (EditText) findViewById(R.id.getDirectory);
        postDirectory = (EditText) findViewById(R.id.postDirectory);

        // Set input according to last preferences
        settings = getSharedPreferences(SETTINGS, 0);
        ip.setText(settings.getString(IP_PREF, null));
        port.setText(settings.getString(PORT_PREF, null));
        postDirectory.setText(settings.getString(POSTDIRECTORY_PREF, null));
        getDirectory.setText(settings.getString(GETDIRECTORY_PREF, null));
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePrefs();
    }

    private void savePrefs() {

        // Make url from input
        String mPostUrl = "http://" + ip.getText().toString() + ":" + port.getText().toString() + postDirectory.getText().toString();
        String mGetUrl = "http://" + ip.getText().toString() + ":" + port.getText().toString() + getDirectory.getText().toString();

        // Save preferences
        SharedPreferences.Editor editor = settings.edit();

        // Save Urls
        editor.putString(GETURL_PREF, mGetUrl);
        editor.putString(POSTURL_PREF, mPostUrl);

        // Save other input
        editor.putString(PORT_PREF, port.getText().toString());
        editor.putString(IP_PREF, ip.getText().toString());
        editor.putString(POSTDIRECTORY_PREF, postDirectory.getText().toString());
        editor.putString(GETDIRECTORY_PREF, getDirectory.getText().toString());

        // Commit the edits!
        // Apply commits in the background
        editor.apply();
    }
}
