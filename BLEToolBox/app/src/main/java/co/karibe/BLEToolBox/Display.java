package co.karibe.BLEToolBox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static co.karibe.BLEToolBox.R.id.toolbar;
/**
 * BLEToolBox:
 * Created by karibe on 6/3/17.
 */

public class Display extends AppCompatActivity {
    private Toolbar mToolbar;
    private BluetoothAdapter mBluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings mSettings;
    private List<ScanFilter> mFilters;
    private BluetoothGatt mGatt;
    private List<BluetoothGattService> mServices;
    private ArrayList<ArrayList<String>> mItems = new ArrayList<>();
    private ListView mListView;
    private ScanResult result;
    private BluetoothDevice mDevice;
    private MenuItem mMenuItem;
    private int mBatteryLevel;
    private int scanCount;
    private String mMessage;
    private BluetoothGattCharacteristic mCharacteristic;
    private BluetoothGattService mService;
    //whether to log debug messages
    private final static boolean debug = true;

    //Gap service UUID
    private static final UUID GenAccess_Service_UUID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");

    //Gatt Service UUID
    private static final UUID GattService_UUID = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
    //Client Characteristic Configuration Descriptor declaration UUID
    //for configuring a characteristic for one client on one server
    private static final UUID DescriptorConfig_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    //Server Characteristic Configuration Descriptor declaration UUID
    //for changing a characteristic for many clients on one server
    //private static final UUID ServerDescriptorConfig_UUID = UUID.fromString("00002903-0000-1000-8000-00805f9b34fb");
    //Battery service UUID
    private static final UUID Battery_Service_UUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    //Battery level characteristic UUID
    private static final UUID Battery_Level_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    //Heart rate service UUID
    private static final UUID HeartRate_Service_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");
    //Heart rate characteristic UUID
    private static final UUID Heart_rate_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    //Device information service UUID
    private static final UUID DeviceInfo_Service_UUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");
    //Device manufacturer characteristic UUID - UTF-8 string
    private static final UUID DeviceManufacturer_UUID = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");
    //Device Serial Number characteristic - UTF-8 string
    private static final UUID DeviceSerialNumber_UUID = UUID.fromString("00002a25-0000-1000-8000-00805f9b34fb");
    //Device model number characteristic
    private static final UUID DeviceModelNumber_UUID = UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb");
    //Device hardware revision characteristic - UTF-8 string
    private static final UUID DeviceHardwareRevision_UUID = UUID.fromString("00002a27-0000-1000-8000-00805f9b34fb");
    //Device firmware revision characteristic - UTF-8 string
    private static final UUID DeviceFirmwareRevision_UUID = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    //Device software revision characteristic - UTF-8 string
    private static final UUID DeviceSoftwareRevision_UUID = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb");
    //Device System ID characteristic - UTF-8 string
    //private static final UUID DeviceSystemID_UUID = UUID.fromString("00002a23-0000-1000-8000-00805f9b34fb");

    //Device name characteristic UUID
    private static final UUID DeviceName_UUID = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");
    //Blood pressure service UUID
    private static final UUID BPS_Service_UUID = UUID.fromString("00001810-0000-1000-8000-00805f9b34fb");
    //Blood pressure characteristic UUID
    private static final UUID Bps_UUID = UUID.fromString("00002a35-0000-1000-8000-00805f9b34fb");
    //Blood pressure feature characteristic UUID 0x2A49
    private static final UUID BloodPressureFeature_UUID = UUID.fromString("00002A49-0000-1000-8000-00805f9b34fb");
    //Intermediate cuff pressure characteristic UUID
    private static final UUID IntermediatePressure_UUID = UUID.fromString("00002A36-0000-1000-8000-00805f9b34fb");

    //private static final UUID FLX_Service_UUID = UUID.fromString("0000F1E3-0000-1000-8000-00805f9b34fb");
    private static final UUID FLX_Service_UUID = UUID.fromString("01ff0300-ba5e-f4ee-5ca1-eb1e5e4b1ce0");
    private static final UUID Flx_UUID = UUID.fromString("0000F1E8-0000-1000-8000-00805f9b34fb");

    //private static final UUID Temperature_Service_UUID = UUID.fromString("01ff0200-ba5e-f4ee-5ca1-eb1e5e4b1ce0");
    //private static final UUID Temperature_UUID = UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb");
    @SuppressWarnings("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get intent
        Intent intent = getIntent();
        mMessage = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //Display required layout
        switch (mMessage){
            case "blood_pressure":
                setContentView(R.layout.blood_pressure);
                break;
            case "heart_rate":
                setContentView(R.layout.heart_rate);
                break;
            case "flex_sensors":
                setContentView(R.layout.flex_sensor);
        }

        //activate our Toolbar
        mToolbar = (Toolbar)findViewById(toolbar);
        String m = mMessage.replace("_"," ");
        mToolbar.setTitle("BLE Toolbox: "+m);
        setSupportActionBar(mToolbar);
        //initialize some objects
        scanCount = 0;
        mDevice = null;
        //handler for the BLE scan thread
        mHandler = new Handler();
        mListView = (ListView) findViewById(R.id.orodha);
        CustomAdapter cus = new CustomAdapter(this, mItems);
        mListView.setAdapter(cus);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                if(debug) {
                    Toast.makeText(Display.this, "You Clicked at ".concat(Integer.toString(position)),
                            Toast.LENGTH_SHORT).show();
                }
                //Connect to selected device
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectToDevice(mDevice);
                    }
                });
            }
        });
        mListView.setVisibility(View.VISIBLE);
        //enable bluetooth
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(Display.this,
                    R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            //TODO: Add scanning filters for all other type of sensors
            if (Build.VERSION.SDK_INT >= 21) {//Only work in BLE enabled devices
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                mSettings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                mFilters = new ArrayList<>();//ScanFilter
                switch(mMessage){
                    case "blood_pressure":
                        ParcelUuid parcelUuid_bp =  new ParcelUuid(UUID.fromString("00001810-0000-0000-0000-000000000000"));
                        ParcelUuid parcelUuidMask_bp = new ParcelUuid(UUID.fromString(getString(R.string.uuid_mask)));
                        ScanFilter filter_bp = new ScanFilter.Builder()
                                .setServiceUuid(parcelUuid_bp, parcelUuidMask_bp)
                                .build();
                        mFilters.add(filter_bp);
                        break;
                    case "heart_rate":
                        ParcelUuid parcelUuid_hr =  new ParcelUuid(UUID.fromString("0000180D-0000-0000-0000-000000000000"));
                        ParcelUuid parcelUuidMask_hr = new ParcelUuid(UUID.fromString(getString(R.string.uuid_mask)));
                        ScanFilter filter_hr = new ScanFilter.Builder()
                                .setServiceUuid(parcelUuid_hr, parcelUuidMask_hr)
                                .build();
                        mFilters.add(filter_hr);
                    case "flex_sensors":
                        /*ParcelUuid parcelUuid_tmp = new ParcelUuid(UUID.fromString("0000F1E3-0000-0000-0000-000000000000"));
                        ParcelUuid parcelUuidMask_tmp = new ParcelUuid(UUID.fromString(getString(R.string.uuid_mask)));
                        ScanFilter filter_tmp = new ScanFilter.Builder()
                                .setServiceUuid(parcelUuid_tmp, parcelUuidMask_tmp)
                                .build();
                        mFilters.add(filter_tmp);*/
                        //edit the Mac Address at the end of string.xml resource
                        ScanFilter filter = new ScanFilter.Builder().setDeviceAddress(getString(R.string.macAddress)).build();
                        mFilters.add(filter);
                    default:
                        Log.w("onCreate","No service filter requested");
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds mItems to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        mMenuItem = menu.findItem(R.id.mActions);
        mMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (debug) {
                    Log.d("Actions_title",item.getTitle().toString());
                }
                if (item.getTitle().toString().equalsIgnoreCase("SCAN")) {
                    scanLeDevice(true);
                    item.setTitle(R.string.stop);
                }else if(item.getTitle().toString().equalsIgnoreCase("STOP")){
                    scanLeDevice(false);
                    item.setTitle(R.string.scan);
                }else if(item.getTitle().toString().equalsIgnoreCase("DISCONNECT")){
                    mGatt.disconnect();
                    mGatt=null;
                    item.setTitle("CONNECT");
                    item.getIcon().setVisible(false,true);
                    TextView txt = (TextView)findViewById(R.id.battery_level);
                    txt.setText("---");
                }else if(item.getTitle().toString().equalsIgnoreCase("CONNECT")){
                    //DONE: Test reconnecting to same mDevice
                    mDevice =mBluetoothAdapter.getRemoteDevice(result.getDevice().getAddress());
                    if(!(mDevice ==null)) {
                        if(debug) {
                            Log.d("mActions", "CONNECT>>mDevice: ".concat(mDevice.getName()).concat(", ").concat(mDevice.getAddress()));
                        }
                        mGatt=null;
                        connectToDevice(mDevice);
                    }else{
                        if (debug) {
                            Log.w("CONNECT", "No mDevice available");
                        }
                        item.setTitle(getString(R.string.scan));
                    }
                }
                return true;
            }
        });
        return true;
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            mFilters = new ArrayList<>();//ScanFilter
            //TODO: Add all other possible services from NXP Connectivity Software examples
            switch(mMessage){
                case "blood_pressure":
                    ParcelUuid parcelUuid_bp =  new ParcelUuid(UUID.fromString("00001810-0000-0000-0000-000000000000"));
                    ParcelUuid parcelUuidMask_bp = new ParcelUuid(UUID.fromString(getString(R.string.uuid_mask)));
                    ScanFilter filter_bp = new ScanFilter.Builder()
                            .setServiceUuid(parcelUuid_bp, parcelUuidMask_bp)
                            .build();
                    mFilters.add(filter_bp);
                    ImageView info = (ImageView)findViewById(R.id.device_information);
                    info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //read device info service
                            BluetoothGattService device_information = mGatt.getService(DeviceInfo_Service_UUID);
                            List<BluetoothGattCharacteristic> x_tics = device_information.getCharacteristics();
                            long tout = 0;
                            for (final BluetoothGattCharacteristic x_tic : x_tics){
                                tout +=1000;
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        mGatt.readCharacteristic(x_tic);
                                    }
                                },tout);
                            }
                        }
                    });
                    break;
                case "heart_rate":
                    ParcelUuid parcelUuid_hr =  new ParcelUuid(UUID.fromString("0000180D-0000-0000-0000-000000000000"));
                    ParcelUuid parcelUuidMask_hr = new ParcelUuid(UUID.fromString(getString(R.string.uuid_mask)));
                    ScanFilter filter_hr = new ScanFilter.Builder()
                            .setServiceUuid(parcelUuid_hr, parcelUuidMask_hr)
                            .build();
                    mFilters.add(filter_hr);
                case "flex_sensors":
                    //ParcelUuid parcelUuid_tmp = new ParcelUuid(UUID.fromString("0000F1E3-0000-0000-0000-000000000000"));
                    //ParcelUuid parcelUuidMask_tmp = new ParcelUuid(UUID.fromString(getString(R.string.uuid_mask)));
                    ScanFilter filter = new ScanFilter.Builder().setDeviceAddress("00:60:37:23:4F:3A").build();
                    mFilters.add(filter);
                    /*ScanFilter filter_tmp = new ScanFilter.Builder()
                            .setServiceUuid(parcelUuid_tmp,parcelUuidMask_tmp)
                            .build();
                    mFilters.add(filter_tmp);*/
                default:
                    Log.w("onCreate","No service filter requested");
            }
        }
    }

    @Override
    protected void onPause() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
        super.onPause();
    }
    @Override
    protected void onStop() {
        Log.w("onStop", "App stopped");
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        if (!(mGatt == null)) {
            mGatt.close();
            mGatt = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {// it was a ble enable request
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();//Bluetooth not enabled, quit app
                return;
            } else {
                Log.i("onActivityResult", "Bluetooth enabled");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLEScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);
            mLEScanner.startScan(mFilters, mSettings, mScanCallback);
            //mLEScanner.startScan(mScanCallback);
            scanCount++;
            if (debug) {
                Log.i("scanCount", Integer.toString(scanCount));
            }

        } else {
            mLEScanner.stopScan(mScanCallback);
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult res) {
            result = res;
            BluetoothDevice btDevice = result.getDevice();
            mDevice =btDevice;
            Log.d("onScanResult device: ",mDevice.toString());
            String s = btDevice.getName();
            String s2 = btDevice.getAddress();
            ArrayList<String> row = new ArrayList<>();
            row.add(0,s);
            row.add(1,s2);
            if (!mItems.contains(row)) {
                mItems.add(row);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMenuItem.setTitle(R.string.scan);
                    ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.d("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            if (debug) {
                Log.d("Scan Failed", "Error Code: " + errorCode);
            }
        }
    };

    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback, 2);
            scanLeDevice(false);// will stop after first mDevice detection
        }else{
            if (debug) {
                Log.d("connectToDevice", "mGatt not NULL");
            }
        }
    }
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (debug) {
                Log.i("onConnectionStateChange", "Status: " + status);
            }
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    if (debug) {
                        Log.d("gattCallback", "STATE_CONNECTED");
                    }
                    mGatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    if (debug){
                        Log.d("gattCallback", "STATE_DISCONNECTED");
                    }
                    break;
                default:
                    if (debug){
                        Log.d("gattCallback", "STATE_OTHER");
                    }
            }
        }
        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            mServices = gatt.getServices();

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    BluetoothGattService DevInfoService = gatt.getService(DeviceInfo_Service_UUID);
                    if(DevInfoService==null){
                        if (debug){
                            Log.d("onServicesDiscovered","Device Information Service not available");
                        }
                    }else{
                        BluetoothGattCharacteristic DeviceManufacturer = DevInfoService.getCharacteristic(DeviceManufacturer_UUID);
                        if(DeviceManufacturer==null){
                            if (debug){
                                Log.d("onServicesDiscovered","Device Manufacturer not available");
                            }
                        }else{
                            if (debug){
                                Log.d("onServicesDiscovered","Device Manufacturer read: "+gatt.readCharacteristic(DeviceManufacturer));
                            }
                        }
                    }
                }
            }, 1000);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    BluetoothGattService GenAccService = gatt.getService(GenAccess_Service_UUID);
                    if(GenAccService==null){
                        if (debug){
                            Log.d("onServicesDiscovered","Generic Access Service not available");
                        }
                    }else{
                        BluetoothGattCharacteristic DeviceName = GenAccService.getCharacteristic(DeviceName_UUID);
                        if(DeviceName==null){
                            if (debug){
                                Log.d("onServicesDiscovered","Device Name not available");
                            }
                        }else{
                            if (debug){
                                Log.d("onServicesDiscovered","Device Name read: "+gatt.readCharacteristic(DeviceName));
                            }
                        }
                    }
                }
            }, 2000);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    BluetoothGattService HeartRateService = gatt.getService(HeartRate_Service_UUID);
                    if(HeartRateService==null){
                        if (debug){
                            Log.d("onServicesDiscovered","Heart rate Service not available");
                        }
                    }else{
                        BluetoothGattCharacteristic HeartRate = HeartRateService.getCharacteristic(Heart_rate_UUID);
                        if (HeartRate==null){
                            if (debug){
                                Log.d("onServicesDiscovered","Heart rate  not available");
                            }
                        }else{
                            if (debug){
                                Log.d("onServicesDiscovered","Heart rate read: "+gatt.readCharacteristic(HeartRate));
                            }
                        }
                    }
                }
            }, 3000);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    BluetoothGattService Battservice = gatt.getService(Battery_Service_UUID);
                    if(Battservice==null){
                        if (debug){
                            Log.d("onServicesDiscovered","Battery Service not available");
                        }
                    }else{
                        BluetoothGattCharacteristic BattLevel = Battservice.getCharacteristic(Battery_Level_UUID);
                        if (BattLevel==null){
                            if (debug){
                                Log.d("onServicesDiscovered","Battery Level not available");
                            }
                        }else{
                            //enable Battery level notification locally
                            gatt.setCharacteristicNotification(BattLevel,true);
                            //enable Battery level notification on server(peripheral)
                            BluetoothGattDescriptor batteryDescriptor = BattLevel.getDescriptor(DescriptorConfig_UUID);
                            batteryDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(batteryDescriptor);
                            if (debug){
                                Log.d("onServicesDiscovered","enabled battery notification");
                            }
                        }
                    }
                }
            }, 4000);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    BluetoothGattService BloodPressureService = gatt.getService(BPS_Service_UUID);
                    if(BloodPressureService==null){
                        if (debug){
                            Log.d("onServicesDiscovered","Blood pressure Service not available");
                        }
                    }else{
                        BluetoothGattCharacteristic BPFeature = BloodPressureService.getCharacteristic(BloodPressureFeature_UUID);
                        if(BPFeature==null){
                            if (debug){
                                Log.d("onServicesDiscovered","BPFeature is not available");
                            }
                        }else{
                            gatt.readCharacteristic(BPFeature);
                            if (debug){
                                Log.d("onServicesDiscovered","Reading BPFeature characteristic");
                            }
                        }
                    }
                }
            }, 5000);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    BluetoothGattService BloodPressureService = gatt.getService(BPS_Service_UUID);
                    if(BloodPressureService==null){
                        if (debug){
                            Log.d("onServicesDiscovered","Blood pressure Service not available");
                        }
                    }else{
                        BluetoothGattCharacteristic intermediatePressure = BloodPressureService.getCharacteristic(IntermediatePressure_UUID);
                        if (intermediatePressure==null){
                            if (debug){
                                Log.d("onServicesDiscovered","Blood pressure intermediate not available");
                            }
                        }else{
                            //enable Pressure value notification locally
                            gatt.setCharacteristicNotification(intermediatePressure,true);
                            //enable notification on server
                            BluetoothGattDescriptor descriptor = intermediatePressure.getDescriptor(DescriptorConfig_UUID);
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                            if (debug){
                                Log.d("onServicesDiscovered","enabled intermediate blood pressure notification");
                            }
                        }
                    }
                }
            }, 6000);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    BluetoothGattService BloodPressureService = gatt.getService(BPS_Service_UUID);
                    if (BloodPressureService == null) {
                        if (debug) {
                            Log.d("onServicesDiscovered", "Blood pressure service not available");
                        }
                    } else {
                        BluetoothGattCharacteristic pressure = BloodPressureService.getCharacteristic(Bps_UUID);
                        if (pressure == null) {
                            if (debug) {
                                Log.d("onServicesDiscovered", "No Blood Pressure Measurements");
                            }
                        } else {
                            gatt.setCharacteristicNotification(pressure, true);
                            BluetoothGattDescriptor descriptor = pressure.getDescriptor(DescriptorConfig_UUID);
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                            if (debug) {
                                Log.d("onServicesDiscovered", "Enabled BP measurement notification");
                            }
                        }
                    }
                }
            },7000);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    BluetoothGattService FlexSensorService = gatt.getService(FLX_Service_UUID);
                    if (FlexSensorService == null) {
                        if (debug) {
                            Log.d("onServicesDiscovered", "Flex Sensors service not available");
                        }
                    } else {
                        BluetoothGattCharacteristic flex_sensors = FlexSensorService.getCharacteristic(Flx_UUID);
                        if (flex_sensors == null) {
                            if (debug) {
                                Log.d("onServicesDiscovered", "No Flex Sensors Characteristic found");
                            }
                        } else {
                            gatt.setCharacteristicNotification(flex_sensors, true);
                            BluetoothGattDescriptor descriptor = flex_sensors.getDescriptor(DescriptorConfig_UUID);
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                            if (debug) {
                                Log.d("onServicesDiscovered", "Enabled Flex Sensors measurement notification");
                            }
                        }
                    }
                }
            },8000);

            mItems.clear();
            int i=0;
            for(BluetoothGattService service: mServices) {
                if (service.getUuid().equals(Battery_Service_UUID)) {
                    ArrayList<String> it = new ArrayList<>();
                    it.add(0, "Battery Service");
                    it.add(1, service.getUuid().toString());
                    mItems.add(i, it);
                    i++;
                } else if (service.getUuid().equals(HeartRate_Service_UUID)){
                    ArrayList<String> it = new ArrayList<>();
                    it.add(0, "Heart Rate Service");
                    it.add(1, service.getUuid().toString());
                    mItems.add(i, it);
                    i++;
                } else if (service.getUuid().equals(DeviceInfo_Service_UUID)) {
                    ArrayList<String> it = new ArrayList<>();
                    it.add(0, "Device Info Service");
                    it.add(1, service.getUuid().toString());
                    mItems.add(i, it);
                    i++;
                }else if (service.getUuid().equals(GenAccess_Service_UUID)) {
                    ArrayList<String> it = new ArrayList<>();
                    it.add(0, "Generic Access Service");
                    it.add(1, service.getUuid().toString());
                    mItems.add(i, it);
                    i++;
                }else if(service.getUuid().equals(BPS_Service_UUID)){
                    ArrayList<String> it = new ArrayList<>();
                    it.add(0, "Blood Pressure Service");
                    it.add(1, service.getUuid().toString());
                    mItems.add(i, it);
                    i++;
                }else if(service.getUuid().equals(GattService_UUID)){
                    ArrayList<String> it = new ArrayList<>();
                    it.add(0, "Gatt Service");
                    it.add(1, service.getUuid().toString());
                    mItems.add(i, it);
                    i++;
                }else if(service.getUuid().equals(FLX_Service_UUID)){
                    ArrayList<String> it = new ArrayList<>();
                    it.add(0, "Fles sensors Service");
                    it.add(1, service.getUuid().toString());
                    mItems.add(i, it);
                    i++;
                }else {
                    ArrayList<String> it = new ArrayList<>();
                    it.add(0, " Other Service");
                    it.add(1, service.getUuid().toString());
                    mItems.add(i, it);
                    i++;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            List<BluetoothGattCharacteristic> x_tics = mServices.get(position).getCharacteristics();
                            long timeout=0;
                            for(BluetoothGattCharacteristic x_tic : x_tics){
                                timeout += 1000;
                                mCharacteristic = x_tic;
                                mService = mServices.get(position);
                                //if (debug){
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            Log.d("onServicesDiscovered", mService.toString()+" read: "+gatt.readCharacteristic(mCharacteristic));
                                        }
                                    }, timeout);
                                //}
                            }
                            /*
                            if (debug){
                                Log.i("Characteristics",x_tic.getUuid().toString());
                            }
                            if(x_tic.getUuid().toString().equalsIgnoreCase(DeviceName_UUID.toString())){
                                if (debug){
                                    Log.d("onServicesDiscovered","Device name read: "+gatt.readCharacteristic(x_tic));
                                }
                            }
                            if(x_tic.getUuid().toString().equalsIgnoreCase(DeviceManufacturer_UUID.toString())){
                                if (debug){
                                    Log.d("onServicesDiscovered","Device Manufacturer read: "+gatt.readCharacteristic(x_tic));
                                }
                            }
                            if(x_tic.getUuid().toString().equalsIgnoreCase(Heart_rate_UUID.toString())){
                                if (debug){
                                    Log.d("onServicesDiscovered","Heart rate read: "+gatt.readCharacteristic(x_tic));
                                }
                            }
                            if(x_tic.getUuid().toString().equalsIgnoreCase(Battery_Level_UUID.toString())){
                                if (debug){
                                    Log.d("onServicesDiscovered","Battery level read: "+gatt.readCharacteristic(x_tic));
                                }
                            }
                            if(x_tic.getUuid().toString().equalsIgnoreCase(Bps_UUID.toString())){
                                if (debug){
                                    Log.d("onServicesDiscovered","Blood pressure read: "+gatt.readCharacteristic(x_tic));
                                }
                            }*/

                        }
                    });
                    mMenuItem.setTitle(R.string.disconnect);
                }
            });
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            if(status==BluetoothGatt.GATT_SUCCESS) {
                //blood pressure feature
                if(characteristic.getUuid().equals(BloodPressureFeature_UUID)){
                    //010110 => BP features read result
                    //Body mov't detection not supported
                    //Cuff fit detection is supported
                    //Irregular Pulse detection is not supported
                    //Pulse rate range detection is supported
                    //Measurement position detection is supported
                    //Multiple bond support not supported
                    int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,0);
                    if (debug){
                        String[] flagsStr = {
                                "body movement detection",
                                "cuff fit detection",
                                "irregular pulse detection",
                                "pulse rate range detection",
                                "measurement position",
                                "multiple bond support"
                        };
                        for(int i=0;i<6;i++){
                            String notstr = (((flags>>i)&1)==1)?" supports ":" does not support ";
                            Log.d("onCharacteristicRead","Blood Pressure Sensor"+notstr+flagsStr[i]);
                        }
                    }
                }
                //Battery level manual read
                else if (characteristic.getUuid().equals(Battery_Level_UUID)) {
                    int level = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    mBatteryLevel = level;
                    if (debug){
                        Log.d("onCharacteristicRead", "Battery level: " +level+"%");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView txt = (TextView)findViewById(R.id.battery_level);
                            txt.setText(Integer.toString(mBatteryLevel).concat("%"));
                        }
                    });
                }
                //mDevice manufacturer manual read
                else if(characteristic.getUuid().equals(DeviceManufacturer_UUID)){
                    if (debug) {
                        Log.d("onCharacteristicRead", "Device Manufacturer: " + characteristic.getStringValue(0));
                        Log.d("Length of Manu Name: ", Integer.toString(characteristic.getValue().length));
                    }
                }
                //mDevice model number read
                else if(characteristic.getUuid().equals(DeviceModelNumber_UUID)){
                    if (debug) {
                        Log.d("onCharacteristicRead", "Device Model Number: " + characteristic.getStringValue(0));
                    }
                }
                //mDevice serial number
                else if(characteristic.getUuid().equals(DeviceSerialNumber_UUID)){
                    if (debug) {
                        Log.d("onCharacteristicRead", "Device Serial Number: " + characteristic.getStringValue(0));
                    }
                }
                //mDevice Hardware revision
                else if(characteristic.getUuid().equals(DeviceHardwareRevision_UUID)){
                    if (debug) {
                        Log.d("onCharacteristicRead", "Device Hardware Revision: " + characteristic.getStringValue(0));
                    }
                }
                //mDevice firmware revision
                else if(characteristic.getUuid().equals(DeviceFirmwareRevision_UUID)){
                    if (debug) {
                        Log.d("onCharacteristicRead", "Device Firmware Revision: " + characteristic.getStringValue(0));
                    }
                }
                //mDevice software revision
                else if(characteristic.getUuid().equals(DeviceSoftwareRevision_UUID)){
                    if (debug) {
                        Log.d("onCharacteristicRead", "Device Software Revision: " + characteristic.getStringValue(0));
                    }
                }
                //heart rate read
                else if(characteristic.getUuid().equals(Heart_rate_UUID)){
                    if (debug) {
                        Log.d("onCharacteristicRead", "Heart Rate: " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,0)+"Bpm");
                    }
                }
                //device name read
                else if (characteristic.getUuid().equals(DeviceName_UUID)){
                    final String deviceName = characteristic.getStringValue(0);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mToolbar.setTitle(deviceName);
                            setSupportActionBar(mToolbar);
                        }
                    });

                    if (debug) {
                        Log.d("onCharacteristicRead", "Device Name: " + deviceName);
                    }
                }else if(characteristic.getUuid().equals(Bps_UUID)){
                    if (debug) {
                        Log.d("onCharacteristicRead", "Blood Pressure: " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,0));
                    }
                }
            }
        }
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            if(characteristic.getUuid().equals(Battery_Level_UUID)) {
                mBatteryLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView txt = (TextView) findViewById(R.id.battery_level);
                        txt.setText(Integer.toString(mBatteryLevel).concat("%"));
                    }
                });
                if (debug) {
                    Log.d("onCharacteristicChanged","Battery level = "+Integer.toString(mBatteryLevel));
                }
            }else if(characteristic.getUuid().equals(IntermediatePressure_UUID)){
                float pressure = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT,0);
                if (debug) {
                    Log.d("onCharacteristicChanged","Intermediate pressure value = "+Float.toString(pressure));
                }
            }else if(characteristic.getUuid().equals(Bps_UUID)){
                /*! Blood Pressure Service - Measurement
                    typedef struct bpsMeasurement_tag
                    {
                        bpsUnits_t                  unit; //1 bit in 1st byte => charValue[0] = ((pMeasurement->unit)?gBps_UnitInkPa_c:gBps_UnitInMmHg_c);
                        bool_t                      timeStampPresent; //1 bit in 1st byte => charValue[0] |= gBps_TimeStampPresent_c;
                        bool_t                      pulseRatePresent; //1 bit in 1st byte
                        bool_t                      userIdPresent; //1 bit in 1st byte
                        bool_t                      measurementStatusPresent; //1 bit in 1st byte
                        ieee11073_16BitFloat_t      systolicValue; //2 bytes
                        ieee11073_16BitFeloat_t      diastolicValue; //2 bytes
                        ieee11073_16BitFloat_t      meanArterialPressure; //2 bytes
                        ctsDateTime_t               timeStamp; //7 bytes may not be present
                        ieee11073_16BitFloat_t      pulseRate; //2 bytes
                        uint8_t                     userId; //1 byte
                        bpsMeasureStatusFlags_t     measurementStatus; //2 bytes
                    }bpsMeasurement_t;
                */
                int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,0);
                int units = flags&1;
                String bpUnits = (units==1)?"Kpa":"mmHg";
                int timeStampPresent = flags>>1&0x1;
                int pulseRatePresent = flags>>2&0x1;
                int userIdPresent  = flags>>3&0x1;
                int measurementStatusPresent = flags>>4&0x1;
                final float systolicValue = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT,1);
                final float diastolicValue = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT,3);
                final float meanAPValue = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT,5);
                final int year = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16,7);
                int month = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,9);
                int day = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,10);
                final String month_str =(month<10)?("0"+month):(""+month);
                final String day_str =(day<10)?("0"+day):(""+day);
                final int hour = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,11);
                int minute = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,12);
                int sec = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,13);
                final String minute_str = (minute<10)?"0"+minute:Integer.toString(minute);
                final String second_str = (sec<10)?"0"+sec:""+sec;
                final float heartRate = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT,14);
                int userId = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,16);
                int measurementFlags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16,17);
                //update UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView systolic = (TextView)findViewById(R.id.systolic);
                        TextView diastolic = (TextView)findViewById(R.id.diastolic);
                        TextView meanAP = (TextView)findViewById(R.id.meanAP);
                        TextView heart_rate = (TextView)findViewById(R.id.bp_heart_rate);
                        TextView datetime = (TextView)findViewById(R.id.datetime);
                        systolic.setText(Float.toString(systolicValue));
                        diastolic.setText(Float.toString(diastolicValue));
                        meanAP.setText(Float.toString(meanAPValue));
                        //heart_rate.setText(Float.toString(heartRate));
                        heart_rate.setText(String.format(Locale.getDefault(),"%f", heartRate));
                        datetime.setText(day_str +"/"+ month_str +"/"+Integer.toString(year)+" "+Integer.toString(hour)+":"+ minute_str +":"+ second_str);
                    }
                });
                if (debug) {
                    //log the values
                    Log.d("onCharacteristicChanged", "userId: " +String.format(Locale.getDefault(), "%d", userId));
                    Log.d("onCharacteristicChanged", "measurementFlags: " +String.format(Locale.getDefault(), "%d", measurementFlags));
                    Log.d("onCharacteristicChanged", "Flags: units -> " + units + ", timeStampPresent-> " + timeStampPresent + ", pulseRatePresent-> " + pulseRatePresent + ", userIdPresent-> " + userIdPresent + ", measurementStatusPresent-> " + measurementStatusPresent);
                    Log.d("onCharacteristicChanged", "systolic -> " + systolicValue + bpUnits);
                    Log.d("onCharacteristicChanged", "diastolic -> " + diastolicValue + bpUnits);
                    Log.d("onCharacteristicChanged", "meanAP -> " + meanAPValue + bpUnits);
                    Log.d("onCharacteristicChanged", "Heart rate  -> " + heartRate + "bpm");
                    Log.d("onCharacteristicChanged", "Time&Date -> " + hour + ":" + minute_str + ":" + second_str + " " + year + "/" + month_str + "/" + day_str);

                }

            }else if(characteristic.getUuid().equals(Flx_UUID)) {
                final byte fingers[] = characteristic.getValue();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView thumb_f = (TextView)findViewById(R.id.thumb2);
                        TextView index_f = (TextView)findViewById(R.id.index2);
                        TextView middle_f = (TextView)findViewById(R.id.middle2);
                        TextView ring_f = (TextView)findViewById(R.id.ring2);
                        TextView pinky_f = (TextView)findViewById(R.id.small2);
                        int thumb = (fingers[1] & 0xFF) << (Byte.SIZE);
                        thumb |= (fingers[0] & 0xFF);
                        thumb_f.setText(Integer.toString(thumb));
                        //Log.d("fingers data: ",Integer.toString(fingers.length));
                        int index = (fingers[3] & 0xFF) << (Byte.SIZE);
                        index |= (fingers[2] & 0xFF);
                        index_f.setText(Integer.toString(index));
                        int middle = (fingers[5] & 0xFF) << (Byte.SIZE);
                        middle |= (fingers[4] & 0xFF);
                        middle_f.setText(Integer.toString(middle));
                        int ring = (fingers[7] & 0xFF) << (Byte.SIZE);
                        ring |= (fingers[6] & 0xFF);
                        ring_f.setText(Integer.toString(ring));
                        int pinky = (fingers[9] & 0xFF) << (Byte.SIZE);
                        pinky |= (fingers[8] & 0xFF);
                        pinky_f.setText(Integer.toString(pinky));
                    }
                });
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){
            if(status==BluetoothGatt.GATT_SUCCESS){
                Log.d("onDescriptorRead",descriptor.getValue().toString());
            }
        }
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){
            Log.d("onDescriptorWrite","Status: "+Integer.toString(status)+", Descriptor: "+descriptor.getUuid().toString());
        }
    };

    private class CustomAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Activity activity;
        private ArrayList<ArrayList<String>> data;

        private CustomAdapter(Activity a, ArrayList<ArrayList<String>> arr) {
            activity=a;
            data=arr;
            mInflater = (LayoutInflater)Display.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_view, parent,false);
            }
            TextView tv1 = (TextView)convertView.findViewById(R.id.left_text);
            TextView tv2 = (TextView)convertView.findViewById(R.id.right_text);
            tv1.setText(data.get(position).get(0));
            tv2.setText(data.get(position).get(1));
            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}