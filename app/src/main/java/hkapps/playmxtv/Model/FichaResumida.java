package hkapps.playmxtv.Model;

import java.util.Objects;

/**
 * Created by hkfuertes on 24/04/2017.
 */

public class FichaResumida {
    /*
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

    String title;
    String poster;
    String id;
    String rating;
    String your_rating;

    //Pueden ser null --> indicara que es resumen de capitulo.
    String id_capitulo;
    String capitulo;

    public FichaResumida(String id,String title, String poster,  String rating, String your_rating){
        this.title = title;
        this.poster = poster;
        this.id = id;
        this.rating = rating;
        this.your_rating = your_rating;
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
        if(object instanceof FichaResumida){
            return ((FichaResumida) object).id.equals(id);
        }else return false;
    }
}
