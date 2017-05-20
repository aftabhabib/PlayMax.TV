package hkapps.playmxtv.Services;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by hkfuertes on 20/05/2017.
 */

public class LoginServer extends NanoHTTPD {
    private final Listener listener;
    Context mContext;

    public LoginServer(Context mContext, int port, LoginServer.Listener listener) {
        super(port);
        this.mContext = mContext;
        this.listener = listener;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg="ERROR";
        try {
            session.parseBody(new HashMap<String, String>());
            Map<String, String> parms = session.getParms();
            if (parms.get("username") == null && parms.get("password") == null) {
                msg = LoadPage("login.html");
            } else {
                listener.onResponse(parms.get("username"),parms.get("password"));
                msg = LoadPage("done.html");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newFixedLengthResponse( msg );
    }

    public String LoadPage(String inFile) {
        String tContents = "";

        try {
            InputStream stream = mContext.getAssets().open(inFile);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (IOException e) {
            // Handle exceptions here
        }

        return tContents;

    }

    public interface Listener {
        void onResponse(String username, String password);
    }
}
