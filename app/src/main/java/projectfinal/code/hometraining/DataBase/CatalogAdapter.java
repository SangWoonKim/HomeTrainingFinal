package projectfinal.code.hometraining.DataBase;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

/*
* 운동목록에서 쓰일 listview의 어뎁터*/
public class CatalogAdapter extends BaseAdapter {
    //배열형식 즉 x축 배열로 Exercise_Result클래스의 변수들을 선언
    private List<Exercise_Catalog> catalogs;
    private Context context;

    public CatalogAdapter(List<Exercise_Catalog> catalogs, Context context){
        this.catalogs=catalogs;
        this.context=context;
    }
    @Override
    public int getCount() {
        return this.catalogs.size();
    }

    @Override
    public Object getItem(int position) {
        return this.catalogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;

        if (convertView == null) {
            convertView = new LinearLayout(context);
            //item항목 크기 제어
            ((LinearLayout) convertView).setOrientation(LinearLayout.VERTICAL);
            ImageView E_image = new ImageView(context);
            //크기제어 적용
            E_image.setScaleType(ImageView.ScaleType.FIT_XY);
            ((LinearLayout) convertView).addView(E_image);
            Exercise_Catalog catalog =(Exercise_Catalog) getItem(position);

            holder = new Holder();

            holder.ivC_image = E_image;
            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }
        Exercise_Catalog catalog =(Exercise_Catalog) getItem(position);

        String DBPath = catalog.getC_imageOrg();
        String type ="drawable";
        String packageName = context.getPackageName();

        int resid = context.getResources().getIdentifier(DBPath,type,packageName);
        holder.ivC_image.setImageResource(resid);
        return convertView;
    }
}
