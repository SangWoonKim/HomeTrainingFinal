package projectfinal.code.hometraining.Exercise_Settings.Application;

import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
/*
* 테마 환경설정을 위한 클래스
* */
public class DarkThemeUtil {
    public static final String LIGHT_MODE = "light";
    public static final String DARK_MODE = "dark";
    public static final String DEFAULT_MODE = "default";

    public static void applytheme(String color){
        switch (color){
            case LIGHT_MODE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case DARK_MODE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                //안드로이드 10이상일 경우
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                    //다크모드 활성화 했을 경우 나이트모드 설정
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
                //안드로이드 10 미만
            else{
                //절전모드 활성화 했을 경우 나이트 모드 설정
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
            }
        }
    }
}
