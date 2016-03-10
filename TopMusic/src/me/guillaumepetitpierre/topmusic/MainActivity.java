package me.guillaumepetitpierre.topmusic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    ArrayList<Song> songs;
    SongAdapter sa;
    DrawerListAdapter dla;
    DrawerListAdapter odla;
    SQLiteDatabase db;
    MyDataSource mds;
    OkHttpClient client;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    int actualPlaylist = 1;
    ArrayList<NavItem> navItems;
    ArrayList<NavItem> onlineNavItems;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);

        setSupportActionBar(myToolbar);
        ListView songsView = (ListView)findViewById(R.id.songsList);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Rock");


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                getSupportActionBar().setTitle("TopMusic");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d("Nit", "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mds = new MyDataSource(this.getApplicationContext());
        mds.open();

        db = mds.getDB();

        client = new OkHttpClient();

        songs = new ArrayList<Song>();

        sa = new SongAdapter(this, songs);
        populateList();

        songsView.setAdapter(sa);

        songsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Song selectedSong = songs.get(position);
                AboutSongDialog asd = new AboutSongDialog(position, selectedSong.getTitle(), selectedSong.getArtist());
                asd.show(getFragmentManager(), "props");
                return true;
            }
        });

        songsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song actSong = songs.get(position);
                String searched = searchSong(actSong.getTitle(), actSong.getArtist());
                try{
                    JSONObject searchedObject = new JSONObject(searched);
                    JSONObject results = searchedObject.getJSONObject("response");
                    JSONArray hits = results.getJSONArray("hits");
                    JSONObject firstHit = hits.getJSONObject(0);
                    JSONObject songRes = firstHit.getJSONObject("result");
                    int songId = songRes.getInt("id");
                    try{
                        String secondResult = new CheckGeniusTask().execute(Integer.toString(songId)).get();
                        JSONObject secondSearch = new JSONObject(secondResult);
                        JSONObject secondResponse = secondSearch.getJSONObject("response").getJSONObject("song");
                        String realSongTitle = secondResponse.getString("title");
                        String bannerUrl = secondResponse.getString("header_image_url");
                        JSONObject primaryArtist = secondResponse.getJSONObject("primary_artist");
                        String realArtistName = primaryArtist.getString("name");
                        JSONArray mediaArray = secondResponse.getJSONArray("media");
                        JSONObject mediaContent = mediaArray.getJSONObject(0);
                        String youtubeUrl = null;
                        if(mediaContent.getString("provider").equals("youtube")){
                            youtubeUrl = mediaContent.getString("url");
                        }
                        Log.d("Song title", realSongTitle);
                        Log.d("Banner", bannerUrl);
                        Log.d("Artist name", realArtistName);
                        Log.d("Youtube URL", youtubeUrl);
                        Intent in = new Intent(getApplicationContext(), YoutubeVideoDialog.class);
                        in.putExtra("banner", bannerUrl);
                        in.putExtra("yturl", youtubeUrl);
                        in.putExtra("artist", realArtistName);
                        in.putExtra("title", realSongTitle);
                        startActivity(in);
                    } catch(Exception e){
                        e.printStackTrace();
                    }


                } catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });

        navItems = new ArrayList<NavItem>();
        onlineNavItems = new ArrayList<NavItem>();
        populateDrawer();

        ListView dl = (ListView)findViewById(R.id.listDrawer);
        dla = new DrawerListAdapter(this, navItems);
        dl.setAdapter(dla);

        ListView odl = (ListView)findViewById(R.id.onlineListDrawer);
        odla = new DrawerListAdapter(this, onlineNavItems);
        odl.setAdapter(odla);

        dl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavItem selectedItem = navItems.get(position);
                if(selectedItem.getId() != 0){
                    actualPlaylist = selectedItem.getId();
                    populateList();
                    mDrawerLayout.closeDrawers();
                    getSupportActionBar().setTitle(selectedItem.getTitle());
                }
            }
        });

        odl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavItem selectedItem = onlineNavItems.get(position);
                actualPlaylist = -1*selectedItem.getId();
                populateOnlineList();
                mDrawerLayout.closeDrawers();
                getSupportActionBar().setTitle(selectedItem.getTitle()+" - En ligne");
            }
        });





    }

    public String searchSong(String title, String artist){
        String formattedTitle = title.replaceAll(" ", "%20");
        String formattedArtist = artist.replaceAll(" ", "%20");
        try{
            return new CheckGeniusTask().execute(formattedArtist, formattedTitle).get();
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if(id == R.id.action_add){
            AddSongDialog asd = new AddSongDialog();
            asd.show(getFragmentManager(), "");
        } else if(id == R.id.action_share){
            String outStr = "Voici les chansons dans ma playlist:\n";
            int a = 1;
            for(Song s : songs){
                outStr += ""+(a++)+") "+s.getArtist()+" - "+s.getTitle()+"\n";
            }
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, outStr);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        } else if(id == R.id.action_refresh){
            if(actualPlaylist < 0){
                populateOnlineList();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    public void addSong(String title, String artist){
        if(actualPlaylist > 0) {
            //Getting last position
            String[] usefulArray = {"position"};
            Cursor cur = db.query("songs", usefulArray, "fk_playlist=" + actualPlaylist, null, null, null, "position DESC");
            cur.moveToFirst();
            int lastPos = 0;
            if (cur.getCount() > 0) {
                lastPos = cur.getInt(0);
            }
            cur.close();
            ContentValues cv = new ContentValues();
            cv.put("title", title);
            cv.put("artist", artist);
            cv.put("position", lastPos + 1);
            cv.put("fk_playlist", actualPlaylist);
            db.insert("songs", null, cv);
            populateList();
        } else {
            sa.notifyDataSetChanged();
            try{

                new CheckMyDBTask().execute("asng", Integer.toString(-1*actualPlaylist), title.replaceAll(" ", "%20"), artist.replaceAll(" ", "%20")).get();
                populateOnlineList();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    public void removeSong(int id){
        final int aId = id;
        new AlertDialog.Builder(this)
                .setTitle("Suppression")
                .setMessage("Voulez-vous vraiment supprimer "+songs.get(id).getTitle()+" de "+songs.get(id).getArtist()+" ?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(actualPlaylist > 0){
                            db.execSQL("DELETE FROM songs WHERE position="+(aId+1)+" AND fk_playlist="+actualPlaylist+";");
                            db.execSQL("UPDATE songs SET position = position-1 WHERE position > "+(aId+1)+" AND fk_playlist="+actualPlaylist+";");
                            populateList();
                        } else {
                            try{
                                new CheckMyDBTask().execute("dels", Integer.toString(-1*actualPlaylist), ""+(aId+1)).get();
                                populateOnlineList();
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                    }})
                .setNegativeButton("Non", null).show();
    }

    public void populateList(){
        songs.clear();
        sa.notifyDataSetChanged();
        Cursor cur = db.query("songs", mds.getAllColumns(), "fk_playlist="+actualPlaylist, null, null, null, "position ASC");
        cur.moveToFirst();
        while(!cur.isAfterLast()){
            songs.add(new Song(cur.getString(1), cur.getString(2), cur.getInt(3)));
            cur.moveToNext();
        }
        cur.close();
        sa.notifyDataSetChanged();
    }

    public void populateOnlineList(){
        songs.clear();
        sa.notifyDataSetChanged();
        try{
            String songslist = new CheckMyDBTask().execute("slist", Integer.toString(-1*actualPlaylist)).get();
            JSONArray jarray = new JSONArray(songslist);
            for(int i = 0; i < jarray.length(); i++){
                JSONObject jo = jarray.getJSONObject(i);
                songs.add(new Song(jo.getString("title_sng"), jo.getString("artist_sng"), i+1));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        sa.notifyDataSetChanged();

    }

    public void changePosition(int actPos, int newPos){
        Log.d("Pelle","NewPos: "+newPos+" ActPos: "+actPos);
        if(newPos > songs.size()){
            newPos = songs.size();
        } else if(newPos < 1){
            newPos = 1;
        }

        if(actualPlaylist > 0){
            if(newPos < actPos){
                db.execSQL("UPDATE songs SET position=position+1 WHERE position >= "+newPos+" AND fk_playlist="+actualPlaylist+";");
                db.execSQL("UPDATE songs SET position="+newPos+" WHERE position = "+(actPos+1)+" AND fk_playlist="+actualPlaylist+";");
                db.execSQL("UPDATE songs SET position=position-1 WHERE position > "+(actPos+1)+" AND fk_playlist="+actualPlaylist+";");
            } else {
                db.execSQL("UPDATE songs SET position=-1 WHERE position="+actPos+" AND fk_playlist="+actualPlaylist+";");
                db.execSQL("UPDATE songs SET position=position-1 WHERE position > "+actPos+" AND position <= "+newPos+" AND fk_playlist="+actualPlaylist+";");
                db.execSQL("UPDATE songs SET position="+newPos+" WHERE position=-1 AND fk_playlist="+actualPlaylist+";");
            }

            populateList();
        } else {
            try{
                new CheckMyDBTask().execute("chgpos", Integer.toString(-1*actualPlaylist), ""+actPos, ""+newPos).get();
                populateOnlineList();
            } catch(Exception e){
                e.printStackTrace();
            }
        }


    }

    public void populateDrawer(){
        Cursor cur = db.query("playlists", new String[]{"_id", "name"}, null, null, null, null, null);
        cur.moveToFirst();
        while(!cur.isAfterLast()){
            Log.d("Phallus", ""+cur.getInt(0));
            navItems.add(new NavItem(cur.getInt(0), cur.getString(1), R.drawable.ic_action_view_as_list));
            cur.moveToNext();
        }
        cur.close();
        navItems.add(new NavItem(0, "Nouvelle playlist", R.drawable.ic_action_new));
        try {
            String onlinePlaylists = new CheckMyDBTask().execute("plist").get();
            JSONArray jarray = new JSONArray(onlinePlaylists);
            for(int i = 0; i < jarray.length(); i++){
                JSONObject jo = jarray.getJSONObject(i);
                onlineNavItems.add(new NavItem(jo.getInt("id_ply"), jo.getString("name_ply"), R.drawable.ic_action_view_as_list));
            }
        } catch(Exception e){
            e.printStackTrace();
        }


    }

    private class CheckGeniusTask extends AsyncTask<String, Void, String>{
        ProgressDialog pd;
        protected String doInBackground(String... str){
            if(str.length <= 1){
                try{
                    URL url = new URL("https://api.genius.com/songs/"+str[0]);
                    Request req = new Request.Builder().url(url).header("Authorization", "Bearer wI47q5nPLAgh_8tx5L2M1Al7OfxQ6rzl_VkPJDHrkUAnAwVHseLbVny5air6Xh-p").build();
                    Response r = client.newCall(req).execute();
                    return r.body().string();

                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                try{
                    URL url = new URL("https://api.genius.com/search?q="+str[0]+"%20-%20"+str[1]);
                    Request req = new Request.Builder().url(url).header("Authorization", "Bearer wI47q5nPLAgh_8tx5L2M1Al7OfxQ6rzl_VkPJDHrkUAnAwVHseLbVny5air6Xh-p").build();

                    Response r = client.newCall(req).execute();
                    return r.body().string();

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPreExecute(){
            pd = ProgressDialog.show(MainActivity.this, "Veuillez patienter...", "Chargement", true);
        }

        protected void onPostExecute(String res){
            pd.dismiss();
        }
    }

    private class CheckMyDBTask extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... str){
            if(str[0].equals("plist")){
                try {
                    URL url = new URL("http://46.101.130.105/r.php?l=plist");

                    Request req = new Request.Builder().url(url).build();
                    Response r = client.newCall(req).execute();
                    return r.body().string();
                } catch(Exception e){
                    e.printStackTrace();
                }
            } else if(str[0].equals("slist")){
                try{
                    URL url = new URL("http://46.101.130.105/r.php?l=sngs&pl="+str[1]);
                    Request req = new Request.Builder().url(url).build();
                    Response r = client.newCall(req).execute();
                    return r.body().string();
                } catch(Exception e){
                    e.printStackTrace();
                }
            } else if(str[0].equals("asng")){
                try{
                    URL url = new URL("http://46.101.130.105/w.php?action=news&pl="+str[1]+"&s="+str[2]+"&a="+str[3]);
                    Log.d("lol", url.toString());
                    Request req = new Request.Builder().url(url).build();
                    Response r = client.newCall(req).execute();
                    return r.body().string();
                } catch(Exception e){
                    e.printStackTrace();
                }
            } else if(str[0].equals("dels")){
                try{
                    URL url = new URL("http://46.101.130.105/w.php?action=dels&pl="+str[1]+"&pos="+str[2]);
                    Request req = new Request.Builder().url(url).build();
                    Response r = client.newCall(req).execute();
                    return r.body().string();
                } catch(Exception e){
                    e.printStackTrace();
                }
            } else if(str[0].equals("chgpos")){
                try{
                    URL url = new URL("http://46.101.130.105/w.php?action=chgpos&pl="+str[1]+"&op="+str[2]+"&np="+str[3]);
                    Request req = new Request.Builder().url(url).build();
                    Response r = client.newCall(req).execute();
                    return r.body().string();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
}
