package me.guillaumepetitpierre.topmusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by darksnow on 2/9/16.
 */
public class DrawerListAdapter extends BaseAdapter {

    Context context;
    ArrayList<NavItem> navitems;

    public DrawerListAdapter(Context context, ArrayList<NavItem> navitems){
        this.context = context;
        this.navitems = navitems;
    }

    @Override
    public int getCount() {
        return navitems.size();
    }

    @Override
    public Object getItem(int position) {
        return navitems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawerlistitem, null);
        } else {
            view = convertView;
        }

        TextView navTitle = (TextView)view.findViewById(R.id.navtitle);
        ImageView navIcon = (ImageView)view.findViewById(R.id.navicon);

        navTitle.setText(navitems.get(position).getTitle());
        navIcon.setImageResource(navitems.get(position).getIcon());


        return view;
    }
}
