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
 * Created by hkfuertes on 19/05/2017.
 */

public class Enlace implements Serializable{
    /*
    <Item>
        <Id>642439</Id>
        <Host>streamin</Host>
        <Lang>Castellano</Lang>
        <Subtitles>Sin subtítulos</Subtitles>
        <Url>
            <Item>http://streamin.to/9bza8jv5yxb1</Item>
            <Item>http://streamin.to/sdza8jvas5yx</Item>
        </Url>
        <Quality>720p HD</Quality>
        <QualityA>Rip</QualityA>
        <Version>V. Extendida</Version>
        <Rating>0</Rating>
        <Likes>0</Likes>
        <Dislikes>0</Dislikes>
        <YourLike>Unrated</YourLike>
        <added_name>Sergiojamon</added_name>
        <added_id>8009</added_id>
        <AddedAvatar>https://playmax.mx/download/file.php?avatar=8009_1368759049.jpg</AddedAvatar>
    </Item>
     */

    String host;
    String lang,sub;
    String[] urls;
    String quality,qname;
    String version;

    public Enlace(String host, String lang, String sub, String[] urls, String quality, String qname, String version){
        this.host = host;
        this.lang = lang;
        this.sub = sub;
        this.urls = urls;
        this.quality = quality;
        this.qname = qname;
        this.version = version;
    }

    public static Enlace fromXML(String response) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput( new StringReader( response ) );

        String CURRENT_TAG="";
        String CURRENT_TEXT="";
        String host = null, lang=null, sub=null, url_chunk=null, quality = null, qname=null, version=null;
        String messageError=null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                CURRENT_TAG = parser.getName();
            } else if(eventType == XmlPullParser.END_TAG) {
                if(parser.getName().equals(CURRENT_TAG)){
                    switch (CURRENT_TAG){
                        case PlayMaxAPI.HOST_TAG:
                            host = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.LANG_TAG:
                            lang = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.SUB_TAG:
                            sub = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.URL_TAG:
                            url_chunk = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.QUALITY_TAG:
                            quality = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.QNAME_TAG:
                            qname = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.VERSION_TAG:
                            version = CURRENT_TEXT;
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
        if(url_chunk == null) throw new IOException("Missing url");

        String[] urls = url_chunk.replace("<"+PlayMaxAPI.ITEM_TAG+">","").split("</"+PlayMaxAPI.ITEM_TAG+">");

        return new Enlace(host, lang, sub, urls, quality,qname,version);
    }

    public static List<Enlace> listFromXML(String response) throws XmlPullParserException, IOException {
        //Nos quedamos solo con los online
        String online = response.split("<"+PlayMaxAPI.ONLINE_TAG+">")[1];
        String enlaces_raw[] = online.split("<"+PlayMaxAPI.ITEM_TAG+">");
        //Tenemos las fichas troceadas, si encontramos la etiqueta id, es una ficha, y sino no.
        List<Enlace> enlaces = new ArrayList<Enlace>();
        for(String enlaces_raw_item : enlaces_raw){
            if(enlaces_raw_item.contains(PlayMaxAPI.ID_TAG)){
                Enlace current = fromXML(enlaces_raw_item.split("</"+PlayMaxAPI.ITEM_TAG+">")[0]);
                if(current.host.equals("streamcloud")) enlaces.add(current);
            }
        }
        return enlaces;
    }

    public String toString(){
        return this.urls[0];
    }

}
