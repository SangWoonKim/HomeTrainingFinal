package projectfinal.code.hometraining.Exercise_Settings.Bluetooth;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private final static int STATE_DISCONNECTED =0;
    private final static int STATE_CONNECTING = 1;
    private final static int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.project.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.project.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.project.bluetooth.le.ACTION_GATT_SERVICES_CONNECTED";
    public final static String ACTION_DATA_AVAILABLE = "com.project.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.project.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(GattAttributes.HEART_RATE_MEASUREMENT);

    //해당 앱에서 GATT이벤트에 대한 콜백 메소드 구현부
    //예를 들어 연결 변경 및 다른 서비스 발견시
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        // 연결정보 변경시
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            //연결이 완료된 상활일 때
            if (newState == BluetoothProfile.STATE_CONNECTED){
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG,"GATT서버 연결 성공");
                //연결 후 서비스 찾기 시도
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
            }else if (newState == BluetoothProfile.STATE_DISCONNECTED){//연결이 끊긴 상황일 때
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG,"GATT서버와 연결 끊김");
                broadcastUpdate(intentAction);
            }
        }

        //서비스를 발견시 또는 목록이 업데이트 되었을 때 호출되는 콜백 즉 새 서비스를 발견시 호출
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            }else{
                Log.w(TAG,"onServicesDiscovered received" + status);
            }
        }

        //GATT의 서버에서 Characteristic을 읽을시
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS){
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        //GATT의 서버에서 Characteristic값이 변할경우
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE,characteristic);
        }
    };
    //상태값을 전달하는 메소드 (DeviceControlActivity의 broadcastReceiver에)
    private void broadcastUpdate (final String action){
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    //심장박동수를 전달하는 메소드  (DeviceControlActivity의 broadcastReceiver에)
    private void broadcastUpdate (final String action, final BluetoothGattCharacteristic characteristic){
        final Intent intent = new Intent(action);

        //심박수 측정 프로파일에 대한 특수 처리 부분
        //데이터 파싱은 프로파일 사양에 따라 수행
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())){ // 정의한 UUID가 서버의 UUID와 같을 경우
            int flag = characteristic.getProperties(); //characteristic의 속성 가져오기
            int format = -1;
            if ((flag & 0x01) != 0){
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG,"심장박동수를 나타내는 형식을 UINT16으로 표기");
            }else{
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG,"심장박동수를 나타내는 형식을 UINT8으로 표기");
            }
            final int heartRate = characteristic.getIntValue(format,1);
            Log.d(TAG, String.format("심장 박동수 받음 : %d",heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate)); // intent를 통해 broadcastReceiver에 가야할 값을 명시하는 부분

        }else { //심장박동수의 명시된 UUID가 아닌 다른 프로파일일 경우
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder{
        BluetoothLeService getService(){
            return BluetoothLeService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        // 주어진 장치를 사용한 후에는 BluetoothGatt.close()가 호출되는지 확인해야함
        // UI가 서비스에서 연결이 끊어지면 호출
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();


     //로컬 Bluetooth 어댑터에 대한 참조를 초기화
     //초기화에 성공하면 true를 반환

    public boolean initialize(){
        //BluetoothManager
        if (mBluetoothManager == null){
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null){
                Log.e(TAG,"BluetoothManager를 초기화 할수 없습니다");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null){
            Log.e(TAG,"BluetoothAdpater를 얻지 못했습니다.");
            return false;
        }
        return true;
    }
    //Bluetooth LE 장치에서 호스팅되는 GATT 서버에 연결합니다.
    //param address 대상 장치의 장치 주소입니다.
    //return 연결이 성공적으로 시작되면 true를 반환합니다. 연결 결과는 @code를 통해 비동기 적으로보고됩니다.
    //code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt,int,int)

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) { //블루투스 어뎁터에 목록이 없거나 또는 장치에 대한 주소가 없을 때
            Log.w(TAG,"블루투스 어뎁터가 초기화 되지 않았거나 장치에 대한 주소가 정의되지 않았습니다.");
            return false;
        }

        //이전에 장치를 연결했을 때 다시 연결할 경우
        if (mBluetoothDeviceAddress !=null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt !=null){
            Log.d(TAG,"사용하던 mBlueToothGatt를 갖고 연결을 시도합니다.");
            if (mBluetoothGatt.connect()){
                mConnectionState = STATE_CONNECTING;
                return true;
            }else{
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        //주위에 장치가 없거나 블루투스 장치를 찾지 못했을 경우
        if (device == null){
            Log.w(TAG,"장치를 찾지 못했습니다. 연결할 수 없습니다.");
            return false;
        }
        // connect(Context,자동연결 여부,비동기 콜백을 수신 할 콜백 핸들러)
        mBluetoothGatt = device.connectGatt(this,false, mGattCallback);
        Log.d(TAG,"새로운 연결을 만듭니다. ");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }


    //기존 연결을 끊거나 보류중인 연결을 취소합니다.
    // 단절 결과를 BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt,int,int) 통해 비동기 적으로보고
    public void disconnect(){
        if (mBluetoothAdapter == null || mBluetoothGatt ==null){
            Log.w(TAG, "블루투스 어뎁터가 초기화가 되지 않았습니다.");
            return;
        }
        mBluetoothGatt.disconnect();
    }


     // 지정된 BLE 디바이스를 사용한 후 앱은 이 메소드를 호출하여 리소스가 올바르게 해제되도록해야함.
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }


    public void readCharacteristic(BluetoothGattCharacteristic characteristic){
        if (mBluetoothAdapter == null || mBluetoothGatt == null){
            Log.w(TAG, "블루투스 매니저가 초기화 되지 않음");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

     // characteristic에 대한 알림을 활성화 또는 비활성화 하는 메소드
     // characteristic characteristic에 대한 행동
     // enabled true인 경우 알림을 활성화 하게 됨 false일 경우 비활성화

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled){
        if (mBluetoothAdapter == null || mBluetoothGatt ==null){
            Log.w(TAG,"블루투스 어뎁터가 초기화되지 않았습니다.");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic,enabled);

        //심박수 측정알림부
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())){
            //BluetoothGattDescriptor는 GATT의 characteristic의 속성을 얻어 설명하거나 characteristic의 동작을 제어한다
            //descriptor의 객체에 heart rate에 사용될 고유UUID를 GattAttribute에 명시한 것을 얻어 옴
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            //descripter에 로컬로 저장된 캐시값을 수정
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE); // 알림 활성화
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }



    // 연결된 장치에서 지원되는 GATT서비스 목록을 검색함
    // BluetoothGatt#discoverServices()가 완료된 후에 만 호출해야함
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        //블루투스가 제공하는 GATT서비스 목록을 반환하는 메소드 getServices() 반환값이 List형식이기 때문에 함수와 맞춰야함
        return mBluetoothGatt.getServices();
    }
}