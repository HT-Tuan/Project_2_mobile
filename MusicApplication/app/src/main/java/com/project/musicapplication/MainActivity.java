package com.project.musicapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.project.musicapplication.activity.LoginActivity;
import com.project.musicapplication.firebase.firebaseObject;
import com.project.musicapplication.util.ActivityUtil;

public class MainActivity extends AppCompatActivity {
    ImageView imageNav;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    NavigationView navigationView;
    Menu menu;
    MenuItem loginMenuItem, logoutMenuItem;
    LinearLayout notification;
    ImageView imgPlayOrPause;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_trangchu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.page_trangchu:
                        // Thực hiện các hành động khi người dùng chọn trang chủ
//                        Toast.makeText(getApplicationContext(), " Thực hiện các hành động khi người dùng chọn trang chủ", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.page_timkiem:
                        // Thực hiện các hành động khi người dùng chọn tìm kiếm
                        break;
                    case R.id.page_playlist:
                        // Thực hiện các hành động khi người dùng chọn playlist
                        break;
                    case R.id.page_canhan:
                        // Thực hiện các hành động khi người dùng chọn cá nhân
                        break;
                }
                return true;
            }
        });

        navigationView = findViewById(R.id.navigation_view);
        menu = navigationView.getMenu();
        loginMenuItem = menu.findItem(R.id.login);
        logoutMenuItem = menu.findItem(R.id.logout);
        loginMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ActivityUtil.openActivity(MainActivity.this, LoginActivity.class);
                return true;
            }
        });
        logoutMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                firebaseObject.myUser = null;
                FirebaseAuth.getInstance().signOut();
                firebaseObject.mAuth = FirebaseAuth.getInstance();
                firebaseObject.user = firebaseObject.mAuth.getCurrentUser();
                Toast.makeText( MainActivity.this, "Logout success", Toast.LENGTH_LONG).show();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        imageNav = findViewById(R.id.image_nav);
        drawerLayout = findViewById(R.id.drawer_layout);

        imageNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        imgPlayOrPause = findViewById(R.id.img_play_or_pause);
        imgPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }
}