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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Response;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

import hkapps.playmxtv.Activities.PeliculasDetailsActivity;
import hkapps.playmxtv.Activities.MainActivity;
import hkapps.playmxtv.Activities.SerieDetailsActivity;
import hkapps.playmxtv.Adapters.EpisodePresenter;
import hkapps.playmxtv.Adapters.GridItemPresenter;
import hkapps.playmxtv.Adapters.MovieDetailsOverviewLogoPresenter;
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
public class SerieDetailsFragment extends DetailsFragment implements OnActionClickedListener,
        TemporadaPresenter.OnCapituloListener{
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
    private ArrayObjectAdapter episodeRowAdapter;
    private FullWidthDetailsOverviewSharedElementHelper mHelper;
    private ClassPresenterSelector mPresenterSelector;
    private ArrayObjectAdapter mAdapter;
    private FullWidthDetailsOverviewRowPresenter detailsPresenter;
    private DetailsOverviewRow mainRow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        prepareBackgroundManager();

        mSelectedShow = (Ficha) getActivity().getIntent().getSerializableExtra(MainActivity.FICHA);
        mActiveUser = (Usuario) getActivity().getIntent().getSerializableExtra(MainActivity.USER);

        if (mSelectedShow != null) {
            setupAdapter();
            setupDetailsOverviewRow();
            addEpisodesRow();
            updateBackground(mSelectedShow.getCover());
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }

        //setOnItemViewClickedListener(this);
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


  private void lanzarCapitulo(final Capitulo episode){
      //Recuperar el primer enlace streamcloud de los que me den y lanzar MX Player.
      Requester.request(SerieDetailsFragment.this.getActivity(),
              PlayMaxAPI.getInstance().requestEnlaces(mActiveUser, mSelectedShow, episode.getIdCapitulo()),
              new Response.Listener<String>() {
                  @Override
                  public void onResponse(String response) {
                      try {
                          List<Enlace> enlaces = Enlace.listFromXML(response);

                          MyUtils.showLinkList(SerieDetailsFragment.this.getActivity(), enlaces, new Enlace.EnlaceListener() {
                              @Override
                              public void onEnlaceSelected(Enlace selected) {
                                  StreamCloudRequest.getDirectUrl(SerieDetailsFragment.this.getActivity(), selected.getUrl(), new ScrapperListener() {
                                      @Override
                                      public void onDirectUrlObtained(final String direct_url) {
                                          //Marcar como visto
                                          Requester.request(SerieDetailsFragment.this.getActivity(),
                                                  PlayMaxAPI.getInstance().requestMarkAsViewed(mActiveUser.getSid(), episode.getIdCapitulo()), new Response.Listener<String>() {
                                                      @Override
                                                      public void onResponse(String response) {
                                                          MyUtils.launchMXP(getActivity(), direct_url);
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

    private void setupAdapter() {
        // Set detail background and style.
        detailsPresenter =
                new FullWidthDetailsOverviewRowPresenter(
                        new PeliculasDetailsDescriptionPresenter());

        //detailsPresenter.setBackgroundColor(color);
        detailsPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_HALF);

        // Hook up transition element.
        mHelper = new FullWidthDetailsOverviewSharedElementHelper();
        mHelper.setSharedElementEnterTransition(getActivity(), SerieDetailsActivity.SHARED_ELEMENT_NAME);
        detailsPresenter.setListener(mHelper);
        detailsPresenter.setParticipatingEntranceTransition(false);
        prepareEntranceTransition();

        detailsPresenter.setOnActionClickedListener(this);

        mPresenterSelector = new ClassPresenterSelector();
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);
        setAdapter(mAdapter);
    }

    private void setupDetailsOverviewRow() {
        mainRow = new DetailsOverviewRow(mSelectedShow);

        int width = Utils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = Utils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);

        Glide.with(this)
                .load(mSelectedShow.getPoster())
                .asBitmap()
                .dontAnimate()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<Bitmap>(width, height) {
                    @Override
                    public void onResourceReady(final Bitmap resource,
                                                GlideAnimation glideAnimation) {
                        mainRow.setImageBitmap(getActivity(), resource);
                        //Palette.from(resource).generate(SerieDetailsFragment.this);
                        startEntranceTransition();
                    }
                });

        SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter();

        adapter.set(ACTION_PLAY, new Action(ACTION_PLAY, "Siguiente"));
        adapter.set(ACTION_RANDOM, new Action(ACTION_RANDOM, "Aleatorio"));

        mainRow.setActionsAdapter(adapter);

        mAdapter.add(mainRow);
    }

    private void addEpisodesRow(){
        List<Temporada> temps = Temporada.listFromCapitulos(mSelectedShow.getCapitulos());
        episodeRowAdapter = new ArrayObjectAdapter(new TemporadaPresenter(this));
        episodeRowAdapter.addAll(0, temps);
        HeaderItem header = new HeaderItem(0, "Capitulos");
        mAdapter.add(new ListRow(header, episodeRowAdapter));
    }

    @Override
    public void onActionClicked(Action action) {
        if (action.getId() == ACTION_PLAY){
            lanzarCapitulo(Capitulo.findFirstNonViewed(mSelectedShow.getCapitulos()));
        }else if(action.getId() == ACTION_RANDOM){
            lanzarCapitulo(Capitulo.random(mSelectedShow.getCapitulos()));
        }
    }


    @Override
    public void onCapituloClicked(Capitulo capitulo) {
        lanzarCapitulo(capitulo);
    }

}


