package emad.athena.VolleyUtils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {
    private static MySingleton mInsance;
    private RequestQueue requestQueue;
    private static Context context;

    private  MySingleton(Context context){
        MySingleton.context = context;
        requestQueue = getRequestQueue();
    }
    public RequestQueue getRequestQueue(){
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized MySingleton getmInsance(Context context){

        if (mInsance == null){
            mInsance = new MySingleton(context);
        }
        return mInsance;
    }

    public<T> void addToRequestQueue(Request<T> request){
        requestQueue.add(request);
    }

}
