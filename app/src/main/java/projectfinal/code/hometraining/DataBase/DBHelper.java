package projectfinal.code.hometraining.DataBase;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/*
* sqliteDB에서 데이터를 빼고 쓰며
* 해당 DB를 앱 내부에 복사하는 클래스
* */

public class DBHelper extends SQLiteOpenHelper {
    private Context context;

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
        try {
            boolean boolResult = isCheckDB(context);  // isCheckDB함수에서 반환 결과를 받은 것을 boolResult에 저장
            Log.i("app", "DB Check="+boolResult);
            if(!boolResult){   // 만약 DB가 없을 경우
                copyDB(context); //copyDB함수를 실행하게 한다.
            }else{
            }

        } catch (Exception e) {

        }
    }
    // DB가 있나 체크하기
    public boolean isCheckDB(Context context){
        //파일 경로 명을 String으로 지정
        String filePath = "/data/data/" + context.getPackageName() + "/databases/" + getDatabaseName();
        //파일 객체 생성(파일경로명)
        File file = new File(filePath);
        //파일이 존재하는 경우 그러나 파일의 경로를 검사하는게 아닌 파일의 이름을 검사하는 함수인 exises()를 사용
        if (file.exists()) { //exists()는 반환 결과가 boolean형이므로 파일이 존재하면 true 아닐경우 false를 반환
            return true;
        }
        return false;
    }

    public void copyDB(Context context){
        Log.d("HomeTraining", "copyDB");
        //AssetManager는 assets폴더 내부에 접근할 수 있도록 해준다
        AssetManager manager = context.getAssets(); //getAssets()메소드는 AssetManager를 인스턴스화 시킴
        //폴더 경로와 파일경로 String형식으로 지정
        String folderPath = "/data/data/" + context.getPackageName() + "/databases";
        String filePath = "/data/data/" + context.getPackageName() + "/databases/" +getDatabaseName();
        //폴더와 파일 경로의 객체를 선언
        File folder = new File(folderPath); //인스턴스 생성
        File file = new File(filePath); // 인스턴스 생성
        //복사에 해당하는 스트림 초기화
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        //복사 하는 문장
        try {
            //자바의 데이터는 stream을 통해 입출력
            //입력스트림인 is에 = assets폴더를 접근한 후. 파일을 연다(데이터베이스 파일 이름을)
            InputStream is = manager.open(getDatabaseName());//getDatabaseName()메서드는 DBHelper의 name값을 상속 받는다
            //버퍼스트림은 bis에 = 버퍼스트림 형식인 객체를 선언한다 이때 is의 결과를 넎는다 즉 HomeTraining.db파일을 버퍼스트림에 저장
            BufferedInputStream bis = new BufferedInputStream(is);
            //만약 폴더가 존재할 경우
            if (folder.exists()) {
            }
            //폴더가 존재하지 않을 경우 폴더 생성
            else{
                folder.mkdirs();
            }
            //만약 파일이 존재할 경우
            if (file.exists()) {
                file.delete(); //파일 삭제
                file.createNewFile(); //파일 새로 만들기
            }
            //파일출력스트림의 객체 선언 즉 존재하는 파일을 참조
            fos = new FileOutputStream(file);
            //파일출력스트림에서 참조한 객체를 버퍼출력스트림에서 참조
            bos = new BufferedOutputStream(fos);
            int bufferread = -1;
            //버퍼는 1바이트 단위로 사용 즉 바이트형식의 배열에 1024byte만큼 객체를 선언
            byte[] buffer = new byte[1024];
            //버퍼의 값이 -1이 나올 때까지 실행 즉 버퍼의 크기를 넘은 파일인 경우 정지
            while ((bufferread = bis.read(buffer, 0, 1024)) != -1) { //read()메서드는 버퍼입력스트림으로 부터 한 문자씩을 읽어서 int형으로 반환
                bos.write(buffer, 0, bufferread); // bufferread의 읽은 값만큼 버퍼출력스트림에 저장
            }
            bos.flush(); //버퍼출력스트림 초기화 즉 버퍼스트림안의 버퍼 휘발시키기(비우기)
            bos.close(); //버퍼출력스트림 리소스 해제
            fos.close(); //파일출력스트림 리소스 해제
            bis.close(); //버퍼입력스트림 리소스 해제
            is.close(); //입력스트림 리소스 해제
        } catch (IOException e) {
            Log.e("ErrorMessage : ", e.getMessage());
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    //select 문

    //운동선택 select문
    public List<Exercise> getAllExercise(String part){
        //스트링 버퍼를 객체 선언
        StringBuffer sqlquery = new StringBuffer();
        sqlquery.append("select * from Exercise where E_part = '"+part+"'");
        //읽기전용으로 db파일 열기
        SQLiteDatabase db = getReadableDatabase();
        //커서를 이용하여 읽은 데이터를 cursor에 저장
        Cursor cursor = db.rawQuery(sqlquery.toString(),null);
        //List로 선언된 Body클래스의 변수들을 ArrayList객체로 선언 즉 ArrayList는 추상클래스인 List의 하위 클래스임으로 상속 가능
        List<Exercise> exercises = new ArrayList<Exercise>();
        //초기화
        Exercise exercise = null;

        //커서의 로우가 넘어갈 때 마다 Cursor클래스의 moveToNext()메소드 사용
        while(cursor.moveToNext()){
            //클래스를 사용할 수 있게 인스턴스화 시킴
            exercise = new Exercise();
            //Body테이블의 데이터들을 한 행씩 읽어 Body클래스에 저장
            exercise.setE_part(cursor.getString(1));
            exercise.setE_name(cursor.getString(2));
            exercise.setE_setcal(cursor.getInt(3));
            exercise.setE_image(cursor.getString(4));
            exercise.setE_imageOrg(cursor.getString(5));
            exercise.setE_activeImg(cursor.getString(6));
            exercise.setE_activeImg2(cursor.getString(7));
            exercises.add(exercise);
        }
        cursor.close();
        db.close();
        return exercises;
    }


    //운동목록 select문
    public List<Exercise_Catalog>getAllList(){
        StringBuffer sb = new StringBuffer();
        sb.append(" select * from Exercise_Result");
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(sb.toString(),null);
        List<Exercise_Catalog> catalogs = new ArrayList<Exercise_Catalog>();

        Exercise_Catalog catalog = null;

        while(cursor.moveToNext()){
            catalog = new Exercise_Catalog();

            //Exercise_Result클래스에 0번 칼럼 즉db의 E_name의 행의 값을 setE_part에 넣음
            catalog.setC_part(cursor.getString(0));
            //Exercise_Result클래스에 1번 칼럼 즉db의 E_name의 행의 값을 setE_name에 넣음
            catalog.setC_name(cursor.getString(1));
            //Exercise_Result클래스에 2번 칼럼 즉db의 E_name의 행의 값을 setE_setcal에 넣음
            catalog.setC_setcal(cursor.getInt(2));
            //Exercise_Result클래스에 3번 칼럼 즉db의 E_name의 행의 값을 setE_image에 넣음
            catalog.setC_image(cursor.getString(3));
            //Exercise_Result클래스에 4번 칼럼 즉db의 E_name의 행의 값을 setE_imageOrg에 넣음
            catalog.setC_imageOrg(cursor.getString(4));
            //x축 배열로 한줄 씩추가
            catalogs.add(catalog);
        }
        cursor.close();
        db.close();
        return catalogs;
    }

    //운동목록에서의 총칼로리 계산문 출력
    public Setcal getAllSetcal(){
        StringBuffer sb = new StringBuffer();
        sb.append("select * from Setcal");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sb.toString(),null);

        Setcal setcal =null;

        while (cursor.moveToNext()){
            setcal= new Setcal();
            setcal.setSetcal(cursor.getString(0));
        }
        return setcal;
    }

    //추천음식에서의 목록 출력
    public List<Food> getAllFood(/*String part*/){

        StringBuffer sqlquery = new StringBuffer();
        sqlquery.append("select * from Food ");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlquery.toString(),null);
        List<Food> foods = new ArrayList<Food>();
        Food food = null;

        while (cursor.moveToNext()){
            food= new Food();
            food.setF_part(cursor.getString(1));
            food.setF_name(cursor.getString(2));
            food.setF_setcal(cursor.getInt(3));
            food.setF_image(cursor.getString(4));
            foods.add(food);
        }
        cursor.close();
        db.close();
        return foods;
    }

    //나자신과의 싸움에서 클릭시 TableLayout에 나타나는 목록에 대한 함수
    public List<Myself> getAllMyself(int date){
        StringBuffer sqlquery = new StringBuffer();
        sqlquery.append(" select * from Myself_fight where M_date ='"+date+"'");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlquery.toString(),null);
        List<Myself> myselfList = new ArrayList<Myself>();
        Myself myself =null;

        while (cursor.moveToNext()){
            myself = new Myself();

            myself.setM_date(cursor.getString(0));
            myself.setM_setcal(cursor.getString(1));
            myselfList.add(myself);
        }
        cursor.close();
        db.close();
        return myselfList;
    }

    //운동실행 클릭시 이중배열로 나타낼수 있는 목록에 대한 함수 0번 인덱스 호출
    public List<ActiveC1> getActiveImage1(){
        StringBuffer sqlquery = new StringBuffer();
        sqlquery.append(" select R_activeImg from Exercise_Result ");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlquery.toString(),null);
        List<ActiveC1> activeImage1 = new ArrayList<ActiveC1>();
        ActiveC1 activec1 =null;

        while (cursor.moveToNext()){
            activec1 = new ActiveC1();

            activec1.setC_image(cursor.getString(0));
            activeImage1.add(activec1);
        }
        cursor.close();
        db.close();
        return activeImage1;
    }

    //운동실행 클릭시 이중배열로 나타낼수 있는 목록에 대한 함수 0번 인덱스 호출
    public List<ActiveC2> getActiveImage2(){
        StringBuffer sqlquery = new StringBuffer();
        sqlquery.append(" select R_activeImg2 from Exercise_Result ");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlquery.toString(),null);
        List<ActiveC2> activeImage2 = new ArrayList<ActiveC2>();
        ActiveC2 activec2 =null;

        while (cursor.moveToNext()){
            activec2 = new ActiveC2();

            activec2.setC_imageOrg(cursor.getString(0));
            activeImage2.add(activec2);
        }
        cursor.close();
        db.close();
        return activeImage2;
    }

    public List<Detail_Myself> getAllDetailMyself(int date){
        StringBuffer sqlquery = new StringBuffer();
        sqlquery.append(" select * from Detail_Myself_fight where D_date ='"+date+"'");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlquery.toString(),null);
        List<Detail_Myself> detail_MyselfList = new ArrayList<Detail_Myself>();
        Detail_Myself detail_Myself =null;

        while (cursor.moveToNext()){
            detail_Myself = new Detail_Myself();

            detail_Myself.setD_date(cursor.getString(0));
            detail_Myself.setD_part(cursor.getString(1));
            detail_Myself.setD_name(cursor.getString(2));
            detail_Myself.setD_cal(cursor.getString(3));
            detail_MyselfList.add(detail_Myself);
        }
        cursor.close();
        db.close();
        return detail_MyselfList;
    }




    //insert문
    //운동선택 insert문
    //즉 운동목록에 보여지는 데이터
    public void Exercise_Insert_Result(Exercise exercise){
        //SQLiteDatebase의 getWritableDatabase()메소드를 이용하기
        SQLiteDatabase db = getWritableDatabase();

        StringBuffer sb = new StringBuffer();
        sb.append("INSERT OR REPLACE INTO Exercise_Result(");
        sb.append("R_part,R_name,R_setcal,R_image,R_imageOrg,R_activeImg,R_activeImg2)");
        sb.append("select '#E_part#','#E_name#',#E_setcal#,'#E_image#','#E_imageOrg#','#E_activeImg#','#E_activeImg2#'");
        sb.append("where not exists(select * from Exercise_Result where R_name ='#E_name#')");

        String query = sb.toString();
        query = query.replace("#E_part#",exercise.getE_part());
        query = query.replace("#E_name#",exercise.getE_name());
        query = query.replace("#E_setcal#","'"+exercise.getE_setcal()+"'");
        query = query.replace("#E_image#",exercise.getE_image());
        query = query.replace("#E_imageOrg#",exercise.getE_imageOrg());
        query = query.replace("#E_activeImg#",exercise.getE_activeImg());
        query = query.replace("#E_activeImg2#",exercise.getE_activeImg2());
        db.execSQL(query);
        db.close();
        Toast.makeText(context,"추가완료",Toast.LENGTH_SHORT).show();
    }

    //Exercise_List의 항목의 칼로리를 계산한 후 insert하는 함수
    public void Result_Setcal() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into Setcal (S_setcal) select sum(R_setcal) from Exercise_Result");
        db.close();
    }

    //Myself_fight테이블에 insert하는 함수
    public void myself_fights(Setcal setcals) {
        StringBuffer sb = new StringBuffer();
        // 날짜 메서드를 사용
        SimpleDateFormat DateFormat = new SimpleDateFormat("MMdd");
        Date date = new Date();

        SQLiteDatabase db = getWritableDatabase();
        sb.append(" INSERT OR REPLACE INTO Myself_Fight ");
        sb.append(" ( M_date, M_setcal)");
        sb.append("VALUES (#M_date#, #M_setcal#)");

        String query = sb.toString();
        if (setcals.getSetcal() !=null) {
            query = query.replace("#M_date#", "'" + DateFormat.format(date) + "'");
            query = query.replace("#M_setcal#", "'" + setcals.getSetcal() + "'");
            db.execSQL(query);
        }else {
            db.close();
        }
        db.close();
        Toast.makeText(context,"추가완료",Toast.LENGTH_SHORT).show();
    }

    public void Detail_Myself_fight (List<Exercise_Catalog> catalog){
        /*SQLiteDatabase db= getWritableDatabase();
        SimpleDateFormat DateFormat = new SimpleDateFormat("MMdd");
        Date date = new Date();
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT OR REPLACE INTO Detail_Myself_Fight");
        sb.append(" (D_date, D_part, D_name, D_cal)");
        sb.append("VALUES (#D_date#, #D_setcal#, #D_part#, #D_cal#)");
        String query = sb.toString();
        query = query.replace("#D_date#", "'" + DateFormat.format(date) + "'");
        query = query.replace("#D_part#", "'" + catalog.getC_part() + "'");
        query = query.replace("#D_name#", "'" + catalog.getC_name() + "'");
        query = query.replace("#D_cal#", "'" + catalog.getC_setcal() + "'");
        db.execSQL(query);
        db.close();
        Toast.makeText(context,"추가완료",Toast.LENGTH_SHORT).show();*/
        SQLiteDatabase db= getWritableDatabase();
        SimpleDateFormat DateFormat = new SimpleDateFormat("MMdd");
        Date date = new Date();
        String query;

        for (int index=0; index < catalog.size(); index++){

           query = "insert into Detail_Myself_Fight (D_date,D_part,D_name,D_cal) values ("+ DateFormat.format(date) + "," +"'"+ catalog.get(index).getC_part() +"'"+
                   ","+"'" + catalog.get(index).getC_name() +"'"+ "," + catalog.get(index).getC_setcal()+")";
           db.execSQL(query);
        }


        db.close();
        Toast.makeText(context,"추가완료",Toast.LENGTH_SHORT).show();
    }



    //delete 함수
    //Exercise_Catalog클래스 형식의 매개변수를 받아 사용
    public void Result_Delete(Exercise_Catalog catalog) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Exercise_Result","R_name='"+catalog.getC_name()+"'",null);
        db.close();
    }

    //Exercise_Result 테이블의 데이터를 제거하는 즉 초기화하는 함수
    public void Reset_Exercise_Result_Table() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Exercise_Result",null,null);
        db.close();
        Toast.makeText(context,"초기화 완료",Toast.LENGTH_SHORT).show();
    }

    public void Reset_Setcal_table() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Setcal",null,null);
        db.close();
    }


}
