package hkapps.playmxtv.Liestener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;

import java.util.List;

import hkapps.playmxtv.Activities.BrowseErrorActivity;
import hkapps.playmxtv.Activities.MainActivity;
import hkapps.playmxtv.Activities.PeliculasDetailsActivity;
import hkapps.playmxtv.Activities.SerieDetailsActivity;
import hkapps.playmxtv.Fragments.MainFragment;
import hkapps.playmxtv.Model.Enlace;
import hkapps.playmxtv.Model.Ficha;
import hkapps.playmxtv.Model.Usuario;
import hkapps.playmxtv.R;
import hkapps.playmxtv.Scrapper.ScrapperListener;
import hkapps.playmxtv.Scrapper.StreamCloudRequest;
import hkapps.playmxtv.Services.PlayMaxAPI;
import hkapps.playmxtv.Services.Requester;
import hkapps.playmxtv.Static.MyUtils;

public class ResultsListener implements OnItemViewClickedListener {
    Activity activity;
    Usuario user;

    public ResultsListener(Activity activity, Usuario user){
        this.activity = activity;
        this.user = user;
    }

    @Override
    public void onItemClicked(final Presenter.ViewHolder itemViewHolder, Object item,
                              RowPresenter.ViewHolder rowViewHolder, Row row) {

        if (item instanceof Ficha) {
            final Ficha fr = (Ficha) item;

            //Interfaz para peliculas
            Requester.request(activity, PlayMaxAPI.getInstance().requestFicha(user, fr), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        fr.completeFromXML(response);

                        if(fr.getIdCapitulo()!= null){
                            //Si tenemos capitulo: Lanzamos el detail para capitulo
                            //Recuperar el primer enlace streamcloud de los que me den y lanzar MX Player.
                            Requester.request(activity,PlayMaxAPI.getInstance().requestEnlaces(user, fr, fr.getIdCapitulo()),
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                List<Enlace> enlaces = Enlace.listFromXML(response);
                                                if(enlaces.size() > 0) {
                                                    Log.d("REQ", enlaces.toString());
                                                    StreamCloudRequest.getDirectUrl(activity, enlaces.get(0).toString(), new ScrapperListener() {
                                                        @Override
                                                        public void onDirectUrlObtained(String direct_url) {
                                                            MyUtils.launchMXP(activity, direct_url);
                                                        }
                                                    });
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
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
            if (((String) item).indexOf(activity.getString(R.string.error_fragment)) >= 0) {
                Intent intent = new Intent(activity, BrowseErrorActivity.class);
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, ((String) item), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}