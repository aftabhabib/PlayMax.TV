package hkapps.playmxtv.Services;

import android.util.Xml;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hkfuertes on 24/04/2017.
 */

public abstract class Helper {

    XmlPullParser parser = Xml.newPullParser();
    protected String ns = null;

    public Helper(){};

    public void setString(String response) throws XmlPullParserException, IOException {
        try {
            parser.setInput( new StringReader( response ) );
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}
