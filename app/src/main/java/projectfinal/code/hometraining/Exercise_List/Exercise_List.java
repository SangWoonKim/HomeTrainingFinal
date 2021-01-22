package projectfinal.code.hometraining.Exercise_List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.List;

import projectfinal.code.hometraining.DataBase.CatalogAdapter;
import projectfinal.code.hometraining.DataBase.DBHelper;
import projectfinal.code.hometraining.DataBase.Exercise;
import projectfinal.code.hometraining.DataBase.Exercise_Catalog;
import projectfinal.code.hometraining.DataBase.Setcal;
import projectfinal.code.hometraining.Exercise_Intent.LoginDataGS;
import projectfinal.code.hometraining.Exercise_List.Active.Active;
import projectfinal.code.hometraining.Exercise_List.Active.ActiveCheck;
import projectfinal.code.hometraining.Exercise_List.Myself.Exercise_Myself;
import projectfinal.code.hometraining.Exercise_Timer.alarm.service.AlarmState;
import projectfinal.code.hometraining.R;

public class Exercise_List extends Fragment {
    private View bottomview;
    private DBHelper dbHelper;
    private Button BTN_fight, BTN_accept ,active;
    private SwipeMenuListView result_list;
    // getAllList()를 사용하여 List형식으로 calalogs에 저장한다

    //logindata
    String loginIdData = LoginDataGS.getInstance().getLogin_ID();
    String loginPwData = LoginDataGS.getInstance().getLogin_PW();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bottomview=inflater.inflate(R.layout.exercise_list,container,false);
        return bottomview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        result_list =(SwipeMenuListView)getView().findViewById(R.id.list_list);
        BTN_accept = (Button)getView().findViewById(R.id.BTN_accept);
        BTN_fight = (Button)getView().findViewById(R.id.BTN_fight);
        active = (Button)getView().findViewById(R.id.active);
        result_list.setVisibility(View.VISIBLE);
        if(dbHelper ==null) {
            dbHelper = new DBHelper(getActivity(), "HomeTraining.db", null, 1);
        }
        final List<Exercise_Catalog> catalogs = dbHelper.getAllList();

        final CatalogAdapter adapter = new CatalogAdapter(catalogs,getActivity());

        result_list.setAdapter(adapter);
        result_list.setMenuCreator(creator);

        //slide기능시 리스너
        result_list.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                result_list.smoothOpenMenu(position);
            }

            @Override
            public void onSwipeEnd(int position) {
                result_list.smoothOpenMenu(position);
            }
        });
        //슬라이드 후 버튼을 누를 시 이벤트 설정 즉 항목 삭제 이벤트
        result_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                //calalogs의 Position값을 받아 한개의 값을 catalog에 담는다
                final Exercise_Catalog catalog = catalogs.get(position);

                catalogs.remove(position);
                dbHelper.Result_Delete(catalog);
                adapter.notifyDataSetChanged();
                return false;
            }
        });



        //칼로리에 저장 버튼
        BTN_accept.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {

                //LoginDataGS클래스의 getLogin_ID(),getLogin_PW()의 값이 존재할 경우 실행
                if ((loginIdData!=null)&&(loginPwData!=null)){
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                LayoutInflater factory = LayoutInflater.from(getActivity());
                View view = factory.inflate(R.layout.alert_accept, null);
                //필요한 객체 또는 전역변수 선언
                dbHelper.Result_Setcal();
                final Setcal setcal = dbHelper.getAllSetcal();
                final TextView tv_day_setcal = view.findViewById(R.id.TV_day_setcal);

                // sql문을 사용하여 해당 칼럼에 있는 값을 구하는 함수 적기 즉 dbHelper클래스에서 적용
                // 칼로리 계산 출력
                tv_day_setcal.setText(setcal.getSetcal());
                dialog.setView(view)
                        //다이얼로그의 초기화 버튼 클릭시
                        .setNeutralButton("초기화", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // results(Exercise_result에서 arraylist로 반환)에 들어있던 값들 초기화
                                //배열 초기화
                                catalogs.clear();
                                //sql실행 데이터 삭제
                                dbHelper.Reset_Exercise_Result_Table();
                                //어뎁터에 적용
                                dbHelper.Reset_Setcal_table();
                                dbHelper.close();
                                adapter.notifyDataSetChanged();

                                result_list.invalidateViews();

                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.detach(Exercise_List.this).attach(Exercise_List.this).commit();
                            }
                        })
                        //다이얼 로그의 활동 추가 버튼 클릭시
                        .setPositiveButton("활동 추가", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(AlarmState.getInstance().isCheck_alarm()==true || ActiveCheck.getInstance().isCheck_active()==true){
                                    //setcal의 값이 null이 아닐경우
                                    if (setcal.getSetcal()!=null) {
                                        dbHelper.myself_fights(setcal);
                                        dbHelper.Detail_Myself_fight(catalogs);
                                        catalogs.clear();
                                        dbHelper.Reset_Exercise_Result_Table();
                                        dbHelper.Reset_Setcal_table();
                                        dbHelper.close();
                                        result_list.invalidateViews();
                                        //프래그먼트 갱신
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        ft.detach(Exercise_List.this).attach(Exercise_List.this).commit();
                                    }else{
                                        Toast.makeText(getActivity(),"운동을 채우시고 버튼을 눌러주세요",Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(getActivity(),"운동시간을 설정하시거나 또는 운동시간이 끝나야 사용가능합니다.",Toast.LENGTH_SHORT).show();
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.detach(Exercise_List.this).attach(Exercise_List.this).commit();
                                }
                            }
                        }).create().show();
            }else{ //로그인 하지 않았을시 즉 getLogin_ID(),getLogin_PW()의 값이 null 일 경우
                    Toast.makeText(getActivity(),"로그인 후 사용가능합니다.",Toast.LENGTH_SHORT).show();
                }

             }
        });
        // 나 자신과의 싸움(그래프) 항목으로 이동하는 버튼
        BTN_fight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((loginIdData!=null)&&(loginPwData!=null)){
                    Intent myself_xml = new Intent(getActivity(), Exercise_Myself.class);
                    startActivity(myself_xml);
                }else{
                    Toast.makeText(getActivity(),"로그인 후 사용가능합니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //운동활동에 대한 버튼
        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (catalogs.size() != 0) {
                    Intent exercise_activity = new Intent(getActivity(), Active.class);
                    startActivity(exercise_activity);
                }else {
                    Toast.makeText(getActivity(),"운동을 선택하셔야 사용가능합니다",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    //슬라이드시 나타나는 항목 속성설정
    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem deleteitem= new SwipeMenuItem(getActivity().getApplicationContext());
            //색상
            deleteitem.setBackground(new ColorDrawable(Color.rgb(0xF9,0X3F,0x25)));
            //너비
            deleteitem.setWidth(200);
            //제목
            deleteitem.setTitle("삭제");
            //제목 크기
            deleteitem.setTitleSize(18);
            //제목 색상
            deleteitem.setTitleColor(Color.WHITE);
            //항목 추가
            menu.addMenuItem(deleteitem);
        }
    };


    @Override
    public void onDestroy() {

        //테마 변경시 스택에 있던 모든 액티비티들이 재시작 됨에 따라 초기화가 선언됨 이걸 막아야함
        dbHelper.Reset_Exercise_Result_Table();
        dbHelper.Reset_Setcal_table();
        dbHelper.close();
        super.onDestroy();
        Log.d("ondestroy","list 삭제");
    }
}
