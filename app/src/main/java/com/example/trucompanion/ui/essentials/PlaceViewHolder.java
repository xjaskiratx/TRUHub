package com.example.trucompanion.ui.essentials;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.trucompanion.R;

public class PlaceViewHolder extends RecyclerView.ViewHolder {
    TextView title, desc;

    public PlaceViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.placeTitle);
        desc = itemView.findViewById(R.id.placeDesc);
    }
}
