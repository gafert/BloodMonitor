package fhtw.bsa2.gafert_steiner.BloodMonitor.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import fhtw.bsa2.gafert_steiner.BloodMonitor.R;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.GET_DIRECTORY_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.GET_URL_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.IP_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.PORT_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.POST_DIRECTORY_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.POST_URL_PREF;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.SETTINGS;

public class SettingsActivity extends AppCompatActivity {
    private static String TAG = "SettingsFragment";
    private EditText ip;
    private EditText port;
    private EditText getDirectory;
    private EditText postDirectory;
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
        postDirectory.setText(settings.getString(POST_DIRECTORY_PREF, null));
        getDirectory.setText(settings.getString(GET_DIRECTORY_PREF, null));
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
        editor.putString(GET_URL_PREF, mGetUrl);
        editor.putString(POST_URL_PREF, mPostUrl);

        // Save other input
        editor.putString(PORT_PREF, port.getText().toString());
        editor.putString(IP_PREF, ip.getText().toString());
        editor.putString(POST_DIRECTORY_PREF, postDirectory.getText().toString());
        editor.putString(GET_DIRECTORY_PREF, getDirectory.getText().toString());

        // Commit the edits!
        // Apply commits in the background
        editor.apply();
    }
}