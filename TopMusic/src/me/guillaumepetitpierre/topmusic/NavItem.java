package me.guillaumepetitpierre.topmusic;

/**
 * Created by darksnow on 2/9/16.
 */
public class NavItem {
    private String title;
    private int icon;
    private int id;

    public NavItem(int id, String title, int icon){
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
