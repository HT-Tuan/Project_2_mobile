package com.project.musicapplication.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.project.musicapplication.MainApp;
import com.project.musicapplication.R;
import com.project.musicapplication.activity.MainNewActivity;
import com.project.musicapplication.firebase.firebaseObject;
import com.project.musicapplication.model.Song;
import com.project.musicapplication.service.DanMusicPlayerService;
import com.project.musicapplication.util.FirebaseUtil;
import com.project.musicapplication.util.StaticValue;
import com.project.musicapplication.util.enumMusicActionCode;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DanSongAdapter extends RecyclerView.Adapter<DanSongAdapter.SongViewHolder>  {

    private static List<Song> songList;
    private OnItemClickListener mListener;

    public DanSongAdapter(List<Song> songList) {
        this.songList = songList;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.songlist_view, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.tvTitle.setText(song.getName());
        holder.tvSingle.setText(song.getSinger());
        if(song.getLinkimg() != "")
            Picasso.get()
                    .load(song.getLinkimg())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .fit()
                    .centerCrop()
                    .into(holder.imgSong);
        else holder.imgSong.setImageResource(R.drawable.img_cd);
        // Set other views as needed
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView tvTitle, tvSingle;
        public ImageView imgSong, imgMore, imagemore;


        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title_song);
            tvSingle = itemView.findViewById(R.id.tv_single_song);
            imgSong = itemView.findViewById(R.id.img_song);
            imgMore = itemView.findViewById(R.id.img_more);
            itemView.setOnClickListener(this);
            imgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View root = (View) imgMore.getRootView();
                    BottomNavigationView bottomNavigationView = root.findViewById(R.id.bottom_navigation);
                    int selectedItemId = bottomNavigationView.getSelectedItemId();

                    switch (selectedItemId) {
                        case R.id.page_trangchu:
                            Log.i("Current Item", "Trang chủ");
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                Song song = songList.get(position);
                                // Do something with the song
                                homePageMore(song);
                            }
                            break;
                        case R.id.page_playlist:
                            Log.i("Current Item", "Playlist");
                            break;
                        case R.id.page_canhan:
                            int position1 = getAdapterPosition();
                            if (position1 != RecyclerView.NO_POSITION) {
                                Song song = songList.get(position1);
                                // Do something with the song
                                uploadPageMore(song);
                            }
                            Log.i("Current Item", "Cá nhân");
                            break;
                        default:
                            break;
                    }
                }
            });

        }

        private void uploadPageMore(Song song) {
            View root = (View) imgMore.getRootView();

            PopupMenu popup = new PopupMenu(StaticValue.mainContext, imgMore);
            popup.getMenuInflater().inflate(R.menu.personal_upload_page_more, popup.getMenu());

            // Set click listener for each item
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_upload:
                            // Handle add to playlist action
                            shareSong(song);
                            return true;
//                        case R.id.action_delete:
//                            // Handle add to playlist action
//                            try {
//                                deleteLocalSong(song);
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
//                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.show();
        }

        private void deleteLocalSong(Song song) throws IOException {
            File file = new File(song.getLink());
            Log.i("Delete file at", song.getLink());
            if (file.exists()) {
                if (!file.canWrite()) {
                    // The file is read-only, try to make it writable
                    if (!file.setWritable(true)) {
                        // Could not make the file writable, log error and return
                        Log.i("Delete file", "Could not make the file writable");
                        return;
                    }
                }
                try {
                    FileUtils.forceDelete(file);
                    Log.i("Delete file", "Success");
                } catch (IOException e) {
                    Log.i("Delete file", "Failure");
                }
            } else {
                Log.i("Delete file", "File does not exist");
            }
        }


        private void shareSong(Song song) {
            if(firebaseObject.user == null){
                Log.i("email", "null");
                Toast.makeText( StaticValue.mainContext, "Please login to do this action", Toast.LENGTH_LONG).show();
                return;
            }
            Log.i("Upload file at",Uri.parse(song.getLink()).getPath());

            FirebaseUtil.uploadAudioFileToFirebaseStorage(song, new FirebaseUtil.OnUploadAudioFileListener() {
                @Override
                public void onUploadSuccess(String audioUrl) {
                    Log.i("Upload file"," success!");
                    song.setLink(audioUrl);
                    song.setSinger(firebaseObject.user.getEmail());
                    FirebaseUtil.addSongToFirestore(song, new FirebaseUtil.OnUploadSongListener() {
                        @Override
                        public void onUploadSuccess(String songId) {
                            Toast.makeText( StaticValue.mainContext, "Share success!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onUploadFailure(String e) {
                            Toast.makeText( StaticValue.mainContext, "Share failure!", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onUploadFailure(String errorMessage) {
                    Log.i("Upload file"," failure!");
                }
            });

        }

        private void homePageMore(Song song) {
            // Get the parent view of img_more
            View root = (View) imgMore.getRootView();

            // Create popup menu with 3 items
            PopupMenu popup = new PopupMenu(StaticValue.mainContext, imgMore);
            popup.getMenuInflater().inflate(R.menu.home_page_more_menu, popup.getMenu());

            // Set click listener for each item
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_add_to_favorites:
                            // Handle add to playlist action
                            addToFavorates(song);
                            return true;
                        case R.id.action_download:
                            // Handle remove from favorites action
                            // Check if we have permission to write to external storage
                            if (ContextCompat.checkSelfPermission(StaticValue.mainContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                // If we don't have permission, request it
                                int REQUEST_WRITE_STORAGE = 11;
                                ActivityCompat.requestPermissions((Activity) StaticValue.mainContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
                                return true;
                            }

                            new MainApp.DownloadFileTask().execute(song.getLink(), song.getName(), song.getSinger());
                            return true;
                        default:
                            return false;
                    }
                }
            });

            // Show popup menu
            popup.show();
        }

        private void addToFavorates(Song song) {
            if(firebaseObject.user == null){
                Log.i("email", "null");
                Toast.makeText( StaticValue.mainContext, "Please login to do this action", Toast.LENGTH_LONG).show();
                return;
            }
            List<String> newSongList = new ArrayList<>(StaticValue.favoriteList.getSongs());
            newSongList.add(song.getId());
            FirebaseUtil.updatePlaylistInFirebase("playlists", firebaseObject.user.getEmail(), "YeuThich", newSongList, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Cập nhật thành công
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Cập nhật thất bại
                }
            });
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Song song = songList.get(position);
                // Do something with the song
                StaticValue.currentIndex = position;
                openSongControll(song);
            }
        }

        private void openSongControll(Song song) {
            if(song == null)
                return;
            changeCurrentInfo(song);
            playMusic(song);
        }


        private void changeCurrentInfo(Song song) {
            StaticValue.mCurrentSong = song;
            StaticValue.historyPlaylist.add(0, song);
        }

        private void playMusic(Song song) {
            Log.i("PLAY song", song.getName());
            Intent intent = new Intent(StaticValue.mainContext, DanMusicPlayerService.class);
            StaticValue.curAction = enumMusicActionCode.PLAY;
            intent.setAction(enumMusicActionCode.PLAY.name());
            StaticValue.mainContext.startService(intent);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Lựa chọn");
            MenuItem Add_Playlist = menu.add(Menu.NONE, 1, 1, "Thêm vào danh sách phát");
            MenuItem Upload = menu.add(Menu.NONE, 2, 2, "Tải nhạc xuống");

            Add_Playlist.setOnMenuItemClickListener(this);
            Upload.setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(@NonNull MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
//                            mListener.onAdd_PlaylistClick(position);
                            return true;
                        case 2:
//                            mListener.onUploadClick(position);
                            return true;
                        case 3:
//                              mListener.onDownloadClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void filterSongs(List<Song> filteredList){
        songList = filteredList;
        notifyDataSetChanged();
    }
    public void setOnItemClickListener(DanSongAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
//        void onDownloadClick(int position);
//        void onAdd_PlaylistClick(int position);
    }
}

