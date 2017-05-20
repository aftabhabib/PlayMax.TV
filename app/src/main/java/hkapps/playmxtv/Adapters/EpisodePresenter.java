package hkapps.playmxtv.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v17.leanback.widget.Presenter;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import hkapps.playmxtv.Model.Capitulo;
import hkapps.playmxtv.R;

public class EpisodePresenter extends Presenter {
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;

    Context mContext;

    public EpisodePresenter(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        TextView view = new TextView(parent.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setBackgroundColor(mContext.getResources().getColor(R.color.default_background));
        view.setTextColor(Color.WHITE);
        view.setGravity(Gravity.CENTER);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Capitulo capi = (Capitulo) item;
        ((TextView) viewHolder.view).setText(capi.getNum());
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }
}