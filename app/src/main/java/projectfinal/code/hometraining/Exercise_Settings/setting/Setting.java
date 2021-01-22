package projectfinal.code.hometraining.Exercise_Settings.setting;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import projectfinal.code.hometraining.Exercise_Settings.Application.DarkThemeUtil;
import projectfinal.code.hometraining.R;

public class Setting extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.exercise_settings_preference);

        //preference 키 값을 찾아 참조
        ListPreference themePreference = findPreference("themeSelect");
        //만약 themeSelect의 값이 있을 경우
        //문자열만 불러옴 설정은 바꾸지 않음
        if(themePreference != null){
            //리스너 호출
            themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // 선택지를 String 값으로 받음
                    String themeOption = (String) newValue;
                    DarkThemeUtil.applytheme(themeOption);
                    return true;
                }
            });
        }
    }
}
