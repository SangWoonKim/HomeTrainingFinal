package projectfinal.code.hometraining.Exercise_Settings.userchange;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

//requestQueue 설정

public class UserChangeQueue {
    // 정적 인스턴스 선언
    private static UserChangeQueue queueInstance;
    private static Context queuecontext;
    //Volley 서버 요청 큐
    private RequestQueue requestQueue;

    private UserChangeQueue(Context context){
        queuecontext=context;
        requestQueue = getRequestQueue();
    }
    //싱글턴 인스턴스 getter함수
    public static synchronized UserChangeQueue getInstance(Context context){
        //큐에 값이 없을 경우
        if (queueInstance == null){
            queueInstance = new UserChangeQueue(context);
        }
        return queueInstance;
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue == null){

         requestQueue = Volley.newRequestQueue(queuecontext.getApplicationContext());
        }

        return requestQueue;
    }
    public <T> void addToRequestQueue(Request<T> value){
        getRequestQueue().add(value);
    }

}
