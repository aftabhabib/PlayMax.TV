package hkapps.playmxtv.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hkfuertes on 20/05/2017.
 */

public class Pelicula extends Ficha implements Serializable {

    private List<Enlace> enlaces;

    public Pelicula(String id, String type, String title, String poster, String rating, String your_rating, boolean isSerie) {
        super(id, type, title, poster, rating, your_rating, isSerie);
    }

    public static Pelicula fromFicha(Ficha f){
        return new Pelicula(f.id, f.type, f.title, f.poster, f.rating, f.your_rating, f.isSerie);
    }

    public void setEnlaces(List<Enlace> enlaces){
        this.enlaces = enlaces;
    }
}
