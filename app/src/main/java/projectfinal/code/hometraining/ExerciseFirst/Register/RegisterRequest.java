package projectfinal.code.hometraining.ExerciseFirst.Register;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    //php파일연동
    final static private String URL="http://hometraining.dothome.co.kr/Register.php";

    private Map<String,String> map;

    public RegisterRequest(String U_id, String U_pw, String U_name, int U_phone, String U_address, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("U_id",U_id);
        map.put("U_pw",U_pw);
        map.put("U_name",U_name);
        map.put("U_phone",U_phone+"");
        map.put("U_address",U_address);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
