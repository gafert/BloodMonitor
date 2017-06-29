package fhtw.bsa2.gafert_steiner.BloodMonitor.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
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
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import fhtw.bsa2.gafert_steiner.BloodMonitor.FileIO;
import fhtw.bsa2.gafert_steiner.BloodMonitor.R;
import fhtw.bsa2.gafert_steiner.BloodMonitor.bluetooth.BloodPressureDeviceConnector;
import fhtw.bsa2.gafert_steiner.BloodMonitor.bluetooth.BloodPressureProfile;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.ItemHolder;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_HAPPY;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_NORMAL;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_SAD;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_VERY_HAPPY;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_VERY_SAD;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.LOCATION_REQ_PERM;

/**
 * Contains all input fields to make an {@link Item}
 * Emotion is selected manually as is the Reason
 * Location is also got when the user clicks the add button
 * Used to get the location is the {@link FusedLocationProviderClient}
 * The date can be entered with a {@link DatePickerDialog}
 * Blood Pressure Values can be entered manually or with the help of GATT and Bluetooth
 */
public class AddActivity extends AppCompatActivity {

    RadioGroup emotionPicker;                       // Sets the emotionValue
    Integer emotionValue = FEELING_NORMAL;          // Get the emotion
    EditText reasonTextView;                        // Get the reason
    EditText systolicTestView;
    EditText diastolicTextView;
    EditText heartRateTextView;

    TextView dateTextView;                          // Shows the date
    ImageButton dateImageButton;
    Date date;                                      // Get the date

    GoogleApiClient googleApiClient;
    public BluetoothAdapter mBluetoothAdapter;
    BloodPressureProfile bleProfile;
    BloodPressureDeviceConnector deviceConnector;

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

        systolicTestView = (EditText)findViewById(R.id.systolicTextView);
        diastolicTextView = (EditText)findViewById(R.id.diastolicTextView);
        heartRateTextView = (EditText)findViewById(R.id.heartRateTextView);


        getBluetoothAdapter();
        if(mBluetoothAdapter!=null){
            deviceConnector = new BloodPressureDeviceConnector(this, mBluetoothAdapter);
            bleProfile = new BloodPressureProfile();
            try {
                boolean successful = deviceConnector.connect(bleProfile, "5C:31:3E:00:41:95");
                if(successful){
                    Toasty.success(AddActivity.this, "Blood Pressure Device connected", Toast.LENGTH_LONG).show();
                }else{
                    Toasty.error(AddActivity.this, "Could not connect to Blood Pressure Device", Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                Toasty.error(AddActivity.this, "Could not connect to Blood Pressure Device", Toast.LENGTH_LONG).show();
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d. MMM yyyy", Locale.getDefault());
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
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyy", Locale.getDefault());
                            DecimalFormat mFormat = new DecimalFormat("00");
                            date = simpleDateFormat.parse(String.valueOf(
                                    mFormat.format(month + 1) + "" + mFormat.format(dayOfMonth) + "" + mFormat.format(year)));

                            simpleDateFormat = new SimpleDateFormat("d. MMM yyyy", Locale.getDefault());
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
                            Integer systolicValue = Integer.valueOf(systolicTestView.getText().toString());
                            Integer diastolicValue = Integer.valueOf(diastolicTextView.getText().toString());
                            Integer heartRateValue = Integer.valueOf(heartRateTextView.getText().toString());

                            // TODO: Get bloodPressure values

                            Item item = new Item(location, date, emotionValue, addInf, systolicValue, diastolicValue, heartRateValue);
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
                            Toasty.error(AddActivity.this, "Could not get location. Try again!", Toast.LENGTH_LONG).show();
                            Log.w("AddActivity", "getLastLocation: exception", task.getException());
                        }
                    }
                });
            }
        });
    }

    public void getBluetoothAdapter() {

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Your device does not support Bluetooth", Toast.LENGTH_LONG).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 10);
        }
    }



    public void updateGUI(Float systolicValue, Float diastolicValue, Float pulse){

        Integer syst = Math.round(systolicValue);
        Integer diast = Math.round(diastolicValue);
        Integer pul = Math.round(pulse);
        systolicTestView.setText(String.valueOf(syst));
        diastolicTextView.setText(String.valueOf(diast));

        heartRateTextView.setText(String.valueOf(pul));

        Toasty.success(AddActivity.this, "Received Blood Pressure Values.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FileIO.getInstance().sync(false);
    }
}
