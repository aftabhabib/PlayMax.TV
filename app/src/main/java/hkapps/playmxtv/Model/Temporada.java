package hkapps.playmxtv.Model;

import android.graphics.Paint;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hkfuertes on 20/05/2017.
 */

public class Temporada extends ArrayList<Capitulo> implements Comparable<Temporada> {
    public int num_temp;

    public Temporada(int temporada) {
        this.num_temp = temporada;
    }

    public static List<Temporada> listFromCapitulos(List<Capitulo> capitulos){
        HashMap<Integer, Temporada> retarr = new HashMap<Integer, Temporada>();

        for(Capitulo capi : capitulos){
            if (!retarr.containsKey(capi.getTemporada())){
                retarr.put(capi.getTemporada(),new Temporada(capi.getTemporada()));
            }
            retarr.get(capi.getTemporada()).add(capi);
        }

        List<Temporada> retVal = new ArrayList<Temporada>();
        for(Temporada temp : retarr.values()){
            Collections.sort(temp);
            retVal.add(temp);
        }

        Collections.sort(retVal);

        return retVal;
    }

    //Suponemos que no vamos a mezclar capitulos
    public boolean equals(Object object){
        if(object instanceof Temporada){
            return ((Temporada) object).num_temp == num_temp;
        }else if(object instanceof Integer){
            return (int) object == num_temp;
        }else{
            return false;
        }
    }

    @Override
    public int compareTo(@NonNull Temporada o) {
        return Integer.compare(num_temp, o.num_temp);
    }
}
