package hkapps.playmxtv.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import hkapps.playmxtv.Model.Usuario;
import hkapps.playmxtv.R;
import hkapps.playmxtv.Services.Requester;
import hkapps.playmxtv.Services.UsuarioHelper;

/**
 * Created by hkfuertes on 17/05/2017.
 */

public class LoginActivity extends Activity {
    UsuarioHelper uhelper = new UsuarioHelper();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Requester.request(this,uhelper.getRequestForLogin("username","password"),new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    try {
                        Usuario user = uhelper.getUsuario(response);

                        Intent main = new Intent(LoginActivity.this, MainActivity.class);
                        main.putExtra("user",user);
                        startActivity(main);


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
