package projectfinal.code.hometraining.Exercise_Settings.Bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import projectfinal.code.hometraining.R;

public class DeviceControlActivity extends AppCompatActivity {
    public static final String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView connectionState;
    private TextView dataField;
    private String deviceName;
    private String deviceAddress;
    private ExpandableListView gattServicesList;
    private BluetoothLeService bluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> gattCharacteristic = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    //연결 상태
    private Boolean connected = false;
    private  BluetoothGattCharacteristic notifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    //서비스와 연결을 모니터링 하는 정보를 담는 객체
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        //서비스가 연결되있는 경우
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder)service).getService();
            if (!bluetoothLeService.initialize()){
                Log.e(TAG,"블루투스 초기화 실패");
                finish();
            }
            bluetoothLeService.connect(deviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothLeService= null;
        }
    };

    //broadcasreceiver를 통해 intent로 받은 값(블루투스 기능 서비스의 상태값을 통해)통해 연결 처리
    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //gattUpdateReceiver를 통해 intent에 담긴 action을 가져옴
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)){
                connected=true; //상태값 변경
                updateConnectionState(R.string.connected); //TextView의 ui변경
                invalidateOptionsMenu(); // 메뉴항목 업데이트
                clearUI(); // ListView 초기화
            }else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)){
                connected=true;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            }else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                displayGattService(bluetoothLeService.getSupportedGattServices());
            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)){
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };


    private void displayData(String data){
        if (data!=null){
            dataField.setText(data); //데이터가 존재할 경우 적음
        }
    }


    //BluetoothGattService객체로 부터 받은 정보값들을 ExpadnableListView에 전달
    private void displayGattService(List<BluetoothGattService> gattServices){
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String,String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String ,String >>> gattCharacteristicData= new ArrayList<ArrayList<HashMap<String, String>>>();
        gattCharacteristic = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        //사용가능한 gatt서비스를 하나씩 찾아 정보 전달
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString(); //uuid를 가져온다
            //이름 형식으로 넣는다
            currentServiceData.put(
                    LIST_NAME, GattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // 사용가능한 characteristic을 찾을 때까지 loop
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, GattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            gattCharacteristic.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }


        //어뎁터 구성
        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        gattServicesList.setAdapter(gattServiceAdapter);
    }

    //ExpandableList의 클릭에 대한 함수
    private final ExpandableListView.OnChildClickListener servicesListClickListner = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            if (gattCharacteristic != null) {
                final BluetoothGattCharacteristic characteristic = gattCharacteristic.get(groupPosition).get(childPosition);
                final int charaProp = characteristic.getProperties();
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    //특성에 대한 활성 알림이있는 경우 먼저 이를 지우면 사용자 인터페이스의 데이터 필드가 업데이트되지 않습니다.
                    if (notifyCharacteristic != null) {
                        bluetoothLeService.setCharacteristicNotification(notifyCharacteristic, false);
                        notifyCharacteristic = null;
                    }
                    bluetoothLeService.readCharacteristic(characteristic);
                }
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    notifyCharacteristic = characteristic;
                    bluetoothLeService.setCharacteristicNotification(characteristic, true);
                }
                return true;
            }
            return false;
        }
    };


    //listview의 데이터 삭제 함수
    private void clearUI() {
        gattServicesList.setAdapter((SimpleExpandableListAdapter)null);
        dataField.setText(R.string.no_data);
    }


    //연결상태에 대한 TextView ui를 바꾸기 위한 함수
    private void updateConnectionState(final int resourseId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionState.setText(resourseId);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristic);
        final Intent intent = getIntent();
        deviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        deviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        ((TextView) findViewById(R.id.device_address)).setText(deviceAddress);// ui에 장치 주소 표시
        gattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list); //서비스들 리스트화
        gattServicesList.setOnChildClickListener(servicesListClickListner); // 클릭 리스너 등록
        connectionState = (TextView) findViewById(R.id.connection_state);
        dataField = (TextView) findViewById(R.id.data_value);

        getActionBar().setTitle(deviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true); // 전의 액티비티로 돌아갈수 있게하는 메ㅐ소드
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        //서비스를 실행하는 메소드(intent실행인자, 요청에 대한 결과값에 따라 서비스 처리하는 인자,바인딩 옵션
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (connected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                bluetoothLeService.connect(deviceAddress);
                return true;
            case R.id.menu_disconnect:
                bluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //(서비스에 대한 결과값읕 받는 수신부, )
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        if (bluetoothLeService != null) {
            final boolean result = bluetoothLeService.connect(deviceAddress);
            Log.d(TAG, "연결 요청 결과=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gattUpdateReceiver); //리시버 등록제거
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);// 서비스제거
        bluetoothLeService = null;
    }




}