package tonyg.example.com.exampleblescan.models;


import android.bluetooth.BluetoothGattService;

import java.util.UUID;

/**
 * A visual representation of the services available on a connected BLE device
 * This is paired with a list_item_service.xml that lists the services found by the BLECommManager
 *
 * New in this chapter
 *
 * @author Tony Gaitatzis backupbrain@gmail.com
 * @date 2015-12-21
 */
public class BleGattServiceListItem {
    private final int mItemId;
    private final BluetoothGattService mService;

    public BleGattServiceListItem(BluetoothGattService gattService, int serviceItemID) {
        mItemId = serviceItemID;
        mService = gattService;
    }

    public int getItemId() { return mItemId; }
    public UUID getUuid() { return mService.getUuid(); }
    public int getType() { return mService.getType(); }
    public BluetoothGattService getService() { return mService; }
}
