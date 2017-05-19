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
 * Created by hkfuertes on 24/04/2017.
 */

public class Ficha implements Serializable {
    /* Resumida
    <Ficha>
    <Title><![CDATA[Better Call Saul]]></Title>
        <Poster>https://playmax.mx/caratula/400/72/35615</Poster>
        <Id>35615</Id>
        <IdCapitulo>169468</IdCapitulo>
        <Capitulo>1X04</Capitulo>
        <Rating>9.0</Rating>
        <YourRating>8</YourRating>
    </Ficha>
     */

    /* Completa
<Data>
    <User>
        <Marked>view</Marked>
        <Rating></Rating>
    </User>
    <Info>
        <Title>
            <![CDATA[Hasta el último hombre]]>
        </Title>
        <Poster>https://playmax.mx/caratula/400/77/38189</Poster>
        <Cover>https://playmax.mx/cover/full/77/38189</Cover>
        <Rating>8.9</Rating>
        <IsSerie>0</IsSerie>
        <Type>Película</Type>
        <OriginalTitle>
            <![CDATA[Hacksaw Ridge]]>
        </OriginalTitle>
        <Year>2016</Year>
        <Duration>131</Duration>
        <Country>Estados Unidos</Country>
...
        <Sinopsis>
            <![CDATA[Narra la historia de Desmond Doss, un joven médico militar que participó en la sangrienta batalla de Okinawa, en el Pacífico durante la II Guerra Mundial, y se convirtió en el primer objetor de conciencia en la historia estadounidense en recibir la Medalla de Honor del Congreso. Doss quería servir a su país, pero desde pequeño se había hecho una promesa a sí mismo: no coger jamás ningún arma. ]]>
        </Sinopsis>
    </Info>
</Data>
     */

    String title;
    String poster;
    String id;
    String rating;
    String your_rating;
    String type;
    boolean isSerie;

    String cover;
    String original_title;
    String year;
    String duration;
    String country;
    String sinopsys;

    //Pueden ser null --> indicara que es resumen de capitulo.
    String id_capitulo;
    String capitulo;

    public Ficha(String id, String type, String title, String poster, String rating, String your_rating, boolean isSerie){
        this.title = title;
        this.poster = poster;
        this.id = id;
        this.rating = rating;
        this.your_rating = your_rating;
        this.type = type;
        this.isSerie = isSerie;
    }

    public void setIdCapitulo(String id_capitulo){
        this.id_capitulo = id_capitulo;
    }

    public void setCapitulo(String capitulo){
        this.capitulo = capitulo;
    }

    @Override
    public String toString() {
        return title;
    }

    public boolean equals(Object object){
        if(object instanceof Ficha){
            return ((Ficha) object).id.equals(id);
        }else return false;
    }

    public static Ficha fromXML(String response) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput( new StringReader( response ) );

        String CURRENT_TAG="";
        String CURRENT_TEXT="";
        String title = null, poster=null, id=null, rating=null, your_rating = null, type=null;
        String capitulo = null, id_capitulo=null;
        String messageError=null;
        boolean isSerie=false;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                CURRENT_TAG = parser.getName();
            } else if(eventType == XmlPullParser.END_TAG) {
                if(parser.getName().equals(CURRENT_TAG)){
                    switch (CURRENT_TAG){
                        case PlayMaxAPI.TITLE_TAG:
                            title = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.ID_TAG:
                            id = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.RATING_TAG:
                            rating = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.POSTER_TAG:
                            poster = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.YOUR_RATING_TAG:
                            your_rating = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.CAPITULO_NEW_TAG:
                        case PlayMaxAPI.CAPITULO_TAG:
                            capitulo = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.ID_CAPITULO_NEW_TAG:
                        case PlayMaxAPI.ID_CAPITULO_TAG:
                            id_capitulo = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.ERROR_MESSAGE_TAG:
                            messageError = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.TYPE_TAG:
                            type = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.IS_SERIE_TAG:
                            isSerie = !CURRENT_TEXT.equals("0");
                            break;
                    }
                }
            } else if(eventType == XmlPullParser.TEXT) {
                CURRENT_TEXT = parser.getText();
            }
            eventType = parser.next();
        }
        if(messageError != null) throw new IOException(messageError);
        if(id == null) throw new IOException("Missing id");
        Ficha fr = new Ficha( id,type, title, poster, rating, your_rating, isSerie);

        if(capitulo != null) fr.setCapitulo(capitulo);
        if(id_capitulo != null) fr.setIdCapitulo(id_capitulo);

        return fr;
    }

    /*
    String cover;
    String original_title;
    String year;
    String duration;
    String country;
    String sinopsys;
     */
    public void completeFromXML(String response) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput( new StringReader( response ) );

        String CURRENT_TAG="";
        String CURRENT_TEXT="";
        String messageError=null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                CURRENT_TAG = parser.getName();
            } else if(eventType == XmlPullParser.END_TAG) {
                if(parser.getName().equals(CURRENT_TAG)){
                    switch (CURRENT_TAG){
                        case PlayMaxAPI.ERROR_MESSAGE_TAG:
                            messageError = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.COVER_TAG:
                            cover = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.ORIGINAL_TITLE_TAG:
                            original_title = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.YEAR_TAG:
                            year = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.DURATION:
                            duration = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.COUNTRY_TAG:
                            country = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.SINOPSIS_TAG:
                            sinopsys = CURRENT_TEXT;
                            break;
                    }
                }
            } else if(eventType == XmlPullParser.TEXT) {
                CURRENT_TEXT = parser.getText();
            }
            eventType = parser.next();
        }
        if(messageError != null) throw new IOException(messageError);
    }

    public static List<Ficha> listFromXML(String response) throws XmlPullParserException, IOException {
        String ficha_raw[] = response.split("<"+PlayMaxAPI.FICHA_TAG+">");
        //Tenemos las fichas troceadas, si encontramos la etiqueta id, es una ficha, y sino no.
        List<Ficha> fichas = new ArrayList<Ficha>();
        for(String ficha_raw_item : ficha_raw){
            if(ficha_raw_item.contains(PlayMaxAPI.ID_TAG)){
                Ficha current = fromXML(ficha_raw_item.split("</"+PlayMaxAPI.FICHA_TAG+">")[0]);
                if(!fichas.contains(current)) fichas.add(current);
            }
        }
        return fichas;
    }

    public String getPoster() {
        return poster;
    }

    public String getTitle() {
        return title;
    }

    public String getLastEpisode() {
        if(capitulo != null) return capitulo;
        return null;
    }

    public boolean isSerie() {
        return isSerie;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getCover() {
        return cover;
    }

    public String getDuration() {
        return duration;
    }

    public String getSinopsis() {
        return sinopsys;
    }

    public String getIdCapitulo() {
        return id_capitulo;
    }
}
