package com.justuju.shred.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.justuju.shred.R;

import androidx.appcompat.app.AlertDialog;

public class CustomProgressDialog {

   public static AlertDialog getCustomDialog(Context context){
       final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       final View v = inflater.inflate(R.layout.progress_dialog_view, null);

       final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
       dialogBuilder.setView(v);
       AlertDialog alertDialog = dialogBuilder.create();
       alertDialog.setCanceledOnTouchOutside(false);
       return alertDialog;
   }
}
