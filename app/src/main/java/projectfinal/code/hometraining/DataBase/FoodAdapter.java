package projectfinal.code.hometraining.DataBase;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

public class FoodAdapter extends BaseAdapter {

    private List<Food> foods;
    private Context context;

    public FoodAdapter(List<Food> foods, Context context){
        this.foods=foods;
        this.context=context;
    }

    @Override
    public int getCount() {
        return this.foods.size();
    }

    @Override
    public Object getItem(int position) {
        return this.foods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView==null) {
            convertView = new LinearLayout(context);
            ((LinearLayout)convertView).setOrientation(LinearLayout.VERTICAL);
            ImageView F_image = new ImageView(context);
            F_image.setScaleType(ImageView.ScaleType.FIT_XY);

            ((LinearLayout)convertView).addView(F_image);
            holder = new Holder();

            holder.ivF_image = F_image;
            convertView.setTag(holder);
        }else{
            holder=(Holder)convertView.getTag();
        }
        Food food = (Food) getItem(position);

        String DBpath = food.getF_image();
        String type ="drawable";
        String packagename = context.getPackageName();

        int resid = context.getResources().getIdentifier(DBpath,type,packagename);
        holder.ivF_image.setImageResource(resid);
        return convertView;
    }
}
