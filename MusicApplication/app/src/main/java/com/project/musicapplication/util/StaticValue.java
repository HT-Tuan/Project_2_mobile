package com.project.musicapplication.util;

import android.app.Notification;
import android.content.Context;
import android.media.MediaPlayer;

import com.project.musicapplication.model.Song;

import java.util.ArrayList;
import java.util.List;

public class StaticValue {
    public static MediaPlayer mMediaPlayer = new MediaPlayer();
    public static List<Song> mSongListToPlay = new ArrayList<>();
    public static Song mCurrentSong = null;
    public static enumMusicActionCode curAction = enumMusicActionCode.INIT;
    public static Context mainContext;
    public static String notificationID = "MusicPlayerService";
    public static Notification mCurrentNoti = null;
}
