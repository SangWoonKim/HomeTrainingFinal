package projectfinal.code.hometraining.Exercise_Select.Loins;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import projectfinal.code.hometraining.DataBase.DBHelper;
import projectfinal.code.hometraining.DataBase.Exercise;
import projectfinal.code.hometraining.DataBase.ListAdapter;
import projectfinal.code.hometraining.Exercise_Select.Body.Exercise_Select_Body;
import projectfinal.code.hometraining.R;

public class Exercise_Select_Loins extends AppCompatActivity{
    private DBHelper dbHelper;
    private ListView loninslist;
    private String part;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_select_loins);
        loninslist=(ListView)findViewById(R.id.lonislist);
        loninslist.setVisibility(View.VISIBLE);


        if (dbHelper==null){
            dbHelper = new DBHelper(Exercise_Select_Loins.this,"HomeTraining.db",null,1);
        }
        //getAllExercise에 매개변수에 넣기위한 값
        part="Loins";

        // dbhelper클래스의 getAllBody 함수의 리턴값을 bodies에게 저장
        final List<Exercise> loins = dbHelper.getAllExercise(part);
        //어뎁터 연결 (BodyListAdapter클래스의 매개변수들을 받아 사용)매개변수:(List<Exercise> , Context)
        loninslist.setAdapter(new ListAdapter(loins,Exercise_Select_Loins.this));
        loninslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Exercise_Select_Loins.this);
                LayoutInflater factory = LayoutInflater.from(Exercise_Select_Loins.this);
                View v =factory.inflate(R.layout.select_dialog,null);

                final Exercise loin = loins.get(position);
                final ImageView image = v.findViewById(R.id.dialog_image);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);//or FIT_XY
                String DBpath = loin.getE_image();
                String type ="drawable";
                String packagename = getApplication().getPackageName();

                int resid = getApplication().getResources().getIdentifier(DBpath,type,packagename);
                image.setImageResource(resid);

                dialog.setView(v)
                        .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("추가", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.Exercise_Insert_Result(loin);
                                dbHelper.close();
                            }
                        }).create().show();
            }
        });
    }
}
