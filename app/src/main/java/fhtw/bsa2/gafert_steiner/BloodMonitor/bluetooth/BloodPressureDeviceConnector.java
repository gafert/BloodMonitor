package fhtw.bsa2.gafert_steiner.BloodMonitor.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import fhtw.bsa2.gafert_steiner.BloodMonitor.Constants;
import fhtw.bsa2.gafert_steiner.BloodMonitor.activities.AddActivity;
import fhtw.bsa2.hammer.medical_bluetooth_4_0_devices.ble.DeviceConnector;
import fhtw.bsa2.hammer.medical_bluetooth_4_0_devices.measurement.Measurement;

public class BloodPressureDeviceConnector extends DeviceConnector {

    private AddActivity mActivity;

    public BloodPressureDeviceConnector(Activity activity, BluetoothAdapter adapter) {
        super(activity, adapter);
        this.mActivity = (AddActivity) activity;
    }

    @Override
    public void onNewDataAvailable() {
        // Wenn die Messung fertig ist wird automatsich die Methode hier aufgerufen,
        // hier kannst du die letzte Messung nehmen und irgendwo in deine GUI schreiben
        Measurement measurement = this.getLastMeasurement();

        Float systolicValue = measurement.getMeasurementValueByName(Constants.GATT_CHARACTERISTIC_SYSTOLIC).getValue();
        Float diastolicValue = measurement.getMeasurementValueByName(Constants.GATT_CHARACTERISTIC_DIASTOLIC).getValue();
        Float heartRateValue = measurement.getMeasurementValueByName(Constants.GATT_CHARACTERISTIC_HEART_RATE).getValue();

        mActivity.updateGUI(systolicValue, diastolicValue, heartRateValue);

        Log.d("BloodPressureDevice", "onNewDataAvailable: Received some blood pressure values");
    }
}
