package com.android.moment.moment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by eluleci on 03/12/14.
 */
public class JoinDialogFragment extends DialogFragment {

    private JoinStatusListener joinStatusListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            joinStatusListener = (JoinStatusListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement JoinStatusListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.join_dialog, null);
        builder.setView(view)
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String username = ((EditText) view.findViewById(R.id.username)).getText().toString();
                        joinStatusListener.onUserJoined(username);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JoinDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();

    }

    public interface JoinStatusListener {
        public void onUserJoined(String username);
    }
}
