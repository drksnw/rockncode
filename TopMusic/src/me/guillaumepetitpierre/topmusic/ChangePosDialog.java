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
public class ChangePosDialog extends DialogFragment {

    int actPos;

    public ChangePosDialog(int actPos){
        this.actPos = actPos;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_changepos, null))
                .setPositiveButton("Modifier", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        // TODO changePos
                        Dialog d = (Dialog)dialog;
                        ((MainActivity)getActivity()).changePosition(actPos, Integer.parseInt(((EditText)d.findViewById(R.id.newPos)).getText().toString()));
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
