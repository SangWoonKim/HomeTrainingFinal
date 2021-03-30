package projectfinal.code.hometraining.Exercise_Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import projectfinal.code.hometraining.ExerciseFirst.Login.Login;
import projectfinal.code.hometraining.ExerciseFirst.Register.Register;
import projectfinal.code.hometraining.Exercise_Intent.LoginDataGS;
import projectfinal.code.hometraining.Exercise_Settings.Bluetooth.BluetoothMain;
import projectfinal.code.hometraining.Exercise_Settings.CheckUserRequest.CheckUserRequest;
import projectfinal.code.hometraining.Exercise_Settings.DeleteUserRequest.DeleteUserRequest;
import projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.KakaoMaps;
import projectfinal.code.hometraining.Exercise_Settings.Maps.googleMapsGyms;
import projectfinal.code.hometraining.Exercise_Settings.setting.Setting;
import projectfinal.code.hometraining.Exercise_Settings.userchange.UserChange;
import projectfinal.code.hometraining.R;
import projectfinal.code.hometraining.handler.BackPressCloseHandler;

public class Exercise_Settings extends Fragment implements View.OnClickListener {
    private Button BTN_settings_userchange, BTN_settings_setting, BTN_settings_logout, BTN_settings_exit, BTN_settings_maps
            ,BTN_settings_bluetooth;
    private TextView TV_settings_name, TV_settings_address, TV_settings_phone;
    private View bottomview;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Setting Setting;
    private BackPressCloseHandler settingclose;
    private String response_delete_id, response_delete_pw;
    //LoginDataGS의 getter,setter를 이용하여 각 클래스들에 값 전달할 객체 생성
    //클래스에 final로 선언하여 재정의에 허용하지 않음
    //객체의 final의 의미는 해당 객체를 가리키는 포인터를 바꿀 수 없게 하는 의미
    // 즉 해당 이름이로 새 객체의 선언/ 재할당을 막는다는 뜻
    //final LoginDataGS loginDataGS = new LoginDataGS();

    //LoginDataGS이 들어있는 instance받는 곳
    String loginIdData = LoginDataGS.getInstance().getLogin_ID();
    String loginPwData = LoginDataGS.getInstance().getLogin_PW();
    //life cycle에 의해 프래그먼트에서 뷰를 만드는 시점에 바텀 네비게이션 뷰를 생성한다


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bottomview = inflater.inflate(R.layout.exercise_settings,container,false);
        return bottomview;
    }

    //life cycle에 의해 onCreateView return한 후 onViewCreate에서 코딩할 수 있도록 한다
    // 즉 oncreateview에서 view를(디자인) 얻어온 후 onviewcreate에서 기능구현을 하면 된다.

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BTN_settings_userchange = (Button)view.findViewById(R.id.BTN_settings_userchange);
        BTN_settings_setting = (Button)view.findViewById(R.id.BTN_settings_setting);
        BTN_settings_logout = (Button)view.findViewById(R.id.BTN_settings_logout);
        BTN_settings_exit = (Button)view.findViewById(R.id.BTN_settings_exit);
        BTN_settings_maps = (Button)view.findViewById(R.id.BTN_settings_maps);
        BTN_settings_bluetooth = (Button)view.findViewById(R.id.BTN_settings_bluetooth);

        BTN_settings_maps.setOnClickListener(this);
        BTN_settings_userchange.setOnClickListener(this);
        BTN_settings_setting.setOnClickListener(this);
        BTN_settings_logout.setOnClickListener(this);
        BTN_settings_exit.setOnClickListener(this);
        BTN_settings_bluetooth.setOnClickListener(this);

        TV_settings_name= view.findViewById(R.id.TV_settings_name);



        if ((loginIdData!=null)){
            TV_settings_name.setText(loginIdData+"2조");
        }else{
            TV_settings_name.setText("2조");
        }


        if ((loginIdData!=null)&&(loginPwData!=null)){
        }else{
            BTN_settings_exit.setText("로그인 화면으로 돌아가기");
        }

        TV_settings_phone = view.findViewById(R.id.TV_settings_phone);
        if (loginIdData!=null){
            TV_settings_name.setText("이름: "+loginIdData);
        }else{
            TV_settings_name.setText("헬린이");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //회원정보 변경 클릭시
            case R.id.BTN_settings_userchange:

                if ((loginIdData != null) && (loginPwData != null)){
                     AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                     LayoutInflater factory = LayoutInflater.from(getActivity());
                     View view = factory.inflate(R.layout.alert_userchange, null);

                    final EditText ET_userchange_ID = view.findViewById(R.id.ET_alert_userchange_ID);
                    final EditText ET_userchange_PW = view.findViewById(R.id.ET_alert_userchange_PW);


                         dialog.setView(view)
                        .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String U_id = ET_userchange_ID.getText().toString();
                        String U_pw = ET_userchange_PW.getText().toString();

                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success) {
                                        String U_id = jsonObject.getString("U_id");
                                        String U_pw = jsonObject.getString("U_pw");

                                        Intent pwchange = new Intent(getActivity(), UserChange.class);
                                        pwchange.putExtra("U_id", U_id);
                                        pwchange.putExtra("U_pw", U_pw);
                                        startActivity(pwchange);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        };
                        CheckUserRequest checkUserRequest = new CheckUserRequest(U_id, U_pw, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(getActivity());
                        queue.add(checkUserRequest);
                    }
                }).create().show();
        }else{
                    Toast.makeText(getActivity(),"로그인 후 사용가능한 기능입니다.",Toast.LENGTH_SHORT).show();
                }
               break;

                //환경설정 버튼 클릭시
            case R.id.BTN_settings_setting:
                getActivity();
                fm= getFragmentManager();
                ft=fm.beginTransaction();
                ft.replace(R.id.Main_Frame, new Setting());
                ft.addToBackStack(null);
                ft.commit();
                break;

                //블루투스 연결 클릭시
            case R.id.BTN_settings_bluetooth:
                Intent bluetoothIntent = new Intent(getActivity(), BluetoothMain.class);
                startActivity(bluetoothIntent);
            break;

            //로그아웃 버튼 클릭시
            case R.id.BTN_settings_logout:
                if ((loginIdData!=null) &&(loginPwData!=null)) {
                    Intent logout = new Intent(getActivity(), Login.class);
                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(logout);
                    //현재 액티비티 반환
                    getActivity().finish();
                    /*settingclose.*/
                }else{
                    Toast.makeText(getActivity(),"로그인 후 사용가능한 기능입니다.",Toast.LENGTH_SHORT).show();
                }
                break;



            //근처 헬스장 보기 클릭시
            //카카오 맵 허용 ip등록을 해야 실행가능
            case R.id.BTN_settings_maps:
                if ((loginIdData!=null)&&(loginPwData!=null)) {
                    Intent maps = new Intent(getActivity(), KakaoMaps.class);
                    //Intent maps = new Intent(getActivity(), googleMapsGyms.class);
                    startActivity(maps);
                }else{
                    Toast.makeText(getActivity(),"로그인 후 사용가능한 기능입니다.",Toast.LENGTH_SHORT).show();
                }
                break;

            //탈퇴 클릭시
            case R.id.BTN_settings_exit:

                if ((loginIdData!=null)&&(loginPwData!=null)){

                    AlertDialog.Builder deletedialog = new AlertDialog.Builder(getActivity());
                    LayoutInflater factory = LayoutInflater.from(getActivity());
                    View viewdelete = factory.inflate(R.layout.alert_userchange,null);

                    final EditText ET_userdelete_ID = viewdelete.findViewById(R.id.ET_alert_userchange_ID);
                    final EditText ET_userdelete_PW = viewdelete.findViewById(R.id.ET_alert_userchange_PW);

                    deletedialog.setView(viewdelete)
                            .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String delete_id = ET_userdelete_ID.getText().toString();
                            String delete_pw = ET_userdelete_PW.getText().toString();

                            Response.Listener<String> delete_responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject deletejsonObject = new JSONObject(response);
                                        boolean success = deletejsonObject.getBoolean("success");

                                        if (success){
                                            response_delete_id = deletejsonObject.getString("U_id");
                                            response_delete_pw = deletejsonObject.getString("U_pw");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            };

                            CheckUserRequest checkUserRequest = new CheckUserRequest(delete_id,delete_pw,delete_responseListener);
                            DeleteUserRequest deleteUserRequest = new DeleteUserRequest(delete_id,delete_responseListener);
                            RequestQueue queue = Volley.newRequestQueue(getActivity());
                            queue.add(checkUserRequest);
                            // 로그인 했을 때의 id와 pw가 한번 더 로그인을 하여 php서버에서 id,pw값을 받은 값을 비교하여 맞을 경우
                            if ((loginIdData.equals(response_delete_id)) && (loginPwData.equals(response_delete_pw))) {
                                queue.add(deleteUserRequest);
                                Intent deleteuser = new Intent(getActivity(),Login.class);
                                deleteuser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                deleteuser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(deleteuser);
                                Toast.makeText(getActivity(),"회원탈퇴가 완료되었습니다.",Toast.LENGTH_SHORT).show();
                            }else{
                               Toast.makeText(getActivity(),"비밀번호 또는 아이디를 확인하세요",Toast.LENGTH_SHORT).show();
                           }
                        }
                    }).create().show();
                }else{
                    Intent deleteuser = new Intent(getActivity(),Login.class);
                    deleteuser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    deleteuser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(deleteuser);
                }
                break;
        }
    }
}
