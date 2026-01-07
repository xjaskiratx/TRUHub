package com.example.trucompanion.ui.community;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trucompanion.R;
import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Event;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.model.User;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, dateInput, timeInput;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        titleInput = findViewById(R.id.eventTitleInput);
        descriptionInput = findViewById(R.id.eventDescriptionInput);
        dateInput = findViewById(R.id.eventDateInput);
        timeInput = findViewById(R.id.eventTimeInput);
        saveBtn = findViewById(R.id.saveEventBtn);

        AppDatabase db = AppDatabase.getDatabase(this);

        saveBtn.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String desc = descriptionInput.getText().toString().trim();
            String date = dateInput.getText().toString().trim();
            String time = timeInput.getText().toString().trim();

            if (title.isEmpty() || desc.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            Long eventTimestamp = tryParseDateTime(date, time);

            if (eventTimestamp == null) {
                Toast.makeText(this, "Invalid date or time format", Toast.LENGTH_SHORT).show();
                return;
            }

            long now = System.currentTimeMillis();
            long minAllowed = now + (3 * 60 * 60 * 1000); // 3 hours

            if (eventTimestamp < minAllowed) {
                Toast.makeText(this,
                        "Event must be scheduled at least 3 hours from now.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Session s = db.sessionDao().getLastSession();
            User u = db.userDao().getUserById(s.userId);

            Event ev = new Event();
            ev.title = title;
            ev.description = desc;
            ev.date = date;
            ev.time = time;
            ev.clubId = 0;
            ev.contributorId = u.uid;

            db.eventDao().insertEvent(ev);

            Toast.makeText(this, "Event posted", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private Long tryParseDateTime(String date, String time) {

        String[] dateFormats = {
                "MMMM dd, yyyy",
                "MMM dd, yyyy",
                "MM/dd/yyyy",
                "dd-MM-yyyy"
        };

        String[] timeFormats = {
                "hh:mm a",
                "h:mm a",
                "hh a",
                "h a",
                "hha",
                "haa"
        };

        for (String df : dateFormats) {
            for (String tf : timeFormats) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(df + " " + tf, Locale.getDefault());
                    Date parsed = sdf.parse(date + " " + time);
                    if (parsed != null) {
                        return parsed.getTime();
                    }
                } catch (Exception ignored) {}
            }
        }

        return null;
    }
}
