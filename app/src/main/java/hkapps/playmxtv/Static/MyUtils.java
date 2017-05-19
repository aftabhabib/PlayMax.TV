package hkapps.playmxtv.Static;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by hkfuertes on 19/05/2017.
 */

public class MyUtils {
    public static void launchMXP(Activity act, String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri videoUri = Uri.parse(url);
        //intent.setDataAndType( videoUri, "application/x-mpegURL" );
        intent.setDataAndType( videoUri, "video/*" );
        intent.setPackage( "com.mxtech.videoplayer.ad" );
        act.startActivity( intent );
    }
}
