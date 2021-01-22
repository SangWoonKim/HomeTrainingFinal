package projectfinal.code.hometraining.BottomNavi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import projectfinal.code.hometraining.Exercise_Food.Exercise_Food;
import projectfinal.code.hometraining.Exercise_List.Exercise_List;
import projectfinal.code.hometraining.Exercise_Select.Exercise_Select;
import projectfinal.code.hometraining.Exercise_Settings.Application.DarkThemeUtil;
import projectfinal.code.hometraining.Exercise_Settings.Exercise_Settings;
import projectfinal.code.hometraining.Exercise_Timer.Exercise_Timer;
import projectfinal.code.hometraining.Exercise_Timer.alarm.service.AlarmState;
import projectfinal.code.hometraining.R;
import projectfinal.code.hometraining.handler.BackPressCloseHandler;

public class BottomNaviView extends AppCompatActivity {
private BottomNavigationView bottomNavigationView;
private FragmentManager fm;
private FragmentTransaction ft;
private Exercise_Select select;
private Exercise_Timer timer;
private Exercise_Food food;
private Exercise_List list;
private Exercise_Settings settings;

private BackPressCloseHandler backclose = new BackPressCloseHandler(this);

    private SharedPreferences theme;
    DarkThemeUtil themeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = getSharedPreferences("projectfinal.code.hometraining_preferences",MODE_PRIVATE);
        String themechanged = theme.getString("themeSelect","");

        int a,b,c;
        a=5;
        b=4;
        switch (themechanged){
            case "dark":
                //문제점
                themeList.applytheme(DarkThemeUtil.DARK_MODE);
                break;
            case "light":
                themeList.applytheme(DarkThemeUtil.LIGHT_MODE);
                break;
            case "default":
                themeList.applytheme(DarkThemeUtil.DEFAULT_MODE);
                break;
        }
        setContentView(R.layout.activity_main);

        //객체생성
        select = new Exercise_Select();
        timer = new Exercise_Timer();
        food = new Exercise_Food();
        list = new Exercise_List();
        settings = new Exercise_Settings();

        setFragment(0);// 초기화면 설정

        //바텀 네비게이션 뷰의 id를 찾아 사용
        bottomNavigationView = findViewById(R.id.bottomnaviview);
        //바텀 네비게이션 뷰의 item선택시 이벤트 리스너 발동
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {//menuItem의 아이템 값을 선택시
                    case R.id.exercise_sel: //메뉴의 운동선택 클릭시
                        setFragment(0);
                        break;
                    case R.id.exercise_time: //메뉴의 타이머 클릭시
                        setFragment(1);
                        break;
                    case R.id.exercise_fd: //메뉴의 추천음식 클릭시
                        setFragment(2);
                        break;
                    case R.id.exercise_lt: //메뉴의 운동목록 클릭시
                        setFragment(3);
                        break;
                    case R.id.exercise_st: //메뉴의 설정 클릭시
                        setFragment(4);
                        break;

                }
                return true;
            }
        });
    }
    //프레그먼트 교체가 일어나는 함수 (페이지 번호)
    private void setFragment(int page){
        fm=getSupportFragmentManager(); //프레그먼트 매니저 메서드 호출
        ft=fm.beginTransaction(); //프레그먼트 메니저에 있는 메서드인 beginTransaction()메서드 호출
        switch (page){
            case 0:
                ft.replace(R.id.Main_Frame, select);// 프레임에 Exercise_Select클래스 영역 삽입
                ft.addToBackStack(null); //프레그먼트 이동시 스택에 기록
                ft.commit();// 트렌젝션 설정 적용
                break;
            case 1:
                ft.replace(R.id.Main_Frame, timer);// 프레임에 Exercise_Timer클래스 영역 삽입
                ft.addToBackStack(null); //프레그먼트 이동시 스택에 기록
                ft.commit();// 트렌젝션 설정 적용
                break;
            case 2:
                ft.replace(R.id.Main_Frame, food);// 프레임에 Exercise_Food클래스 영역 삽입
                ft.addToBackStack(null); //프레그먼트 이동시 스택에 기록
                ft.commit();// 트렌젝션 설정 적용
                break;
            case 3:
                ft.replace(R.id.Main_Frame, list);// 프레임에 Exercise_List클래스 영역 삽입
                ft.addToBackStack(null); //프레그먼트 이동시 스택에 기록
                ft.commit();// 트렌젝션 설정 적용
                break;
            case 4:
                ft.replace(R.id.Main_Frame, settings);// 프레임에 Exercise_Settings클래스 영역 삽입
                ft.addToBackStack(null); //프레그먼트 이동시 스택에 기록
                ft.commit();// 트렌젝션 설정 적용
                break;
        }
    }
    //뒤로가기 버튼을 누를경우
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backclose.onBackPressed();
        AlarmState.getInstance().clearAlarm();

    }
}
