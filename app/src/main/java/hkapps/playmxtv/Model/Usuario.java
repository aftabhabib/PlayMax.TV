package hkapps.playmxtv.Model;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;

import hkapps.playmxtv.Services.PlayMaxAPI;

/**
 * Created by hkfuertes on 22/04/2017.
 */

public class Usuario implements Serializable {
    private String Name;
    private String Sid;
    private String Avatar;
    private String Id;

    private String username;
    private String password;

    public Usuario(String id, String name, String sid, String avatar){
        this.Id = id;
        this.Name = name;
        this.Avatar = avatar;
        this.Sid = sid;
    }

    public String getName ()
    {
        return Name;
    }
    public void setName (String Name)
    {
        this.Name = Name;
    }

    public String getSid ()
    {
        return Sid;
    }
    public void setSid (String Sid)
    {
        this.Sid = Sid;
    }

    public String getAvatar ()
    {
        return Avatar;
    }
    public void setAvatar (String Avatar)
    {
        this.Avatar = Avatar;
    }

    public String getId ()
    {
        return Id;
    }
    public void setId (String Id)
    {
        this.Id = Id;
    }

    @Override
    public String toString()
    {
        return "Usuario [Name = "+Name+", Sid = "+Sid+", Avatar = "+Avatar+", Id = "+Id+"]";
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
    public static Usuario fromXML(String response) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput( new StringReader( response ) );

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
                        case PlayMaxAPI.SID_TAG:
                            sid = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.ID_TAG:
                            id = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.AVATAR_TAG:
                            avatar = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.NAME_TAG:
                            name = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.ERROR_MESSAGE_TAG:
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

