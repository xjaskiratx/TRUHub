package com.example.trucompanion.ui.essentials;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trucompanion.MainActivity;
import com.example.trucompanion.R;
import com.example.trucompanion.Searchable;
import com.example.trucompanion.model.Place;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EssentialsFragment extends Fragment implements Searchable, PlaceAdapter.OnPlaceClickListener {

    private ChipGroup levelOneGroup, levelTwoGroup;
    private View levelTwoScroll;
    private RecyclerView placesRecycler;
    private PlaceAdapter adapter;

    private final List<Place> allPlaces = new ArrayList<>();
    private final Map<String, List<String>> subMap = new HashMap<>();

    private String activeCategory = "Food";
    private String activeSubcategory = null;
    private String activeQuery = "";

    private static final String[] MAIN_CATEGORIES = {
            "Food", "Groceries", "Worship", "Viewpoints", "Transit"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_essentials, container, false);

        levelOneGroup = v.findViewById(R.id.levelOneGroup);
        levelTwoGroup = v.findViewById(R.id.levelTwoGroup);
        levelTwoScroll = v.findViewById(R.id.levelTwoScroll);
        placesRecycler = v.findViewById(R.id.placesRecycler);

        placesRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        seedData();

        adapter = new PlaceAdapter(requireContext(), new ArrayList<>(allPlaces), this);
        placesRecycler.setAdapter(adapter);

        setupLevelOneChips();

        return v;
    }

    private void seedData() {
        subMap.put("Worship", List.of("Sikh", "Hindu", "Muslim", "Church"));
        subMap.put("Food", List.of("Asian", "Fast Food", "Indian"));
        subMap.put("Viewpoints", List.of("Nature", "City"));

        allPlaces.add(new Place("U&M Restaurant", "Affordable Asian cuisine near campus",
                "Food", "Asian", 50.6712, -120.3655));
        allPlaces.add(new Place("Pizza Hut", "Fast food option near Sahali Mall",
                "Food", "Fast Food", 50.6634, -120.3559));
        allPlaces.add(new Place("Maurya’s Restaurant", "Authentic Indian cuisine downtown",
                "Food", "Indian", 50.6761, -120.3310));

        allPlaces.add(new Place("Safeway", "Groceries and daily essentials",
                "Groceries", "", 50.6717, -120.3540));
        allPlaces.add(new Place("Superstore", "Bulk groceries and household items",
                "Groceries", "", 50.6693, -120.3565));

        allPlaces.add(new Place("ISKCON Temple", "Peaceful Hindu temple",
                "Worship", "Hindu", 50.6847, -120.3402));
        allPlaces.add(new Place("Gurdwara Sahib", "Sikh temple near downtown",
                "Worship", "Sikh", 50.6739, -120.3308));
        allPlaces.add(new Place("Kamloops Islamic Centre", "Mosque near Columbia Street",
                "Worship", "Muslim", 50.6696, -120.3482));
        allPlaces.add(new Place("St. Paul’s Cathedral", "Historic downtown church",
                "Worship", "Church", 50.6763, -120.3315));

        allPlaces.add(new Place("Peterson Creek Park", "Beautiful nature viewpoint",
                "Viewpoints", "Nature", 50.6605, -120.3408));
        allPlaces.add(new Place("TRU Lookout", "Panoramic city views near campus",
                "Viewpoints", "City", 50.6719, -120.3650));

        allPlaces.add(new Place("BC Transit Stop", "Bus stop right outside TRU",
                "Transit", "", 50.6724, -120.3661));
    }

    private void setupLevelOneChips() {
        levelOneGroup.removeAllViews();
        levelOneGroup.setSingleSelection(true);

        for (String cat : MAIN_CATEGORIES) {
            CategoryChipFactory.addChip(requireContext(), levelOneGroup, cat,
                    cat.equals("Food"), this::onLevelOneSelected);
        }

        onLevelOneSelected("Food");
    }

    private void onLevelOneSelected(String category) {
        ((MainActivity) requireActivity()).clearSearchFocus();

        activeCategory = category;
        activeSubcategory = null;
        levelTwoGroup.removeAllViews();
        levelTwoGroup.setSingleSelection(true);

        List<String> subcats = subMap.get(category);

        if (subcats != null && !subcats.isEmpty()) {
            levelTwoScroll.setVisibility(View.VISIBLE);
            for (String sub : subcats) {
                CategoryChipFactory.addChip(requireContext(), levelTwoGroup, sub,
                        sub.equals(subcats.get(0)), subSel -> {
                            ((MainActivity) requireActivity()).clearSearchFocus();
                            activeSubcategory = subSel;
                            filterPlaces();
                        });
            }

            activeSubcategory = subcats.get(0);

            CategoryChipFactory.updateChipGroupColors(requireContext(), levelTwoGroup, activeSubcategory);

        } else {
            levelTwoScroll.setVisibility(View.GONE);
        }

        filterPlaces();
    }

    @Override
    public void onSearchQueryChanged(String query) {
        activeQuery = query.trim();
        filterPlaces();
    }

    @Override
    public void onPlaceClicked(Place place) {
        ((MainActivity) requireActivity()).clearSearchFocus();
        ((MainActivity) requireActivity()).clearSearchBar();

        activeQuery = "";
        navigateToPlace(place);
        filterPlaces();
    }

    private void navigateToPlace(Place place) {

        activeCategory = place.getCategory();
        activeSubcategory = place.getSubcategory();

        // Update Level 1 chip
        for (int i = 0; i < levelOneGroup.getChildCount(); i++) {
            Chip chip = (Chip) levelOneGroup.getChildAt(i);
            if (chip.getText().toString().equals(activeCategory)) {
                levelOneGroup.check(chip.getId());
                break;
            }
        }

        levelTwoGroup.removeAllViews();
        List<String> subcats = subMap.get(activeCategory);

        if (subcats != null && !subcats.isEmpty()) {
            levelTwoScroll.setVisibility(View.VISIBLE);

            for (String sub : subcats) {
                CategoryChipFactory.addChip(requireContext(), levelTwoGroup, sub,
                        sub.equals(activeSubcategory), subSel -> {
                            ((MainActivity) requireActivity()).clearSearchFocus();
                            activeSubcategory = subSel;
                            filterPlaces();
                        });
            }

            CategoryChipFactory.updateChipGroupColors(requireContext(), levelTwoGroup, activeSubcategory);

        } else {
            levelTwoScroll.setVisibility(View.GONE);
        }

        filterPlaces();

        placesRecycler.post(() -> {
            int position = adapter.getPositionForPlace(place);
            if (position != -1) placesRecycler.smoothScrollToPosition(position);
        });
    }

    private void filterPlaces() {
        List<Place> filtered = new ArrayList<>();

        for (Place p : allPlaces) {
            boolean matchesCategory = p.getCategory().equals(activeCategory);
            boolean matchesSub = (activeSubcategory == null || activeSubcategory.isEmpty())
                    || p.getSubcategory().equals(activeSubcategory);

            boolean matchesSearch = activeQuery.isEmpty()
                    || p.getTitle().toLowerCase().contains(activeQuery.toLowerCase())
                    || p.getDescription().toLowerCase().contains(activeQuery.toLowerCase())
                    || p.getCategory().toLowerCase().contains(activeQuery.toLowerCase())
                    || p.getSubcategory().toLowerCase().contains(activeQuery.toLowerCase());

            if (!activeQuery.isEmpty()) {
                if (matchesSearch) filtered.add(p);
            } else {
                if (matchesCategory && matchesSub) filtered.add(p);
            }
        }

        adapter.updateList(filtered);
    }
}
