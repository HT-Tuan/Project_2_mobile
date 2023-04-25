package com.project.musicapplication.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.project.musicapplication.R;
import com.project.musicapplication.util.StaticValue;
import com.project.musicapplication.util.enumMusicActionCode;

import java.io.IOException;

public class DanMusicPlayerService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            if(StaticValue.mCurrentNoti == null)
                StaticValue.mCurrentNoti = createNotification();
            switch (enumMusicActionCode.valueOf(intent.getAction())) {
                case PLAY:
                case INIT:
                    StaticValue.curAction = enumMusicActionCode.PLAY;
                    playMusic();
                    startForeground(1, StaticValue.mCurrentNoti);
                    break;
                case PAUSE:
                    StaticValue.curAction = enumMusicActionCode.PAUSE;
                    pauseMusic();
                    startForeground(1, StaticValue.mCurrentNoti);
                    break;
                case RESUME:
                    StaticValue.curAction = enumMusicActionCode.RESUME;
                    resumeMusic();
                    startForeground(1, StaticValue.mCurrentNoti);
                    break;
                case NEXT:
                    StaticValue.curAction = enumMusicActionCode.NEXT;
                    break;
                case PRE:
                    StaticValue.curAction = enumMusicActionCode.PRE;
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private void playMusic() {
        if (StaticValue.mCurrentSong != null) {
            try {
                StaticValue.mMediaPlayer.release();
                StaticValue.mMediaPlayer = new MediaPlayer();
                StaticValue.mMediaPlayer.setDataSource(StaticValue.mCurrentSong.getLink());
                StaticValue.mMediaPlayer.prepare();
                StaticValue.mMediaPlayer.start();
                // Send a broadcast to the MainActivity to update the UI
                StaticValue.mCurrentNoti = createNotification();
                Intent intent = new Intent(enumMusicActionCode.PLAY.name());
                sendBroadcast(intent);
            } catch (IOException e) {
                Log.e(StaticValue.notificationID, "Error playing music: " + e.getMessage());
            }
        }
    }

    private void pauseMusic() {
        if (StaticValue.mMediaPlayer.isPlaying()) {
            StaticValue.mMediaPlayer.pause();
            StaticValue.mCurrentNoti = createNotification();
            // Send a broadcast to the MainActivity to update the UI
            Intent intent = new Intent(enumMusicActionCode.PAUSE.name());
            sendBroadcast(intent);
        }
    }

    private void resumeMusic() {
        StaticValue.mMediaPlayer.start();
        StaticValue.mCurrentNoti = createNotification();
        // Send a broadcast to the MainActivity to update the UI
        Intent intent = new Intent(enumMusicActionCode.RESUME.name());
        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        StaticValue.mMediaPlayer.stop();
        StaticValue.mCurrentNoti = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
        remoteViews.setImageViewResource(R.id.img_song, R.drawable.img_cd);
        remoteViews.setTextViewText(R.id.tv_title_song, StaticValue.mCurrentSong.getName());
        remoteViews.setTextViewText(R.id.tv_single_song, StaticValue.mCurrentSong.getSinger());

        Intent intent = new Intent(this, DanMusicPlayerService.class);
        PendingIntent pendingIntent = null;

        switch (StaticValue.curAction) {
            case PLAY:

            case RESUME:
                // Update UI to show that music is resumed
                remoteViews.setImageViewResource(R.id.img_play_or_pause, R.drawable.ic_pause);
                StaticValue.curAction = enumMusicActionCode.PAUSE;
                intent.setAction(enumMusicActionCode.PAUSE.name());
                pendingIntent = PendingIntent.getService(StaticValue.mainContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.img_play_or_pause, pendingIntent);
                break;

            case PAUSE:
                // Update UI to show that music is paused
                remoteViews.setImageViewResource(R.id.img_play_or_pause, R.drawable.ic_play);
                StaticValue.curAction = enumMusicActionCode.RESUME;
                intent.setAction(enumMusicActionCode.RESUME.name());
                pendingIntent = PendingIntent.getService(StaticValue.mainContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.img_play_or_pause, pendingIntent);
                break;

            case NEXT:
                // Update UI to show the next song
                break;
            case PRE:
                // Update UI to show the previous song
                break;
            case INIT:
                // Update UI to show that music is initialized
                remoteViews.setImageViewResource(R.id.img_play_or_pause, R.drawable.ic_pause);
                StaticValue.curAction = enumMusicActionCode.PLAY;
                intent.setAction(enumMusicActionCode.PLAY.name());
                pendingIntent = PendingIntent.getService(StaticValue.mainContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.img_play_or_pause, pendingIntent);
                break;
        }
        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, StaticValue.notificationID)
                .setSmallIcon(R.drawable.img_cd)
                .setCustomContentView(remoteViews)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true)
                .setSound(null);
        Notification notification = builder.build();

        return notification;
    }

}
