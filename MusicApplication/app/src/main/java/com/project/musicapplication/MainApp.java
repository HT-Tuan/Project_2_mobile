package com.project.musicapplication;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.project.musicapplication.util.StaticValue;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        createChannelNotification();
    }

    private void createChannelNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    StaticValue.notificationID,
                    "PLAY_MUSIC_CHANNEL",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    public static class DownloadFileTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {
            int count;
            try {
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(url.openStream());
//                String fileName = url.toString().substring(url.toString().indexOf("musics") + 7, url.toString().indexOf(".mp3")).replaceAll("[^a-zA-Z0-9]", " ") + ".mp3";
                String path = Environment.getExternalStorageDirectory().getPath() + "/Music/" + urls[1].concat(" - "+urls[2] + System.currentTimeMillis()+".mp3");
                OutputStream output = new FileOutputStream(path);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

                Log.i("DOWNLOAD MP3", "Download sucess!!!");
                return true;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                return false;
            }
        }

    }

}
