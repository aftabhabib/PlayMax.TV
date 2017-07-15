package hkapps.playmxtv.Static;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;

import java.util.List;

import hkapps.playmxtv.Activities.MainActivity;
import hkapps.playmxtv.Activities.PlaybackOverlayActivity;
import hkapps.playmxtv.Adapters.EnlacesAdapter;
import hkapps.playmxtv.Adapters.FollowAdapter;
import hkapps.playmxtv.Adapters.TemporadaPresenter;
import hkapps.playmxtv.Fragments.PeliculaDetailsFragment;
import hkapps.playmxtv.Fragments.SerieDetailsFragment;
import hkapps.playmxtv.Model.Capitulo;
import hkapps.playmxtv.Model.Enlace;
import hkapps.playmxtv.Model.Ficha;
import hkapps.playmxtv.Model.Trailer;
import hkapps.playmxtv.Model.Usuario;
import hkapps.playmxtv.R;
import hkapps.playmxtv.Scrapper.ScrapperListener;
import hkapps.playmxtv.Scrapper.StreamCloudRequest;
import hkapps.playmxtv.Services.PlayMaxAPI;
import hkapps.playmxtv.Services.Requester;

/**
 * Created by hkfuertes on 19/05/2017.
 */

public class MyUtils {
    public static final String MXPLAYER_FREE = "com.mxtech.videoplayer.ad";
    public static final String MXPLAYER_PRO = "com.mxtech.videoplayer.pro";
    public static void launchMXP(Context act, String url){
        /*
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri videoUri = Uri.parse(url);
        //intent.setDataAndType( videoUri, "application/x-mpegURL" );
        intent.setDataAndType( videoUri, "video/*" );
        intent.setPackage( "com.mxtech.videoplayer.ad" );
        act.startActivity( intent );
        */

        Intent myIntent;
        PackageManager pm = act.getPackageManager();
        try {
            myIntent = pm.getLaunchIntentForPackage(MXPLAYER_FREE);
            if (null != myIntent){
                Uri videoUri = Uri.parse(url);
                myIntent.setDataAndType( videoUri, "video/*" );
                act.startActivity(myIntent);
            }else{
                try {
                    act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + MXPLAYER_FREE)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + MXPLAYER_FREE)));
                }
            }

        } catch (ActivityNotFoundException e) {
            Toast.makeText(act,"MXPLAYER or STORE not found!",Toast.LENGTH_LONG).show();
        }

    }
    public static void launchYT(Context act, String id){
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

        if(!enlaces.isEmpty()) {
            Dialog dialog = new Dialog(mContext);
            final EnlacesAdapter enlacesAdapter = new EnlacesAdapter(enlaces);

            //Prepare ListView in dialog
            ListView dialog_ListView = new ListView(dialog.getContext());
            dialog_ListView.setAdapter(enlacesAdapter);
            dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    listener.onEnlaceSelected(enlacesAdapter.getItem(position));
                }
            });

            dialog.setContentView(dialog_ListView);
            dialog.create();
            dialog.show();
        }else{
            Toast.makeText(mContext,"No hay enlaces disponibles!",Toast.LENGTH_LONG).show();
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void lanzarCapitulo(final Context activity, final Usuario mActiveUser, Ficha mSelectedShow, final Capitulo episode){
        lanzarEnlace(activity, mActiveUser, mSelectedShow, episode.getIdCapitulo());
    }

    public static void lanzarPelicula(final Context activity, final Usuario mActiveUser, Ficha mSelectedShow){
        lanzarEnlace(activity, mActiveUser, mSelectedShow, null);
    }

    public static void lanzarEnlace(final Context activity, final Usuario mActiveUser, Ficha mSelectedShow, final String episode){
        //Recuperar el primer enlace streamcloud de los que me den y lanzar MX Player.
        Requester.request(activity, PlayMaxAPI.getInstance().requestEnlaces(mActiveUser, mSelectedShow, episode),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Enlace> enlaces = Enlace.listFromXML(response);

                            MyUtils.showLinkList(activity, enlaces, new Enlace.EnlaceListener() {
                                @Override
                                public void onEnlaceSelected(Enlace selected) {
                                    StreamCloudRequest.getDirectUrl(activity, selected.getUrl(), new ScrapperListener() {
                                        @Override
                                        public void onDirectUrlObtained(final String direct_url) {
                                            //Marcar como visto
                                            Requester.request(activity, PlayMaxAPI.getInstance().requestMarkAsViewed(mActiveUser.getSid(), episode), new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    MyUtils.launchMXP(activity, direct_url);
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public static void lanzarTrailer(final Context ctx, Usuario mActiveUser, Ficha mSelectedMovie){
        //Recuperar el primer enlace streamcloud de los que me den y lanzar MX Player.
        Requester.request(ctx,
                PlayMaxAPI.getInstance().requestTrailers(mActiveUser, mSelectedMovie),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Trailer> enlaces = Trailer.listFromXML(response);
                            if(enlaces.size() > 0) {
                                MyUtils.launchYT(ctx, enlaces.get(0).getYTId());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public static int convertDpToPixel(Context ctx, int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static void showFollowList(Context mContext,final Ficha mShow, final Ficha.FollowSelection listener){
        final Dialog dialog = new Dialog(mContext);

        /*
        public static final int follow = 1;
        public static final int pending = 2;
        public static final int favorite = 3;
        public static final int viewed = 4;
         */

        //ORDER MATTERS
        String[] labels = {
                mContext.getResources().getString(R.string.follow_follow),
                mContext.getResources().getString(R.string.follow_pending),
                mContext.getResources().getString(R.string.follow_favorite),
                mContext.getResources().getString(R.string.follow_viewed),
        };

        String[] options = {
                "following","pending","favorite","viewed"
        };

        final FollowAdapter enlacesAdapter = new FollowAdapter(labels, options, mShow.getMarked());

        //Prepare ListView in dialog
        ListView dialog_ListView = new ListView(dialog.getContext());
        dialog_ListView.setAdapter(enlacesAdapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.optionSelected(position+1);
                dialog.dismiss();
            }
        });

        dialog.setContentView(dialog_ListView);
        dialog.create();
        dialog.show();
    }

    public static void lanzarEnlaceDentro(final Context activity, final Usuario mActiveUser, final Ficha mSelectedShow, final Capitulo episode){
        final String capitulo_id=(episode==null?"0":episode.getIdCapitulo());
        //Recuperar el primer enlace streamcloud de los que me den y lanzar MX Player.
        Requester.request(activity, PlayMaxAPI.getInstance().requestEnlaces(mActiveUser, mSelectedShow,capitulo_id),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Enlace> enlaces = Enlace.listFromXML(response);

                            MyUtils.showLinkList(activity, enlaces, new Enlace.EnlaceListener() {
                                @Override
                                public void onEnlaceSelected(Enlace selected) {
                                    StreamCloudRequest.getDirectUrl(activity, selected.getUrl(), new ScrapperListener() {
                                        @Override
                                        public void onDirectUrlObtained(final String direct_url) {
                                            //Marcar como visto
                                            Requester.request(activity, PlayMaxAPI.getInstance().requestMarkAsViewed(mActiveUser.getSid(), capitulo_id), new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    //MyUtils.launchMXP(activity, direct_url);
                                                    Intent intent = new Intent(activity, PlaybackOverlayActivity.class);
                                                    intent.putExtra(MainActivity.FICHA, new Ficha.FichaReproducible(mSelectedShow, direct_url, episode));
                                                    activity.startActivity(intent);
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
