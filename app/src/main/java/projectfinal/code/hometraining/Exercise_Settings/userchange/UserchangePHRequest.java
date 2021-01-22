package projectfinal.code.hometraining.Exercise_Settings.userchange;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UserchangePHRequest extends StringRequest {
    //php파일연동
    final static private String URL="http://hometraining.dothome.co.kr/UserchangePH.php";

    private Map<String,String> map;

    public UserchangePHRequest(int U_phone, String U_id, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("U_Phone",U_phone+"");
        map.put("U_iD",U_id);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
