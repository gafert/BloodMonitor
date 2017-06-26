package fhtw.bsa2.gafert_steiner.BloodMonitor.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fhtw.bsa2.gafert_steiner.BloodMonitor.FileIO;
import fhtw.bsa2.gafert_steiner.BloodMonitor.R;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.ItemHolder;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_HAPPY;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_NORMAL;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_SAD;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_VERY_HAPPY;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_VERY_SAD;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.LOCATION_REQ_PERM;

public class AddActivity extends AppCompatActivity {

    RadioGroup emotionPicker;                       // Sets the emotionValue
    Integer emotionValue = FEELING_NORMAL;          // Get the emotion
    EditText reasonTextView;                        // Get the reason

    TextView dateTextView;                          // Shows the date
    ImageButton dateImageButton;
    Date date;                                      // Get the date

    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Connect google API for location
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

        dateImageButton = (ImageButton) findViewById(R.id.dateButton);
        emotionPicker = (RadioGroup) findViewById(R.id.emotionGroup);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        reasonTextView = (EditText) findViewById(R.id.reasonTextView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d. MMM yyyy");
            String dateString = simpleDateFormat.format(new Date());
            date = simpleDateFormat.parse(dateString);

            dateTextView.setText(dateString);
        } catch (ParseException e) {
            // Could not parse
        }

        emotionPicker.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.veryHappyButton:
                        emotionValue = FEELING_VERY_HAPPY;
                        break;
                    case R.id.happyButton:
                        emotionValue = FEELING_HAPPY;
                        break;
                    case R.id.normalButton:
                        emotionValue = FEELING_NORMAL;
                        break;
                    case R.id.sadButton:
                        emotionValue = FEELING_SAD;
                        break;
                    case R.id.verySadButton:
                        emotionValue = FEELING_VERY_SAD;
                        break;
                }
            }
        });

        // Sets new date picked in datePickerDialog
        View.OnClickListener onDatePick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);                 // Only select present and future
                DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyy");
                            DecimalFormat mFormat = new DecimalFormat("00");
                            date = simpleDateFormat.parse(String.valueOf(
                                    mFormat.format(month + 1) + "" + mFormat.format(dayOfMonth) + "" + mFormat.format(year)));

                            simpleDateFormat = new SimpleDateFormat("d. MMM yyyy");
                            dateTextView.setText(simpleDateFormat.format(date));
                        } catch (ParseException e) {
                            Log.e("AddFragment", "onDateSet: Could not parse to date string");
                        }
                    }
                };
                int year = Integer.parseInt((String) DateFormat.format("yyyy", date));
                int month = Integer.parseInt((String) DateFormat.format("MM", date));
                int day = Integer.parseInt((String) DateFormat.format("dd", date));

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivity.this, R.style.DatePicker, myDateListener, year, month, day);
                datePickerDialog.show();
            }
        };

        // Opens the DatePicker and changes the dateTextView accordingly
        dateImageButton.setOnClickListener(onDatePick);
        dateTextView.setOnClickListener(onDatePick);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check permissions
                if (ActivityCompat.checkSelfPermission(AddActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(AddActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        ActivityCompat.requestPermissions(AddActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_PERM);
                    } else {
                        Toast.makeText(AddActivity.this, "You need to run Android 6.0 or above!", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                // Get Location
                // If got -> make Item add it to the itemHolder
                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(AddActivity.this);
                mFusedLocationClient.getLastLocation().addOnCompleteListener(AddActivity.this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            String addInf = reasonTextView.getText().toString();
                            Item item = new Item(location, date, emotionValue, addInf);
                            if (ItemHolder.getInstance().add(item)) {
                                // Reset Add site
                                emotionPicker.check(R.id.normalButton);
                                reasonTextView.setText(null);

                                // Created a new Dialog
                                final Dialog submitDialog = new Dialog(AddActivity.this, R.style.BetterDialog);
                                submitDialog.setContentView(R.layout.dialog_submit);
                                submitDialog.show();

                                // Hide Dialog after certain time
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        submitDialog.dismiss();
                                        AddActivity.this.finish();
                                    }
                                }, 1000);
                            }
                        } else {
                            Log.w("AddActivity", "getLastLocation: exception", task.getException());
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FileIO.getInstance().sync(false);
    }
}
