package fhtw.bsa2.gafert_steiner.BloodMonitor.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;

import fhtw.bsa2.gafert_steiner.BloodMonitor.activities.AddActivity;
import fhtw.bsa2.hammer.medical_bluetooth_4_0_devices.ble.DeviceConnector;
import fhtw.bsa2.hammer.medical_bluetooth_4_0_devices.measurement.Measurement;

/**
 * Created by Fabian on 26.06.2017.
 */

public class BloodPressureDeviceConnector extends DeviceConnector {

    private AddActivity mActivity;

    public BloodPressureDeviceConnector(Activity activity, BluetoothAdapter adapter) {
        super(activity, adapter);
        this.mActivity=(AddActivity) activity;
    }

    @Override
    public void onNewDataAvailable() {
        //Wenn die Messung fertig ist wird automatsich die Methode hier aufgerufen, hier kannst du die letzte Messung nehmen und irgendwo in deine GUI schreiben
        Measurement currentMessure = this.getLastMeasurement();

        Float systolicValue = currentMessure.getMeasurementValueByName("Systolischer Blutdruck").getValue();
        Float diastolicValue = currentMessure.getMeasurementValueByName("Diastolischer Blutdruck").getValue();
        Float heartRateValue = currentMessure.getMeasurementValueByName("Puls").getValue();

        mActivity.updateGUI(systolicValue, diastolicValue, heartRateValue);


        Log.d("EMPFANGEN", "currently received some blood pressure values");
    }
}
