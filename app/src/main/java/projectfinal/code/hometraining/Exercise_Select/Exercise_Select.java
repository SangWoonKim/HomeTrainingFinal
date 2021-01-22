package projectfinal.code.hometraining.Exercise_Select;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import projectfinal.code.hometraining.Exercise_Select.Body.Exercise_Select_Body;
import projectfinal.code.hometraining.Exercise_Select.Loins.Exercise_Select_Loins;
import projectfinal.code.hometraining.Exercise_Select.Lower.Exercise_Select_Lower;
import projectfinal.code.hometraining.Exercise_Select.Upper.Exercise_Select_Upper;
import projectfinal.code.hometraining.R;

public class Exercise_Select extends Fragment implements View.OnClickListener{
    private View bottomview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bottomview = inflater.inflate(R.layout.exercise_select,container,false);
        return bottomview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton IMG_BTN_Upper = (ImageButton)view.findViewById(R.id.IMG_BTN_Upper);
        ImageButton IMG_BTN_Lower = (ImageButton)view.findViewById(R.id.IMG_BTN_Lower);
        ImageButton IMG_BTN_Body = (ImageButton)view.findViewById(R.id.IMG_BTN_Body);
        ImageButton IMG_BTN_Loins = (ImageButton)view.findViewById(R.id.IMG_BTN_Loins);
        IMG_BTN_Upper.setOnClickListener(this);
        IMG_BTN_Lower.setOnClickListener(this);
        IMG_BTN_Body.setOnClickListener(this);
        IMG_BTN_Loins.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id. IMG_BTN_Upper){
            Intent Upper = new Intent(getActivity(), Exercise_Select_Upper.class);
            startActivity(Upper);
        }
        if(v.getId() == R.id. IMG_BTN_Lower){
            Intent Lower = new Intent(getActivity(), Exercise_Select_Lower.class);
            startActivity(Lower);
        }
        if(v.getId() == R.id. IMG_BTN_Body){
            Intent Body = new Intent(getActivity(), Exercise_Select_Body.class);
            startActivity(Body);
        }
        if(v.getId() == R.id. IMG_BTN_Loins){
            Intent Loins = new Intent(getActivity(), Exercise_Select_Loins.class);
            startActivity(Loins);
        }

    }
}
