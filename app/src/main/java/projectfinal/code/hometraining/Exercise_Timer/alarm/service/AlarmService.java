package projectfinal.code.hometraining.Exercise_Timer.alarm.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import projectfinal.code.hometraining.Exercise_Timer.alarm.ExerciseAlarm;
import projectfinal.code.hometraining.R;

public class AlarmService extends Service {
    MediaPlayer mediaPlayer;
    int startId;
    boolean isRunning;
   // AlarmServiceThread alarmServiceThread;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    //최초 실행시 한번 실행되는 동작메소드
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT>=26){
            String CHANNEL_ID="default";
            AlarmState.getInstance().setCheck_alarm(true);
            //final AlarmState alarmState = new AlarmState();

            NotificationChannel channel =new NotificationChannel(CHANNEL_ID,"alarmServiceChannel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            Notification noticompat = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setContentTitle("HomeTraining")
                    .setContentText("자신이 설정한 운동의 시간이 끝났습니다.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .build();
            startForeground(1,noticompat);
            notificationManager.notify(1,noticompat);
            //alarmState.setStateAlarm(true);

            Log.d("onCreate()실행","notification 서비스 실행");

        }
    }



    //서비스가 시작되면 자동으로 호출되는 메소드
    //백그라운드에서 실행되는 동작들이 들어가는 메소드문
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String CHANNEL_ID="default";

        /*NotificationChannel channel =new NotificationChannel(CHANNEL_ID,"alarmServiceChannel", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        ServiceHandler serviceHandler = new ServiceHandler();
        alarmServiceThread = new AlarmServiceThread(serviceHandler);
        alarmServiceThread.start();
        return START_STICKY;*/


        String getState = intent.getExtras().getString("state");

        //assert란? 첫번 째 인자가 boolean으로 평가되는 표현식 또는 값을 받아 true이면 실행 false면 AssertionError를 예외를 발생시키는 예약어

        assert getState!=null;
        switch (getState){
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        // 알람음 재생 X , 알람음 시작 클릭
        if(!this.isRunning && startId == 1) {

           mediaPlayer = MediaPlayer.create(this,R.raw.tellme);
           mediaPlayer.start();

            this.isRunning = true;
            this.startId = 0;
        }

        // 알람음 재생 O , 알람음 종료 버튼 클릭
        else if(this.isRunning && startId == 0) {

           mediaPlayer.stop();
           mediaPlayer.reset();
           mediaPlayer.release();

            this.isRunning = false;
            this.startId = 0;
            this.onDestroy();
        }

        // 알람음 재생 X , 알람음 종료 버튼 클릭
        else if(!this.isRunning && startId == 0) {

            this.isRunning = false;
            this.startId = 0;
            this.onDestroy();

        }

        // 알람음 재생 O , 알람음 시작 버튼 클릭
        else if(this.isRunning && startId == 1){

            this.isRunning = true;
            this.startId = 1;
        }

        else {
        }
        Log.d("onstartCommand()실행","백그라운드 서비스 실행");
        return START_NOT_STICKY;

    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        //alarmServiceThread.stopThread();
        //alarmService=null;
        Log.d("onDestory() 실행", "서비스 파괴");
    }

  /*  class ServiceHandler extends Handler{

        String CHANNEL_ID="default";

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(@NonNull Message msg) {
            Intent serviceintent = new Intent(AlarmService.this, ExerciseAlarm.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(AlarmService.this,0,serviceintent,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationChannel channel =new NotificationChannel(CHANNEL_ID,"alarmServiceChannel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            Notification noticompat = new NotificationCompat.Builder(AlarmService.this,CHANNEL_ID)
                    .setContentTitle("HomeTraining")
                    .setContentText("자신이 설정한 운동의 시간이 끝났습니다.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .build();
            startForeground(1,noticompat);
            notificationManager.notify(1,noticompat);
        }
    }*/

}

