package hkapps.playmxtv.Model;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import hkapps.playmxtv.Services.PlayMaxAPI;

/**
 * Created by hkfuertes on 20/05/2017.
 */

public class Trailer implements Serializable {

    /*
    <Trailers>
    <Item>
    <IdYoutube>_dK-MBJ7N2M</IdYoutube>
    <Title><![CDATA[Juego de Tronos: Sexta Temporada - Primer Teaser-Tráiler]]></Title>
    <Language>Teaser trailer en V.O</Language>
    </Item>
    ...
    </Trailers>
     */
    String id_youtube;
    String title;

    public Trailer (String id_youtube, String title){
        this.id_youtube = id_youtube;
        this.title = title;
    }


    public static Trailer fromXML(String response) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput( new StringReader( response ) );

        String CURRENT_TAG="";
        String CURRENT_TEXT="";
        String id_youtube = null, title=null;
        String messageError=null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                CURRENT_TAG = parser.getName();
            } else if(eventType == XmlPullParser.END_TAG) {
                if(parser.getName().equals(CURRENT_TAG)){
                    switch (CURRENT_TAG){
                        case PlayMaxAPI.ID_YOUTUBE_TAG:
                            id_youtube = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.TITLE_TAG:
                            title = CURRENT_TEXT;
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

        return new Trailer(id_youtube, title);
    }

    public static List<Trailer> listFromXML(String response) throws XmlPullParserException, IOException {
        String enlaces_raw[] = response.split("<"+PlayMaxAPI.ITEM_TAG+">");
        //Tenemos las fichas troceadas, si encontramos la etiqueta id, es una ficha, y sino no.
        List<Trailer> enlaces = new ArrayList<Trailer>();
        for(String enlaces_raw_item : enlaces_raw){
            if(enlaces_raw_item.contains(PlayMaxAPI.ID_TAG)){
                Trailer current = fromXML(enlaces_raw_item.split("</"+PlayMaxAPI.ITEM_TAG+">")[0]);
                enlaces.add(current);
            }
        }
        return enlaces;
    }

    public String getYTId() {
        return id_youtube;
    }
}
