package hkapps.playmxtv.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import hkapps.playmxtv.Model.Capitulo;
import hkapps.playmxtv.Model.Enlace;
import hkapps.playmxtv.Model.Temporada;
import hkapps.playmxtv.R;

/**
 * Created by hkfuertes on 20/05/2017.
 */

public class EnlacesAdapter extends BaseAdapter {

    List<Enlace> links;

    public EnlacesAdapter(List<Enlace> links){
        this.links = links;
    }

    @Override
    public int getCount() {
        return links.size();
    }

    @Override
    public Enlace getItem(int position) {
        return links.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.enlace_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(new ViewHolder(convertView));

        } else
            holder = (ViewHolder) convertView.getTag();

        holder.idioma.setText(getItem(position).getIdioma()+"");
        holder.calidad.setText(getItem(position).getCalidad()+"");

        return convertView;
    }

    private class ViewHolder{
        public TextView idioma, calidad;

        public ViewHolder(View view){
            idioma = (TextView) view.findViewById(R.id.link_lang);
            calidad = (TextView) view.findViewById(R.id.link_quality);
        }
    }
}
