package projectfinal.code.hometraining.ExerciseSplash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import projectfinal.code.hometraining.ExerciseFirst.Login.Login;

public class Exercise_Splash extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            //대기 초 설정
            Thread.sleep(3000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this, Login.class));
        finish();
    }
}
