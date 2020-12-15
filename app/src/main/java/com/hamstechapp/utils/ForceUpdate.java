package com.hamstechapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.hamstechapp.R;

public class ForceUpdate {

    Context context;
    Dialog dialog;
    TextView btnUpdate;

    public ForceUpdate(Context context){
        this.context = context;
    }


    public void showLoadingDialog() {

        dialog  = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(false);
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.app_update);
        btnUpdate = dialog.findViewById(R.id.btnUpdate);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.hamstechonline")));
            }
        });

        dialog.show();

    }

    public void hideDialog(){
        dialog.dismiss();
    }

}
