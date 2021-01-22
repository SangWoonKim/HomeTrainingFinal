package projectfinal.code.hometraining.Exercise_Settings.userchange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import projectfinal.code.hometraining.BottomNavi.BottomNaviView;
import projectfinal.code.hometraining.ExerciseFirst.Login.Login;
import projectfinal.code.hometraining.ExerciseFirst.Register.Register;
import projectfinal.code.hometraining.ExerciseFirst.Register.RegisterRequest;
import projectfinal.code.hometraining.R;

public class UserChange extends AppCompatActivity implements View.OnClickListener {
    private Button BTN_userchange_Accept, BTN_userchange_Close;
    private EditText ET_userchange_PW, ET_userchange_PWCheck, ET_userchange_Address, ET_userchange_Phone;
    private Response.Listener<String> responseListener,responseListenerAD,responseListenerPH;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_settings_userchange);
    BTN_userchange_Accept = (Button)findViewById(R.id.BTN_userchange_Accept);
    BTN_userchange_Accept.setOnClickListener(this);
    BTN_userchange_Close = (Button)findViewById(R.id.BTN_userchange_Close);
    BTN_userchange_Close.setOnClickListener(this);

    ET_userchange_PW = (EditText)findViewById(R.id.ET_userchange_PW);
    ET_userchange_PWCheck = (EditText)findViewById(R.id.ET_userchange_PWCheck);
    ET_userchange_Address = (EditText)findViewById(R.id.ET_userchange_Address);
    ET_userchange_Phone = (EditText)findViewById(R.id.ET_userchange_Phone);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.BTN_userchange_Accept:
                String U_pw =ET_userchange_PW.getText().toString();
                String U_pwcheck =ET_userchange_PWCheck.getText().toString();
                String U_address= ET_userchange_Address.getText().toString();
                int U_phone = Integer.parseInt(ET_userchange_Phone.getText().toString());



                // Exercise_settings에서 intent로 전달된 값 받기
                Intent id_data_receive = getIntent();
                final String U_id = id_data_receive.getStringExtra("U_id");

                //비밀번호 비교 if문
                if(U_pw.equals(U_pwcheck)){
                    //서버에서 응답받을 객체 생성
                    responseListener =new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                                if (success){
                                    Toast.makeText(getApplicationContext(),"비밀번호 변경되었습니다",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "비밀번호를 다시 확인하세요", Toast.LENGTH_SHORT).show();
                }



                //주소를 위한 부분
                if (U_address!=null) {
                    responseListenerAD = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObjectAD = new JSONObject(response);
                                boolean success = jsonObjectAD.getBoolean("success");

                                if (success) {

                                } else {
                                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                }
                else {
                    Toast.makeText(getApplicationContext(),"주소를 확인하세요",Toast.LENGTH_SHORT).show();
                }
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                };


                //휴대폰을 위한 부분
                //if (U_phone!=null){
                 responseListenerPH = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObjectPH = new JSONObject(response);
                            boolean success = jsonObjectPH.getBoolean("success");

                            if(success){
                            }else{
                                Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //캐시 인스턴스화
                Cache cache = new DiskBasedCache(getCacheDir(),1024*1024);

                Network network = new BasicNetwork(new HurlStack());

                //서버에 데이터 요청하는 부분
                UserchangeADRequest userchangeADRequest = new UserchangeADRequest(U_address,U_id,responseListenerAD,errorListener);
                UserchangeRequest userchangeRequest = new UserchangeRequest(U_pw,U_id,responseListener);
                UserchangePHRequest userchangePHRequest = new UserchangePHRequest(U_phone,U_id, responseListenerPH);

               RequestQueue queue = new RequestQueue(cache,network);
               queue.start();
                queue.add(userchangeADRequest);
                queue.add(userchangeRequest);
                queue.add(userchangePHRequest);


                Intent initial = new Intent(this, Login.class);
                //액티비티 스택 초기화 플래그
                initial.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                initial.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(initial);
                break;
            case R.id.BTN_userchange_Close:
                break;
        }
    }
}
