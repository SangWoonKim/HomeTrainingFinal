package projectfinal.code.hometraining.Exercise_Settings.userchange;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UserchangeADRequest extends StringRequest {

    //php파일연동
    final static private String URL="http://hometraining.dothome.co.kr/UserchangeAD.php";

    private Map<String,String> map;

    public UserchangeADRequest(String U_address, String U_id, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL, listener, errorListener);

        map = new HashMap<>();
        map.put("U_Address",U_address);
        map.put("U_Id",U_id);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
