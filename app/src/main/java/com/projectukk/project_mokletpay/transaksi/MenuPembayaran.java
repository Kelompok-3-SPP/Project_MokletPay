package com.projectukk.project_mokletpay.transaksi;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.projectukk.project_mokletpay.R;
import com.projectukk.project_mokletpay.admintransaksi.ApprovalActivity;
import com.projectukk.project_mokletpay.admintransaksi.SiswaPembayaran;

public class MenuPembayaran extends AppCompatActivity {

    private CardView cv1, cv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pembayaran);

        cv1 = findViewById(R.id.cv1);
        cv2 = findViewById(R.id.cv2);
        ActionButton();
    }

    private void ActionButton() {
        cv1.setOnClickListener(v -> startActivity(new Intent(MenuPembayaran.this, SiswaPembayaran.class)));
        cv2.setOnClickListener(v -> startActivity(new Intent(MenuPembayaran.this, ApprovalActivity.class)));
    }
}