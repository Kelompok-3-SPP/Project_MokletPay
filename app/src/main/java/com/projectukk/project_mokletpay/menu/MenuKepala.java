package com.projectukk.project_mokletpay.menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.projectukk.project_mokletpay.R;
import com.projectukk.project_mokletpay.auth.ProfilKepalaSekolahActivity;
import com.projectukk.project_mokletpay.helper.SessionManager;
import com.projectukk.project_mokletpay.transaksi.TransaksiPembayaran;

import java.io.File;
import java.util.HashMap;

public class MenuKepala extends AppCompatActivity {
    public SessionManager SessionManager;
    public static String iduser, username;
    private AppCompatTextView text_nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_kepala);
        SessionManager = new SessionManager(MenuKepala.this);
        SessionManager.checkLogin();
        HashMap<String, String> user = SessionManager.getUserDetails();
        iduser = user.get(SessionManager.KEY_ID);
        username = user.get(SessionManager.KEY_USERNAME);

        text_nama = findViewById(R.id.text_nama);
        text_nama.setText("Halo " + username);

        findViewById(R.id.img_logout).setOnClickListener(v -> {
            logoutUser();
        });
        findViewById(R.id.cv1).setOnClickListener(v -> {
            Intent x = new Intent(MenuKepala.this, ProfilKepalaSekolahActivity.class);
            x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            x.putExtra("idsiswa", iduser);
            startActivity(x);
        });
        findViewById(R.id.cv2).setOnClickListener(v -> startActivity(new Intent(MenuKepala.this, TransaksiPembayaran.class)));
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