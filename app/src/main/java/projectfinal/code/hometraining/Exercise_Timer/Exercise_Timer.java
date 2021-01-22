package projectfinal.code.hometraining.Exercise_Timer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Timer;

import projectfinal.code.hometraining.Exercise_List.Active.ActiveCheck;
import projectfinal.code.hometraining.Exercise_Timer.alarm.ExerciseAlarm;
import projectfinal.code.hometraining.R;

public class Exercise_Timer extends Fragment implements View.OnClickListener {
    private View bottomview;
    //맴버변수화
    private TextView timer;
    private TextView Record;
    private ScrollView scrollView;
    private Button BTN_Start, BTN_Record, BTN_alarm;

    //상태를 표시하는'상수' 지정
    private static final int INIT =0;//처음 (초기값)
    private static final int RUN =1; //실행중
    private static final int PAUSE =2; // 정지

    //상태값을 저장하는 변수
    private static int status =INIT;

    //기록할 때 순서 체크를 위한 변수
    private int count =1;

    //타이머 시간 값을 저장할 변수
    private long BaseTime,PauseTime;

    //ActiveCheck의 값 instance얻는 부분


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bottomview = inflater.inflate(R.layout.exercise_timer,container,false);
        return bottomview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        timer = (TextView)getView().findViewById(R.id.Timer);
        scrollView = (ScrollView)getView().findViewById(R.id.TimerScrollview);
        Record=(TextView)getView().findViewById(R.id.Record);
        BTN_Start = (Button)getView().findViewById(R.id.BTN_Start);
        BTN_Record = (Button)getView().findViewById(R.id.BTN_Record);
        BTN_alarm = (Button)getView().findViewById(R.id.BTN_alarm);
        BTN_alarm.setOnClickListener(this);
        BTN_Record.setEnabled(false);
        BTN_Start.setOnClickListener(this);
        BTN_Record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.BTN_Start:
                StartButton();
                break;
            case R.id.BTN_Record:
                RecordButton();
                break;
            case R.id.BTN_alarm:
                //운동을 하지 않았을경우 즉 false상태여야한다
                if (ActiveCheck.getInstance().isCheck_active() == false){
                    Intent intent = new Intent(getActivity(), ExerciseAlarm.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(),"오늘의 운동은 끝났습니다.",Toast.LENGTH_LONG).show();
                }
        }
    }

    //시작버튼 누를시 호출되는 함수
    private void StartButton(){
        switch (status){
            case INIT: //초기화된 변수를 다시 캐스팅해야 상태값을 확인 가능 즉 달라지는 상태값을 알기위해 기준이 되는 초기값 필요
                //어플리케이션이 실행되고 나서 실제로 경과된 시간
                BaseTime = SystemClock.elapsedRealtime(); //elapsedRealtime()메서드를 사용해서 BaseTime에 반환되는 값 저장 즉 0이 저장
                //Handler 실행
                handler.sendEmptyMessage(0);
                BTN_Start.setText("멈춤"); //시작버튼을 누를시 텍스트 변환
                BTN_Record.setEnabled(true);
                status=RUN; //상태변환

                break;

            case RUN: //실행중일 경우
                //handler 정지
                handler.removeMessages(0);
                //정지시간 체크
                PauseTime = SystemClock.elapsedRealtime(); //시작버튼 이벤트 실행 후 handler가 실행된 시간을 PauseTime에반환
                BTN_Start.setText("다시시작"); //멈춤버튼을 시작버튼이였던 버튼 텍스트 변환
                BTN_Record.setText("초기화"); // 멈춤버튼을 리셋버튼이였던 버튼 텍스트 변환
                status=PAUSE; //정지상태로 변환
                break;

            case PAUSE: //정지상태일 경우
                long ReStart = SystemClock.elapsedRealtime();
                BaseTime+=(ReStart-PauseTime); // BaseTime에 실행시간과 정지시간을 뺀 결과값을 반환

                //핸들러
                handler.sendEmptyMessage(0);
                BTN_Start.setText("멈춤");
                BTN_Record.setText("기록");
                status=RUN;
        }
    }
    //기록버튼 클릭시 이벤트
    private void RecordButton(){
        switch (status){
            case RUN: //타이머가 실행중일 때
                String TimeList = Record.getText().toString();
                TimeList +=String.format("%2d.%s\n",count,getTime()); //TimeList에 기록 추가()
                Record.setText(TimeList); //record(TextView)에 삽입
                count++; //기록번호 증가 즉 버튼을 누를때 마다 증가하게 됨
                break;
            case PAUSE: // 타이머가 정지중일 때
                BTN_Start.setText("시작");
                BTN_Record.setText("기록");

                timer.setText("00:00:00");
                Record.setText("");

                BaseTime=0; //기본시간 초기화
                PauseTime=0; // 일시정지시간 초기화
                count=1; //기록 순서 초기화
                status  = INIT; //상태 초기화
                break;
        }
    }

    //경과시간을 얻는 함수
    private final String getTime(){
        long NowTime =SystemClock.elapsedRealtime(); //경과된 시간 체크
        long OverTime = NowTime-BaseTime; //함수가 실행된 이후 경과 시간

        long m = OverTime/1000/60;  //분
        long s =(OverTime/1000)%60; //초
        long ms = OverTime%1000;    //밀리초

        String RecTime = String.format("%02d:%02d:%03d",m,s,ms);
        return RecTime;
    }
    //Handler 객체 선언

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            timer.setText(getTime()); //헨들러를 이용하여 텍스트에 적용
            handler.sendEmptyMessage(0);
        }
    };
}
