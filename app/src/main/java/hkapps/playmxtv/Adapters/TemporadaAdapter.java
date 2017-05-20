package hkapps.playmxtv.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import hkapps.playmxtv.Model.Capitulo;
import hkapps.playmxtv.Model.Temporada;
import hkapps.playmxtv.R;

/**
 * Created by hkfuertes on 20/05/2017.
 */

public class TemporadaAdapter extends BaseAdapter {

    Temporada temp;

    public TemporadaAdapter(Temporada temp){
        this.temp = temp;
    }

    @Override
    public int getCount() {
        return temp.size();
    }

    @Override
    public Capitulo getItem(int position) {
        return temp.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.temporada_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(new ViewHolder(convertView));

        } else
            holder = (ViewHolder) convertView.getTag();

        holder.nombre.setText(getItem(position).getNombre());
        holder.numero.setText(getItem(position).getNum()+"");

        return convertView;
    }

    private class ViewHolder{
        public TextView nombre, numero;

        public ViewHolder(View view){
            nombre = (TextView) view.findViewById(R.id.episode_name);
            numero = (TextView) view.findViewById(R.id.episode_number);
        }
    }
}
