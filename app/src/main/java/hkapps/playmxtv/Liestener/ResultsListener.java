package hkapps.playmxtv.Liestener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;

import com.android.volley.Response;

import java.util.List;

import hkapps.playmxtv.Activities.LoginActivity;
import hkapps.playmxtv.Activities.MainActivity;
import hkapps.playmxtv.Activities.PeliculasDetailsActivity;
import hkapps.playmxtv.Activities.SerieDetailsActivity;
import hkapps.playmxtv.Fragments.MainFragment;
import hkapps.playmxtv.Model.Enlace;
import hkapps.playmxtv.Model.Ficha;
import hkapps.playmxtv.Model.Usuario;
import hkapps.playmxtv.Scrapper.ScrapperListener;
import hkapps.playmxtv.Scrapper.StreamCloudRequest;
import hkapps.playmxtv.Services.PlayMaxAPI;
import hkapps.playmxtv.Services.Requester;
import hkapps.playmxtv.Static.MyUtils;

public class ResultsListener implements OnItemViewClickedListener {
    Activity activity;
    Usuario user;
    long episode_row_id;

    public ResultsListener(Activity activity, Usuario user){
        this.activity = activity;
        this.user = user;
        this.episode_row_id = episode_row_id;
    }

    @Override
    public void onItemClicked(final Presenter.ViewHolder itemViewHolder, Object item,
                              RowPresenter.ViewHolder rowViewHolder, final Row row) {

        Bitmap bitmap = MyUtils.drawableToBitmap(((ImageCardView) itemViewHolder.view).getMainImage());

        Log.d("RESULTLISTENER", "Item Clicked!");
        Log.d("RESULTLISTENER",item.toString());
        if (item instanceof Ficha) {
            final Ficha fr = (Ficha) item;
            Log.d("RESULTLISTENER",fr.toString());

            //Interfaz para peliculas
            Requester.request(activity, PlayMaxAPI.getInstance().requestFicha(user, fr), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        fr.completeFromXML(response);

                        if(row.getHeaderItem().getId() == MainFragment.EPISODE_ROW_ID){
                            //Si tenemos capitulo: Lanzamos el detail para capitulo
                            //Recuperar el primer enlace streamcloud de los que me den y lanzar MX Player.
                            MyUtils.lanzarCapitulo(activity, user, fr, fr.getIdCapitulo());
                        }else if(fr.isSerie()){
                            //Es Serie
                            Intent intent = new Intent(activity, SerieDetailsActivity.class);
                            intent.putExtra(MainActivity.FICHA, fr);
                            intent.putExtra(MainActivity.USER, user);

                            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    activity,
                                    ((ImageCardView) itemViewHolder.view).getMainImageView(),
                                    SerieDetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                            activity.startActivity(intent, bundle);
                        }else {
                            //Es pelicula: lanzamos el selector de pelicula
                            Intent intent = new Intent(activity, PeliculasDetailsActivity.class);
                            intent.putExtra(MainActivity.FICHA, fr);
                            intent.putExtra(MainActivity.USER, user);


                            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    activity,
                                    ((ImageCardView) itemViewHolder.view).getMainImageView(),
                                    PeliculasDetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                            activity.startActivity(intent, bundle);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        } else if (item instanceof String) {
            //Close session
            SharedPreferences prefs = itemViewHolder.view.getContext().getSharedPreferences(LoginActivity.LOGIN_CREDS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(LoginActivity.USERNAME_TAG);
            editor.remove(LoginActivity.PASSWORD_TAG);
            editor.apply();
        }
    }
}