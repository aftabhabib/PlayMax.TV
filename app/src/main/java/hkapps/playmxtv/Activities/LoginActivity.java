package hkapps.playmxtv.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Response;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import hkapps.playmxtv.Model.Usuario;
import hkapps.playmxtv.R;
import hkapps.playmxtv.Services.PlayMaxAPI;
import hkapps.playmxtv.Services.Requester;

/**
 * Created by hkfuertes on 17/05/2017.
 */

public class LoginActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            Requester.request(this, PlayMaxAPI.getInstance().requestLogin("usuario","clave"),new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    try {
                        Usuario user = Usuario.fromXML(response);

                        if(user != null) {
                            Intent main = new Intent(LoginActivity.this, MainActivity.class);
                            main.putExtra("user", user);
                            //main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(main);
                            finish();
                        }


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
