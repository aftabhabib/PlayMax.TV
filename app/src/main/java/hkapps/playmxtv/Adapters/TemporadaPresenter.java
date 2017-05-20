package hkapps.playmxtv.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v17.leanback.widget.Presenter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import hkapps.playmxtv.Model.Capitulo;
import hkapps.playmxtv.Model.Temporada;
import hkapps.playmxtv.R;

public class TemporadaPresenter extends Presenter {
    private static final int GRID_ITEM_WIDTH = 900;
    private static final int GRID_ITEM_HEIGHT = 700;
    private final OnCapituloListener listener;

    public TemporadaPresenter(OnCapituloListener listener){
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.temp_card_presenter,null);
        view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
        view.setBackgroundColor(parent.getContext().getResources().getColor(R.color.default_semi_background));

        return new TempViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        final Temporada temp = (Temporada) item;
        TempViewHolder vholder = (TempViewHolder) viewHolder;

        vholder.titulo.setText(("Temporada "+temp.num_temp));
        TemporadaAdapter tadapter = new TemporadaAdapter(temp);
        vholder.lista.setAdapter(tadapter);
        vholder.lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onCapituloClicked(temp.get(position));
            }
        });

    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }

    public class TempViewHolder extends Presenter.ViewHolder{
        public ListView lista;
        public TextView titulo;

        public TempViewHolder(View view) {
            super(view);
            this.titulo = (TextView) view.findViewById(R.id.temporada_title);
            this.lista = (ListView) view.findViewById(R.id.temporada_ep);
        }
    }

    public interface OnCapituloListener{
        void onCapituloClicked(Capitulo capitulo);
    }
}