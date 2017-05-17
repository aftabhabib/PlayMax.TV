package hkapps.playmxtv.Services;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import hkapps.playmxtv.Model.Usuario;
import hkapps.playmxtv.Static.Constants;

/**
 * Created by hkfuertes on 24/04/2017.
 */

public class UsuarioHelper extends Helper {
    private final String SID_TAG = "Sid";
    private final String ID_TAG = "Id";
    private final String NAME_TAG = "Name";
    private final String AVATAR_TAG = "Avatar";

    private final String ERROR_TAG = "Error";
    private final String ERROR_MESSAGE_TAG = "MessageError";

    /*
    <Data>
        <UserInfo>
            <Sid>a55bf54e99x556vb81db47c47c5cd1a16</Sid>
            <Id>2</Id>
            <Name>PlayMax</Name>
            <Avatar>https://playmax.mx/download/file.php?avatar=2_1290380171.jpg</Avatar>
        </UserInfo>
    </Data>
     */

    public Requester.Requestable getRequestForLogin(final String username, final String password){
        return new Requester.Requestable() {
            @Override
            public String getUrl() {
                return "https://playmax.mx/ucp.php?mode=login&apikey="+ Constants.APIKEY;
            }

            @Override
            public int getMethod() {
                return Request.Method.POST;
            }

            @Override
            public Map<String, String> getBody() {

                Map<String,String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };
    }

    public Requester.Requestable getRequestForFichas(final Usuario user){
        return new Requester.Requestable() {
            @Override
            public String getUrl() {
                return "https://playmax.mx/tusfichas.php?apikey="+ Constants.APIKEY+"&sid="+user.getSid();
            }

            @Override
            public int getMethod() {
                return Request.Method.GET;
            }

            @Override
            public Map<String,String> getBody() {
                return null;
            }
        };
    }

    public Usuario getUsuario(String response) throws XmlPullParserException, IOException{
        //We set the response xml string
        setString(response);

        String CURRENT_TAG="";
        String CURRENT_TEXT="";
        String sid = null, id=null, name=null, avatar=null;
        String messageError=null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                CURRENT_TAG = parser.getName();
            } else if(eventType == XmlPullParser.END_TAG) {
                if(parser.getName().equals(CURRENT_TAG)){
                    switch (CURRENT_TAG){
                        case SID_TAG:
                            sid = CURRENT_TEXT;
                            break;
                        case ID_TAG:
                            id = CURRENT_TEXT;
                            break;
                        case AVATAR_TAG:
                            avatar = CURRENT_TEXT;
                            break;
                        case NAME_TAG:
                            name = CURRENT_TEXT;
                            break;
                        case ERROR_MESSAGE_TAG:
                            messageError = CURRENT_TEXT;
                            break;
                    }
                }
            } else if(eventType == XmlPullParser.TEXT) {
                CURRENT_TEXT = parser.getText();
            }
            eventType = parser.next();
        }
        if(messageError != null) throw new IOException(messageError);
        if(sid == null) throw new IOException("Missing Sid");
        return new Usuario(id,name,sid,avatar);
    }
}
