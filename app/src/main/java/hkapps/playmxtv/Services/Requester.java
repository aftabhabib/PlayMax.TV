package hkapps.playmxtv.Services;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hkfuertes on 25/04/2017.
 */

public class Requester {

    public static void request(Context context, Requestable requestable, Response.Listener<String> listener){

        if (listener == null) {
            listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // nothing to be done
                }
            };
        }

        switch (requestable.getMethod()){
            case Request.Method.GET:
                GET_request(context,requestable,listener);
                break;
            case Request.Method.POST:
                POST_request(context,requestable,listener);
                break;
        }
    }

    public static void request(Context context, Requestable requestable){
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // nothing to be done
            }
        };

        switch (requestable.getMethod()){
            case Request.Method.GET:
                GET_request(context,requestable,listener);
                break;
            case Request.Method.POST:
                POST_request(context,requestable,listener);
                break;
        }
    }
    private static void GET_request(Context context, Requestable requestable, Response.Listener<String> listener){
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

    private static void POST_request(Context context, final Requestable requestable, Response.Listener<String> listener){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(requestable.getMethod(), requestable.getUrl(), listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Requester",error.getLocalizedMessage());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                return requestable.getBody();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public interface Requestable{
        String getUrl();
        int getMethod();
        Map<String, String> getBody();
    }
}


