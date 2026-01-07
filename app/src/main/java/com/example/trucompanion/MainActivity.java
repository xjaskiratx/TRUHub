package com.example.trucompanion;

import android.content.Context;
import android.content.Intent; // Required for Intent
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.trucompanion.data.AppDatabase;
import com.example.trucompanion.model.Session;
import com.example.trucompanion.ui.auth.LoginActivity; // Required for Logout redirect
import com.example.trucompanion.ui.checklist.ChecklistFragment;
import com.example.trucompanion.ui.community.CommunityFragment;
import com.example.trucompanion.ui.essentials.EssentialsFragment;
import com.example.trucompanion.ui.explore.ExploreFragment;
import com.example.trucompanion.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText searchBar;
    private TextInputLayout searchBarContainer;

    private int currentIndex = -1;
    private int currentItemId = R.id.nav_checklist;

    private boolean isGuest = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        searchBar = findViewById(R.id.searchBar);
        searchBarContainer = findViewById(R.id.searchBarContainer);

        configureSearchBarAppearance();

        AppDatabase db = AppDatabase.getDatabase(this);
        Session session = db.sessionDao().getLastSession();

        isGuest = (session == null);

        bottomNav.setOnItemSelectedListener(item -> {
            int newItemId = item.getItemId();
            int newIndex = getMenuIndex(bottomNav, newItemId);

            Fragment selected = null;

            if (newItemId == R.id.nav_checklist) {
                selected = new ChecklistFragment();
                showSearchBar(true);
                setPlaceholder("Search checklist items…");

            } else if (newItemId == R.id.nav_places) {
                selected = new EssentialsFragment();
                showSearchBar(true);
                setPlaceholder("Search places around the city…");

            } else if (newItemId == R.id.nav_explore) {
                selected = new ExploreFragment();
                showSearchBar(true);
                setPlaceholder("Search buildings…");

            } else if (newItemId == R.id.nav_community) {
                selected = new CommunityFragment();
                if (isGuest) {
                    showSearchBar(false);
                } else {
                    showSearchBar(true);
                    setPlaceholder("Search clubs & events…");
                }

            } else if (newItemId == R.id.nav_profile) {
                selected = new ProfileFragment();
                showSearchBar(false);
            }

            if (selected == null) return false;

            if (currentIndex == -1 || newIndex == currentIndex) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, selected)
                        .commit();
                currentIndex = newIndex;
                currentItemId = newItemId;
                return true;
            }

            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            if (newIndex > currentIndex) {
                tx.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                tx.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            }

            tx.replace(R.id.fragmentContainer, selected).commit();
            currentIndex = newIndex;
            currentItemId = newItemId;
            return true;
        });

        bottomNav.setSelectedItemId(currentItemId);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Fragment current = getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentContainer);

                if (current instanceof Searchable) {
                    ((Searchable) current).onSearchQueryChanged(s.toString());
                }
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    public void logoutUser() {
        AppDatabase db = AppDatabase.getDatabase(this);

        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.sessionDao().deleteAll();

            runOnUiThread(() -> {
                recreate();
            });
        });
    }

    private void configureSearchBarAppearance() {
        if (searchBar == null) return;

        searchBar.setSingleLine(true);
        searchBar.setMaxLines(1);
        searchBar.setIncludeFontPadding(false);
        searchBar.setGravity(Gravity.CENTER_VERTICAL);
        searchBar.setPadding(16, 0, 16, 0);
    }

    private int getMenuIndex(BottomNavigationView nav, int itemId) {
        Menu menu = nav.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).getItemId() == itemId) return i;
        }
        return -1;
    }

    private void showSearchBar(boolean show) {
        if (searchBarContainer == null) return;

        if (show) {
            searchBarContainer.setVisibility(View.VISIBLE);
        } else {
            searchBarContainer.setVisibility(View.GONE);
            clearSearchBar();
            clearSearchFocus();
        }
    }

    private void setPlaceholder(String text) {
        if (searchBar != null) searchBar.setHint(text);
    }

    public void clearSearchBar() {
        if (searchBar != null && searchBar.getText() != null) {
            searchBar.setText("");
        }
    }

    public void clearSearchFocus() {
        if (searchBar != null) {
            searchBar.clearFocus();
            View currentFocus = this.getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
            }
        }
    }
}