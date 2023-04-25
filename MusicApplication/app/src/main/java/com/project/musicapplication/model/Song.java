package com.project.musicapplication.model;


import java.io.Serializable;

public class Song implements Serializable {
    private String id;
    private String name;
    private String singer;
    private String link;
    private String linkimg;
    private String mKey;


    public Song() {
    }

    public Song(String id, String name, String singer, String link, String linkimg) {
        this.id = id;
        this.name = name;
        this.singer = singer;
        this.link = link;
        this.linkimg = linkimg;
    }

    public String getLinkimg() {
        return linkimg;
    }

    public void setLinkimg(String linkimg) {
        this.linkimg = linkimg;
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

    public String getKey() {
        return mKey;
    }
    public void setKey(String key) {
        mKey = key;
    }
}
