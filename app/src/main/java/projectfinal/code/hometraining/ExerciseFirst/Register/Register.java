package projectfinal.code.hometraining.ExerciseFirst.Register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import projectfinal.code.hometraining.ExerciseFirst.Login.Login;
import projectfinal.code.hometraining.R;

public class Register extends AppCompatActivity implements View.OnClickListener {
    private Button BTN_register_Register, BTN_register_Close;
    private EditText ET_register_Id, ET_register_Password, ET_register_Passwordcheck, ET_register_Name, ET_register_Phone, ET_register_Address;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        BTN_register_Register = (Button)findViewById(R.id.BTN_register_Register);
        BTN_register_Register.setOnClickListener(this);

        BTN_register_Close = (Button)findViewById(R.id.BTN_register_Close);
        BTN_register_Close.setOnClickListener(this);

        ET_register_Id =(EditText)findViewById(R.id.ET_register_Id);
        ET_register_Password =(EditText)findViewById(R.id.ET_register_Password);
        ET_register_Passwordcheck =(EditText)findViewById(R.id.ET_register_Passwordcheck);
        ET_register_Name =(EditText)findViewById(R.id.ET_register_Name);
        ET_register_Phone =(EditText)findViewById(R.id.ET_register_phone);
        ET_register_Address =(EditText)findViewById(R.id.ET_register_Address);
    }

    @Override
    public void onClick(View v) {
        //회원가입 버튼 클릭시
        if (v.getId()==R.id.BTN_register_Register){
            //editview의 쓰여진 값으로 초기화 하여 변수 선언
            String U_id = ET_register_Id.getText().toString();
            String U_pw = ET_register_Password.getText().toString();
            String U_pwcheck = ET_register_Passwordcheck.getText().toString();
            String U_name = ET_register_Name.getText().toString();
            int U_phone = Integer.parseInt(ET_register_Phone.getText().toString());
            String U_address = ET_register_Address.getText().toString();

            //비밀번호 확인 즉 저장된 값이 같을 경우 회원가입 실행
            if (U_pw.equals(U_pwcheck)) {

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    //응답을 받을 경우 함수호출 하지만 응답이 없어서 호출이 안됨 즉 URL,php또는 db에 문제
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //php서버에서 success에 저장된 값(true)을 받아 boolean형 success변수에 넣어준다
                            boolean success = jsonObject.getBoolean("success");

                            // php서버에서 응답을 받았을 경우 사용자에게 알려주는 부분분
                           if (success) {
                                Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                                Intent Register_Register = new Intent(Register.this, Login.class);
                                startActivity(Register_Register);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(U_id, U_pw, U_name, U_phone, U_address, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Register.this);
                //이 클래스에 대한 내용을 서버에 전송 즉 매개변수들을 php서버로 리턴
                queue.add(registerRequest);
            }
            // 비밀번호가 틀릴경우 회원가입 진행 불가
            else{
                Toast.makeText(getApplicationContext(), "비밀번호를 다시 확인하십시오", Toast.LENGTH_SHORT).show();
            }
        }

        //취소버튼 클릭시
        if (v.getId() ==R.id.BTN_register_Close){
            Intent Register_Close = new Intent(Register.this, Login.class);
            startActivity(Register_Close);
            finish();
        }
    }
}
