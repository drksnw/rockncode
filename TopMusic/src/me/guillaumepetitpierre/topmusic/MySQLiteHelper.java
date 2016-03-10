package me.guillaumepetitpierre.topmusic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by darksnow on 2/8/16.
 */
public class MySQLiteHelper extends SQLiteOpenHelper{

    private static final String TABLE_SONGS = "songs";
    private static final String DATABASE_NAME = "topmusic.db";

    public MySQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_SONGS+" (_id integer primary key autoincrement, title text not null, artist text not null, position integer not null, fk_playlist integer not null);");
        db.execSQL("create table playlists (_id integer primary key autoincrement, name text not null);");
        //Default values
        db.execSQL("INSERT INTO playlists (name) VALUES ('Rock');");
        db.execSQL("INSERT INTO playlists (name) VALUES ('Rap');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "The database is old. Removing it.");
        db.execSQL("DROP TABLE "+TABLE_SONGS+";");
        db.execSQL("DROP TABLE playlists;");
        onCreate(db);
    }
}
