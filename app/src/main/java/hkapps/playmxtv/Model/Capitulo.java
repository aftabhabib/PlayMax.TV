package hkapps.playmxtv.Model;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import hkapps.playmxtv.Services.PlayMaxAPI;

/**
 * Created by hkfuertes on 20/05/2017.
 */

public class Capitulo implements Serializable,Comparable<Capitulo> {
    /*
    <Episodes>
        <Season_0>
            <Item>
                <Id>230939</Id>
                <Season>0</Season>
                <Episode>01</Episode>
                <EpisodeName>
                    <![CDATA[Sense8: La creación el mundo]]>
                </EpisodeName>
                <EpisodeDate>Sab, 01 Ago 2015</EpisodeDate>
                <EpisodeDateUnix>1438412400</EpisodeDateUnix>
                <EpisodeViewed>no</EpisodeViewed>
                <EpisodeRating>0</EpisodeRating>
                <EpisodeUserRating></EpisodeUserRating>
            </Item>
        </Season_0>
    </Episodes>
     */
    String id_capitulo;
    int temporada;
    int num_capitulo;
    String nombre;
    String dateunix;
    String viewed;

    public Capitulo(String id_capitulo, String temporada, String num_capitulo, String nombre, String dateunix, String viewed){
        this.id_capitulo = id_capitulo;
        this.temporada = Integer.parseInt(temporada);
        this.num_capitulo = Integer.parseInt(num_capitulo);
        this.nombre = nombre;
        this.dateunix = dateunix;
        this.viewed = viewed;
    }

    public static Capitulo fromXML(String response) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput( new StringReader( response ) );

        String CURRENT_TAG="";
        String CURRENT_TEXT="";
        String id_capitulo = null, temporada=null, num_capitulo=null, nombre=null,dateunix=null, viewed=null;
        String messageError=null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                CURRENT_TAG = parser.getName();
            } else if(eventType == XmlPullParser.END_TAG) {
                if(parser.getName().equals(CURRENT_TAG)){
                    switch (CURRENT_TAG){
                        case PlayMaxAPI.ID_TAG:
                            id_capitulo = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.SEASON_TAG:
                            temporada = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.EPISODE_NUM_TAG:
                            num_capitulo = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.EPISODE_NAME_TAG:
                            nombre = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.EPISODE_DATEUNIX_TAG:
                            dateunix = CURRENT_TEXT;
                            break;
                        case PlayMaxAPI.EPISODE_VIEWED_TAG:
                            viewed = CURRENT_TEXT;
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
        if(id_capitulo == null) throw new IOException("Missing EPISODE_ID");

        return new Capitulo(id_capitulo, temporada,num_capitulo,nombre, dateunix, viewed);
    }

    public static List<Capitulo> listFromXML(String response) throws XmlPullParserException, IOException {
        //Nos quedamos solo con los online
        String episodes = response.split("<"+PlayMaxAPI.EPISODES_TAG+">")[2];
        //Log.d("CAPITULO", episodes);
        String enlaces_raw[] = episodes.split("<"+PlayMaxAPI.ITEM_TAG+">");
        //Tenemos las fichas troceadas, si encontramos la etiqueta id, es una ficha, y sino no.
        List<Capitulo> enlaces = new ArrayList<Capitulo>();
        for(String enlaces_raw_item : enlaces_raw){
            if(enlaces_raw_item.contains(PlayMaxAPI.ID_TAG)){
                Capitulo current = fromXML(enlaces_raw_item.split("</"+PlayMaxAPI.ITEM_TAG+">")[0]);
                enlaces.add(current);
            }
        }
        return enlaces;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIdCapitulo() {
        return id_capitulo;
    }

    public String toString(){
        return num_capitulo+" "+nombre;
    }

    public int getNum() {
        return num_capitulo;
    }

    public static List<Capitulo> filterByTemporada(List<Capitulo> lista, int temp) {
        List<Capitulo> filtrado = new ArrayList<Capitulo>();
        for (Capitulo current : lista) {
            if (current.getTemporada() == temp){
                Log.d("CAPITULO", current.getTemporada() + " - "+ temp);
                filtrado.add(current);
            }
        }
        return filtrado;
    }

    public int getTemporada() {
        return temporada;
    }

    public static Capitulo random(List<Capitulo> capitulos){
        Random r = new Random(System.currentTimeMillis());
        int index = r.nextInt(capitulos.size()+ 1);
        return capitulos.get(index);
    }


    @Override
    public int compareTo(@NonNull Capitulo o) {
        return Integer.compare(num_capitulo, o.num_capitulo);
    }
}
