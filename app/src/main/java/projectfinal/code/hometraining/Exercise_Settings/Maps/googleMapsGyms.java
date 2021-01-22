package projectfinal.code.hometraining.Exercise_Settings.Maps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;
import projectfinal.code.hometraining.R;

public class googleMapsGyms extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, PlacesListener {
    private GoogleMap mMap;
    private Marker currentMarker = null;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final String TAG = "googlemap_gyms";
    private static final int GPS_ENABLE_REQUEST_CODE=2001;
    public static final int UPDATE_INTERVAL =1000;
    public static final int FASTEST_UPDATE_INTERVAL=500;

    // onRequestPermissionsResult에 수신된 결과에서 ActivitytCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용
    public static final int PERMISSION_REQUEST_CODE =100;
    boolean needResquest =false;

    //앱을 실행하기 위한 퍼미션 정의
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    Location currentlocation;      //위치를 전역변수로 지정
    LatLng currentposition;        //위도경도를 전역변수로 지정

    List<Marker> previous_marker= null; // 주변 헬스장 찾기를 위한 마커

    private FusedLocationProviderClient mfusedLocationClient; // 위치 정보를 관련한 객체
    private LocationRequest locationRequest; // 높은 정확도를 가진 위치를 나타내기 위한 위치 연결상태를 정의하는 객체 위와 비슷하나 정확성에서 차이
    private Location location;

    private View mlayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow() 현재 상태의 창모양 및 동작 이벤트에 대한 활동 받기 AppcompatActivity에서 Window 추상 클래스를 상속받아 사용
        //즉 현재 화면을 계속 켜짐 상태로 놓기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.exercise_settings_googlemapsgyms);
        // 한 레이아웃 전체를 id로 지정하여 불러옴
        previous_marker = new ArrayList<Marker>();

        Button button= (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlaceInformation(currentposition);
            }
        });
        mlayout = findViewById(R.id.exercise_settings_googlemapsgyms_layout);

        locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL) // 위치 업데이트를 실행하는 메소드 (1000ms즉 1초마다 갱신) 따라서 전력량에 많은 영향을 미침 (정확도 하락)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL); // 위치 업데이트를 빠르게 실행하는 메소드 (500ms 즉 0.5초마다 갱신)전력량에 오지게 많은 영향을 미침 (갱신되는 간격이 짧아 정확성 증가)
        //위치 설정 요청 객체 Builder를 사용하여 확장
        LocationSettingsRequest.Builder locationBuilder = new LocationSettingsRequest.Builder();
        //위치에 대한 요청을 추가
        locationBuilder.addLocationRequest(locationRequest);
        //객체화(인스턴스화)
        mfusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // 형태에 맞는 view와 클래스 동기화
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.googleMap);
        // onMapReady()를 호출하여 값을 동기화한다
        // 즉 함수에서 실행한 값을 화면에 출력하는 메소드
        mapFragment.getMapAsync(this);
    }

    //.getMapAsync()메소드로 인해 만들어진 함수
    //화면에 출력되는 연산을 처리
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"onMapReady:");
        mMap = googleMap;
        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자가 보이지 전에 지도의 초기위치를 함수에 놓은 값으로 이동
        setDefaultLocation();

        //런타임 퍼미션 처리
        // 위치 퍼미션을 갖고 있는지 검사.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

        if ((hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) && (hasCoarseLocationPermission==PackageManager.PERMISSION_GRANTED)){
            //퍼미션 검사후 true일 경우

            //위치 업데이트 함수 수행
            startLocationUpdates();
        }else {//퍼미션을 허용하지 않거나 다른이유로 퍼미션 요청이 필요할 경우

            //사용자가 퍼미션 요청을 거부한 적이 있는 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[0])){
                Snackbar.make(mlayout,"기능을 사용하시려면 위치 접근 권한을 확인하십시오",Snackbar.LENGTH_INDEFINITE)
                        .setAction("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //사용자에게 퍼미션 요청을 수행. 요청 결과는 onRequestPermissionResult에서 수신됨
                                ActivityCompat.requestPermissions(googleMapsGyms.this,REQUIRED_PERMISSIONS,PERMISSION_REQUEST_CODE);
                            }
                        }).show();
            }else { // 사용자가 퍼미션 거부를 한 적이 없는 경우 바로 퍼미션 요청을 수행
                ActivityCompat.requestPermissions(this,REQUIRED_PERMISSIONS,PERMISSION_REQUEST_CODE);
            }
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 맵 보이는 크기 지정
        mMap.animateCamera(CameraUpdateFactory.zoomTo(200));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG,"onMapClick");
            }
        });
    }


    //위치의 값을 전달하는 객체를 인스턴스 화하여 사용
    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult){
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();
            //배열에 값이 들어가있을 경우
            if (locationList.size()>0){
                location = locationList.get(locationList.size()-1);
                //현재위치의 위도와 경도를 받아 인스턴스화
                currentposition = new LatLng(location.getLatitude(),location.getLongitude());

                String markerTitle = getCurrentAddress(currentposition);
                String markerSnippet ="위도"+String.valueOf(location.getLatitude())+"경도"+String.valueOf(location.getLongitude());
                Log.d(TAG,"onLocationResult:"+markerSnippet);

                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location,markerTitle,markerSnippet);
                currentlocation=location;
            }
        }
    };


    //위치 업데이트 수행 함수
    private void startLocationUpdates(){

        if (!checkLocationServicesStatus()){
            Log.d(TAG,"startLocationUpdate: call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if ((hasFineLocationPermission!=PackageManager.PERMISSION_GRANTED) ||(hasCoarseLocationPermission!=PackageManager.PERMISSION_GRANTED)){
                Log.d(TAG,"StartLocationUpdate:퍼미션 갖고있지 않음");
                return;
            }
            Log.d(TAG,"startLocationUpdate: 위치 연결에 대한 정의 정보를 불러온 후 위치 정보 호출 시작");
            mfusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
            if (checkPermission())
                mMap.setMyLocationEnabled(true);
        }
    }


    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG,"onStart");
        if (checkPermission()){
            Log.d(TAG,"onStart:위치 연결에 대한 정의 정보를 불러온 후 위치 정보 호출 시작");
            mfusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);
        }
    }


    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG,"onStart");

        if (mfusedLocationClient != null) {
            Log.d(TAG,"onStop: stopLocationUpdate");
            mfusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


    public String getCurrentAddress(LatLng latLng){
        //지오코더 걍 GPS를 주소로 변환 하는 객체
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        // 주소 형식으로 배열 선언
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
        }catch (IOException ioException){
            //네트워크 Exception
            Toast.makeText(this,"연결을 확인하세요",Toast.LENGTH_SHORT).show();
            return "지오코더 서비스 사용불가";
        }catch (IllegalArgumentException illegalArgumentException){
            Toast.makeText(this,"잘못된 GPS좌표",Toast.LENGTH_SHORT).show();
            return "잘못된 GPS좌표";
        }

        if (addresses == null || addresses.size() ==0){
            Toast.makeText(this,"주소미발견",Toast.LENGTH_SHORT).show();
            return "주소미발견";
        }else{
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }


    public boolean checkLocationServicesStatus(){
        //getSystemService() 주어진 매개변수에 대응하는 안드로이드가 제공하는 시스템-레벨 서비스를 요청 하여 객체 생성
        //LOCATION_SERVICE 상수형 매개변수 GPS를 통한 위치 서비스를 제공하는 LocationManager를 반환하는 상수
        //즉 getSystemService()메소드를 이용하여 LocationManager를 반환하는 객체 생성
        LocationManager locationManager =(LocationManager)getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    //현재 지역 마커 설정(지역의 좌표가 담긴 매개 변수, 지역의 이름, 지역의 설명)
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet){

        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        //카메라 위치 변경
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.animateCamera(cameraUpdate);
    }


    //초기 마커 설정
    public void setDefaultLocation(){

        //초기 위치, 학교
        LatLng DEFAULT_LOCATION = new LatLng(37.586855, 127.097701);
        String markerTitle = "위치정보를 가져올 수 없습니다";
        String markerSnippet = "gps기능 및 권한 활성이 되어있는지 확인하세요";

        //마커가 들어갔을 경우 마커 초기값 삭제
        if (currentMarker != null) currentMarker.remove();
        //마커에 대한 모든 데이터 삭제
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //카메라 위치 변경
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.animateCamera(cameraUpdate);
    }


    // 퍼미션 처리를 위한 메소드들
    private boolean checkPermission(){
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;
    }


    //ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 받는 메소드
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        //요청 코드가 PERMISSIONS_REQUEST_CODE이고, 요청한 퍼미션 개수만큼 수신 되었다면

        boolean check_result = true;

        //모든 퍼미션이 허용되었는지 확인

        for (int result : grandResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                check_result = false;
                break;
            }
        }
        if (check_result) {
            //퍼미션을 허용했다면 위치 업데이트 시작
            startLocationUpdates();
        } else {
            //거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명하고 실행을 정지
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                //사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                Snackbar.make(mlayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).show();
            } else {
                //"다시 묻지 않음"을 선택 후 거부한 경우 설정에서 퍼미션을 허용해야 앱을 사용할 수 있게 함
                Snackbar.make(mlayout, "퍼미션이 거부되었습니다. 설정에서 퍼미션을 허용해주세요.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).show();
            }
        }
    }


    //GPS활성화 메소드
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder msgbuilder = new AlertDialog.Builder(this);
        msgbuilder.setTitle("위치 서비스 비활성화");
        msgbuilder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        msgbuilder.setCancelable(true);
        msgbuilder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        msgbuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        msgbuilder.create().show();
    }


    //결과를 수신하는 메소드
    //((startActivityForResult()메소드에 전달한 요청코드) requestCode, (두 번째 활동이 지정한 결과 코드)resultCode, (결과 데이터를 전달하는 Intent) data )
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //응답하고있는 요청
        switch (requestCode){
            //gps가 허용되었는지
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if(checkLocationServicesStatus()){
                    if(checkLocationServicesStatus()){
                        Log.d(TAG, "onActivityResult: GPS활성화 ok");

                        needResquest = true;
                        return;
                    }
                }
                break;
        }
    }



    //주변 장소를 정의 하기 위한 함수부
    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(noman.googleplaces.Place place : places){
                    LatLng latLng = new LatLng(place.getLatitude(),place.getLongitude());

                    String makerSnippet = getCurrentAddress(latLng);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(makerSnippet);
                    Marker item = mMap.addMarker(markerOptions);
                    previous_marker.add(item);
                }
                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);
            }
        });
    }

    @Override
    public void onPlacesFinished() {

    }
    public void showPlaceInformation(LatLng location)
    {
        mMap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(googleMapsGyms.this)
                .key("AIzaSyCPSmjctOEByqe1aVzu3bexiFfLMwheWKE")
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(1000) //1000 미터 내에서 검색
                // .type(PlaceType.BUS_STATION) //음식점
                .build()
                .execute();
    }
}
