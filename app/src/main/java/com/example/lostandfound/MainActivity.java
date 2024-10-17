package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import com.example.lostandfound.ui.profile.ProfileActivity;
import com.example.lostandfound.ui.settings.SettingsActivity;
import com.example.lostandfound.ui.settings.SettingsViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.lostandfound.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;

    private DrawerLayout drawerLayout;
    private ImageButton drawerMenuButton;
    private ImageButton closeDrawerButton;

    private boolean isMenuExpanded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set up main model
        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Set toolbar as the topActionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_lost, R.id.navigation_found, R.id.navigation_search, R.id.navigation_chat)
                .setOpenableLayout(drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        // set the menu button to open up the drawer
        drawerLayout = findViewById(R.id.drawer_layout);

        drawerMenuButton = findViewById(R.id.drawer_menu_button);
        drawerMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.open();
                }
            }
        });

        // set up button to close drawer
        NavigationView navigationView = findViewById(R.id.nav_drawer_view);
        LinearLayout navHeader = (LinearLayout) navigationView.getHeaderView(0);

        closeDrawerButton = navHeader.findViewById(R.id.drawer_close_button);
        closeDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        // set on click listeners for the items in the nav view
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int clickedId = item.getItemId();

                if (clickedId == R.id.nav_drawer_profile){
                    // start profile activity
                    Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(i);

                } else if (clickedId == R.id.nav_drawer_settings){
                    // start settings activity
                    Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(i);
                }

                // close the drawer after an item is clicked
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        // set up expand and shrink menu visuals
        isMenuExpanded = false;
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isMenuExpanded){
                    expandMenu();

                } else {
                    collapseMenu();

                }
            }
        });




    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    // methods to set up animation to open and close the floating action button
    public void expandMenu(){
        isMenuExpanded = true;
    }

    public void collapseMenu(){
        isMenuExpanded = false;

        binding.foundFloatingActionButton.animate().translationY(300);
    }
}