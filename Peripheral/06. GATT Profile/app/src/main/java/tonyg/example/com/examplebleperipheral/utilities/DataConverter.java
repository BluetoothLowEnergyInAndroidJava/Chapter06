package tonyg.example.com.examplebleperipheral.utilities;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Convert data formats
 *
 * @author Tony Gaitatzis backupbrain@gmail.com
 * @date 2015-12-21
 */

public class DataConverter {

    /**
     * convert bytes to hexadecimal for debugging purposes
     *
     * @param intValue the data to convert
     * @param length the max length of the data
     * @return byte array version of the intValue
     */
    public static byte[] intToBytes(int intValue, int length) {
        ByteBuffer b = ByteBuffer.allocate(length);

        // BLE Data is always little-endian
        b.order(ByteOrder.LITTLE_ENDIAN);
        b.putInt(intValue);

        return b.array();
    }

}
