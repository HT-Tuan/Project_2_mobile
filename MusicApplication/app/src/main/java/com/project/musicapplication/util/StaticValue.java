package com.project.musicapplication.util;

import android.app.Notification;
import android.content.Context;
import android.media.MediaPlayer;

import androidx.recyclerview.widget.RecyclerView;

import com.project.musicapplication.adapter.DanSongAdapter;
import com.project.musicapplication.model.PlayList;
import com.project.musicapplication.model.Song;

import java.util.ArrayList;
import java.util.List;

public class StaticValue {
    public static MediaPlayer mMediaPlayer = new MediaPlayer();
    public static List<Song> mSongListToPlay = new ArrayList<>();
    public static PlayList favoriteList = new PlayList();
    public static List<Song> historyPlaylist = new ArrayList<>();
    public static Song mCurrentSong = null;
    public static enumMusicActionCode curAction = enumMusicActionCode.INIT;
    public static Context mainContext;
    public static String notificationID = "111111";
    public static Notification mCurrentNoti = null;

    public static List<Song> mSong = new ArrayList<>();
    public static DanSongAdapter songAdapter;
    public static RecyclerView recyclerView;
    public static int currentIndex = 0;
}
