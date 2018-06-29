package fhtw.bsa2.gafert_steiner.BloodMonitor.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;

import fhtw.bsa2.hammer.medical_bluetooth_4_0_devices.deviceProfiles.BLEFeature;
import fhtw.bsa2.hammer.medical_bluetooth_4_0_devices.gatt.GattCharacteristic;

/**
 * Created by Fabian on 27.06.2017.
 */

public class BloodPressureFeature implements BLEFeature {

    private BluetoothGattCharacteristic characteristic;

    @Override
    public GattCharacteristic getGattCharacteristicFeature() {
        return GattCharacteristic.BLOOD_PRESSURE_FEATURE;
    }

    @Override
    public void setProperties(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        this.characteristic=bluetoothGattCharacteristic;
    }

    @Override
    public String getFeatureString() {
        return this.toString();
    }
}
