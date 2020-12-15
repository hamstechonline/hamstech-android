package com.hamstechapp.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hamstechapp.R;

public class HocLoadingDialog {

    Context context;
    Dialog dialog;
    ImageView imageView;

    public HocLoadingDialog(Context context){
        this.context = context;
    }


    public void showLoadingDialog() {

        dialog  = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(false);
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.loading_progress);
        imageView = dialog.findViewById(R.id.progressBar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //imageView.setBackgroundColor(Color.TRANSPARENT);
        //imageView.setImageResource(context.getResources().getDrawable(R.drawable.loader_original));
        Glide.with(context)
                .asGif()
                .load(R.raw.loader_original)
                .into(imageView);

        dialog.show();

    }

    public void hideDialog(){
        dialog.dismiss();
    }

}
