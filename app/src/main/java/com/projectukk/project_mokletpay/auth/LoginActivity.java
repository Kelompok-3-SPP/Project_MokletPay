package com.projectukk.project_mokletpay.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.projectukk.project_mokletpay.R;
import com.projectukk.project_mokletpay.helper.Connection;
import com.projectukk.project_mokletpay.helper.SessionManager;
import com.projectukk.project_mokletpay.helper.utils.CekKoneksi;
import com.projectukk.project_mokletpay.helper.utils.CustomDialog;
import com.projectukk.project_mokletpay.helper.utils.CustomProgressbar;
import com.projectukk.project_mokletpay.menu.MainAdmin;
import com.projectukk.project_mokletpay.menu.MainSiswa;
import com.projectukk.project_mokletpay.menu.MainPetugas;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class LoginActivity extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();
    private EditText inputUsername, inputkatasandi;
    private AppCompatTextView btnLogin;
    private SessionManager SessionManager;
    String status_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SessionManager = new SessionManager(getApplicationContext());
//        if (SessionManager.isLoggedIn()) {
//            Intent intent = new Intent(LoginActivity.this, MainAdmin.class);
//            startActivity(intent);
//            finish();
//        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent i = getIntent();
        status_login = i.getStringExtra("status_login");

        inputUsername = findViewById(R.id.inputUsername);
        inputkatasandi = findViewById(R.id.inputkatasandi);
        btnLogin = findViewById(R.id.btnLogin);

        AksiTombol();
    }

    private void AksiTombol() {
        btnLogin.setOnClickListener(v -> {
            if (koneksi.isConnected( LoginActivity.this)) {

                String username = inputUsername.getText().toString().trim();
                String pass_login = inputkatasandi.getText().toString().trim();

                if (!username.isEmpty() && !pass_login.isEmpty()) {

                    if (status_login.equals("1")){
                        LoginProses(username, pass_login);
                    } else if (status_login.equals("3")){
                        LoginProsesSiswa(username, pass_login);
                    } else {
                        LoginProsesPetugas(username, pass_login);
                    }
                } else {
                    CustomDialog.errorDialog(LoginActivity.this, "Data tidak boleh ada yang kosong.");
                }
            } else {
                CustomDialog.noInternet(LoginActivity.this);
            }
        });
    }

    private void LoginProses(final String username, final String pass_login) {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_login_admin.php")
                .addQueryParameter("username", username)
                .addQueryParameter("password", pass_login)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        clearApplicationData();
                        SessionManager.createLoginSession(
                                response.optString("idforeign"),
                                response.optString("nama_admin"),
                                response.optString("status_user"));
                        Intent intent = new Intent(LoginActivity.this, MainAdmin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(LoginActivity.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(LoginActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    private void LoginProsesSiswa(final String username, final String pass_login) {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_login_siswa.php")
                .addQueryParameter("username", username)
                .addQueryParameter("password", pass_login)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        clearApplicationData();
                        SessionManager.createLoginSession(
                                response.optString("idforeign"),
                                response.optString("nama"),
                                response.optString("status_user"));
                        Intent intent = new Intent(LoginActivity.this, MainSiswa.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(LoginActivity.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(LoginActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    private void LoginProsesPetugas(final String username, final String pass_login) {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_login_Petugas.php")
                .addQueryParameter("username", username)
                .addQueryParameter("password", pass_login)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        clearApplicationData();
                        SessionManager.createLoginSession(
                                response.optString("idforeign"),
                                response.optString("nama_petugas"),
                                response.optString("status_user"));
                        Intent intent = new Intent(LoginActivity.this, MainPetugas.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(LoginActivity.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(LoginActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
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