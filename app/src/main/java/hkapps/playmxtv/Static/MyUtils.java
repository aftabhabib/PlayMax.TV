package hkapps.playmxtv.Static;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import hkapps.playmxtv.Adapters.EnlacesAdapter;
import hkapps.playmxtv.Adapters.TemporadaPresenter;
import hkapps.playmxtv.Model.Capitulo;
import hkapps.playmxtv.Model.Enlace;

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
    public static void launchYT(Activity act, String id){
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            try {
                act.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                act.startActivity(webIntent);
            }
    }

    public static void showLinkList(Context mContext, final List<Enlace> enlaces, final Enlace.EnlaceListener listener){
        Dialog dialog = new Dialog(mContext);
        final EnlacesAdapter enlacesAdapter = new EnlacesAdapter(enlaces);

        //Prepare ListView in dialog
        ListView dialog_ListView = new ListView(dialog.getContext());
        dialog_ListView.setAdapter(enlacesAdapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onEnlaceSelected(enlacesAdapter.getItem(position));
            }});

        dialog.setContentView(dialog_ListView);
        dialog.create();
        dialog.show();
    }
}
