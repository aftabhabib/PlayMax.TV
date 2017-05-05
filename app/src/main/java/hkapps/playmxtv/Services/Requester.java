package hkapps.playmxtv.Services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by hkfuertes on 25/04/2017.
 */

public class Requester {

    public static void request(Context context, Requestable requestable, Response.Listener<String> listener){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(requestable.getMethod(), requestable.getUrl(), listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Requester",error.getLocalizedMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    interface Requestable{
        String getUrl();
        int getMethod();
        JSONObject getBody() throws JSONException;
    }
}


