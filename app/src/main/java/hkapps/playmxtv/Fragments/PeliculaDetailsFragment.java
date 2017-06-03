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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.volley.Response;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

import hkapps.playmxtv.Activities.PeliculasDetailsActivity;
import hkapps.playmxtv.Activities.MainActivity;
import hkapps.playmxtv.Activities.SerieDetailsActivity;
import hkapps.playmxtv.Adapters.PeliculasDetailsDescriptionPresenter;
import hkapps.playmxtv.Adapters.StringPresenter;
import hkapps.playmxtv.Model.Enlace;
import hkapps.playmxtv.Model.Ficha;
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
public class PeliculaDetailsFragment extends DetailsFragment implements OnActionClickedListener {
    private static final String TAG = "VideoDetailsFragment";

    private static final int ACTION_WATCH_TRAILER = 1;
    private static final int ACTION_PLAY = 2;

    private static final int DETAIL_THUMB_WIDTH = 400;
    private static final int DETAIL_THUMB_HEIGHT = 600;

    private Ficha mSelectedMovie;

    private BackgroundManager mBackgroundManager;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Usuario mActiveUser;
    private ArrayObjectAdapter mRowsAdapter;
    private DetailsOverviewRow detailsOverview;
    private FullWidthDetailsOverviewRowPresenter rowPresenter;
    private FullWidthDetailsOverviewRowPresenter detailsPresenter;
    private FullWidthDetailsOverviewSharedElementHelper mHelper;
    private ClassPresenterSelector mPresenterSelector;
    private ArrayObjectAdapter mAdapter;
    private DetailsOverviewRow mainRow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        prepareBackgroundManager();

        mSelectedMovie = (Ficha) getActivity().getIntent().getSerializableExtra(MainActivity.FICHA);
        mActiveUser = (Usuario) getActivity().getIntent().getSerializableExtra(MainActivity.USER);
        if (mSelectedMovie != null) {
            this.setupAdapter();
            this.setupDetailsOverviewRow();
            updateBackground(mSelectedMovie.getCover());
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
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


  private void lanzarPelicula(){
      //Recuperar el primer enlace streamcloud de los que me den y lanzar MX Player.
      Requester.request(PeliculaDetailsFragment.this.getActivity(),
              PlayMaxAPI.getInstance().requestEnlaces(mActiveUser, mSelectedMovie, "0"),
              new Response.Listener<String>() {
                  @Override
                  public void onResponse(String response) {
                      try {
                          List<Enlace> enlaces = Enlace.listFromXML(response);
                          MyUtils.showLinkList(PeliculaDetailsFragment.this.getActivity(), enlaces, new Enlace.EnlaceListener() {
                              @Override
                              public void onEnlaceSelected(Enlace selected) {
                                  StreamCloudRequest.getDirectUrl(PeliculaDetailsFragment.this.getActivity(), selected.getUrl(), new ScrapperListener() {
                                      @Override
                                      public void onDirectUrlObtained(String direct_url) {
                                          MyUtils.launchMXP(getActivity(), direct_url);
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

    private void lanzarTrailer(){
        //Recuperar el primer enlace streamcloud de los que me den y lanzar MX Player.
        Requester.request(PeliculaDetailsFragment.this.getActivity(),
                PlayMaxAPI.getInstance().requestTrailers(mActiveUser, mSelectedMovie),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Trailer> enlaces = Trailer.listFromXML(response);
                            if(enlaces.size() > 0) {
                                MyUtils.launchYT(PeliculaDetailsFragment.this.getActivity(), enlaces.get(0).getYTId());
                            }
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

        //detailsPresenter.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.selected_background));
        detailsPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_HALF);

        // Hook up transition element.
        mHelper = new FullWidthDetailsOverviewSharedElementHelper();
        mHelper.setSharedElementEnterTransition(getActivity(), PeliculasDetailsActivity.SHARED_ELEMENT_NAME);
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
        mainRow = new DetailsOverviewRow(mSelectedMovie);

        int width = Utils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = Utils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);

        Glide.with(this)
                .load(mSelectedMovie.getPoster())
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

        adapter.set(ACTION_PLAY, new Action(ACTION_PLAY, "Reproducir"));
        adapter.set(ACTION_WATCH_TRAILER, new Action(ACTION_WATCH_TRAILER, "Trailer"));

        mainRow.setActionsAdapter(adapter);

        mAdapter.add(mainRow);
    }

    @Override
    public void onActionClicked(Action action) {
        if (action.getId() == ACTION_PLAY){
            lanzarPelicula();
        }else if(action.getId() == ACTION_WATCH_TRAILER){
            lanzarTrailer();
        }
    }
}


