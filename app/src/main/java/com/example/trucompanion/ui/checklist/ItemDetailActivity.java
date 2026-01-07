package com.example.trucompanion.ui.checklist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.trucompanion.R;
import com.google.android.material.appbar.MaterialToolbar;

public class ItemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        TextView titleTv = findViewById(R.id.detailTitle);
        TextView descTv = findViewById(R.id.detailDesc);

        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("desc");

        titleTv.setText(title);
        descTv.setText(desc);
    }
}
