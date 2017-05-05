package hkapps.playmxtv.Services;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import hkapps.playmxtv.Model.Usuario;

/**
 * Created by hkfuertes on 24/04/2017.
 */

public class UsuarioHelper extends Helper<Usuario> implements Requester.Requestable {
    private final String SID_TAG = "Sid";
    private final String ID_TAG = "Id";
    private final String NAME_TAG = "Name";
    private final String AVATAR_TAG = "Avatar";
    private String username,password;

    public UsuarioHelper(String username, String password){
        this.username = username;
        this.password = password;
    }

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

    @Override
    protected Usuario generateEntity(XmlPullParser xpp) throws XmlPullParserException, IOException{
        String CURRENT_TAG="";
        String CURRENT_TEXT="";
        String sid = null, id=null, name=null, avatar=null;
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                CURRENT_TAG = xpp.getName();
            } else if(eventType == XmlPullParser.END_TAG) {
                if(xpp.getName().equals(CURRENT_TAG)){
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
                    }
                }
            } else if(eventType == XmlPullParser.TEXT) {
                CURRENT_TEXT = xpp.getText();
            }
            eventType = xpp.next();
        }
        if(sid == null) throw new IOException("Missing Sid");
        return new Usuario(id,name,sid,avatar);
    }

    @Override
    public String getUrl() {
        return "https://playmax.mx/ucp.php?mode=login&apikey="+"añldkfjañslkfj";
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public JSONObject getBody() throws JSONException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username);
        jsonBody.put("password", password);
        return jsonBody;
    }
}
