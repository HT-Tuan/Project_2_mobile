package com.project.musicapplication.model;

import java.util.ArrayList;
import java.util.List;

public class PlayList {
    String id;
    String name;
    String email;
    List<String> Songs = new ArrayList<>();
    String tag;
    public PlayList() {
    }

    public PlayList(String id, String name, String email, List<String> songs, String tag) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.Songs = songs;
        this.tag = tag;
    }

    public List<String> getSongs() {
        return Songs;
    }

    public void setSongs(List<String> songs) {
        this.Songs = songs;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
