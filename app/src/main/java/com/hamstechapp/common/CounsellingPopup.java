package com.hamstechapp.common;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.hamstechapp.R;
import com.hamstechapp.activities.CounsellingActivity;
import com.hamstechapp.utils.UserDataConstants;

public class CounsellingPopup {

    Context context;
    Dialog dialog;
    Button btnOnline,btnOnCampus;

    public CounsellingPopup(Context context){
        this.context = context;
    }

    public void showLoadingDialog() {
        dialog  = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(true);
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.counselling_popup);
        btnOnline = dialog.findViewById(R.id.btnOnline);
        btnOnCampus = dialog.findViewById(R.id.btnOnCampus);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        btnOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataConstants.counsellingPopup = 0;
                Intent counsellingIntent = new Intent(context, CounsellingActivity.class);
                context.startActivity(counsellingIntent);
                dialog.dismiss();
            }
        });

        btnOnCampus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataConstants.counsellingPopup = 1;
                Intent counsellingIntent = new Intent(context, CounsellingActivity.class);
                context.startActivity(counsellingIntent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void hideDialog(){
        dialog.dismiss();
    }
}
