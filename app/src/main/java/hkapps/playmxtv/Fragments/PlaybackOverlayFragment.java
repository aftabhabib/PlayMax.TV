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

import android.app.Activity;

import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRow.FastForwardAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.PlayPauseAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RepeatAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RewindAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ShuffleAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipNextAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipPreviousAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ThumbsDownAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ThumbsUpAction;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hkapps.playmxtv.Activities.MainActivity;
import hkapps.playmxtv.Adapters.CardPresenter;
import hkapps.playmxtv.Model.Ficha;
import hkapps.playmxtv.R;

/*
 * Class for video playback with media control
 */
public class PlaybackOverlayFragment extends android.support.v17.leanback.app.PlaybackOverlayFragment {
    private static final String TAG = "PlaybackControlsFragmnt";

    private static final boolean SHOW_DETAIL = true;
    private static final boolean HIDE_MORE_ACTIONS = false; //there are no more actions
    private static final int PRIMARY_CONTROLS = 5;
    private static final boolean SHOW_IMAGE = PRIMARY_CONTROLS <= 5;
    private static final int BACKGROUND_TYPE = PlaybackOverlayFragment.BG_DARK;
    private static final int CARD_WIDTH = 200;
    private static final int CARD_HEIGHT = 300;
    private static final int DEFAULT_UPDATE_PERIOD = 1000;
    private static final int UPDATE_PERIOD = 16;
    private static final int SIMULATED_BUFFERED_TIME = 10000;

    private ArrayObjectAdapter mRowsAdapter;
    private ArrayObjectAdapter mPrimaryActionsAdapter;
    private ArrayObjectAdapter mSecondaryActionsAdapter;
    private PlayPauseAction mPlayPauseAction;
    private FastForwardAction mFastForwardAction;
    private RewindAction mRewindAction;
    private SkipNextAction mSkipNextAction;
    private SkipPreviousAction mSkipPreviousAction;
    private PlaybackControlsRow mPlaybackControlsRow;
    private ArrayList<Ficha.FichaReproducible> mItems = new ArrayList<Ficha.FichaReproducible>();
    private int mCurrentItem;
    private Handler mHandler;
    private Runnable mRunnable;
    private Ficha.FichaReproducible mSelectedMovie;

    private OnPlayPauseClickedListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItems = new ArrayList<Ficha.FichaReproducible>();
        mSelectedMovie = (Ficha.FichaReproducible) getActivity().getIntent().getSerializableExtra(MainActivity.FICHA);

        mItems.add(mSelectedMovie);

        mHandler = new Handler();

        //setBackgroundType(BACKGROUND_TYPE);
        //setFadingEnabled(false);

        setupRows();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnPlayPauseClickedListener) {
            mCallback = (OnPlayPauseClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlayPauseClickedListener");
        }
    }

    private void setupRows() {

        ClassPresenterSelector ps = new ClassPresenterSelector();

        PlaybackControlsRowPresenter playbackControlsRowPresenter;
        if (SHOW_DETAIL) {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter(
                    new DescriptionPresenter());
        } else {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter();
        }
        playbackControlsRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            public void onActionClicked(Action action) {
                if (action.getId() == mPlayPauseAction.getId()) {
                    togglePlayback(mPlayPauseAction.getIndex() == PlayPauseAction.PLAY);
                } else if (action.getId() == mSkipNextAction.getId()) {
                    next();
                } else if (action.getId() == mSkipPreviousAction.getId()) {
                    prev();
                } else if (action.getId() == mFastForwardAction.getId()) {
                    Toast.makeText(getActivity(), "TODO: Fast Forward", Toast.LENGTH_SHORT).show();
                } else if (action.getId() == mRewindAction.getId()) {
                    Toast.makeText(getActivity(), "TODO: Rewind", Toast.LENGTH_SHORT).show();
                }
                if (action instanceof PlaybackControlsRow.MultiAction) {
                    notifyChanged(action);
                }
            }
        });
        playbackControlsRowPresenter.setSecondaryActionsHidden(HIDE_MORE_ACTIONS);

        ps.addClassPresenter(PlaybackControlsRow.class, playbackControlsRowPresenter);
        ps.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(ps);

        addPlaybackControlsRow();

        setAdapter(mRowsAdapter);
    }

    public void togglePlayback(boolean playPause) {
        if (playPause) {
            startProgressAutomation();
            setFadingEnabled(true);
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem),
                    mPlaybackControlsRow.getCurrentTime(), true);
            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlayPauseAction.PAUSE));
        } else {
            stopProgressAutomation();
            setFadingEnabled(false);
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem),
                    mPlaybackControlsRow.getCurrentTime(), false);
            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlayPauseAction.PLAY));
        }
        notifyChanged(mPlayPauseAction);
    }

    private int getDuration() {
        Ficha.FichaReproducible movie = mItems.get(mCurrentItem);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mmr.setDataSource(movie.getDirectUrl(), new HashMap<String, String>());
        } else {
            mmr.setDataSource(movie.getDirectUrl());
        }
        String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long duration = Long.parseLong(time);
        return (int) duration;
    }

    private void addPlaybackControlsRow() {
        if (SHOW_DETAIL) {
            mPlaybackControlsRow = new PlaybackControlsRow(mSelectedMovie);
        } else {
            mPlaybackControlsRow = new PlaybackControlsRow();
        }
        mRowsAdapter.add(mPlaybackControlsRow);

        updatePlaybackRow(mCurrentItem);

        ControlButtonPresenterSelector presenterSelector = new ControlButtonPresenterSelector();
        mPrimaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        mSecondaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        mPlaybackControlsRow.setPrimaryActionsAdapter(mPrimaryActionsAdapter);
        //mPlaybackControlsRow.setSecondaryActionsAdapter(mSecondaryActionsAdapter);

        mPlayPauseAction = new PlayPauseAction(getActivity());
        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(getActivity());
        mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(getActivity());
        mFastForwardAction = new PlaybackControlsRow.FastForwardAction(getActivity());
        mRewindAction = new PlaybackControlsRow.RewindAction(getActivity());


        if(mSelectedMovie.isSerie())
            mPrimaryActionsAdapter.add(mSkipPreviousAction);
        mPrimaryActionsAdapter.add(new PlaybackControlsRow.RewindAction(getActivity()));
        mPrimaryActionsAdapter.add(mPlayPauseAction);
        mPrimaryActionsAdapter.add(new PlaybackControlsRow.FastForwardAction(getActivity()));
        if(mSelectedMovie.isSerie())
            mPrimaryActionsAdapter.add(mSkipNextAction);

        //mSecondaryActionsAdapter.add(new PlaybackControlsRow.HighQualityAction(getActivity()));
        //mSecondaryActionsAdapter.add(new PlaybackControlsRow.ClosedCaptioningAction(getActivity()));
    }

    private void notifyChanged(Action action) {
        ArrayObjectAdapter adapter = mPrimaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
        adapter = mSecondaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
    }

    private void updatePlaybackRow(int index) {
        if (mPlaybackControlsRow.getItem() != null) {
            Ficha.FichaReproducible item = (Ficha.FichaReproducible) mPlaybackControlsRow.getItem();
            item.setTitle(mItems.get(mCurrentItem).getTitle());
            //item.setStudio(mItems.get(mCurrentItem).getStudio());
        }
        if (SHOW_IMAGE) {
            updateVideoImage(mItems.get(mCurrentItem).getPoster().toString());
        }
        mRowsAdapter.notifyArrayItemRangeChanged(0, 1);
        mPlaybackControlsRow.setTotalTime(getDuration());
        mPlaybackControlsRow.setCurrentTime(0);
        mPlaybackControlsRow.setBufferedProgress(0);
    }

    private int getUpdatePeriod() {
        if (getView() == null || mPlaybackControlsRow.getTotalTime() <= 0) {
            return DEFAULT_UPDATE_PERIOD;
        }
        return Math.max(UPDATE_PERIOD, mPlaybackControlsRow.getTotalTime() / getView().getWidth());
    }

    private void startProgressAutomation() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                int updatePeriod = getUpdatePeriod();
                int currentTime = mPlaybackControlsRow.getCurrentTime() + updatePeriod;
                int totalTime = mPlaybackControlsRow.getTotalTime();
                mPlaybackControlsRow.setCurrentTime(currentTime);
                mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);

                if (totalTime > 0 && totalTime <= currentTime) {
                    next();
                }
                mHandler.postDelayed(this, updatePeriod);
            }
        };
        mHandler.postDelayed(mRunnable, getUpdatePeriod());
    }

    private void next() {
        if (++mCurrentItem >= mItems.size()) {
            mCurrentItem = 0;
        }

        if (mPlayPauseAction.getIndex() == PlayPauseAction.PLAY) {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, false);
        } else {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, true);
        }
        updatePlaybackRow(mCurrentItem);
    }

    private void prev() {
        if (--mCurrentItem < 0) {
            mCurrentItem = mItems.size() - 1;
        }
        if (mPlayPauseAction.getIndex() == PlayPauseAction.PLAY) {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, false);
        } else {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, true);
        }
        updatePlaybackRow(mCurrentItem);
    }

    private void stopProgressAutomation() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void onStop() {
        stopProgressAutomation();
        super.onStop();
    }

    protected void updateVideoImage(String uri) {
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .into(new SimpleTarget<GlideDrawable>(CARD_WIDTH, CARD_HEIGHT) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mPlaybackControlsRow.setImageDrawable(resource);
                        mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
                    }
                });
    }

    // Container Activity must implement this interface
    public interface OnPlayPauseClickedListener {
        void onFragmentPlayPause(Ficha.FichaReproducible movie, int position, Boolean playPause);
    }

    static class DescriptionPresenter extends AbstractDetailsDescriptionPresenter {
        @Override
        protected void onBindDescription(ViewHolder viewHolder, Object item) {
            if(item instanceof Ficha.FichaReproducible){
                Ficha.FichaReproducible f = (Ficha.FichaReproducible) item;
                viewHolder.getTitle().setText(f.getTitle());
                if(f.isSerie() && f.getCapitulo() != null)
                    viewHolder.getSubtitle().setText(f.getCapitulo().getNombre());
            }

        }
    }
}