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

import android.content.Context;
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
import hkapps.playmxtv.Adapters.PeliculasDetailsDescriptionPresenter;
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

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class PeliculaDetailsFragment extends DetailsFragment implements OnActionClickedListener {
    private static final String TAG = "VideoDetailsFragment";

    private static final int ACTION_WATCH_TRAILER = 2;
    private static final int ACTION_PLAY = 1;
    private static final int ACTION_FOLLOW = 3;

    private static final int DETAIL_THUMB_WIDTH = 400;
    private static final int DETAIL_THUMB_HEIGHT = 600;

    private Ficha mSelectedMovie;

    private BackgroundManager mBackgroundManager;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Usuario mActiveUser;

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



    private void setupAdapter() {
        // Set detail background and style.
        detailsPresenter =
                new FullWidthDetailsOverviewRowPresenter(
                        new PeliculasDetailsDescriptionPresenter());

        detailsPresenter.setBackgroundColor(getActivity().getResources().getColor( R.color.description_pane));
        detailsPresenter.setActionsBackgroundColor(getActivity().getResources().getColor( R.color.description_pane));

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

        int width = MyUtils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = MyUtils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);

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

        Context context = this.getActivity();
        adapter.set(ACTION_PLAY, new Action(ACTION_PLAY, context.getResources().getString(R.string.play)));
        adapter.set(ACTION_WATCH_TRAILER, new Action(ACTION_WATCH_TRAILER,context.getResources().getString(R.string.trailer)));
        adapter.set(ACTION_FOLLOW, new Action(ACTION_FOLLOW, context.getResources().getString(R.string.follow)));

        mainRow.setActionsAdapter(adapter);

        mAdapter.add(mainRow);
    }

    @Override
    public void onActionClicked(Action action) {
        if (action.getId() == ACTION_PLAY){
            MyUtils.lanzarPelicula(this.getActivity(), mActiveUser, mSelectedMovie);
        }else if(action.getId() == ACTION_WATCH_TRAILER){
            MyUtils.lanzarTrailer(this.getActivity(), mActiveUser, mSelectedMovie);
        }else if(action.getId() == ACTION_FOLLOW){
            MyUtils.showFollowList(this.getActivity(), mSelectedMovie, new Ficha.FollowSelection() {
                @Override
                public void optionSelected(final int code) {
                    Requester.request(getActivity(),
                            PlayMaxAPI.getInstance().requestMarkFollow(mActiveUser.getSid(), mSelectedMovie.getId(), code), new Response.Listener<String>(){
                                @Override
                                public void onResponse(String response) {
                                    if(response.equalsIgnoreCase("0")){
                                        // deSelected
                                        mSelectedMovie.setMarked("");
                                    }else{
                                        // Selected
                                        mSelectedMovie.setMarked(Ficha.options[code - 1]);
                                    }
                                }
                            });
                }
            });
        }
    }
}


