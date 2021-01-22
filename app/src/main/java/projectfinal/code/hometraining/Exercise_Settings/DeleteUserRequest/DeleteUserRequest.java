package projectfinal.code.hometraining.Exercise_Settings.DeleteUserRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
/*회원나가기를 위한 클래스*/
public class DeleteUserRequest extends StringRequest {
    final static private String URL ="http://hometraining.dothome.co.kr/Userdelete.php";

    private Map<String,String> map;

    public DeleteUserRequest(String U_id, Response.Listener<String> listener) {
        super(Method.POST, URL, listener,null);

        map = new HashMap<>();

        map.put("U_did",U_id);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
