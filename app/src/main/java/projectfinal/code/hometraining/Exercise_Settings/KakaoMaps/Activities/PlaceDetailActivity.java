package projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.Category_search.Document;
import projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.Utils.IntentKey;
import projectfinal.code.hometraining.R;
/*지도에 나오는 장소에 대한 정보를 담는 클래스*/
public class PlaceDetailActivity extends AppCompatActivity {
    TextView placeNameText;
    TextView addressText;
    TextView categoryText;
    TextView urlText;
    TextView phoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_settings_kakaomaps_place_detail);
        placeNameText = findViewById(R.id.place_detail_TV_name);
        addressText = findViewById(R.id.placedetail_TV_address);
        categoryText = findViewById(R.id.placedetail_TV_category);
        urlText = findViewById(R.id.placedetail_TV_url);
        phoneText = findViewById(R.id.placedetail_TV_phone);
        processIntent();
    }

    private void processIntent(){
        Intent processIntent = getIntent();
        Document document = processIntent.getParcelableExtra(IntentKey.PLACE_SEARCH_DETAIL_EXTRA);
        placeNameText.setText(document.getPlaceName());
        addressText.setText(document.getAddressName());
        categoryText.setText(document.getCategoryName());
        urlText.setText(document.getPlaceUrl());
        phoneText.setText(document.getPhone());
    }
}
