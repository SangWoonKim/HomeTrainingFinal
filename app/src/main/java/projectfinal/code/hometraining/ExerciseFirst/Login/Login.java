package projectfinal.code.hometraining.ExerciseFirst.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.kakao.util.maps.helper.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import projectfinal.code.hometraining.BottomNavi.BottomNaviView;
import projectfinal.code.hometraining.ExerciseFirst.Register.Register;
import projectfinal.code.hometraining.Exercise_Intent.LoginDataGS;
import projectfinal.code.hometraining.Exercise_Settings.Application.DarkThemeUtil;
import projectfinal.code.hometraining.Exercise_Settings.Exercise_Settings;
import projectfinal.code.hometraining.R;
import projectfinal.code.hometraining.handler.BackPressCloseHandler;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "hashkey:" ;
    private Button BTN_login_Register, BTN_login_Login;
private TextView TV_login_restricted;
private EditText ET_login_Id, ET_login_Password;
private CheckBox idpwsavebox;
private SharedPreferences idpwsaved;
private SharedPreferences.Editor editor;
private BackPressCloseHandler closelogin = new BackPressCloseHandler(this);
// 정보 전달을 위한 번들객체 선언
// public으로 선언할거면 클래스 하나 만들어서 getter setter쓰겠다.
public Bundle userinfo;
//정보를 전달할 프레그먼트를 인스턴스화
public Fragment exercise_settings =new Exercise_Settings();

    private SharedPreferences theme;
    DarkThemeUtil themeList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = getSharedPreferences("projectfinal.code.hometraining_preferences",MODE_PRIVATE);
        String themechanged = theme.getString("themeSelect","");
        switch (themechanged){
            case "dark":
                themeList.applytheme(DarkThemeUtil.DARK_MODE);
                break;
            case "light":
                themeList.applytheme(DarkThemeUtil.LIGHT_MODE);
                break;
            case "default":
                themeList.applytheme(DarkThemeUtil.DEFAULT_MODE);
                break;
        }
        setContentView(R.layout.login);
        TV_login_restricted = (TextView)findViewById(R.id.TV_login_restricted);
        TV_login_restricted.setOnClickListener(this);

        BTN_login_Register = (Button)findViewById(R.id.BTN_login_Register);
        BTN_login_Register.setOnClickListener(this);

        BTN_login_Login = (Button)findViewById(R.id.BTN_login_Login);
        BTN_login_Login.setOnClickListener(this);

        ET_login_Id=(EditText)findViewById(R.id.ET_login_Id);
        ET_login_Password =(EditText)findViewById(R.id.ET_login_Password);
        //checkbox동기화
        idpwsavebox =(CheckBox)findViewById(R.id.CB_login_Save);
        idpwsavebox.setOnClickListener(this);
        //preference
        idpwsaved = getSharedPreferences("getidpw",MODE_PRIVATE);
        editor = idpwsaved.edit();

        //preference에 저장한 값을 불러오기 부분

        String id = idpwsaved.getString("id","");
        String pw = idpwsaved.getString("pw","");
        boolean check = idpwsaved.getBoolean("check",false);

        //불러온 값을 각 view에 뿌림
            ET_login_Id.setText(id);
            ET_login_Password.setText(pw);
            idpwsavebox.setChecked(check);
           // getHashKey();

    }


    @Override
    public void onClick(View v) {
        //비회원 입장 TextView 클릭시
        if (v.getId() == R.id.TV_login_restricted){
            Intent restricted_login = new Intent(this,BottomNaviView.class);
            Toast.makeText(getApplicationContext(), "회원가입시 많은 기능을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
            restricted_login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            restricted_login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(restricted_login);
        }

        //회원가입 버튼 클릭시
        if (v.getId()==R.id.BTN_login_Register){

            LoginDataGS.getInstance().setLogin_ID(null);
            LoginDataGS.getInstance().setLogin_PW(null);


            Intent login_register = new Intent(Login.this, Register.class);
            startActivity(login_register);
            finish();
        }
        //체크박스 버튼 클릭시
        if (idpwsavebox.isChecked() ==true){
            String gid, gpw;
            Boolean gcheck;
            gid = ET_login_Id.getText().toString();
            gpw = ET_login_Password.getText().toString();
            gcheck = idpwsavebox.isChecked();
            //editor에 상태 값 넣기
            editor.putString("id",gid);
            editor.putString("pw",gpw);
            editor.putBoolean("check",gcheck);
            //적용
            editor.commit();
        }else{
            editor.clear();
            editor.commit();
        }

        //로그인 버튼 클릭시
        if (v.getId() == R.id.BTN_login_Login){
            //edittext에 입력된 값 저장
            String U_id = ET_login_Id.getText().toString();
            String U_pw = ET_login_Password.getText().toString();

            //Volley의 Response객체를 이용하여Listener메서드<문자열 형태로>를 선언한다
            //Listenersms 파싱된 응답을 전달하기 위한 콜백 인터페이스 메서드이다.
            Response.Listener<String> responseListener = new Response.Listener<String>() {
               //응답을 받을경우 실행하는 함수
                @Override
                public void onResponse(String response) {
                    try {
                        //JSONObject객체 선언
                        JSONObject jsonObject = new JSONObject(response);
                        //php $response["success"] = "true"로 저장 즉[key값이자 name이다].
                        //즉 서버통신 확인 구문 php에서 success에는 true 가 저장되어있음
                        //php에서 getboolean형식으로 결과값(true)을 받아서 success에 저장
                        boolean success = jsonObject.getBoolean("success");

                        //로그인 성공시
                        if (success){
                          //php에서 JSONObject객체형식으로 U_id 변수안의 데이터를 가져와서 String 형식으로 U_id에 저장
                          //php에서 데이터를 받지 못할 경우 로그인이 안됨
                          String U_id = jsonObject.getString("U_id");
                          String U_pw = jsonObject.getString("U_pw");

                            //setter 로 값넣기
                            LoginDataGS.getInstance().setLogin_ID(U_id);
                            LoginDataGS.getInstance().setLogin_PW(U_pw);

                            //로그인시 이동하는 기능
                          Intent login = new Intent(Login.this, BottomNaviView.class);
                          login.putExtra("U_id", U_id);
                          login.putExtra("U_pw", U_pw);
                          Toast.makeText(getApplicationContext(), "로그인성공", Toast.LENGTH_SHORT).show();
                          login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                          login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                          startActivity(login);

                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            };
            //아이디 비밀번호가 틀리게 php에 전달할 경우 error를 받아 출력
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "비밀번호 또는 id를 확인하세요", Toast.LENGTH_SHORT).show();
                }
            };

            //Bundle을 이용하여 U_id에 저장 php에서 받은 값 저장
            //Bundle이란 상태정보를 저장하는 객체
            userinfo = new Bundle();
            userinfo.putString("U_id",U_id);
            userinfo.putString("U_pw",U_pw);
            exercise_settings.setArguments(userinfo);

            //LoginRequest의 형식의 객체를 선언
            LoginRequest loginRequest = new LoginRequest(U_id, U_pw, responseListener,errorListener);
            //RequestQueue라는 객체에 로그인 클래스의 정보를 넣은 후
            loginRequest.setShouldCache(false);
            RequestQueue queue = Volley.newRequestQueue(Login.this);
            queue.add(loginRequest);
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closelogin.onBackPressed();
    }






    //해쉬키 얻는 함수
    /*private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }

    }*/


}
