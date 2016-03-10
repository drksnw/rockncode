package me.guillaumepetitpierre.topmusic;

/**
 * Created by darksnow on 2/8/16.
 */
public class Song {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getPos() { return pos; }

    private String title;
    private String artist;
    private int pos;

    public Song(String title, String artist, int pos){
        this.title = title;
        this.artist = artist;
        this.pos = pos;
    }


}
