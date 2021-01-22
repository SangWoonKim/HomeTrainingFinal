package projectfinal.code.hometraining.Exercise_List.Myself;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import projectfinal.code.hometraining.DataBase.DBHelper;
import projectfinal.code.hometraining.DataBase.Detail_Myself;
import projectfinal.code.hometraining.DataBase.Detail_MyselfAdapter;
import projectfinal.code.hometraining.DataBase.Myself;
import projectfinal.code.hometraining.R;

/*나자신과의 싸움 클래스
* 그래프와 tableLayout을 이용한 클래스*/
public class Exercise_Myself extends AppCompatActivity {
    private DBHelper dbHelper;
    private BarChart myselfchart;
    private SQLiteDatabase db;
    private ListView detail_list;

    //데이터 모이는 MpandroidChart의 커스텀배열 객체
    private BarDataSet barDataSet;
    private TableLayout exercise_Table;
    //차트에 들어가는 항목 선언
    ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
    //x축 배열 선언
    ArrayList<String> xvalue = new ArrayList<String>();

    //tableLayout의 TextView에 들어가는 항목들 선언

    //매개변수로 들어갈 전역변수 선언
    private int date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_select_list_myself);
        dbHelper = new DBHelper(Exercise_Myself.this,"HomeTraining.db",null,1);
        myselfchart=(BarChart)findViewById(R.id.myselfchart);
        //배열 초기화
        exercise_Table = (TableLayout)findViewById(R.id.exercise_Table);
        detail_list = (ListView)findViewById(R.id.detail_listview);
        barEntries.clear();
        xvalue.clear();

        //DB에 있는 data조회 부
       db=dbHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select*from Myself_fight",null);
        if(cursor !=null && cursor.getCount() !=0){
            while (cursor.moveToNext()){
                //db에서 조회한 데이터들을 배열에 넣은 후 형변환하여 좌표형식으로 넣음
                barEntries.add(new BarEntry(Float.parseFloat(cursor.getString(0)),Integer.parseInt(cursor.getString(1))));
                xvalue.add(""+cursor.getString(0));
            }
        }
        cursor.close();
        db.close();
        //데이터 모음에("항목들(data), 항목의 목록 이름")을 넣은 객체 생성
        barDataSet = new BarDataSet(barEntries,"운동량");
        //NPAndroid의 추상 클래스? 인터페이스? 이용하여 BarData에 넣을 수 있는 형식으로 객체 생성
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(barDataSet);

        //그래프에 널을 수 있는 메소드 형식으로 객체 생성
        BarData data = new BarData(dataSets);
        XAxis xAxis = myselfchart.getXAxis();

        //데이터 삽입
        myselfchart.setData(data);
        myselfchart.animateXY(0,100);

        //갱신
        myselfchart.invalidate();
        myselfchart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            //항목 클릭시 이벤트
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //Tablelayout의 행의 개수를 불러옴
                int tableRowCount = exercise_Table.getChildCount();
                //1을 초기값으로 한 이유는 첫 번째 로우는 삭제하지 않기 위해
                for (int firstCount =1; firstCount<tableRowCount; firstCount++){
                    View child = exercise_Table.getChildAt(firstCount);
                    if (child instanceof TableRow) ((ViewGroup)child).removeAllViews();
                }
                //date에 getX의 정수값을 형변환 하여 저장
                date = (int) e.getX();
                //tableLayout에 정보를 출력하기 위한 함수
               // if ()
                tablerowModify();

                //x좌표로 select해서 테이블에 나타낼것임 float을 캐스팅하여 int로 변환하면 날자에 맞게 찾을 수 있음
                Toast.makeText(Exercise_Myself.this,"index"+e,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void tablerowModify(){
        final List<Detail_Myself> detail_Myselfs = dbHelper.getAllDetailMyself(date);
        Detail_MyselfAdapter detail_myselfAdapter = new Detail_MyselfAdapter(detail_Myselfs,Exercise_Myself.this);
        //detail_list.setAdapter(new Detail_MyselfAdapter(detail_Myselfs,Exercise_Myself.this));
        detail_list.setAdapter(detail_myselfAdapter);
        detail_myselfAdapter.notifyDataSetChanged();
    }
}
