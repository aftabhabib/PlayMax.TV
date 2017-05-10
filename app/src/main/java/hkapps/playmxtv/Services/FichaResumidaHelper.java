package hkapps.playmxtv.Services;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hkapps.playmxtv.Model.FichaResumida;

/**
 * Created by hkfuertes on 24/04/2017.
 */

public class FichaResumidaHelper extends Helper {
    private final String FICHA_TAG = "Ficha";

    private final String TITLE_TAG = "Title";
    private final String POSTER_TAG = "Poster";
    private final String ID_TAG = "Id";
    private final String RATING_TAG = "Rating";
    private final String YOUR_RATING_TAG = "YourRating";

    private final String CAPITULO_TAG = "Capitulo";
    private final String ID_CAPITULO_TAG = "IdCapitulo";

    public FichaResumida getFichaResumida(String response) throws XmlPullParserException, IOException {
        //We set the response xml string
        setString(response);

        String CURRENT_TAG="";
        String CURRENT_TEXT="";
        String title = null, poster=null, id=null, rating=null, your_rating = null;
        String capitulo = null, id_capitulo=null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                CURRENT_TAG = parser.getName();
            } else if(eventType == XmlPullParser.END_TAG) {
                if(parser.getName().equals(CURRENT_TAG)){
                    switch (CURRENT_TAG){
                        case TITLE_TAG:
                            title = CURRENT_TEXT;
                            break;
                        case ID_TAG:
                            id = CURRENT_TEXT;
                            break;
                        case RATING_TAG:
                            rating = CURRENT_TEXT;
                            break;
                        case POSTER_TAG:
                            poster = CURRENT_TEXT;
                            break;
                        case YOUR_RATING_TAG:
                            your_rating = CURRENT_TEXT;
                            break;
                        case CAPITULO_TAG:
                            capitulo = CURRENT_TEXT;
                            break;
                        case ID_CAPITULO_TAG:
                            id_capitulo = CURRENT_TEXT;
                            break;
                    }
                }
            } else if(eventType == XmlPullParser.TEXT) {
                CURRENT_TEXT = parser.getText();
            }
            eventType = parser.next();
        }
        if(id == null) throw new IOException("Missing id");
        return new FichaResumida( id,title, poster, rating, your_rating);
    }

    public List<FichaResumida> generateMultipleEntity(String response) throws XmlPullParserException, IOException {
        String ficha_raw[] = response.split("<"+FICHA_TAG+">");
        //Tenemos las fichas troceadas, si encontramos la etiqueta id, es una ficha, y sino no.
        List<FichaResumida> fichas = new ArrayList<FichaResumida>();
        for(String ficha_raw_item : ficha_raw){
            if(ficha_raw_item.contains(ID_TAG)){
                FichaResumida current = getFichaResumida(ficha_raw_item.split("</"+FICHA_TAG+">")[0]);
                if(!fichas.contains(current)) fichas.add(current);
            }
        }
        return fichas;
    }

}
