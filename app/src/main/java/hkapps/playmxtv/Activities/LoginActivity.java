package hkapps.playmxtv.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import hkapps.playmxtv.Model.Usuario;
import hkapps.playmxtv.R;
import hkapps.playmxtv.Services.LoginServer;
import hkapps.playmxtv.Services.PlayMaxAPI;
import hkapps.playmxtv.Services.Requester;


/**
 * Created by hkfuertes on 17/05/2017.
 */

public class LoginActivity extends Activity implements LoginServer.Listener {

    private static final String LOG_TAG = "LOGIN";
    public static final String LOGIN_CREDS = "CREDENCIALES_LOGIN";
    public static final String USERNAME_TAG = "USERNAME_TAG";
    public static final String PASSWORD_TAG = "PASSWORD_TAG";
    public static final String SID_TAG = "SID_TAG";
    private LoginServer loginServer;

    private String username;
    private String password;
    private SharedPreferences prefs;

    TextView ip_address;

    private static final int PORT = 8080;
    private TextView status;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        status = (TextView) findViewById(R.id.login_status);
        status.setText(R.string.login_getting_settings);

        ip_address = (TextView) findViewById(R.id.login_ip_address);
        ip_address.setText("http://"+getLocalIpAddress()+":"+PORT);

        prefs = this.getSharedPreferences(LOGIN_CREDS, Context.MODE_PRIVATE);
        username = prefs.getString(USERNAME_TAG,null);
        password = prefs.getString(PASSWORD_TAG,null);

        if(username == null || password == null){
            status.setText(R.string.login_settings_not_found_starting);
            //Servidor
            loginServer = new LoginServer(this, PORT, this);

            Log.d(LOG_TAG, getLocalIpAddress());
            try {
                loginServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{

            loginUser(username,password);
        }



    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&&inetAddress instanceof Inet4Address) {
                        String ipAddress=inetAddress.getHostAddress().toString();
                        Log.e("IP address",""+ipAddress);
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }

    private void loginUser(String username, String password){
        status.setText(R.string.login_request);
        try {
            Requester.request(this, PlayMaxAPI.getInstance().requestLogin(username,password),new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    try {
                        Usuario user = Usuario.fromXML(response);

                        if(user != null) {

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(SID_TAG,user.getSid());
                            editor.apply();

                            Intent main = new Intent(LoginActivity.this, MainActivity.class);
                            main.putExtra("user", user);
                            //main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(main);
                            finish();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        if(loginServer != null)
                            loginServer = new LoginServer(LoginActivity.this, 8080, LoginActivity.this);
                        try {
                            loginServer.start();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loginServer != null && loginServer.isAlive()) loginServer.stop();
    }

    @Override
    public void onResponse(String username, String password) {

        status.setText(R.string.login_received_from_server);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USERNAME_TAG,username);
        editor.putString(PASSWORD_TAG,password);
        editor.apply();

        loginUser(username,password);
    }
}
