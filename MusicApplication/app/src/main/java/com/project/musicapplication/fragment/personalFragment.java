package com.project.musicapplication.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.project.musicapplication.R;
import com.project.musicapplication.adapter.DanSongAdapter;
import com.project.musicapplication.firebase.firebaseObject;
import com.project.musicapplication.model.PlayList;
import com.project.musicapplication.model.Song;
import com.project.musicapplication.util.FirebaseUtil;
import com.project.musicapplication.util.StaticValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link personalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class personalFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private DanSongAdapter adapter;
    private List<Song> songs = new ArrayList<>();

    public personalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment personalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static personalFragment newInstance(String param1, String param2) {
        personalFragment fragment = new personalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_personal, container, false);
        tabLayout = rootView.findViewById(R.id.tabLayout);
        recyclerView = rootView.findViewById(R.id.recyclerViewSongs);
//        songs = new ArrayList<>();
//        songs.add(new Song("", "Hello","Unknow","linkmp3","linkimg"));
//        songs.add(new Song("", "Hello1","Unknow","linkmp3","linkimg"));
//        songs.add(new Song("", "Hello2","Unknow","linkmp3","linkimg"));
        // Check for permission to read external storage
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            // Permission is already granted
            songs = getLocalSongs();
            // Do something with localSongs
        }
        songs = getLocalSongs();
        adapter = new DanSongAdapter(songs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tabLayout.addOnTabSelectedListener(this);
        return rootView;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        songs.clear();
        switch (tab.getPosition()) {
            case 0:
                songs = getLocalSongs();
                break;
            case 1:
                if(firebaseObject.user == null){
                    Log.i("email", "null");
                    break;
                }
                Log.i("email", firebaseObject.user.getEmail());
                FirebaseUtil.getPlaylistFromFirebase("playlists", "email", firebaseObject.user.getEmail(), "tag", "YeuThich", new FirebaseUtil.OnPlaylistListListener() {
                    @Override
                    public void onSongList(List<PlayList> playLists) {
                        if(playLists != null && playLists.size() > 0){

                            StaticValue.favoriteList = playLists.get(0);
                        } else
                            StaticValue.favoriteList = new PlayList();

                        //create a temporary list to store songs retrieved from Firebase
                        List<Song> tempSongs = new ArrayList<>();

                        //get songs by Id in playList
//                        Log.i("Yeu thich playlish", playList.getSongs().toString());
                        for (String songId :
                                StaticValue.favoriteList.getSongs()) {
                            FirebaseUtil.getASongFromFirebaseById("songs", songId, new FirebaseUtil.OnSongListener() {

                                @Override
                                public void onSong(Song song) {
                                    tempSongs.add(song);
                                    Log.i("Song info in Yeu thich", song.getId() +"::"+ song.getName() +"::"+ song.getLink());

                                    //check if all songs have been retrieved from Firebase
                                    if (tempSongs.size() ==
                                            StaticValue.favoriteList.getSongs().size()) {
                                        //update the songs list with the retrieved songs
                                        songs.clear();
                                        songs.addAll(tempSongs);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                });
                break;
            case 2:
                songs = new ArrayList<>(StaticValue.historyPlaylist);
                break;
        }

        adapter = new DanSongAdapter(songs);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        songs.clear();
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    public List<Song> getLocalSongs() {

        List<Song> localSongs = new ArrayList<>();
        File externalStorage = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/");
        if (externalStorage.exists() && externalStorage.isDirectory()) {
            File[] files = externalStorage.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".mp3")) {
                        String title = file.getName().substring(0, file.getName().lastIndexOf("."));
                        localSongs.add(new Song( "", title, "Unknown", file.getAbsolutePath(), ""));
                    }
                }
            } else {
                Log.e("getLocalSongs", "Failed to list files in " + externalStorage.getAbsolutePath());
            }
        } else {
            Log.e("getLocalSongs", externalStorage.getAbsolutePath() + " does not exist or is not a directory");
        }
        return localSongs;
    }

}