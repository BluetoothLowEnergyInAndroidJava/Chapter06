package tonyg.example.com.exampleblescan.adapters;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tonyg.example.com.exampleblescan.R;
import tonyg.example.com.exampleblescan.ble.BlePeripheral;
import tonyg.example.com.exampleblescan.models.BleGattCharacteristicListItem;
import tonyg.example.com.exampleblescan.models.BleGattServiceListItem;

/**
 * Manages the BleGattServiceListItem so that we can populate the GATT Profile List
 * Uses a BaseExpandableListAdapter to create a tree-like structure
 *
 * New in this chapter
 *
 * @author Tony Gaitatzis backupbrain@gmail.com
 * @date 2015-12-21
 */
public class BleGattProfileListAdapter extends BaseExpandableListAdapter {
    private final static String TAG = BleGattProfileListAdapter.class.getSimpleName();

    private ArrayList<BleGattServiceListItem> mBleGattServiceListItems = new ArrayList<>(); // list of Services
    private Map<Integer, ArrayList<BleGattCharacteristicListItem>> mBleCharacteristicListItems = new HashMap<Integer, ArrayList<BleGattCharacteristicListItem>>(); // list of Characteristics

    /**
     * Instantiate the class
     */
    public BleGattProfileListAdapter() {
    }

    /** PARENT (GATT SERVICE) METHODS FOR COLLAPSIBLE TREE STRUCTURE **/

    /**
     * Get the Service ListItem listed in a groupPosition
     * @param groupPosition the position of the ListItem
     * @return the ServiceListItem
     */
    @Override
    public BleGattServiceListItem getGroup(int groupPosition) {
        return mBleGattServiceListItems.get(groupPosition);
    }

    /**
     * How many Services are listed
     * @return number of Services
     */
    @Override
    public int getGroupCount() {
        return mBleGattServiceListItems.size();
    }

    /**
     * Add a new Service to be listed in the ListView
     * @param service the GATT Service to add
     */
    public void addService(BluetoothGattService service) {
        int serviceItemID = mBleGattServiceListItems.size();
        BleGattServiceListItem serviceListItem = new BleGattServiceListItem(service, serviceItemID);
        mBleGattServiceListItems.add(serviceListItem);
        mBleCharacteristicListItems.put(serviceItemID, new ArrayList<BleGattCharacteristicListItem>());
    }

    /**
     * Add a new Characteristic to be listed in the ListView
     *
     * @param service the Service that this Characteristic belongs to
     * @param characteristic the Gatt Characteristic to add
     * @throws Exception if such a service does not exist
     */
    public void addCharacteristic(BluetoothGattService service, BluetoothGattCharacteristic characteristic) throws Exception {
        // find the Service in this listView with matching UUID from the input service
        int serviceItemId = -1;
        for (BleGattServiceListItem bleGattServiceListItem : mBleGattServiceListItems) {
            //serviceItemId++;
            if (bleGattServiceListItem.getService().getUuid().equals(service.getUuid())) {
                Log.v(TAG, "Service found with UUID: "+service.getUuid().toString());
                serviceItemId = bleGattServiceListItem.getItemId();
                //break; // FIXME: this cause problems before.  Why?
            }
        }

        // Throw an exception if no such service exists
        if (serviceItemId < 0) throw new Exception("Service not found with UUID: "+service.getUuid().toString());

        // add characterstic to the end of the sub-list for the parent service
        if (!mBleCharacteristicListItems.containsKey(serviceItemId)) {
            mBleCharacteristicListItems.put(serviceItemId, new ArrayList<BleGattCharacteristicListItem>());
        }

        int characteristicItemId = mBleCharacteristicListItems.size();
        BleGattCharacteristicListItem characteristicListItem = new BleGattCharacteristicListItem(characteristic, characteristicItemId);
        mBleCharacteristicListItems.get(serviceItemId).add(characteristicListItem);
    }

    /**
     * Clear all ListItems from ListView
     */
    public void clear() {
        mBleGattServiceListItems.clear();
        mBleCharacteristicListItems.clear();
    }

    /**
     * Get the Service ListItem at some position
     *
     * @param position the position of the ListItem
     * @return BleGattServiceListItem at position
     */
    @Override
    public long getGroupId(int position) {
        return mBleGattServiceListItems.get(position).getItemId();
    }

    /**
     * This GroupViewHolder represents what UI components are in each Service ListItem in the ListView
     */
    public static class GroupViewHolder{
        public TextView mUuidTV;
        public TextView mServiceTypeTV;
    }


    /**
     * Generate a new Service ListItem for some known position in the ListView
     *
     * @param position the position of the Service ListItem
     * @param convertView An existing List Item
     * @param parent The Parent ViewGroup
     * @return The Service List Item
     */
    @Override
    public View getGroupView(int position, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;
        GroupViewHolder serviceListItemView;

        // if this ListItem does not exist yet, generate it
        // otherwise, use it
        if(convertView == null) {
            // convert list_item_peripheral.xml.xml to a View
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            v = inflater.inflate(R.layout.list_item_ble_service, parent, false);

            // match the UI stuff in the list Item to what's in the xml file
            serviceListItemView = new GroupViewHolder();
            serviceListItemView.mUuidTV = (TextView) v.findViewById(R.id.uuid);
            serviceListItemView.mServiceTypeTV = (TextView) v.findViewById(R.id.service_type);


            v.setTag( serviceListItemView );
        } else {
            serviceListItemView = (GroupViewHolder) v.getTag();
        }

        // if there are known Services, create a ListItem that says so
        // otherwise, display a ListItem with Bluetooth Service information
        if (getGroupCount() <= 0) {
            serviceListItemView.mUuidTV.setText(R.string.peripheral_list_empty);
        } else {
            BleGattServiceListItem item = getGroup(position);

            serviceListItemView.mUuidTV.setText(item.getUuid().toString());
            int type = item.getType();
            // Is this a primary or secondary service
            if (type == BluetoothGattService.SERVICE_TYPE_PRIMARY) {
                serviceListItemView.mServiceTypeTV.setText(R.string.service_type_primary);
            } else {
                serviceListItemView.mServiceTypeTV.setText(R.string.service_type_secondary);
            }
        }
        return v;
    }

    /**
     * The IDs for this ListView do not change
     * @return <b>true</b>
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }



    /** CHILD (GATT CHARACTERISTIC) METHODS FOR COLLAPSIBLE TREE STRUCTURE **/

    /**
     * Get the Characteristic ListItem at some position in the ListView
     * @param groupPosition the position of the Service ListItem in the ListView
     * @param childPosition the sub-position of the Characteristic ListItem under the Service
     * @return BleGattCharactersiticListItem at some groupPosition, childPosition
     */
    @Override
    public BleGattCharacteristicListItem getChild(int groupPosition, int childPosition) {
        return mBleCharacteristicListItems.get(groupPosition).get(childPosition);
    }

    /**
     * Get the ID of a Charactersitic ListItem
     *
     * @param groupPosition the position of a Service ListItem
     * @param childPosition The sub-position of a Characteristic ListItem
     * @return the ID of the Characteristic ListItem
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mBleCharacteristicListItems.get(groupPosition).get(childPosition).getItemId();
    }


    /**
     * How many Characteristics exist under a Service
     *
     * @param groupPosition The position of the Service ListItem in the ListView
     * @return the number of Characteristics in this Service
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return mBleCharacteristicListItems.get(groupPosition).size();
    }

    /**
     * Charactersitics are selectable because the user can click on them to open the TalkActivity
     *
     * @param groupPosition The Service ListItem position
     * @param childPosition The Charecteristic ListItem sub-position
     * @return <b>true</b>
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



    /**
     * This ChildViewHolder represents what UI components are in each Characteristic List Item in the ListView
     */
    public static class ChildViewHolder{
        public TextView mUuidTV; // displays UUID
        public TextView mPropertyReadableTV; // displays when Characteristic is readable
        public TextView mPropertyWritableTV; // displays when Characteristic is writeable
        public TextView mPropertyNotifiableTV; // displays when Characteristic is Notifiable
        public TextView mPropertyNoneTV; // displays when no access is given
    }

    /**
     * Generate a new Characteristic ListItem for some known position in the ListView
     *
     * @param groupPosition the position of the Service ListItem
     * @param childPosition the position of the Characterstic ListItem
     * @param convertView An existing List Item
     * @param parent The Parent ViewGroup
     * @return The Characteristic ListItem
     */
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = convertView;
        ChildViewHolder characteristicListItemView;

        BleGattCharacteristicListItem item = getChild(groupPosition, childPosition);
        BluetoothGattCharacteristic characteristic = item.getCharacteristic();

        // if this ListItem does not exist yet, generate it
        // otherwise, use it
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            v = inflater.inflate(R.layout.list_item_ble_characteristic, null);

            // match the UI stuff in the list Item to what's in the xml file
            characteristicListItemView = new ChildViewHolder();
            characteristicListItemView.mUuidTV = (TextView) v.findViewById(R.id.uuid);
            characteristicListItemView.mPropertyReadableTV = (TextView)v.findViewById(R.id.property_read);
            characteristicListItemView.mPropertyWritableTV = (TextView)v.findViewById(R.id.property_write);
            characteristicListItemView.mPropertyNotifiableTV = (TextView)v.findViewById(R.id.property_notify);
            characteristicListItemView.mPropertyNoneTV = (TextView)v.findViewById(R.id.property_none);

        } else {
            characteristicListItemView = (ChildViewHolder) v.getTag();
        }

        if (characteristicListItemView != null) {

            // display the UUID of the characteristic
            characteristicListItemView.mUuidTV.setText(characteristic.getUuid().toString());

            // Display the read/write/notify attributes of the Characteristic
            if (BlePeripheral.isCharacteristicReadable(characteristic)) {
                characteristicListItemView.mPropertyReadableTV.setVisibility(View.VISIBLE);
            } else {
                characteristicListItemView.mPropertyReadableTV.setVisibility(View.GONE);
            }
            if (BlePeripheral.isCharacteristicWritable(characteristic)) {
                characteristicListItemView.mPropertyWritableTV.setVisibility(View.VISIBLE);
            } else {
                characteristicListItemView.mPropertyWritableTV.setVisibility(View.GONE);
            }
            if (BlePeripheral.isCharacteristicNotifiable(characteristic)) {
                characteristicListItemView.mPropertyNotifiableTV.setVisibility(View.VISIBLE);
            } else {
                characteristicListItemView.mPropertyNotifiableTV.setVisibility(View.GONE);
            }
            if (!BlePeripheral.isCharacteristicNotifiable(characteristic) &&
                    !BlePeripheral.isCharacteristicWritable(characteristic) &&
                    !BlePeripheral.isCharacteristicReadable(characteristic)) {
                characteristicListItemView.mPropertyNoneTV.setVisibility(View.VISIBLE);
            } else {
                characteristicListItemView.mPropertyNoneTV.setVisibility(View.GONE);

            }
        }


        return v;
    }

}