package com.example.todo_app.helpers;

import android.graphics.Color;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class HelperFunctions {

    public static HelperFunctions helpers = new HelperFunctions();

    public void showSnackBar(View view, String message, int red, int green, int blue) {
        Snackbar
                .make(view, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.rgb(red, green, blue))
                .show();
    }
}
