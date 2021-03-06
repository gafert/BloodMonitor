package fhtw.bsa2.gafert_steiner.BloodMonitor.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fhtw.bsa2.gafert_steiner.BloodMonitor.Constants;
import fhtw.bsa2.hammer.medical_bluetooth_4_0_devices.deviceProfiles.BLEFeature;
import fhtw.bsa2.hammer.medical_bluetooth_4_0_devices.deviceProfiles.BLEProfile;
import fhtw.bsa2.hammer.medical_bluetooth_4_0_devices.gatt.GattCharacteristic;
import fhtw.bsa2.hammer.medical_bluetooth_4_0_devices.gatt.GattService;
import fhtw.bsa2.hammer.medical_bluetooth_4_0_devices.measurement.Measurement;
import fhtw.bsa2.hammer.medical_bluetooth_4_0_devices.measurement.MeasurementValue;
import fhtw.bsa2.hammer.medical_bluetooth_4_0_devices.measurement.SIUnit;

public class BloodPressureProfile implements BLEProfile {

    private List<Measurement> measurements = new ArrayList<Measurement>();
    private BloodPressureFeature features = new BloodPressureFeature();

    @Override
    public Map<GattService, List<GattCharacteristic>> getCharacteristicsList() {
        Map<GattService, List<GattCharacteristic>> map = new HashMap<GattService, List<GattCharacteristic>>();
        List<GattCharacteristic> chars = new ArrayList<GattCharacteristic>();

        chars.add(GattCharacteristic.BLOOD_PRESSURE_MEASUREMENT);
        map.put(GattService.BLOOD_PRESSURE, chars);

        return map;
    }

    @Override
    public void setFeatureProperties(BluetoothGattCharacteristic featureProperties) {
        features.setProperties(featureProperties);
    }

    @Override
    public BLEFeature getBLEFeature() {
        return features;
    }

    @Override
    public void addData(GattCharacteristic gattCharacteristic, byte[] data) {
        //Implement your parse logic and add the received values to the list of measurements
        //Hier tust du parsen und die Werte einer Liste hinzufügen:
        Log.d(this.getClass().toString(), "AddData has been called with" + gattCharacteristic.toString() + "--------" + data.length);
        MeasurementValue syst = new MeasurementValue((float) data[1], SIUnit.PRESSURE, Constants.GATT_CHARACTERISTIC_SYSTOLIC);
        MeasurementValue diast = new MeasurementValue((float) data[3], SIUnit.PRESSURE, Constants.GATT_CHARACTERISTIC_DIASTOLIC);
        MeasurementValue pulse = new MeasurementValue((float) data[7], SIUnit.PRESSURE, Constants.GATT_CHARACTERISTIC_HEART_RATE);

        Measurement measurement = new Measurement();
        measurement.addMeasurmentValue(syst);
        measurement.addMeasurmentValue(diast);
        measurement.addMeasurmentValue(pulse);

        measurements.add(measurement);
    }

    @Override
    public Measurement getLastMeasurement() {
        return measurements.get(measurements.size() - 1);
    }

    @Override
    public List<Measurement> getMeasurments() {
        return this.measurements;
    }
}
