package projectfinal.code.hometraining.Exercise_Settings.CheckUserRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
/*
*
* */
public class CheckUserRequest extends StringRequest {
    final static private String URL="http://hometraining.dothome.co.kr/UserCheck.php";
    //Map은 키를 값에 매핑하는 객체 ex)list
    //ex)Map<key,value>
    private Map<String,String> map;

    public CheckUserRequest(String U_id, String U_pw, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);
        //HashMap이란 2차원 배열을 가져 키(이름),데이터를 갖고 데이터에 접근 하는 객체 ex)arraylist
        map = new HashMap<>();
        //intent 값 넘기기와 비슷
        //즉 php mysqli_stmt_bind_param에 있는 같은 이름의 키(colume명)에 해당하는 값을 삽입한다
        map.put("U_ID",U_id);
        map.put("U_PW",U_pw);
    }

    // 동일 패키지 또는 하위 클래스에서의 서버가 요청하는 Map<K,V>형식의 parameter값을 받아 결과를 리턴하는 함수
    // POST방식이며 get방식도 있다
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
