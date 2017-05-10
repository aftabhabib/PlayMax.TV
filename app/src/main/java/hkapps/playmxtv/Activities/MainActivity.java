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

import com.android.volley.Response;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import hkapps.playmxtv.Model.FichaResumida;
import hkapps.playmxtv.Model.Usuario;
import hkapps.playmxtv.R;
import hkapps.playmxtv.Services.FichaResumidaHelper;
import hkapps.playmxtv.Services.Requester;
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


        FichaResumidaHelper frhelper = new FichaResumidaHelper();
        final UsuarioHelper uhelper = new UsuarioHelper();

        try {
            Requester.request(this,uhelper.getRequestForLogin("hkfuertes","gorilafeliz"),new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    try {
                        Usuario user = uhelper.getUsuario(response);
                        Log.d("REQ",user.toString());
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
