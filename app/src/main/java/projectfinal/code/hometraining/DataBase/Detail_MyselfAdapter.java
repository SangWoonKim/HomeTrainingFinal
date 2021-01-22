package projectfinal.code.hometraining.DataBase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import projectfinal.code.hometraining.R;

public class Detail_MyselfAdapter extends BaseAdapter {

    private List<Detail_Myself> detail_myselfs;
    private Context context;

    public Detail_MyselfAdapter(List<Detail_Myself> detail_myselfs, Context context){
        this.detail_myselfs=detail_myselfs;
        this.context=context;
    }

    //listview에 보여질 item수 정의
    @Override
    public int getCount() {
        return detail_myselfs.size();
    }

    //한 줄 정의(date,part,name,cal)
    @Override
    public Object getItem(int position) {
        return this.detail_myselfs.get(position);
    }

    //item을 구별하기 위한 것  position사용
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder= null;

        if (convertView == null){
            //convertView에 표시할 list의 item항목 정의
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_listview_item,null,false);

            holder = new Holder();
            holder.text_D = (TextView)convertView.findViewById(R.id.text_date);
            holder.text_P = (TextView)convertView.findViewById(R.id.text_part);
            holder.text_N = (TextView)convertView.findViewById(R.id.text_name);
            holder.text_C = (TextView)convertView.findViewById(R.id.text_cal);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        Detail_Myself detail_myself = (Detail_Myself) getItem(position);

        holder.text_D.setText(detail_myself.getD_date());
        holder.text_P.setText(detail_myself.getD_part());
        holder.text_N.setText(detail_myself.getD_name());
        holder.text_C.setText(detail_myself.getD_cal());

        return convertView;
    }
}
