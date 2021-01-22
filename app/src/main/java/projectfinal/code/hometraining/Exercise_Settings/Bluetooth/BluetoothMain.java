package projectfinal.code.hometraining.Exercise_Settings.Bluetooth;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import projectfinal.code.hometraining.R;

public class BluetoothMain extends ListActivity {

    private BluetoothAdapter mbluetoothAdapter;  //블루투스 연결자, 블루투스를 스캔하거나, 장치들에 대한 정보를 담은 객체
    private LeDeviceListAdapter leDeviceListAdapter;
    private boolean scanning; //스캔 상태값
    private Handler viewhandler;

    private static final int REQUEST_ENABLE_BT =1;
    public static final long SCAN_PERIOD = 10000;
//http://airpage.org/xe/mobile_data/27366 구글 fitness API를 이용한 개발 옵션

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setTitle(R.string.title_devices);
        viewhandler = new Handler() ;

        //기기가 BLE를 이원하는지 검사
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this, R.string.ble_not_supported,Toast.LENGTH_LONG).show();
            finish();
        }

        //블루투스 어뎁터 생성
        //BluetoothManager객체는 블루투스의 기능을 총괄한다.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mbluetoothAdapter = bluetoothManager.getAdapter();
    }
    //검색 실행중일 때의 메뉴 생성및 변환
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bluetooth_menu,menu);

        if (!scanning){
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        }else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.exercise_bluetooth_prograss);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_scan:
                leDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = leDeviceListAdapter.getDevice(position);
        if (device==null){
            return;
        }
        final Intent deviceControlIntent = new Intent(this,DeviceControlActivity.class);
        deviceControlIntent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        deviceControlIntent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (scanning){
            mbluetoothAdapter.stopLeScan(leScanCallback);
            scanning=false;
        }
    }

    private void scanLeDevice(final boolean enable){
        if (enable){
            viewhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning =false;
                    mbluetoothAdapter.stopLeScan(leScanCallback);
                    invalidateOptionsMenu();
                }
            },SCAN_PERIOD);
            scanning = true;
            mbluetoothAdapter.startLeScan(leScanCallback);
        }else {
            scanning = false;
            mbluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mbluetoothAdapter.isEnabled()){
            //블루투스 활성화 하는 intent날림
            //즉 블루투스어뎁터에 블루투스 활성화 작업을 전달
            Intent enableBlueToothIntent = new Intent(mbluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlueToothIntent, REQUEST_ENABLE_BT);
        }
        leDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(leDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && requestCode == Activity.RESULT_CANCELED){
            finish();
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        leDeviceListAdapter.clear();
    }

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflater;

        //생성자
        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflater = BluetoothMain.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflater.inflate(R.layout.bluetooth_device_list, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            //BluetoothDevice는 bluetooth의 장치를 나타내는 객체
            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    //블루투스어뎁터가. 스캔에 대한 결과를 콜백 받을 때 사용하는 인터페이스.lescancallback
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        //새로운 장치가 발견될 때마다 onLeScan호출
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    leDeviceListAdapter.addDevice(device);
                    leDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    private class ViewHolder {
        public TextView deviceName;
        public TextView deviceAddress;
    }
}
