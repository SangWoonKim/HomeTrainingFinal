package projectfinal.code.hometraining.Exercise_Food;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import projectfinal.code.hometraining.DataBase.DBHelper;
import projectfinal.code.hometraining.DataBase.Food;
import projectfinal.code.hometraining.DataBase.FoodAdapter;
import projectfinal.code.hometraining.R;
/*운동식단 클래스
* 프래그먼트 형식
* */
public class Exercise_Food extends Fragment {
    private View bottomview;
    private ListView food_list;
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bottomview = inflater.inflate(R.layout.exercise_food,container,false);
        return bottomview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //음식조회를 위한 함수 만들기
        food_list = (ListView)getView().findViewById(R.id.food_list);
        food_list.setVisibility(View.VISIBLE);
        //dbhelper의 객체가 없을 경우
        if (dbHelper == null){
            //dbhelper생성
            dbHelper = new DBHelper(getActivity(),"HomeTraining.db",null,1);
        }


        final List<Food> foods = dbHelper.getAllFood();
        //foodAdapter생성
        final FoodAdapter adapter = new FoodAdapter(foods,getActivity());
        //listview에 나타냄 즉
        food_list.setAdapter(adapter);

    }
}
