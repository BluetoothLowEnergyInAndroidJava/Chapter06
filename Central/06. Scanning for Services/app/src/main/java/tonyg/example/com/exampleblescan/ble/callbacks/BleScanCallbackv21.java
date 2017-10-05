package tonyg.example.com.exampleblescan.ble.callbacks;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import java.util.List;

import tonyg.example.com.exampleblescan.models.BlePeripheralListItem;


/**
 * BleScanCallbackv18 has callbacks to support not only onLeScan when a new Peripheral is found,
 * but also onScanComplete when BLE scanning has stopped
 *
 * @author Tony Gaitatzis backupbrain@gmail.com
 * @date 2015-12-18
 */
public abstract class BleScanCallbackv21 extends ScanCallback {


    /**
     * New Perpheral found.
     *
     * @param callbackType int: Determines how this callback was triggered. Could be one of CALLBACK_TYPE_ALL_MATCHES, CALLBACK_TYPE_FIRST_MATCH or CALLBACK_TYPE_MATCH_LOST
     * @param result a Bluetooth Low Energy Scan Result, containing the Bluetooth Device, RSSI, and other information
     */
    @Override
    public abstract void onScanResult(int callbackType, ScanResult result);

    /**
     * New Perpherals found.
     *
     * @param results List: List of scan results that are previously scanned.
     */
    @Override
    public abstract void onBatchScanResults(List<ScanResult> results);

    /**
     * Problem initializing the scan.  See the error code for reason
     *
     * @param errorCode	int: Error code (one of SCAN_FAILED_*) for scan failure.
     */
    @Override
    public abstract void onScanFailed(int errorCode);

    /**
     * Scan has completed
     */
    public abstract void onScanComplete();

}
