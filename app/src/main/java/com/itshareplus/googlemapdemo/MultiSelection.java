package com.itshareplus.googlemapdemo;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.ArrayList;

/**
 * Created by 貴花 on 2017/10/29.
 */

public class MultiSelection extends DialogFragment {

    ArrayList<String> list = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return null;
    }

    private void showChosenMarker() {
    }
}
