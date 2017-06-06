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

import java.net.URI;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import hkapps.playmxtv.Activities.MainActivity;
import hkapps.playmxtv.Activities.PeliculasDetailsActivity;
import hkapps.playmxtv.Activities.BrowseErrorActivity;
import hkapps.playmxtv.Activities.SearchActivity;
import hkapps.playmxtv.Activities.SerieDetailsActivity;
import hkapps.playmxtv.Adapters.CardPresenter;
import hkapps.playmxtv.Liestener.ResultsListener;
import hkapps.playmxtv.Model.Enlace;
import hkapps.playmxtv.Model.Ficha;
import hkapps.playmxtv.Model.Usuario;
import hkapps.playmxtv.R;
import hkapps.playmxtv.Scrapper.ScrapperListener;
import hkapps.playmxtv.Scrapper.StreamCloudRequest;
import hkapps.playmxtv.Services.PlayMaxAPI;
import hkapps.playmxtv.Services.Requester;
import hkapps.playmxtv.Static.MyUtils;
import hkapps.playmxtv.Static.Utils;
import hkapps.playmxtv.Views.CircleTransform;

public class MainFragment extends BrowseFragment {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;

    public static final long EPISODE_ROW_ID = 0x42;
    public static final long SHOWS_ROW_ID = 0x43;
    public static final long MOVIES_ROW_ID = 0x44;
    public static final long RECOMENDED_MOVIES_ROW_ID = 0x45;
    public static final long RECOMENDED_SHOW_ROW_ID = 0x46;
    public static final long SETTINGS_ROW_ID = 0x47;
    private static final int AVATAR_SIZE = 250;

    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private URI mBackgroundURI;
    private BackgroundManager mBackgroundManager;

    private Usuario user;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        //Recuperamos el usuario
        recoverUser();

        //We paint the elements
        setupUIElements();

        //Creamos el adaptador
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        //Anidadas para matener el orden
        //Pedimos las series
        Requester.request(getActivity(), PlayMaxAPI.getInstance().requestSumary(user), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    List<Ficha> fichas = Ficha.listFromXML(response);
                    Log.d("REQ",fichas.toString());

                    loadMyRows(fichas);


                    Requester.request(getActivity(), PlayMaxAPI.getInstance().requestCatalogue(user), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                List<Ficha> fichas = Ficha.listFromXML(response);
                                Log.d("REQ",fichas.toString());

                                loadRecomendedRows(fichas);
                                //Load Settings
                                //loadSettings();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        //Load Settings
        //loadSettings();
        setupEventListeners();
    }

    private void recoverUser(){
        Intent me = this.getActivity().getIntent();
        user = (Usuario) me.getSerializableExtra("user");

        Log.d("REQ", user.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }

    private void loadMyRows(List<Ficha> fichas) {

        ArrayObjectAdapter proximos = new ArrayObjectAdapter(new CardPresenter());
        ArrayObjectAdapter series = new ArrayObjectAdapter(new CardPresenter());
        ArrayObjectAdapter peliculas = new ArrayObjectAdapter(new CardPresenter());

        HeaderItem hproximos = new HeaderItem(EPISODE_ROW_ID,"Proximos Capitulos");
        HeaderItem hseries = new HeaderItem(SHOWS_ROW_ID,"Tus Series");
        HeaderItem hpeliculas = new HeaderItem(MOVIES_ROW_ID,"Tus Peliculas");

        for(Ficha fr : fichas){
            if(fr.getLastEpisode() != null)
                try {
                    proximos.add(fr.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

            if(fr.isSerie())
                series.add(fr);
            else peliculas.add(fr);
        }
        if(proximos.size() > 0) mRowsAdapter.add(new ListRow(hproximos, proximos));
        mRowsAdapter.add(new ListRow(hseries, series));
        mRowsAdapter.add(new ListRow(hpeliculas, peliculas));

        setAdapter(mRowsAdapter);

    }
    private void loadRecomendedRows(List<Ficha> fichas) {

        ArrayObjectAdapter series = new ArrayObjectAdapter(new CardPresenter());
        ArrayObjectAdapter peliculas = new ArrayObjectAdapter(new CardPresenter());

        HeaderItem hseries = new HeaderItem(RECOMENDED_SHOW_ROW_ID,"Series");
        HeaderItem hpeliculas = new HeaderItem(RECOMENDED_MOVIES_ROW_ID,"Peliculas");

        for(Ficha fr : fichas){
            if(fr.isSerie())
                series.add(fr);
            else peliculas.add(fr);
        }
        mRowsAdapter.add(new ListRow(hseries, series));
        mRowsAdapter.add(new ListRow(hpeliculas, peliculas));

        setAdapter(mRowsAdapter);

    }

    private void loadSettings(){
        HeaderItem gridHeader = new HeaderItem(SETTINGS_ROW_ID,"Ajustes");

        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add(getResources().getString(R.string.logout));
        //gridRowAdapter.add(getString(R.string.error_fragment));
        //gridRowAdapter.add(getResources().getString(R.string.personal_settings));
        mRowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));
    }

    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }
    
    void setAvatarImage(){
        int width = Utils.convertDpToPixel(getActivity().getApplicationContext(), AVATAR_SIZE);
        int height = Utils.convertDpToPixel(getActivity().getApplicationContext(), AVATAR_SIZE);
        Glide.with(getActivity())
                .load(user.getAvatar())
                .centerCrop()
                //.error(R.drawable.default_background)
                .transform(new CircleTransform(this.getActivity()))
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        setBadgeDrawable(resource);
                    }
                });
    }

    private void setupUIElements() {
        setAvatarImage();
        //setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.app_icon));
        //setTitle("PlayMax.TV"); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        //setHeadersState(HEADERS_DISABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent search = new Intent(MainFragment.this.getActivity(), SearchActivity.class);
                search.putExtra(MainActivity.USER,user);
                MainFragment.this.getActivity().startActivity(search);
            }
        });

        setOnItemViewClickedListener(new ResultsListener(this.getActivity(), user));
        //setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    protected void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
        mBackgroundTimer.cancel();
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }



    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundURI != null) {
                        updateBackground(mBackgroundURI.toString());
                    }
                }
            });

        }
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

}
