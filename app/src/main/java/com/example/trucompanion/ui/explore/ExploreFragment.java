package com.example.trucompanion.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trucompanion.R;
import com.example.trucompanion.Searchable;
import com.example.trucompanion.model.Building;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment implements Searchable {

    private RecyclerView recyclerView;
    private BuildingAdapter adapter;
    private final List<Building> buildingList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        recyclerView = view.findViewById(R.id.recyclerExplore);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadBuildings();
        adapter = new BuildingAdapter(getContext(), new ArrayList<>(buildingList));
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadBuildings() {
        buildingList.add(new Building("Science Building",
                R.drawable.tru_building_1,
                "The Science Building houses labs and classrooms for biology, chemistry, and physics."));

        buildingList.add(new Building("House of Learning",
                R.drawable.tru_building_2,
                "A modern facility with classrooms, study spaces, and the TRU Library."));

        buildingList.add(new Building("Old Main",
                R.drawable.tru_building_3,
                "The main administrative and academic building of TRU, with stunning architecture."));

        buildingList.add(new Building("Campus Activity Centre",
                R.drawable.tru_building_4,
                "Includes the cafeteria, student services, and spaces for events and clubs."));
    }

    @Override
    public void onSearchQueryChanged(String query) {
        query = query.toLowerCase().trim();

        if (query.isEmpty()) {
            adapter.updateList(buildingList);
            return;
        }

        List<Building> filtered = new ArrayList<>();
        for (Building b : buildingList) {
            if (b.getName().toLowerCase().contains(query) ||
                    b.getDescription().toLowerCase().contains(query)) {
                filtered.add(b);
            }
        }

        adapter.updateList(filtered);
    }
}
