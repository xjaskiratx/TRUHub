package com.example.trucompanion.ui.explore;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trucompanion.BuildingDetailActivity;
import com.example.trucompanion.R;
import com.example.trucompanion.model.Building;

import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.ViewHolder> {

    private final Context context;
    private final List<Building> buildings;

    public BuildingAdapter(Context context, List<Building> buildings) {
        this.context = context;
        this.buildings = buildings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_building_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Building b = buildings.get(position);

        holder.name.setText(b.getName());
        holder.image.setImageResource(b.getImageResId());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BuildingDetailActivity.class);
            intent.putExtra("name", b.getName());
            intent.putExtra("description", b.getDescription());
            intent.putExtra("imageResId", b.getImageResId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return buildings.size();
    }

    public void updateList(List<Building> newList) {
        buildings.clear();
        buildings.addAll(newList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.buildingName);
            image = itemView.findViewById(R.id.buildingImage);
        }
    }
}
