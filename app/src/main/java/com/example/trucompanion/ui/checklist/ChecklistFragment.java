package com.example.trucompanion.ui.checklist;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trucompanion.R;
import com.example.trucompanion.Searchable;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ChecklistFragment extends Fragment implements Searchable {

    private RecyclerView recyclerView;
    private ChecklistAdapter adapter;
    private ChipGroup filterChipGroup;
    private FloatingActionButton fab;

    private AppDatabase db;
    private final List<Task> current = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_checklist, container, false);

        recyclerView = v.findViewById(R.id.checklistRecycler);
        filterChipGroup = v.findViewById(R.id.filterChipGroup);
        fab = v.findViewById(R.id.fabAddTask);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = AppDatabase.getDatabase(requireContext());

        if (db.checklistDao().getAll().isEmpty()) {
            seedDefaults();
        }

        current.clear();
        current.addAll(db.checklistDao().getAll());
        adapter = new ChecklistAdapter(requireContext(), current, db);
        recyclerView.setAdapter(adapter);

        updateChipStyles(filterChipGroup);

        filterChipGroup.setOnCheckedStateChangeListener((group, ids) -> {
            if (ids == null || ids.isEmpty()) return;

            updateChipStyles(group);

            int id = ids.get(0);
            if (id == R.id.chipDefault) {
                updateList(db.checklistDao().getDefaultTasks());
            } else if (id == R.id.chipCustom) {
                updateList(db.checklistDao().getCustomTasks());
            } else {
                updateList(db.checklistDao().getAll());
            }
        });

        fab.setOnClickListener(view -> showAddDialog());

        if (filterChipGroup.getCheckedChipId() == View.NO_ID) {
            filterChipGroup.check(R.id.chipAll);
            updateChipStyles(filterChipGroup);
        }

        return v;
    }

    private void updateChipStyles(ChipGroup group) {
        int checkedId = group.getCheckedChipId();

        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (!(child instanceof Chip)) continue;

            Chip chip = (Chip) child;

            if (chip.getId() == checkedId) {

                chip.setChipBackgroundColor(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark)
                ));
                chip.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

                chip.setCheckedIconTint(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), android.R.color.white)
                ));
                chip.setChipIconTint(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), android.R.color.white)
                ));

            } else {

                chip.setChipBackgroundColor(null);
                chip.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));

                chip.setCheckedIconTint(null);
                chip.setChipIconTint(null);
            }
        }
    }

    private void updateList(List<Task> tasks) {
        adapter.updateList(tasks);
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_task, null);
        EditText titleEt = dialogView.findViewById(R.id.taskTitleInput);
        EditText descEt = dialogView.findViewById(R.id.taskDescInput);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Add Task")
                .setView(dialogView)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
            );
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.white)
            );

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.black)
            );

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String title = titleEt.getText().toString().trim();
                String desc = descEt.getText().toString().trim();

                if (!title.isEmpty()) {
                    db.checklistDao().insert(new Task(title, desc, false, false));
                    refreshList();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }


    private void seedDefaults() {
        db.checklistDao().insert(new Task("Get TRU ID", "Visit the Student ID Centre", false, true));
        db.checklistDao().insert(new Task("Set up bank account", "Open an account in Kamloops", false, true));
        db.checklistDao().insert(new Task("Buy bus pass", "Get your U-Pass from TRUSU", false, true));
    }

    private void refreshList() {
        int id = filterChipGroup.getCheckedChipId();
        if (id == R.id.chipDefault) {
            updateList(db.checklistDao().getDefaultTasks());
        } else if (id == R.id.chipCustom) {
            updateList(db.checklistDao().getCustomTasks());
        } else {
            updateList(db.checklistDao().getAll());
        }
    }

    @Override
    public void onSearchQueryChanged(String query) {
        String pattern = "%" + query.trim() + "%";
        List<Task> results;

        if (query.isEmpty()) {
            refreshList();
            return;
        }

        results = db.checklistDao().searchTasks(pattern);
        adapter.updateList(results);
    }
}
