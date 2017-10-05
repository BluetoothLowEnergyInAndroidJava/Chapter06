package tonyg.example.com.exampleblescan.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tonyg.example.com.exampleblescan.ble.callbacks.BleScanCallbackv21;
import tonyg.example.com.exampleblescan.ble.callbacks.BleScanCallbackv18;


/**
 * This class helps us manage Bluetooth Low Energy scanning functions.
 *
 * @author Tony Gaitatzis backupbrain@gmail.com
 * @date 2015-12-12
 */
public class BleCommManager {
    private static final String TAG = BleCommManager.class.getSimpleName();
    private static final long SCAN_PERIOD = 5000; // 5 seconds of scanning time

    private BluetoothAdapter mBluetoothAdapter; // Andrdoid's Bluetooth Adapter
    private BluetoothLeScanner mBluetoothLeScanner; // Ble scanner - API >= 21


    private Timer mTimer = new Timer(); // scan timer

    /**
     * Initialize the BleCommManager
     *
     * @param context the Activity context
     * @throws Exception Bluetooth Low Energy is not supported on this Android device
     */
    public BleCommManager(final Context context) throws Exception {
        // make sure Android device supports Bluetooth Low Energy
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throw new Exception("Bluetooth Not Supported");
        }

        // get a reference to the Bluetooth Manager class, which allows us to talk to talk to the BLE radio
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


    }

    /**
     * Get the Android Bluetooth Adapter
     *
     * @return BluetoothAdapter Android Bluetooth Adapter
     */
    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }


    /**
     * Scan for Peripherals
     *
     * @param bleScanCallbackv18 APIv18 compatible ScanCallback
     * @param bleScanCallbackv21 APIv21 compatible ScanCallback
     * @throws Exception
     */
    public void scanForPeripherals(final BleScanCallbackv18 bleScanCallbackv18, final BleScanCallbackv21 bleScanCallbackv21) throws Exception {
        // Don't proceed if there is already a scan in progress
        mTimer.cancel();

        // Use BluetoothAdapter.startLeScan() for Android API 18, 19, and 20
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // Scan for SCAN_PERIOD milliseconds.
            // at the end of that time, stop the scan.
            new Thread() {
                @Override
                public void run() {
                    mBluetoothAdapter.startLeScan(bleScanCallbackv18);

                    try {
                        Thread.sleep(SCAN_PERIOD);
                        mBluetoothAdapter.stopLeScan(bleScanCallbackv18);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
            // alert the system that BLE scanning has stopped after SCAN_PERIOD milliseconds
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stopScanning(bleScanCallbackv18, bleScanCallbackv21);
                }
            }, SCAN_PERIOD);


        } else { // use BluetoothLeScanner.startScan() for API 21 (Lollipop) or greater
            final ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            final List<ScanFilter> filters = new ArrayList<ScanFilter>();
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();


            new Thread() {
                @Override
                public void run() {
                    mBluetoothLeScanner.startScan(filters, settings, bleScanCallbackv21);

                    try {
                        Thread.sleep(SCAN_PERIOD);
                        mBluetoothLeScanner.stopScan(bleScanCallbackv21);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            // alert the system that BLE scanning has stopped after SCAN_PERIOD milliseconds
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stopScanning(bleScanCallbackv18, bleScanCallbackv21);
                }
            }, SCAN_PERIOD);
        }

    }


    /**
     * Stop Scanning
     *
     * @param bleScanCallbackv18 APIv18 compatible ScanCallback
     * @param bleScanCallbackv21 APIv21 compatible ScanCallback
     */
    public void stopScanning(final BleScanCallbackv18 bleScanCallbackv18, final BleScanCallbackv21 bleScanCallbackv21) {
        mTimer.cancel();

        // propagate the onScanComplete through the system
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter.stopLeScan(bleScanCallbackv18);
            bleScanCallbackv18.onScanComplete();
        } else {
            mBluetoothLeScanner.stopScan(bleScanCallbackv21);
            bleScanCallbackv21.onScanComplete();
        }
    }
}

