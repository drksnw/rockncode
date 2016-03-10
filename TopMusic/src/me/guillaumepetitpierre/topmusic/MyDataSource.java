package me.guillaumepetitpierre.topmusic;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by darksnow on 2/8/16.
 */
public class MyDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { "_id",
            "title",
            "artist",
            "position"};

    public MyDataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public SQLiteDatabase getDB(){
        return database;
    }

    public String[] getAllColumns(){
        return allColumns;
    }
}
