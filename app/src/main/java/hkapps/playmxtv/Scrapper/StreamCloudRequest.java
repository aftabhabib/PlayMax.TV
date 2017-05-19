package hkapps.playmxtv.Scrapper;


import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hkapps.playmxtv.Model.Enlace;
import hkapps.playmxtv.Services.PlayMaxAPI;
import hkapps.playmxtv.Services.Requester;
import hkapps.playmxtv.Services.Requester.Requestable;

/**
 * Created by hkfuertes on 19/05/2017.
 */

public class StreamCloudRequest extends StringRequest {


    private static final String TAG = "STREAMCLOUD";
    private Map<String, String> params;

    public StreamCloudRequest(int method, String url, Response.Listener<String> listener) {
        super(method, url, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String>  params = new HashMap<String, String>();
        params.put("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.8.1.14) Gecko/20080404 Firefox/2.0.0.14");

        return params;
    }

    protected void setParams(Map<String, String> params){
        this.params = params;
    }

    @Override
    protected Map<String,String> getParams(){
        return params;
    }


    public static void getDirectUrl(Context ctx, final String url, final ScrapperListener listener){
        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(ctx);

        StreamCloudRequest scr = new StreamCloudRequest(Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Map<String,String> params = extractParams(response);
                    params.put("op", "download2");
                    Log.d("STREAMCLOUD", params.toString());

                    StreamCloudRequest scr2  = new StreamCloudRequest(Method.POST, url, new Response.Listener<String>(){

                        @Override
                        public void onResponse(String response) {
                            //Log.d("STREAMCLOUD", response);
                            Pattern p = Pattern.compile("file: \\\"(.*)\\/video\\.mp4\\\",");
                            Matcher m = p.matcher(response);
                            if(m.find()){
                                String direct_link = m.group(1) + "/video.mp4";
                                listener.onDirectUrlObtained(direct_link);
                                Log.d(TAG, direct_link);
                            } else {
                                Log.d(TAG, "file-pattern not found! '");
                            }
                        }
                    });

                    scr2.setParams(params);

                    queue.add(scr2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Add the request to the RequestQueue.
        queue.add(scr);
    }

    private static Map<String,String> extractParams(String response) throws XmlPullParserException, IOException {
        Document doc = Jsoup.parse(response);

        HashMap<String, String> params = new HashMap<String, String>();

        Elements inputs = doc.getElementsByTag("input");
        for(Element input : inputs){
            if(input.attr("type").equals("hidden")){
                params.put(input.attr("name"), input.attr("value"));
            }
        }
        return params;
    }

}
