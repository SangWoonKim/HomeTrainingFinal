package projectfinal.code.hometraining.Exercise_List.Active;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import projectfinal.code.hometraining.DataBase.ActiveC1;
import projectfinal.code.hometraining.DataBase.ActiveC2;
import projectfinal.code.hometraining.DataBase.DBHelper;
import projectfinal.code.hometraining.R;

/*운동활동에서 쓰이는 클래스 사진들을 갖고 애니메이션화 시키는 클래스*/
public class Active extends AppCompatActivity {

    private Boolean status = true;  //애니메이션 행동에 대한 상태 초기화
    private int recode = -1; // 2차 배열의 행값 초기화
    private int index = 0; // 2차 배열의 열값 초기화
    private int Yindex =0; // 애니메이션을 돌릴 연산 초기화
    private DBHelper dbHelper;

    List<ActiveC1> activeC1List; //반복되는 이미지 1번째
    List<ActiveC2> activeC2List; //반복되는 이미지 2번째

    ArrayList<List<String>> activeList = new ArrayList<>(); //이차원동적배열로 생성

    ImageView active_imageview;
    Button active_start, active_exit;

    Thread animateThread; //쓰레드 호출

    //핸들러를 생성하여 애니매이션처럼 보이게하는 부분
    Handler animatehandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                //휴식하는 이미지를 띄우기 위한 함수
                rest();
            }else if (msg.what ==2){
                //활동이 완료되었을시 실행하는 함수
                activefinish();
            }else{
                //계속 변하는 이미지(1번째 2번째)를 번갈아 띄우기 위한 함수
                updateThread();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_active);

        if(dbHelper ==null) {
            dbHelper = new DBHelper(this, "HomeTraining.db", null, 1);
        }
        // 각 배열에 데이터 할당
        activeC1List=dbHelper.getActiveImage1();
        activeC2List=dbHelper.getActiveImage2();


        //2차원 배열 생성
            for ( Yindex = 0; Yindex < activeC1List.size(); Yindex++) {
                //위에서 할당한 배열로된 데이터를 2차원 배열 삽입
                activeList.add(new ArrayList<>(Arrays.asList(activeC1List.get(Yindex).getC_image(),activeC2List.get(Yindex).getC_imageOrg())));
            }


        active_imageview = (ImageView)findViewById(R.id.active_imageview);
        active_start = (Button)findViewById(R.id.active_start);
        active_exit = (Button)findViewById(R.id.active_exit);

        //시작버튼 이벤트
        active_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //버튼에 대한 이름 변경 함수
                state();
                animateThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //만약 애니메이션 thread실행중일 때
                        if (status == true){
                            //true일동안 반복적으로 handler에 메시지를 보냄
                            while (status) {
                                try {
                                    //animatehandler에서 얻어온 메시지를 다시 보냄
                                    animatehandler.sendMessage(animatehandler.obtainMessage());
                                    Thread.sleep(500);
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }
                            }
                        }else{ //애니메이션 thread가 멈춰있을 경우
                            if (recode == activeList.size()-1){ //이차원 배열의 크기와 recode의 크기가 값을 경우

                                Message finishmessage = animatehandler.obtainMessage();  //message객체를 이용하여 handler에 메세지를 보냄

                                finishmessage.what = 2;  //finsishmessage를 2라고 정의하고 보냄
                                animatehandler.sendMessage(finishmessage);
                            }
                            else {
                                Message message = animatehandler.obtainMessage();
                                message.what = 1;
                                //recode값을 증가시켜서 이차원배열의 다음 행의 값을 읽게함
                                recode++;
                                animatehandler.sendMessage(message);
                            }
                        }
                    }
                });
                //thread 시작
                animateThread.start();
            }
        });



        active_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    // 애니메이션을 모방한 슬라이드 함수
    private void updateThread() {

               //애니메이션의 반복을 위한 식
               int mod = index % 2;

               //이미지의 경로 정의
               String DBPath = activeList.get(recode).get(mod);
               String type = "drawable";
               String packageName = this.getPackageName();

               int resid = getResources().getIdentifier(DBPath, type, packageName);

               //애니메이션이 돌아갈 때마다 바뀌는 함수
               switch (mod) {
                   case 0:
                       index++;
                       active_imageview.setImageResource(resid);
                       break;

                   case 1:
                       index++;
                       active_imageview.setImageResource(resid);
                       break;
               }
    }



    //버튼 이름 바꾸는 함수
    private Boolean state(){
        if (status == true) {
            active_start.setText("운동시작");
            status = false;
        } else if (status == false) {
            active_start.setText("쉬는시간");
            status = true;
        }
        return status;
    }

    //휴식시간일 때 이미지를 띄우는 함수
    private void rest(){
       active_imageview.setImageResource(R.drawable.rest);
    }

    //배열에 있는 모든 이미지가 없어진경우 뜨는 함수
    private void activefinish(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(Active.this);
        LayoutInflater layoutset = LayoutInflater.from(Active.this);
        View v = layoutset.inflate(R.layout.alert_accept,null);
        final TextView textview = v.findViewById(R.id.TV_day_setcal);
        textview.setText(R.string.activefinish);
        dialog.setView(v)
        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActiveCheck.getInstance().setCheck_active(true);
                onBackPressed();
            }
        }).create().show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        status=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status = false;
        if (animateThread==null) { //스레드 존재유뮤 확인 객체 생성상태일경우
            recode = 0;

        }else{
            animateThread.interrupt();
        }
        recode = 0;
        animatehandler.sendEmptyMessage(0);
        animatehandler.removeMessages(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        status=false;
        finish();
    }
}
