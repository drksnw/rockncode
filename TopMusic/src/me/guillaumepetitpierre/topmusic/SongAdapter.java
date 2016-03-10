package me.guillaumepetitpierre.topmusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darksnow on 2/8/16.
 */
public class SongAdapter extends ArrayAdapter<Song> {
    private Context context;
    private ArrayList<Song> objects;
    public SongAdapter(Context c, ArrayList<Song> objects){
        super(c, -1, objects);
        context = c;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.myadapter, parent, false);
        TextView title = (TextView)rowView.findViewById(R.id.txtTitle);
        TextView artist = (TextView)rowView.findViewById(R.id.txtArtist);
        TextView pos = (TextView)rowView.findViewById(R.id.txtPos);
        pos.setText(Integer.toString(position+1));
        title.setText(objects.get(position).getTitle());
        artist.setText(objects.get(position).getArtist());

        //TODO implement album cover

        return rowView;
    }
}
