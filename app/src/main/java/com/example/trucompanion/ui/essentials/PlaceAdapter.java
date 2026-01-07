package com.example.trucompanion.ui.essentials;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trucompanion.R;
import com.example.trucompanion.model.Place;
import com.example.trucompanion.ui.map.MapViewActivity; // new import

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private final List<Place> places;
    private final OnPlaceClickListener listener;
    private final Context context;

    /** Updated constructor with context for map intent */
    public PlaceAdapter(Context context, List<Place> places, OnPlaceClickListener listener) {
        this.context = context;
        this.places = places;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place_card, parent, false);
        return new PlaceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = places.get(position);
        holder.title.setText(place.getTitle());
        holder.desc.setText(place.getDescription());

        String meta = place.getCategory();
        if (place.getSubcategory() != null && !place.getSubcategory().isEmpty()) {
            meta += " • " + place.getSubcategory();
        }
        holder.meta.setText(meta);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlaceClicked(place);
            }
        });

        holder.mapPin.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MapViewActivity.class);
            intent.putExtra("title", place.getTitle());
            intent.putExtra("latitude", place.getLatitude());
            intent.putExtra("longitude", place.getLongitude());
            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public void updateList(List<Place> newList) {
        places.clear();
        places.addAll(newList);
        notifyDataSetChanged();
    }

    public int getPositionForPlace(Place place) {
        for (int i = 0; i < places.size(); i++) {
            if (places.get(i).getTitle().equals(place.getTitle())) {
                return i;
            }
        }
        return -1;
    }

    public interface OnPlaceClickListener {
        void onPlaceClicked(Place place);
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, meta;
        ImageView mapPin;

        PlaceViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.placeTitle);
            desc = itemView.findViewById(R.id.placeDesc);
            meta = itemView.findViewById(R.id.placeMeta);
            mapPin = itemView.findViewById(R.id.mapPinIcon);
        }
    }
}
