package tonyg.example.com.exampleblescan.models;

import android.bluetooth.BluetoothDevice;

/**
 * A visual representation of a Bluetooth Low Energy Device.
 * This is paired with a ble_list_item.xml that lets us list all the devices found by the BleCommManager
 *
 * @author Tony Gaitatzis backupbrain@gmail.com
 * @date 2015-12-17
 */
public class BlePeripheralListItem {
    private int mItemId;
    private int mRssi;
    private BluetoothDevice mBluetoothDevice;

    public BlePeripheralListItem(BluetoothDevice bluetoothDevice) {
        mBluetoothDevice = bluetoothDevice;
    }

    public void setItemId(int id) {  mItemId = id; }
    public void setRssi(int rssi) {
        mRssi = rssi;
    }

    public int getItemId() { return mItemId; }
    public String getBroadcastName() { return mBluetoothDevice.getName(); }
    public String getMacAddress() {
        return mBluetoothDevice.getAddress();
    }
    public int getRssi() { return mRssi; }
    public BluetoothDevice getDevice() { return mBluetoothDevice; }
}
