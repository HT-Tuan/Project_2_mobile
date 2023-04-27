package com.project.musicapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.musicapplication.activity.LoginActivity;
import com.project.musicapplication.adapter.ViewpageAdapter;
import com.project.musicapplication.adapter.tuanSongAdapter;
import com.project.musicapplication.firebase.firebaseObject;
import com.project.musicapplication.model.Song;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class MainActivity extends AppCompatActivity implements tuanSongAdapter.OnItemClickListener {

    private SearchView search_view;
    private tuanSongAdapter songAdapter;
    private List<Song> mSongs;
    private ExoPlayer player;
    private ConstraintLayout playerView;
    private RecyclerView recyclerView;
    private FirebaseFirestore mStorage;
    private ProgressBar progress_circle;
    private boolean isSuccess = false;
    private static final int REQUEST_CODE = 123;
    private ViewPager viewPager;


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
//                    case R.id.page_timkiem:
                        // Thực hiện các hành động khi người dùng chọn tìm kiếm
//                        break;
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
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
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
                isSuccess = false;
                songAdapter.filterSongs(new ArrayList<>());
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
        // // //
        search_view = findViewById(R.id.search_view);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSong(newText.toLowerCase());
                return true;
            }
        });
        player = new ExoPlayer.Builder(this).build();
        mSongs = new ArrayList<>();
        playerView = findViewById(R.id.playerView);

        songAdapter = new tuanSongAdapter(MainActivity.this, mSongs, player, playerView);
        songAdapter.setOnItemClickListener(MainActivity.this);
        mStorage = FirebaseFirestore.getInstance();
        viewPager = findViewById(R.id.viewPager);
        //
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(isSuccess == false)
                    return false;
                switch (item.getItemId()){
                    case R.id.page_trangchu:
                        viewPager.setCurrentItem(0);
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        showHome();
                        break;
                    case R.id.page_playlist:
                        viewPager.setCurrentItem(1);
                        Toast.makeText(MainActivity.this, "PlayList", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.page_canhan:
                        viewPager.setCurrentItem(2);
                        Toast.makeText(MainActivity.this, "Personal", Toast.LENGTH_SHORT).show();
                        break;

                }
                return true;
            }
        });
        setUpViewPage();
        //thong bao
        Toast.makeText(MainActivity.this, "Bạn vui lòng đăng nhập/đăng ký trước khi sử dụng app", Toast.LENGTH_SHORT).show();

    }

    private void setUpViewPage() {
        ViewpageAdapter viewpageAdapter = new ViewpageAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewpageAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.page_trangchu).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.page_playlist).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.page_canhan).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void showHome(){
        progress_circle = findViewById(R.id.progress_circle);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(songAdapter);


        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(songAdapter);
        scaleInAnimationAdapter.setDuration(1000);
        scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());
        scaleInAnimationAdapter.setFirstOnly(false);
        recyclerView.setAdapter(scaleInAnimationAdapter);
        progress_circle.setVisibility(View.VISIBLE);

        mStorage.collection("songs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                mSongs.clear();
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Song song = document.toObject(Song.class);
                        song.setKey(document.getId());
                        mSongs.add(song);
                    }
                    songAdapter.notifyDataSetChanged();

                    progress_circle.setVisibility(View.INVISIBLE);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    progress_circle.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    private void filterSong(String query) {
        List<Song> filteredList= new ArrayList<>();
        if(mSongs.size() > 0){
            for (Song song: mSongs){
                if (song.getName().toLowerCase().contains(query)){
                    filteredList.add(song);
                }
            }
        }
        if (songAdapter != null){
            songAdapter.filterSongs(filteredList);
        }

    }

    @Override
    public void onItemClick(List<Song> mSong, int position) {
        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
        if (!player.isPlaying()) {
            player.setMediaItems(getMediaItems(mSong), position, 0);
        } else {
            player.pause();
            player.seekTo(position, 0);
        }
        player.prepare();
        player.play();

        //
        playerView.setVisibility(View.VISIBLE);
    }
    private List<MediaItem> getMediaItems(List<Song> mSong) {
        List<MediaItem> mediaItems = new ArrayList<>();

        for (Song song: mSong){
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(song.getLink())
                    .setMediaMetadata(getMetadata(song))
                    .build();
            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }
    private MediaMetadata getMetadata(Song song) {
        return new MediaMetadata.Builder()
                .setTitle(song.getName())
                .build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player.isPlaying())
        {
            player.stop();
        }
        player.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                isSuccess = true;
                bottomNavigationView.setSelectedItemId(R.id.page_trangchu);
            }
        }
    }
}