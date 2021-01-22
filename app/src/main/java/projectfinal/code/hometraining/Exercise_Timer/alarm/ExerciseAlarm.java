package projectfinal.code.hometraining.Exercise_Timer.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import projectfinal.code.hometraining.Exercise_Timer.alarm.receiver.Alarm_Receiver;
import projectfinal.code.hometraining.Exercise_Timer.alarm.service.AlarmService;
import projectfinal.code.hometraining.R;

public class ExerciseAlarm extends AppCompatActivity {
    AlarmManager alarmManager;
    Context context;
    PendingIntent sendintent;
    TimePicker timePicker;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_timer_alarm);
        this.context=this;

        alarmManager= (AlarmManager)getSystemService(ALARM_SERVICE);
        timePicker = findViewById(R.id.time_picker);

        final Calendar calendar = Calendar.getInstance();
        final Intent receiver_intent = new Intent(this, Alarm_Receiver.class);

        Button alarm_on = (Button)findViewById(R.id.btn_start);
        //설정버튼 클릭시
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
                calendar.set(Calendar.MINUTE,timePicker.getMinute());

                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                receiver_intent.putExtra("state","alarm on");
                sendintent = PendingIntent.getBroadcast(ExerciseAlarm.this,0,receiver_intent,PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),sendintent);
                Toast.makeText(getApplicationContext(), "시간이 설정되었습니다", Toast.LENGTH_SHORT).show();
            }
        });

        //초기화버튼클릭시
        Button alarm_off = (Button)findViewById(R.id.btn_finish);
        alarm_off.setOnClickListener(new View.OnClickListener() {
           // boolean alarmUp= (PendingIntent.getBroadcast(context,0,receiver_intent,PendingIntent.FLAG_NO_CREATE)!=null);

            @Override
            public void onClick(View v) {
                //alarmManager.cancel(sendintent);
                receiver_intent.putExtra("state","alarm off");
                sendBroadcast(receiver_intent);
            }
        });
    }
}
