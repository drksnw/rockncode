package me.guillaumepetitpierre.topmusic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

/**
 * Created by darksnow on 2/8/16.
 */
public class AddSongDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_addsong, null))
            .setPositiveButton("Ajouter", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id){
                    // TODO add song
                    Dialog d = (Dialog)dialog;
                    ((MainActivity)getActivity()).addSong(((EditText)d.findViewById(R.id.newTitle)).getText().toString(), ((EditText)d.findViewById(R.id.newArtist)).getText().toString());
                }
            })
            .setNegativeButton("Annuler", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id){
                    // TODO do nothing
                }
            });

        return builder.create();

    }
}
