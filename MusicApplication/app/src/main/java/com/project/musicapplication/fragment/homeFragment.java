package com.project.musicapplication.fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.musicapplication.MainActivity;
import com.project.musicapplication.R;
import com.project.musicapplication.activity.MainNewActivity;
import com.project.musicapplication.adapter.DanSongAdapter;
import com.project.musicapplication.firebase.firebaseObject;
import com.project.musicapplication.model.Song;
import com.project.musicapplication.util.GetDataFromFirebaseUtil;
import com.project.musicapplication.util.StaticValue;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private DanSongAdapter songAdapter;
    private List<Song> songList;
    private ProgressBar progressBar;
    private FirebaseFirestore mStorage;

    public homeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(songAdapter);
        progressBar = rootView.findViewById(R.id.progress_circle);
//         Initialize the songList and songAdapter as needed
        GetDataFromFirebaseUtil.getSongFromFirebase("songs", 20, new GetDataFromFirebaseUtil.OnSongListListener() {
            @Override
            public void onSongList(List<Song> songListIp) {
                if(songListIp != null){
                    songList = songListIp;
                    songAdapter = new DanSongAdapter(songList);
                    recyclerView.setAdapter(songAdapter);
                    StaticValue.songAdapter = songAdapter;
                }
                else
                    songList =  new ArrayList<>();
                progressBar.setVisibility(View.INVISIBLE);
                StaticValue.recyclerView = recyclerView;
            }
        });
        return rootView;
    }
}