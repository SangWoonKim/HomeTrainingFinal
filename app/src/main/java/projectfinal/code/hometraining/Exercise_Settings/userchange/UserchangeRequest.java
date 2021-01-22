package projectfinal.code.hometraining.Exercise_Settings.userchange;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UserchangeRequest extends StringRequest {

    //php파일 url지정
    final static private String URL="http://hometraining.dothome.co.kr/Userchange.php";

    private Map<String,String> map;
    public UserchangeRequest(String U_pw, String U_id, /*String U_adrress, String U_phone,*/ Response.Listener<String> listener) {
        super(Method.POST, URL, listener,null);

        map = new HashMap<>();
        map.put("U_pw",U_pw);
        map.put("U_id",U_id);
        /*if (U_adrress != null) {
            map.put("U_address", U_adrress);
        }

        if (U_phone != null) {
            map.put("U_phone", U_phone);
        }*/
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
