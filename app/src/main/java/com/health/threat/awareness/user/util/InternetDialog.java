package com.health.threat.awareness.user.util;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.health.threat.awareness.user.R;

public class InternetDialog {

    private final Context context;

    public InternetDialog(Context context) {
        this.context = context;
    }

    public void showNoInternetDialog() {
        final Dialog dialog1 = new Dialog(context, R.style.df_dialog);
        dialog1.setContentView(R.layout.dialog_no_internet);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(true);
        dialog1.findViewById(R.id.btnSpinAndWinRedeem).setOnClickListener(view -> dialog1.dismiss());
        dialog1.show();
    }

    public boolean getInternetStatus() {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            //show no internet dialog
            showNoInternetDialog();
        }
        return isConnected;
    }
}

