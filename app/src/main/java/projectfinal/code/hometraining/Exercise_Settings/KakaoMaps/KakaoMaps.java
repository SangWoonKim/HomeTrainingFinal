package projectfinal.code.hometraining.Exercise_Settings.KakaoMaps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import net.daum.android.map.MapViewEventListener;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

import projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.Activities.PlaceDetailActivity;
import projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.Category_search.Category_Result;
import projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.Category_search.Document;
import projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.Utils.IntentKey;
import projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.api.APIinterface;
import projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.api.RetrofitAPI;
import projectfinal.code.hometraining.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KakaoMaps extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.POIItemEventListener, View.OnClickListener {

    final static String TAG = "Kakao Map";
    public static final String LOG_TAG ="KakaoActivity";
    private MapView mMapView;
    private Button kakao_search;
    ViewGroup mMapViewContainer;
    //gps 코드
    public static final int GPS_ENABLE_REQUEST_CODE = 2001;
    public static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    ArrayList<Document> gyms = new ArrayList<>();// 헬스장 //근처헬스장(검색문)

    MapPOIItem searchMarker = new MapPOIItem(); //검색할 때 표시될 마커
    MapPoint currentMapPoint; //맵의 좌표에 대한 객체

    //좌표에 해당하는 변수
    private double mCurrentLng; //Long = X, Lat = Yㅌ
    private double mCurrentLat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_settings_kakaomaps);
        kakao_search = (Button)findViewById(R.id.kakao_search);
        kakao_search.setOnClickListener(this::onClick);
        mMapView = new MapView(this);
        mMapViewContainer = findViewById(R.id.kakao_maps);
        mMapViewContainer.addView(mMapView);
        mMapView.setCurrentLocationEventListener(this);
        mMapView.setPOIItemEventListener(this);

        //사용자가 gps를 활성시키지 않았을 경우
        if (!checkLocationServiceStatus()){
            // gps 활성화를 위한 위치서비스 제공 다이어그램 메소드를 활성화
            showDialogForLocationServiceSetting();
        //gps를 활성화 시킨 경우
        }else{
            //위치 서비스 퍼미션을 갖고 있는지 확인
            checkRunTimePermission();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
    }


    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fall");
    }

    //단말의 현위치 좌표값을 받는 통보 받는 메소드
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(TAG,String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy(%f)",mapPointGeo.latitude,mapPointGeo.longitude,v));
        //MapPoint 전역변수인 currentMapPoint에 구글geo좌표형식으로 현재 위치를 저장한다.
        currentMapPoint= MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude,mapPointGeo.longitude);
        //이 좌표로 지도 중심 이동
        mMapView.setMapCenterPoint(currentMapPoint,true);
        //전역변수로 현재 좌표 저장
        mCurrentLat = mapPointGeo.latitude;
        mCurrentLng = mapPointGeo.longitude;
        Log.d(TAG, "현재위치 => " + mCurrentLat + "  " + mCurrentLng);
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
        Log.i(TAG, "onCurrentLocationUpdateFailed");
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
        Log.i(TAG, "onCurrentLocationUpdateCancelled");
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }

    private void onFinishReverseGeoCoding(String result){
        Toast.makeText(this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }

    //ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴반든 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length){

            //요청 코드가 PERMISSIONS_REQUEST_CODE이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            //모든 퍼미션을 허용했는지 체크

            for (int result : grantResults){
                if (result != PackageManager.PERMISSION_GRANTED){
                    check_result=false;
                    break;
                }
            }

            if (check_result){
                Log.d("check","finish");
                // 위치 값을 가져올 수 있음
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            }else{
                //거부한 퍼미션이 있을 경우 앱을 사용할 수 없는 이유를 설명하고 앱을 종료
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[0])){
                    Toast.makeText(this,"퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요",Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    // 위치 퍼미션을 갖고 있는지 체크
    void checkRunTimePermission(){

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);

        //퍼미션을 가지고 있을 경우
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED){

            //위치 값 가져올 수 있음
            //setCurrentLocationTrackingMode (지도랑 현재위치 좌표 찍어주고 따라다닌다.)
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        }
        //퍼미션 요청을 허용한 적이 없을 경우
        else{
            //사용자가 퍼미션 거부를 한적이 있을 경우
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[0])){
                //사용자에게 퍼미션이 필요한 이유를 설명
                Toast.makeText(this,"이 앱을 실행하려면 위치 접근 권한이 필요합니다.",Toast.LENGTH_LONG).show();
                // 사용자에게 퍼미션 요청을 한다, 요청 결과는 onRequestPermissionResult에서 수신됨
                ActivityCompat.requestPermissions(this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }
            //사용자가 퍼미션 거부를 한적이 없을 경우, 바로 퍼미션 요청을 진행
            else{
                ActivityCompat.requestPermissions(this,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //GPS활성화 메소드 들

    //gps 활성화를 위한 위치서비스 제공 다이어그램
    private void showDialogForLocationServiceSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("기능을 사용하기 위해서는 위치 서비스가 필요합니다.\n"+ "위치 서비스를 설정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent GPSsetting = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(GPSsetting,GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 gps를 활성 시켰는지 검사
                if (checkLocationServiceStatus()){
                    if (checkLocationServiceStatus()){
                        Log.d("GPS status","gps활성화 되어있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServiceStatus(){
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }





// 근처를 검색을 요청하는 메소드(현재좌표 x,y)
    private void requestSearchLocal(double x, double y) {
        gyms.clear();
        //apiInterface객체에 카카오 rest API를 사용할 수 있도록 정보를 연결하고 객체 생성
        APIinterface apiInterface = RetrofitAPI.getApiClient().create(APIinterface.class);
        //서버에 요청하는 부분 근데 안됨
        Call<Category_Result> call = apiInterface.getSearchLocationDetail(getString(R.string.restapi_key),"근처 헬스장",x + "",y + "",1000,15);
        // 큐에 삽입 (결과를 받는 객체 생성)
        call.enqueue(new Callback<Category_Result>() {
            //응답받을 경우
            @Override
            public void onResponse(Call<Category_Result> call, Response<Category_Result> response) {
                // 응답이 성공적으로 받을 경우
                if (response.isSuccessful()){
                    assert response.body() != null;
                    if (response.body().getDocuments() !=null) {
                        Log.d(TAG, "gyms search success");
                        // 서버에서 받은 마커들 삽입
                        gyms.addAll(response.body().getDocuments());
                    }
                    //통신 성공시 거리circle 생성
                    MapCircle circle = new MapCircle(
                            MapPoint.mapPointWithGeoCoord(y,x),  //center
                            1000,  //radius
                            Color.argb(128,255,0,0), //strockColor
                            Color.argb(128,0,255,0)); //fillColor
                    circle.setTag(5678);
                    mMapView.addCircle(circle);
                    Log.d("gyms",gyms.size()+"");
                    //마커 생성
                    int tagNum = 10;
                    //헬스장 개수 만큼 반복
                    for (Document document : gyms){
                        MapPOIItem marker = new MapPOIItem();
                        marker.setItemName(document.getPlaceName());
                        marker.setTag(tagNum++);
                    //    double x =Double.parseDouble(document.getY());
                      //  double y =Double.parseDouble(document.getX());

                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(document.getY(),document.getX());
                        marker.setMapPoint(mapPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                        mMapView.addPOIItem(marker);
                    }
                }
            }

            @Override
            public void onFailure(Call<Category_Result> call, Throwable t) {
                Log.d(TAG, "FAIL");
                Toast.makeText(getApplicationContext(),"오류가 일어났습니다 토큰 확인",Toast.LENGTH_SHORT).show();
            }
        });
    }






    //길찾기를 하기 위한 카카오맵 호출(없을 경우 플레이스토어 링크로 이동)
    public void showMap(Uri geoLocation){
        Intent kakaoMapsAppIntent;
        try {
            Toast.makeText(this,"카카오맵으로 길찾기를 시도합니다",Toast.LENGTH_LONG).show();
            kakaoMapsAppIntent = new Intent(Intent.ACTION_VIEW, geoLocation);
            kakaoMapsAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(kakaoMapsAppIntent);
        }catch (Exception e){
            Toast.makeText(this,"카카오 맵이 없습니다. 다운 받아야 기능이 활성화 됩니다.",Toast.LENGTH_LONG).show();
            kakaoMapsAppIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=net.daum.android.map&hl=ko"));
            kakaoMapsAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(kakaoMapsAppIntent);
        }
    }









    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    //사용자가 마커를 선택한 경우 호출
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        double lat = mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude; // 해당 마커에서 double형으로 좌표 얻기
        double lng = mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude; // 해당 마커에서 double형으로 y좌표 얻기
        Toast.makeText(this,mapPOIItem.getItemName(),Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("선택해주세요");
        builder.setCancelable(true);
        builder.setPositiveButton("장소 정보", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                APIinterface apiInterface = RetrofitAPI.getApiClient().create(APIinterface.class);
                Call<Category_Result> call = apiInterface.getSearchLocationDetail(getString(R.string.restapi_key),mapPOIItem.getItemName(),String.valueOf(lat),String.valueOf(lng),1000,1);
                call.enqueue(new Callback<Category_Result>() {
                    @Override
                    public void onResponse(Call<Category_Result> call, Response<Category_Result> response) {
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(KakaoMaps.this, PlaceDetailActivity.class);
                            assert response.body() != null;
                            intent.putExtra(IntentKey.PLACE_SEARCH_DETAIL_EXTRA, response.body().getDocuments().get(0));
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<Category_Result> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"해당장소에 대한 상세정보는 없습니다.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(KakaoMaps.this, PlaceDetailActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
        builder.setNeutralButton("길 찾기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showMap(Uri.parse("daummaps://route?sp=" + mCurrentLat + "," + mCurrentLng + "&ep=" + lat + "," + lng + "&by=FOOT"));
            }
        });
        builder.create().show();

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.kakao_search){
            Toast.makeText(this,"현재위치기준 1km 검색 시작",Toast.LENGTH_SHORT).show();
            //현재 위치 기준으로 1km 검색
            mMapView.removeAllPOIItems();
            mMapView.removeAllCircles();
            requestSearchLocal(mCurrentLng,mCurrentLat);
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        }
    }
}
