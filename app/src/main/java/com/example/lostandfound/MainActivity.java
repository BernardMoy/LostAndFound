package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
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
        // the alpha value is set in the XML to be initially 0
        isMenuExpanded = false;

        binding.foundFloatingActionButton.setEnabled(false);
        binding.lostFloatingActionButton.setEnabled(false);

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

        // get dimensions in the form of dp, they do not have to be converted to px.
        float firstButtonDistance = -(getResources().getDimension(R.dimen.floating_button_size) + getResources().getDimension(R.dimen.content_margin));
        float secondButtonDistance = firstButtonDistance * 2;

        // enable the buttons
        binding.lostFloatingActionButton.setEnabled(true);
        binding.foundFloatingActionButton.setEnabled(true);

        // translate the buttons upwards
        binding.foundFloatingActionButton.animate().translationY(firstButtonDistance);
        binding.foundButtonText.animate().translationY(firstButtonDistance);
        binding.lostFloatingActionButton.animate().translationY(secondButtonDistance);
        binding.lostButtonText.animate().translationY(secondButtonDistance);

        // set alpha to be visible
        binding.foundFloatingActionButton.animate().alpha(1f);
        binding.foundButtonText.animate().alpha(1f);
        binding.lostFloatingActionButton.animate().alpha(1f);
        binding.lostButtonText.animate().alpha(1f);

        // change the drawable on the floating button
        binding.floatingActionButton.setImageResource(R.drawable.cross_icon);
    }

    public void collapseMenu(){
        isMenuExpanded = false;

        // disable the buttons
        binding.lostFloatingActionButton.setEnabled(false);
        binding.foundFloatingActionButton.setEnabled(false);

        // translate the buttons downwards
        binding.foundFloatingActionButton.animate().translationY(0);
        binding.foundButtonText.animate().translationY(0);
        binding.lostFloatingActionButton.animate().translationY(0);
        binding.lostButtonText.animate().translationY(0);

        // set alpha to be invisible
        binding.foundFloatingActionButton.animate().alpha(0f);
        binding.foundButtonText.animate().alpha(0f);
        binding.lostFloatingActionButton.animate().alpha(0f);
        binding.lostButtonText.animate().alpha(0f);

        // change the drawable on the floating button
        binding.floatingActionButton.setImageResource(R.drawable.add_icon);
    }
}