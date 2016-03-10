package me.guillaumepetitpierre.topmusic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by darksnow on 2/8/16.
 */
public class AboutSongDialog extends DialogFragment {

    private int pos;
    private String title;
    private String artist;

    public AboutSongDialog(int pos, String title, String artist){
        this.pos = pos;
        this.title = title;
        this.artist = artist;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title+" : Propriétés");
        builder.setItems(R.array.songproperties, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 2){
                    ((MainActivity)getActivity()).removeSong(pos);
                } else if(which == 1){
                    ChangePosDialog cps = new ChangePosDialog(pos+1);
                    cps.show(((MainActivity)getActivity()).getFragmentManager(), "");
                } else if(which == 0){
                    YoutubeVideoDialog yvd = new YoutubeVideoDialog();
                    Intent intent = new Intent(getActivity(), YoutubeVideoDialog.class);
                    startActivity(intent);
                }
            }
        });
        return builder.create();
    }
}
