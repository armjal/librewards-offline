package com.example.librewards.views;

import static java.util.Objects.requireNonNull;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.librewards.R;

public class ViewUtils {
    private final Context context;
    public ViewUtils(Context context){
        this.context = context;
    }

    public void toastMessage(String message){
        Toast.makeText(context.getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    public void showPopup(String text) {
        Dialog popup = new Dialog(context);
        requireNonNull(popup.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setContentView(R.layout.popup_layout);
        ImageView closeBtn = popup.findViewById(R.id.closeBtn);
        TextView popupText = popup.findViewById(R.id.popupText);

        popupText.setText(text);
        closeBtn.setOnClickListener(v -> popup.dismiss());
        popup.show();
    }
}
