package tonyg.example.com.exampleblescan.models;


import android.bluetooth.BluetoothGattCharacteristic;

/**
 * A visual representation of the characteristics available in a BLE service
 * This is paired with a list_item_ble_characteristic.xml that lists the services found by the BleCommManager
 *
 * New in this chapter
 *
 * @author Tony Gaitatzis backupbrain@gmail.com
 * @date 2015-12-21
 */
public class BleGattCharacteristicListItem {
    private int mItemId;
    private BluetoothGattCharacteristic mCharacteristic;

    public BleGattCharacteristicListItem(BluetoothGattCharacteristic characteristic, int itemId) {
        mCharacteristic = characteristic;
        mItemId = itemId;
    }

    public int getItemId() { return mItemId; }
    public BluetoothGattCharacteristic getCharacteristic() { return mCharacteristic; }
}
