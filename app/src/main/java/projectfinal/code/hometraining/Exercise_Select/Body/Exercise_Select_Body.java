package projectfinal.code.hometraining.Exercise_Select.Body;

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

import projectfinal.code.hometraining.DataBase.Exercise;
import projectfinal.code.hometraining.DataBase.ListAdapter;
import projectfinal.code.hometraining.DataBase.DBHelper;
import projectfinal.code.hometraining.R;

public class Exercise_Select_Body extends AppCompatActivity {
    private DBHelper dbHelper;
    private ListView bodylist;
    private String part;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_select_body);
        bodylist=(ListView)findViewById(R.id.bodylist);
        bodylist.setVisibility(View.VISIBLE);

        if (dbHelper==null){
            dbHelper = new DBHelper(Exercise_Select_Body.this,"HomeTraining.db",null,1);
        }
        //getAllExercise에 매개변수에 넣기위한 값
        part="Body";

        // dbhelper클래스의 getAllExercise 함수의 리턴값을 bodies에게 저장
        final List<Exercise> bodies = dbHelper.getAllExercise(part);
        //어뎁터 연결 (BodyListAdapter클래스의 매개변수들을 받아 사용)매개변수:(List<Exercise> , Context)
        bodylist.setAdapter(new ListAdapter(bodies,Exercise_Select_Body.this));
        bodylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               AlertDialog.Builder dialog = new AlertDialog.Builder(Exercise_Select_Body.this);
                LayoutInflater factory = LayoutInflater.from(Exercise_Select_Body.this);
                View v =factory.inflate(R.layout.select_dialog,null);
                final Exercise body = bodies.get(position);
                final ImageView image = v.findViewById(R.id.dialog_image);

                String DBpath = body.getE_image();
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
                                dbHelper.Exercise_Insert_Result(body);
                                dbHelper.close();
                            }
                        }).create().show();
            }
        });
    }
}
