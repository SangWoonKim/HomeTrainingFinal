package projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/*retrofitApi를 이용하여 query를 보낼 url을 지정하고 */
public class RetrofitAPI {
    private static final String BASE_URL = "https://dapi.kakao.com/";
    private static Retrofit retrofit;

    //singleton을 이용하여 instance 를 나누어 주는 식으로 함
    public static Retrofit getApiClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return  retrofit;
    }

}
