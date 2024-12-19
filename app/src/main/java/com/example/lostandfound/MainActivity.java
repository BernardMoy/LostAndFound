package com.example.lostandfound;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.lostandfound.Utility.ImageManager;
import com.example.lostandfound.Utility.SharedPreferencesNames;
import com.example.lostandfound.ui.ActivityLog.ActivityLogActivity;
import com.example.lostandfound.ui.Login.LoginActivity;
import com.example.lostandfound.ui.NewFound.NewFoundActivity;
import com.example.lostandfound.ui.NewLost.NewLostActivity;
import com.example.lostandfound.ui.Notifications.NotificationsActivity;
import com.example.lostandfound.ui.Profile.ProfileActivity;
import com.example.lostandfound.ui.ReportIssue.ReportIssueActivity;
import com.example.lostandfound.ui.Settings.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.lostandfound.databinding.ActivityMainBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;

    private DrawerLayout drawerLayout;
    private ImageButton drawerMenuButton;
    private ImageButton closeDrawerButton;

    private boolean isMenuExpanded;

    private NavigationView navigationDrawer;
    private LinearLayout navHeader;

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


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        BottomNavigationView navView = findViewById(R.id.nav_view);
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
        navigationDrawer = findViewById(R.id.nav_drawer_view);
        navHeader = (LinearLayout) navigationDrawer.getHeaderView(0);

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
        navigationDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int clickedId = item.getItemId();

                if (clickedId == R.id.nav_drawer_my_lost){
                    navView.setSelectedItemId(R.id.navigation_lost);

                } else if (clickedId == R.id.nav_drawer_my_found) {
                    navView.setSelectedItemId(R.id.navigation_found);

                } else if (clickedId == R.id.nav_drawer_search) {
                    navView.setSelectedItemId(R.id.navigation_search);

                } else if (clickedId == R.id.nav_drawer_chat){
                    navView.setSelectedItemId(R.id.navigation_chat);

                } else if (clickedId == R.id.nav_drawer_notifications){
                    Intent i = new Intent(MainActivity.this, NotificationsActivity.class);
                    startActivity(i);

                } else if (clickedId == R.id.nav_drawer_new_lost){
                    Intent i = new Intent(MainActivity.this, NewLostActivity.class);
                    startActivity(i);

                } else if (clickedId == R.id.nav_drawer_new_found){
                    Intent i = new Intent(MainActivity.this, NewFoundActivity.class);
                    startActivity(i);

                } else if (clickedId == R.id.nav_drawer_activity_log){
                    Intent i = new Intent(MainActivity.this, ActivityLogActivity.class);
                    startActivity(i);

                } else if (clickedId == R.id.nav_drawer_settings){
                    // start Settings activity
                    Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(i);

                } else if (clickedId == R.id.nav_drawer_profile){
                    // start Profile activity
                    Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(i);

                } else if (clickedId == R.id.nav_drawer_take_a_tour){

                } else if (clickedId == R.id.nav_drawer_how_it_works){

                } else if (clickedId == R.id.nav_drawer_report_an_issue){
                    // start report issue activity
                    Intent i = new Intent(MainActivity.this, ReportIssueActivity.class);
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

        // listeners for the lost and found buttons
        binding.lostFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapseMenu();
                Intent i = new Intent(MainActivity.this, NewLostActivity.class);
                startActivity(i);
            }
        });

        binding.foundFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapseMenu();
                Intent i = new Intent(MainActivity.this, NewFoundActivity.class);
                startActivity(i);
            }
        });

        // listener for the + button
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

        // start notification activity when the notif icon is clicked
        binding.toolbarNotificationsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NotificationsActivity.class);
                startActivity(i);
            }
        });

        // Start Profile activity when the Profile icon is clicked
        binding.toolbarProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        // Start log in activity when the log in button is clicked
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUserDisplayedData();
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


    // method to update the displayed data depending on whether they are logged in
    public void updateUserDisplayedData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            // hide the log in button
            binding.loginButton.setEnabled(false);
            binding.loginButton.setVisibility(View.GONE);
            binding.profileAndNotificationsIcons.setEnabled(true);
            binding.profileAndNotificationsIcons.setVisibility(View.VISIBLE);

            // get data from sharedpreferences
            SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesNames.NAME_USERS, MODE_PRIVATE);
            String firstName = sharedPreferences.getString(SharedPreferencesNames.USER_FIRSTNAME, "");
            String lastName = sharedPreferences.getString(SharedPreferencesNames.USER_LASTNAME, "");
            String email = sharedPreferences.getString(SharedPreferencesNames.USER_EMAIL, "");
            String avatar = sharedPreferences.getString(SharedPreferencesNames.USER_AVATAR, "");

            String displayedName = firstName + " " + lastName;

            // display the data in different parts of the application
            // make the linear layout visible
            navHeader.findViewById(R.id.user_info_linear_layout).setVisibility(View.VISIBLE);
            navHeader.findViewById(R.id.not_logged_in_textview).setVisibility(View.GONE);

            // nav drawer on the left
            ((TextView) navHeader.findViewById(R.id.nav_drawer_name)).setText(displayedName);
            ((TextView) navHeader.findViewById(R.id.nav_drawer_email)).setText(email);

            // get the user's avatar if they are logged in
            // set the toolbar avatar
            if (avatar.isEmpty()){
                binding.toolbarProfileIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.profile_icon));
            } else {
                binding.toolbarProfileIcon.setImageURI(ImageManager.INSTANCE.stringToUri(this, avatar));
            }

            // set the nav drawer avatar
            if (avatar.isEmpty()){
                ((ShapeableImageView)navHeader.findViewById(R.id.nav_drawer_profile_icon))
                        .setImageDrawable(ContextCompat.getDrawable(this, R.drawable.profile_icon));
            } else {
                ((ShapeableImageView)navHeader.findViewById(R.id.nav_drawer_profile_icon))
                        .setImageURI(ImageManager.INSTANCE.stringToUri(this, avatar));
            }

        } else {
            // user is not signed in

            // make the linear layout not visible
            navHeader.findViewById(R.id.user_info_linear_layout).setVisibility(View.GONE);
            navHeader.findViewById(R.id.not_logged_in_textview).setVisibility(View.VISIBLE);

            // show the Login button
            binding.profileAndNotificationsIcons.setEnabled(false);
            binding.profileAndNotificationsIcons.setVisibility(View.GONE);
            binding.loginButton.setEnabled(true);
            binding.loginButton.setVisibility(View.VISIBLE);
        }
    }
}