package projectfinal.code.hometraining.Exercise_Settings.Application;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
//
public class DarkThemeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences savetheme = PreferenceManager.getDefaultSharedPreferences(this);
        String themechange = savetheme.getString("themeSelect", DarkThemeUtil.DEFAULT_MODE);
        DarkThemeUtil.applytheme(themechange);
    }
}
