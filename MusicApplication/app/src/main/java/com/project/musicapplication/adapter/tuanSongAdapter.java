package com.project.musicapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.project.musicapplication.R;
import com.project.musicapplication.model.Song;
import com.squareup.picasso.Picasso;

import java.util.List;


public class tuanSongAdapter extends RecyclerView.Adapter<tuanSongAdapter.SongViewHolder>{

    private Context mContext;
    private List<Song> mSong;
    private OnItemClickListener mListener;

    private ExoPlayer player;
    private ConstraintLayout playerView;

    public tuanSongAdapter(Context mContext, List<Song> mSong, ExoPlayer player, ConstraintLayout playerView) {
        this.mContext = mContext;
        this.mSong = mSong;
        this.player = player;
        this.playerView = playerView;
    }

    @Override
    public int getItemCount() {
        return mSong.size();
    }

    @NonNull
    @Override
    public tuanSongAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.songlist_view, parent, false);
        return new SongViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song songCurrent= mSong.get(position);
        holder.textViewName.setText(songCurrent.getName() + " - " + songCurrent.getSinger());
        Picasso.get()
                .load(songCurrent.getLinkimg())
                .placeholder(R.mipmap.ic_launcher_round)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView textViewName;
        public ImageView imageView, imagemore;
        public SongViewHolder(View itemview){
            super(itemview);

//            textViewName = (TextView)itemview.findViewById(R.id.titleView);
            imageView = (ImageView)itemview.findViewById(R.id.img_song);
            imagemore = (ImageView)itemview.findViewById(R.id.img_more);
            itemview.setOnClickListener(this);
            imagemore.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                   mListener.onItemClick(mSong, position);
                }
            }
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

    public interface OnItemClickListener {
        void onItemClick(List<Song> mSong, int position);
//        void onDownloadClick(int position);
//        void onAdd_PlaylistClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    //search
    @SuppressLint("NotifyDataSetChanged")
    public void filterSongs(List<Song> filteredList){
        mSong = filteredList;
        notifyDataSetChanged();
    }
}
