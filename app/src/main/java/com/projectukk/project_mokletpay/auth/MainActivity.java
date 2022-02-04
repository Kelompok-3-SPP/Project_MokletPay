package com.projectukk.project_mokletpay.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.projectukk.project_mokletpay.R;

public class MainActivity extends AppCompatActivity {
    private ImageButton btnSiswa, btnPetugas, btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSiswa = findViewById(R.id.btnSiswa);
        btnPetugas = findViewById(R.id.btnPetugas);
        btnAdmin = findViewById(R.id.btnAdmin);

        btnSiswa.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.putExtra("status_login", "3");
            startActivity(i);
        });
        btnPetugas.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.putExtra("status_login", "2");
            startActivity(i);
        });
        btnAdmin.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.putExtra("status_login", "1");
            startActivity(i);
        });
    }
}