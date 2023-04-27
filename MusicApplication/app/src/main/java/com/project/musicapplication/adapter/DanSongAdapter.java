package com.project.musicapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.musicapplication.R;
import com.project.musicapplication.model.Song;
import com.project.musicapplication.service.DanMusicPlayerService;
import com.project.musicapplication.util.StaticValue;
import com.project.musicapplication.util.enumMusicActionCode;
import com.squareup.picasso.Picasso;

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
        Picasso.get()
                .load(song.getLinkimg())
                .placeholder(R.mipmap.ic_launcher_round)
                .fit()
                .centerCrop()
                .into(holder.imgSong);
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
            imgMore.setOnCreateContextMenuListener(this);
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

