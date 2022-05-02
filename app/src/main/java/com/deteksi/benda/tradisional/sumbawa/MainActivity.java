package com.deteksi.benda.tradisional.sumbawa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import android.content.Intent;
import android.os.Bundle;

import om.deteksi.benda.tradisional.sumbawa.R;


public class MainActivity extends AppCompatActivity {

    AppCompatImageButton btnLive, btnCapture;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLive = findViewById(R.id.btn_live);
        btnCapture = findViewById(R.id.btn_capture);

        btnLive.setOnClickListener(view -> {
            intent = new Intent(this, DetectorActivity.class);
            startActivity(intent);
        });

        btnCapture.setOnClickListener(view -> {
            intent = new Intent(this, CaptureActivity.class);
            startActivity(intent);
        });
    }
}