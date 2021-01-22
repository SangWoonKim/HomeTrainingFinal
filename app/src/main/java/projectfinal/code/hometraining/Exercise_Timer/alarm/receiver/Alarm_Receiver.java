package projectfinal.code.hometraining.Exercise_Timer.alarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import projectfinal.code.hometraining.Exercise_Timer.alarm.service.AlarmService;

public class Alarm_Receiver extends BroadcastReceiver{
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        String getAlarmString = intent.getExtras().getString("state");
        Intent service_intent = new Intent(context, AlarmService.class);

        service_intent.putExtra("state",getAlarmString);

        //서비스 시작 메소드
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.context.startForegroundService(service_intent);
        }else {
            this.context.startService(service_intent);
        }
    }
}
