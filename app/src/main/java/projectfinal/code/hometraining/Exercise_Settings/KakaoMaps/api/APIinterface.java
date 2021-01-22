package projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.api;

import projectfinal.code.hometraining.Exercise_Settings.KakaoMaps.Category_search.Category_Result;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;
/*restApi를 이용하여 query를 보내 검색하는 클래스 */
public interface APIinterface {

    //장소이름으로 키워드로로 검색

    @GET("v2/local/search/keyword.json")
    Call<Category_Result> getSearchLocationDetail(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("x") String x,
            @Query("y") String y,
            @Query("radius") int radius,
            @Query("size") int size
    );
}
