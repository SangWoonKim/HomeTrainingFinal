package projectfinal.code.hometraining.DataBase;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

/*운동선택에서  쓰이는 listview의 어뎁터*/
public class ListAdapter extends BaseAdapter {
    private List<Exercise> exercises;
    private Context context;

    public ListAdapter(List<Exercise> bodies, Context context){
        this.exercises=bodies;
        this.context=context;
    }
    // 항목의 개수를 반환하는 함수
    @Override
    public int getCount() {
        return this.exercises.size();
    }


    @Override
    public Object getItem(int position) {
        return this.exercises.get(position);
    }

    //항목의 아이템의 인텍스 값을 반환하는 함수
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;

        if (convertView==null){
            convertView = new LinearLayout(context);
            ((LinearLayout)convertView).setOrientation(LinearLayout.VERTICAL);
            ImageView B_image = new ImageView(context);
            B_image.setScaleType(ImageView.ScaleType.FIT_XY);
            ((LinearLayout)convertView).addView(B_image);

            holder = new Holder();

            holder.ivE_image = B_image;
            convertView.setTag(holder);
        }
        else{
            holder=(Holder)convertView.getTag();
        }
        Exercise exercise = (Exercise) getItem(position);

        String DBpath = exercise.getE_imageOrg();
        String type ="drawable";
        String packagename = context.getPackageName();

        int resid = context.getResources().getIdentifier(DBpath,type,packagename);
        holder.ivE_image.setImageResource(resid);
        return convertView;
    }
}
