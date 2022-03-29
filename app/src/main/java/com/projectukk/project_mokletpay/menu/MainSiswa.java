package com.projectukk.project_mokletpay.menu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;

import com.projectukk.project_mokletpay.R;
import com.projectukk.project_mokletpay.auth.BioSiswaActivity;
import com.projectukk.project_mokletpay.auth.InformasiActivity;
import com.projectukk.project_mokletpay.helper.SessionManager;
import com.projectukk.project_mokletpay.transaksi.TransaksiPembayaran;

import java.io.File;
import java.util.HashMap;

public class MainSiswa extends AppCompatActivity {
    private TextView text_nama;
    private AppCompatImageView img_logout;
    public SessionManager SessionManager;
    public static String iduser, username;
    private CardView cv1, cv2, cv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_siswa);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        SessionManager = new SessionManager(MainSiswa.this);
        SessionManager.checkLogin();
        HashMap<String, String> user = SessionManager.getUserDetails();
        iduser = user.get(SessionManager.KEY_ID);
        username = user.get(SessionManager.KEY_USERNAME);

        text_nama = findViewById(R.id.text_nama);
        img_logout = findViewById(R.id.img_logout);
        cv1 = findViewById(R.id.cv1);
        cv2 = findViewById(R.id.cv2);
        cv3 = findViewById(R.id.cv3);

        ActionButton();
        text_nama.setText("Halo " + username);
    }

    private void ActionButton() {
        img_logout.setOnClickListener(v -> logoutUser());
        cv1.setOnClickListener(v -> {
            Intent x = new Intent(MainSiswa.this, BioSiswaActivity.class);
            x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            x.putExtra("idsiswa", iduser);
            startActivity(x);
        });
        cv2.setOnClickListener(v -> startActivity(new Intent(MainSiswa.this, TransaksiPembayaran.class)));
        cv3.setOnClickListener(v -> startActivity(new Intent(MainSiswa.this, InformasiActivity.class)));
    }

    private void logoutUser() {
        clearApplicationData();
        SessionManager.logoutUser();
        finishAffinity();
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            assert children != null;
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            int i = 0;
            assert children != null;
            while (i < children.length) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
                i++;
            }
        }

        assert dir != null;
        return dir.delete();
    }
}