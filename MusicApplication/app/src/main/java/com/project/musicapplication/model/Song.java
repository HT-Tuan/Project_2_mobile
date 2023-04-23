package com.project.musicapplication.model;

public class Song {
    String id;
    String name;
    String singer;
    String link;
    PlayList playList;

    public Song() {
    }

    public Song(String id, String name, String singer, String link, PlayList playList) {
        this.id = id;
        this.name = name;
        this.singer = singer;
        this.link = link;
        this.playList = playList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public PlayList getPlayList() {
        return playList;
    }

    public void setPlayList(PlayList playList) {
        this.playList = playList;
    }
}
