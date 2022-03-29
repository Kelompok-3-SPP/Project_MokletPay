package com.projectukk.project_mokletpay.admintransaksi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.projectukk.project_mokletpay.R;
import com.projectukk.project_mokletpay.petugas.ListPetugas;

public class TambahAkun extends AppCompatActivity {
    private ImageButton btnSiswa, btnPetugas, btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_akun);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }

        btnSiswa = findViewById(R.id.btnSiswa);
        btnPetugas = findViewById(R.id.btnPetugas);
        btnAdmin = findViewById(R.id.btnAdmin);

        btnPetugas.setOnClickListener(v -> {
            Intent i = new Intent(TambahAkun.this, ListPetugas.class);
            startActivity(i);
        });
        btnAdmin.setOnClickListener(v -> {
            Intent i = new Intent(TambahAkun.this, ListAdmin.class);
            startActivity(i);
        });
    }
}