package com.example.trucompanion.ui.essentials;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.example.trucompanion.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public final class CategoryChipFactory {

    public interface OnSelect { void onSelect(String label); }

    private CategoryChipFactory() {}

    public static void addChip(Context context,
                               ChipGroup group,
                               String label,
                               boolean checked,
                               OnSelect callback) {

        Chip chip = (Chip) LayoutInflater.from(context)
                .inflate(R.layout.prefab_chip, group, false);

        chip.setId(View.generateViewId());
        chip.setText(label);
        chip.setCheckable(true);
        chip.setClickable(true);
        chip.setEnsureMinTouchTargetSize(true);

        chip.setChecked(checked);

        chip.setChipBackgroundColor(
                ContextCompat.getColorStateList(context, R.color.chip_background_selector)
        );
        chip.setTextColor(
                ContextCompat.getColorStateList(context, R.color.chip_text_selector)
        );

        chip.setChipStrokeWidth(1f);

        chip.setOnClickListener(v -> {

            if (!chip.isChecked())
                chip.setChecked(true);

            updateChipGroupColors(context, group, chip.getText().toString());

            callback.onSelect(label);
        });

        int margin = (int) (8 * context.getResources().getDisplayMetrics().density);
        ViewGroup.MarginLayoutParams lp =
                new ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        lp.setMargins(margin, 0, margin, 0);
        chip.setLayoutParams(lp);

        group.addView(chip);

        updateChipGroupColors(context, group, checked ? label : null);
    }

    public static void updateChipGroupColors(Context context, ChipGroup group, String selectedLabel) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (!(child instanceof Chip)) continue;

            Chip chip = (Chip) child;
            boolean isSelected = chip.getText().toString().equals(selectedLabel);

            chip.setChecked(isSelected);

            if (isSelected) {
                chip.setChipStrokeWidth(1f);
            } else {
                chip.setChipStrokeWidth(0f);
            }
        }
    }
}
