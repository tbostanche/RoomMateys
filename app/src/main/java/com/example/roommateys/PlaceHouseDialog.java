package com.example.roommateys;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

public class PlaceHouseDialog extends AppCompatDialogFragment {
    public PlaceHouseDialog() {
        super();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Loading map")
                .setMessage("Once the map has loaded, please tap the map where you would like to place your house")
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                });
        return builder.create();
    }
}
