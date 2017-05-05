/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package hkapps.playmxtv.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import hkapps.playmxtv.Model.FichaResumida;
import hkapps.playmxtv.Model.Usuario;
import hkapps.playmxtv.R;
import hkapps.playmxtv.Services.FichaResumidaHelper;
import hkapps.playmxtv.Services.UsuarioHelper;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String text = "<Data>\n" +
                "<Capitulos>\n" +
                "<Slope>\n" +
                "<Ficha>\n" +
                "<Title><![CDATA[Better Call Saul]]></Title>\n" +
                "<Poster>https://playmax.mx/caratula/400/72/35615</Poster>\n" +
                "<Id>35615</Id>\n" +
                "<IdCapitulo>169468</IdCapitulo>\n" +
                "<Capitulo>1X04</Capitulo>\n" +
                "<Rating>9.0</Rating>\n" +
                "<YourRating>8</YourRating>\n" +
                "</Ficha>\n" +
                "...\n" +
                "</Slope>\n" +
                "</Capitulos>\n" +
                "<Series>\n" +
                "<Following>\n" +
                "<Ficha>\n" +
                "<Title><![CDATA[Better Call Saul]]></Title>\n" +
                "<Poster>https://playmax.mx/caratula/400/72/35615</Poster>\n" +
                "<Id>35615</Id>\n" +
                "<Rating>9.0</Rating>\n" +
                "<YourRating>8</YourRating>\n" +
                "</Ficha>\n" +
                "<Folder>\n" +
                "<Title><![CDATA[Thriller / Drama]]></Title>\n" +
                "<Fichas>\n" +
                "<Ficha>\n" +
                "<Title><![CDATA[The Wire (Bajo escucha)]]></Title>\n" +
                "<Poster>https://playmax.mx/caratula/400/1/45</Poster>\n" +
                "<Id>45</Id>\n" +
                "<Rating>9.2</Rating>\n" +
                "<YourRating>10</YourRating>\n" +
                "</Ficha>\n" +
                "...\n" +
                "</Fichas>\n" +
                "</Folder>\n" +
                "...\n" +
                "</Following>\n" +
                "<Slope>\n" +
                "<Ficha>\n" +
                "<Title><![CDATA[Agent Carter]]></Title>\n" +
                "<Poster>https://playmax.mx/caratula/400/72/35725</Poster>\n" +
                "<Id>35725</Id>\n" +
                "<Rating>8.2</Rating>\n" +
                "<YourRating></YourRating>\n" +
                "</Ficha>\n" +
                "...\n" +
                "</Slope>\n" +
                "<Favorite>\n" +
                "<Ficha>\n" +
                "<Title><![CDATA[Sin identidad]]></Title>\n" +
                "<Poster>https://playmax.mx/caratula/400/1/259</Poster>\n" +
                "<Id>259</Id>\n" +
                "<Rating>9.0</Rating>\n" +
                "<YourRating></YourRating>\n" +
                "</Ficha>\n" +
                "...\n" +
                "</Favorite>\n" +
                "<View>\n" +
                "<Ficha>\n" +
                "<Title><![CDATA[Wayward Pines]]></Title>\n" +
                "<Poster>https://playmax.mx/caratula/400/73/36094</Poster>\n" +
                "<Id>36094</Id>\n" +
                "<Rating>8.5</Rating>\n" +
                "<YourRating></YourRating>\n" +
                "</Ficha>\n" +
                "...\n" +
                "</View>\n" +
                "</Series>\n" +
                "<Films>\n" +
                "<Slope>\n" +
                "<Ficha>\n" +
                "<Title><![CDATA[Piratas del Caribe 5]]></Title>\n" +
                "<Poster>https://playmax.mx/caratula/400/72/35719</Poster>\n" +
                "<Id>35719</Id>\n" +
                "<Rating>7.0</Rating>\n" +
                "<YourRating></YourRating>\n" +
                "</Ficha>\n" +
                "...\n" +
                "</Slope>\n" +
                "<Favorite>\n" +
                "<Ficha>\n" +
                "<Title><![CDATA[Brick Mansions (La fortaleza)]]></Title>\n" +
                "<Poster>https://playmax.mx/caratula/400/2/579</Poster>\n" +
                "<Id>579</Id>\n" +
                "<Rating>6.8</Rating>\n" +
                "<YourRating></YourRating>\n" +
                "</Ficha>\n" +
                "...\n" +
                "</Favorite>\n" +
                "<View>\n" +
                "<Ficha>\n" +
                "<Title><![CDATA[Cincuenta sombras de Grey]]></Title>\n" +
                "<Poster>https://playmax.mx/caratula/400/1/134</Poster>\n" +
                "<Id>134</Id>\n" +
                "<Rating>6.1</Rating>\n" +
                "<YourRating>7</YourRating>\n" +
                "</Ficha>\n" +
                "...\n" +
                "</View>\n" +
                "</Films>\n" +
                "<Documentary>\n" +
                "<Following>\n" +
                "<Ficha>\n" +
                "<Title><![CDATA[Making a Murderer]]></Title>\n" +
                "<Poster>https://playmax.mx/caratula/400/74/36792</Poster>\n" +
                "<Id>36792</Id>\n" +
                "<Rating>10</Rating>\n" +
                "<YourRating></YourRating>\n" +
                "</Ficha>\n" +
                "...\n" +
                "</Following>\n" +
                "<Slope>\n" +
                "<Ficha>\n" +
                "<Title><![CDATA[Galapagos 3D]]></Title>\n" +
                "<Poster>https://playmax.mx/caratula/400/3/1475</Poster>\n" +
                "<Id>1475</Id>\n" +
                "<Rating>0.0</Rating>\n" +
                "<YourRating></YourRating>\n" +
                "</Ficha>\n" +
                "...\n" +
                "</Slope>\n" +
                "<Favorite>\n" +
                "<Ficha>\n" +
                "<Title><![CDATA[Cosmos: A Space-Time Odyssey]]></Title>\n" +
                "<Poster>https://playmax.mx/caratula/400/1/172</Poster>\n" +
                "<Id>172</Id>\n" +
                "<Rating>9.3</Rating>\n" +
                "<YourRating>10</YourRating>\n" +
                "</Ficha>\n" +
                "...\n" +
                "</Favorite>\n" +
                "<View>\n" +
                "<Ficha>\n" +
                "<Title><![CDATA[The Age of Stupid]]></Title>\n" +
                "<Poster>https://playmax.mx/caratula/400/22/10514</Poster>\n" +
                "<Id>10514</Id>\n" +
                "<Rating>9.0</Rating>\n" +
                "<YourRating>9</YourRating>\n" +
                "</Ficha>\n" +
                "...\n" +
                "</View>\n" +
                "</Documentary>\n" +
                "</Data>";
        FichaResumidaHelper frhelper = new FichaResumidaHelper();

        try {
            List<FichaResumida> fichas = frhelper.generateMultipleEntity(text);
            Log.d("TEST", fichas.toString());
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
