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

package hkapps.playmxtv.Fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.BaseOnItemViewClickedListener;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.Response;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

import hkapps.playmxtv.Activities.PeliculasDetailsActivity;
import hkapps.playmxtv.Activities.MainActivity;
import hkapps.playmxtv.Adapters.EpisodePresenter;
import hkapps.playmxtv.Adapters.GridItemPresenter;
import hkapps.playmxtv.Adapters.PeliculasDetailsDescriptionPresenter;
import hkapps.playmxtv.Adapters.StringPresenter;
import hkapps.playmxtv.Adapters.TemporadaPresenter;
import hkapps.playmxtv.Model.Capitulo;
import hkapps.playmxtv.Model.Enlace;
import hkapps.playmxtv.Model.Ficha;
import hkapps.playmxtv.Model.Temporada;
import hkapps.playmxtv.Model.Trailer;
import hkapps.playmxtv.Model.Usuario;
import hkapps.playmxtv.R;
import hkapps.playmxtv.Scrapper.ScrapperListener;
import hkapps.playmxtv.Scrapper.StreamCloudRequest;
import hkapps.playmxtv.Services.PlayMaxAPI;
import hkapps.playmxtv.Services.Requester;
import hkapps.playmxtv.Static.MyUtils;
import hkapps.playmxtv.Static.Utils;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class SerieDetailsFragment extends DetailsFragment implements OnActionClickedListener, BaseOnItemViewClickedListener, TemporadaPresenter.OnCapituloListener {
    private static final String TAG = "VideoDetailsFragment";

    private static final int ACTION_RANDOM = 1;
    private static final int ACTION_PLAY = 2;

    private static final int DETAIL_THUMB_WIDTH = 400;
    private static final int DETAIL_THUMB_HEIGHT = 600;

    private Ficha mSelectedShow;

    private BackgroundManager mBackgroundManager;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Usuario mActiveUser;
    private ArrayObjectAdapter mRowsAdapter;
    private DetailsOverviewRow detailsOverview;
    private FullWidthDetailsOverviewRowPresenter rowPresenter;
    private ArrayObjectAdapter episodeRowAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        prepareBackgroundManager();

        mSelectedShow = (Ficha) getActivity().getIntent().getSerializableExtra(MainActivity.FICHA);
        mActiveUser = (Usuario) getActivity().getIntent().getSerializableExtra(MainActivity.USER);

        if (mSelectedShow != null) {
            buildDetails();
            updateBackground(mSelectedShow.getCover());
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }

        setOnItemViewClickedListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    protected void updateBackground(String uri) {
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(mMetrics.widthPixels, mMetrics.heightPixels) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
    }


  private void lanzarCapitulo(Capitulo episode){
      //Recuperar el primer enlace streamcloud de los que me den y lanzar MX Player.
      Requester.request(SerieDetailsFragment.this.getActivity(),
              PlayMaxAPI.getInstance().requestEnlaces(mActiveUser, mSelectedShow, episode.getIdCapitulo()),
              new Response.Listener<String>() {
                  @Override
                  public void onResponse(String response) {
                      try {
                          List<Enlace> enlaces = Enlace.listFromXML(response);
                          if(enlaces.size() > 0) {
                              Log.d("REQ", enlaces.toString());
                              StreamCloudRequest.getDirectUrl(SerieDetailsFragment.this.getActivity(), enlaces.get(0).toString(), new ScrapperListener() {
                                  @Override
                                  public void onDirectUrlObtained(String direct_url) {
                                      MyUtils.launchMXP(getActivity(), direct_url);
                                  }
                              });
                          }
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                  }
              });
  }

    private void buildDetails() {
        ClassPresenterSelector selector = new ClassPresenterSelector();
        // Attach your media item details presenter to the row presenter:
        rowPresenter = new FullWidthDetailsOverviewRowPresenter(new PeliculasDetailsDescriptionPresenter(this.getActivity()));
        rowPresenter.setBackgroundColor(getResources().getColor(R.color.selected_background));

        rowPresenter.setOnActionClickedListener(this);

        selector.addClassPresenter(DetailsOverviewRow.class, rowPresenter);
        selector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(selector);

        detailsOverview = new DetailsOverviewRow(mSelectedShow);
        //detailsOverview.setImageDrawable(getResources().getDrawable(R.drawable.default_background));

        int width = Utils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = Utils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);



        // Add images and action buttons to the details view
        detailsOverview.addAction(new Action(ACTION_PLAY, "Reproducir"));
        detailsOverview.addAction(new Action(ACTION_RANDOM, "Aleatorio"));
        mRowsAdapter.add(detailsOverview);

        Glide.with(getActivity())
                .load(mSelectedShow.getPoster())
                .fitCenter()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        Log.d(TAG, "details overview card image url ready: " + resource);
                        detailsOverview.setImageDrawable(resource);
                        detailsOverview.setImageScaleUpAllowed(true);
                        mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
                    }
                });

        List<Temporada> temps = Temporada.listFromCapitulos(mSelectedShow.getCapitulos());
        episodeRowAdapter = new ArrayObjectAdapter(new TemporadaPresenter(this));
        episodeRowAdapter.addAll(0, temps);
        HeaderItem header = new HeaderItem(0, "Capitulos");
        mRowsAdapter.add(new ListRow(header, episodeRowAdapter));

        /*
        ListRow current;
        for(int i = 0; i<mSelectedShow.getSeasons(); i++) {
            List<Capitulo> temp = Capitulo.filterByTemporada(mSelectedShow.getCapitulos(),i);
            if(temp.size() > 0){
                episodeRowAdapter = new ArrayObjectAdapter(new EpisodePresenter(this.getActivity()));
                episodeRowAdapter.addAll(0, temp);
                HeaderItem header = new HeaderItem(i, "Temporada "+i);
                current = new ListRow(header, episodeRowAdapter);
                mRowsAdapter.add(current);
            }
        }
         */
        setAdapter(mRowsAdapter);
    }

    @Override
    public void onActionClicked(Action action) {
        if (action.getId() == ACTION_PLAY){
            //lanzarPelicula();
        }else if(action.getId() == ACTION_RANDOM){
            lanzarCapitulo(Capitulo.random(mSelectedShow.getCapitulos()));
        }
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Object row) {
        if(item instanceof Action){
            Action action = (Action) item;
            if (action.getId() == ACTION_PLAY){
                //lanzarPelicula();
            }else if(action.getId() == ACTION_RANDOM){
                lanzarCapitulo(Capitulo.random(mSelectedShow.getCapitulos()));
            }
        }

    }

    @Override
    public void onCapituloClicked(Capitulo capitulo) {
        lanzarCapitulo(capitulo);
    }
}


