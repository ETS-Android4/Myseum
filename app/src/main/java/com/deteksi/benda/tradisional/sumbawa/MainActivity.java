package com.deteksi.benda.tradisional.sumbawa;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import om.deteksi.benda.tradisional.sumbawa.R;


public class MainActivity extends AppCompatActivity {

    AppCompatImageButton btnLive, btnCapture, btnInfo;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLive = findViewById(R.id.btn_live);
        btnCapture = findViewById(R.id.btn_capture);
        btnInfo = findViewById(R.id.btn_info);

        btnLive.setOnClickListener(view -> {
            intent = new Intent(this, DetectorActivity.class);
            startActivity(intent);
        });

        btnCapture.setOnClickListener(view -> {
            intent = new Intent(this, CaptureActivity.class);
            startActivity(intent);
        });

        btnInfo.setOnClickListener(view -> {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setView(viewDialog(this));
            alertDialog.setTitle("Tentang");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        });
    }

    public View viewDialog(Context context) {
        return LayoutInflater.from(context)
                .inflate(R.layout.custom_dialog_about, null, false);
    }
}