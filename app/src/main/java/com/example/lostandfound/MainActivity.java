package com.example.lostandfound;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.lostandfound.Data.FirebaseNames;
import com.example.lostandfound.Data.SharedPreferencesNames;
import com.example.lostandfound.FirebaseManagers.UserManager;
import com.example.lostandfound.Utility.AnimationManager;
import com.example.lostandfound.Utility.AutoLoadingManager;
import com.example.lostandfound.Utility.DeviceThemeManager;
import com.example.lostandfound.Utility.FontSizeManager;
import com.example.lostandfound.Utility.ImageManager;
import com.example.lostandfound.databinding.ActivityMainBinding;
import com.example.lostandfound.ui.ActivityLog.ActivityLogActivity;
import com.example.lostandfound.ui.HowItWorks.HowItWorksActivity;
import com.example.lostandfound.ui.Login.LoginActivity;
import com.example.lostandfound.ui.NewFound.NewFoundActivity;
import com.example.lostandfound.ui.NewLost.NewLostActivity;
import com.example.lostandfound.ui.Notifications.NotificationsActivity;
import com.example.lostandfound.ui.Profile.ProfileActivity;
import com.example.lostandfound.ui.ReportIssue.ReportIssueActivity;
import com.example.lostandfound.ui.Settings.SettingsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private ImageButton drawerMenuButton;
    private ImageButton closeDrawerButton;

    private boolean isMenuExpanded;

    private NavigationView navigationDrawer;
    private LinearLayout navHeader;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set toolbar as the topActionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialise firebase app
        FirebaseApp.initializeApp(MainActivity.this);
        db = FirebaseFirestore.getInstance();

        // create a new notification channel
        NotificationChannel channel = new NotificationChannel(
                "FCM_CHANNEL",
                "Notifications",
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        // request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
           boolean hasPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED;

           if (!hasPermission){
               ActivityCompat.requestPermissions(
                       this,
                       new String[]{Manifest.permission.POST_NOTIFICATIONS},
                       0
               );
           }
        }

        // load the theme from theme manager
        DeviceThemeManager.INSTANCE.loadTheme(
                MainActivity.this
        );

        // load the font size from the manager
        FontSizeManager.INSTANCE.loadFontSize(
                MainActivity.this
        );

        // load whether animations are enabled from the manager
        AnimationManager.INSTANCE.loadAnimationEnabled(
                MainActivity.this
        );

        // load whether auto loading is enabled
        AutoLoadingManager.INSTANCE.loadAutoLoadingEnabled(
                MainActivity.this
        );

        // set font size for this XML activity
        ViewGroup parentView = binding.drawerLayout;
        FontSizeManager.INSTANCE.setFontSizeXML(parentView, MainActivity.this);

        // set font size for the XML of the nav drawer layout
        /*
        ViewGroup parentViewNavDrawer = (ViewGroup) binding.navDrawerView.getHeaderView(0);
        FontSizeManager.INSTANCE.setFontSizeXML(parentViewNavDrawer, MainActivity.this);
         */

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_lost, R.id.navigation_found, R.id.navigation_chat)
                .setOpenableLayout(drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // set up a badge (Red dot) for the chat icon
        BadgeDrawable badgeDrawable = navView.getOrCreateBadge(R.id.navigation_chat);
        badgeDrawable.setBackgroundColor(ContextCompat.getColor(this, R.color.error));

        // initially not visible
        badgeDrawable.setVisible(false);


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

        // set up the sequential buttons for the 3 tutorial stages
        binding.tutorial1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tutorial1.setVisibility(GONE);
                binding.tutorial2.setVisibility(VISIBLE);
            }
        });
        binding.tutorial2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tutorial2.setVisibility(GONE);
                binding.tutorial3.setVisibility(VISIBLE);
            }
        });
        binding.tutorial3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tutorial3.setVisibility(GONE);
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
                    Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(i);

                } else if (clickedId == R.id.nav_drawer_profile){
                    Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(i);

                } else if (clickedId == R.id.nav_drawer_take_a_tour){
                    // make the first step of the tutorial show
                    binding.tutorial1.setVisibility(VISIBLE);
                    binding.tutorial2.setVisibility(GONE);
                    binding.tutorial3.setVisibility(GONE);

                } else if (clickedId == R.id.nav_drawer_how_it_works){
                    Intent i = new Intent(MainActivity.this, HowItWorksActivity.class);
                    startActivity(i);

                } else if (clickedId == R.id.nav_drawer_report_an_issue){
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

        // set tooltip text for the floating buttons
        binding.floatingActionButton.setTooltipText("Report item");
        binding.lostFloatingActionButton.setTooltipText("Report lost item");
        binding.foundFloatingActionButton.setTooltipText("Report found item");

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


        // Display the notifications red dot based on whether there are any unread messages from the current user
        // set up snapshot listener for any changed value of the query
        db.collection(FirebaseNames.COLLECTION_NOTIFICATIONS)
                .whereEqualTo(FirebaseNames.NOTIFICATION_USER_ID, UserManager.getUserID())
                .whereEqualTo(FirebaseNames.NOTIFICATION_IS_READ, false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            Toast.makeText(MainActivity.this, "Failed to listen for new notifications", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // check if the query is not empty
                        if (value != null && !value.isEmpty()){
                            binding.notificationsDot.setVisibility(VISIBLE);
                        } else {
                            binding.notificationsDot.setVisibility(GONE);
                        }
                    }
                });

        // initially when the app is first started, check if there are any unread notifications
        // if yes, display the red dot
        db.collection(FirebaseNames.COLLECTION_NOTIFICATIONS)
                .whereEqualTo(FirebaseNames.NOTIFICATION_USER_ID, UserManager.getUserID())
                .whereEqualTo(FirebaseNames.NOTIFICATION_IS_READ, false)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            binding.notificationsDot.setVisibility(VISIBLE);
                        } else {
                            binding.notificationsDot.setVisibility(GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Notification fetch error", e.getMessage());
                    }
                });

        // Display the bottom nav bar chat icon red dot
        // based on whether there are messages sent towards the current user and is unread
        db.collection(FirebaseNames.COLLECTION_CHATS)
                .whereEqualTo(FirebaseNames.CHAT_RECIPIENT_USER_ID, UserManager.getUserID())
                .whereEqualTo(FirebaseNames.CHAT_IS_READ_BY_RECIPIENT, false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            Toast.makeText(MainActivity.this, "Failed to listen for chats", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // check if the query is not empty
                        if (value != null && !value.isEmpty()){
                            badgeDrawable.setVisible(true);
                        } else {
                            badgeDrawable.setVisible(false);
                        }
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

        // make the overlay appear
        binding.overlay.animate().alpha(1f);

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

        // make the overlay not appear
        binding.overlay.animate().alpha(0f);

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
        if (UserManager.isUserLoggedIn()){ // user is signed in
            // hide the log in button
            binding.loginButton.setEnabled(false);
            binding.loginButton.setVisibility(GONE);
            binding.profileAndNotificationsIcons.setEnabled(true);
            binding.profileAndNotificationsIcons.setVisibility(VISIBLE);

            // get data from sharedpreferences
            SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesNames.NAME_USERS, MODE_PRIVATE);
            String firstName = sharedPreferences.getString(SharedPreferencesNames.USER_FIRSTNAME, "");
            String lastName = sharedPreferences.getString(SharedPreferencesNames.USER_LASTNAME, "");
            String email = sharedPreferences.getString(SharedPreferencesNames.USER_EMAIL, "");
            String avatar = sharedPreferences.getString(SharedPreferencesNames.USER_AVATAR, "");

            String displayedName = firstName + " " + lastName;

            // display the data in different parts of the application
            // make the linear layout visible
            navHeader.findViewById(R.id.user_info_linear_layout).setVisibility(VISIBLE);
            navHeader.findViewById(R.id.not_logged_in_textview).setVisibility(GONE);

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


            // show elements that have been hidden
            Menu navDrawerMenu = navigationDrawer.getMenu();
            navDrawerMenu.findItem(R.id.drawer_menu_menu).setVisible(true);
            navDrawerMenu.findItem(R.id.drawer_menu_create).setVisible(true);
            navDrawerMenu.findItem(R.id.drawer_menu_activity).setVisible(true);
            navDrawerMenu.findItem(R.id.nav_drawer_profile).setVisible(true);
            navDrawerMenu.findItem(R.id.nav_drawer_report_an_issue).setVisible(true);


        } else {
            // user is not signed in

            // make the linear layout not visible
            navHeader.findViewById(R.id.user_info_linear_layout).setVisibility(GONE);
            navHeader.findViewById(R.id.not_logged_in_textview).setVisibility(VISIBLE);

            // show the Login button
            binding.profileAndNotificationsIcons.setEnabled(false);
            binding.profileAndNotificationsIcons.setVisibility(GONE);
            binding.loginButton.setEnabled(true);
            binding.loginButton.setVisibility(VISIBLE);


            // hide some elements of the navigation drawer
            Menu navDrawerMenu = navigationDrawer.getMenu();
            navDrawerMenu.findItem(R.id.drawer_menu_menu).setVisible(false);
            navDrawerMenu.findItem(R.id.drawer_menu_create).setVisible(false);
            navDrawerMenu.findItem(R.id.drawer_menu_activity).setVisible(false);
            navDrawerMenu.findItem(R.id.nav_drawer_profile).setVisible(false);
            navDrawerMenu.findItem(R.id.nav_drawer_report_an_issue).setVisible(false);
        }
    }
}