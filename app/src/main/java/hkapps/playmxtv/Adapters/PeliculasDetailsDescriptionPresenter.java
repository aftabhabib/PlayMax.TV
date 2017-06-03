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

package hkapps.playmxtv.Adapters;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.w3c.dom.Text;

import hkapps.playmxtv.Model.Ficha;
import hkapps.playmxtv.R;

public class PeliculasDetailsDescriptionPresenter extends Presenter {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peliculas_details_description, null);
        return new PeliculasDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        PeliculasDetailViewHolder pview = (PeliculasDetailViewHolder) viewHolder;

        Ficha movie = (Ficha) item;

        if (movie != null) {
            pview.title.setText(movie.getTitle());
            pview.subtitle.setText("Duracion: "+movie.getDuration()+" min");
            pview.body.setText(movie.getSinopsis());
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

    public class PeliculasDetailViewHolder extends Presenter.ViewHolder{

        public TextView title,subtitle,body;
        public PeliculasDetailViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.lb_details_description_title);
            subtitle = (TextView) view.findViewById(R.id.lb_details_description_subtitle);
            body = (TextView) view.findViewById(R.id.lb_details_description_body);
        }
    }
}
