package projectfinal.code.hometraining.handler;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context){
        this.activity=context;
    }

    public void onBackPressed(){
        if(System.currentTimeMillis() > backKeyPressedTime +1000){
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime +1000){
            toast.cancel();

            /*//앱을 완전 종료하는 방법
            activity.moveTaskToBack(true);
            activity.finishAndRemoveTask();
            android.os.Process.killProcess(android.os.Process.myPid());
            */

            //태스크 리스트에 앱이 남기를 원하는 경우
            activity.moveTaskToBack(true);  //테스크(즉 현재 실행 앱을 백그라운드로 이동시키는 메소드)
            activity.finish();
            android.os.Process.killProcess(android.os.Process.myPid());  //앱 프로세스종료
        }
    }

    public void showGuide(){
        toast = Toast.makeText(activity,"한번 더 누르면 종료됩니다",Toast.LENGTH_SHORT);
        toast.show();
    }
}
