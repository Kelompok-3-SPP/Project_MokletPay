package com.projectukk.project_mokletpay.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.projectukk.project_mokletpay.R;
import com.projectukk.project_mokletpay.helper.Connection;
import com.projectukk.project_mokletpay.helper.utils.CekKoneksi;
import com.projectukk.project_mokletpay.helper.utils.CustomDialog;
import com.projectukk.project_mokletpay.helper.utils.CustomProgressbar;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfilPetugasActivity extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    private LinearLayout ly00, ly11;
    private EditText et_nama, et_username;
    String idpetugas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_petugas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent i = getIntent();
        idpetugas = i.getStringExtra("idpetugas");

        ly00 = findViewById(R.id.ly00);
        ly11 = findViewById(R.id.ly11);
        et_nama = findViewById(R.id.et_nama);
        et_username = findViewById(R.id.et_username);

        ly00.setVisibility(View.VISIBLE);
        ly11.setVisibility(View.GONE);
        LoadData();

        actionButton();
    }

    private void actionButton() {
        findViewById(R.id.back).setOnClickListener(v -> finish());
        findViewById(R.id.text_hapus).setOnClickListener(v -> {
            if (koneksi.isConnected(ProfilPetugasActivity.this)){
                HapusData(idpetugas);
            } else {
                CustomDialog.noInternet(ProfilPetugasActivity.this);
            }
        });

    }

    private void LoadData() {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_akun.php")
                .addQueryParameter("TAG", "detail_petugas")
                .addQueryParameter("idpetugas", idpetugas)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        et_nama.setText(response.optString("nama"));
                        et_username.setText(response.optString("notelp"));

                        ly00.setVisibility(View.GONE);
                        ly11.setVisibility(View.VISIBLE);
                        customProgress.hideProgress();
                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(ProfilPetugasActivity.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(ProfilPetugasActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    private void UpdateData() {
        AndroidNetworking.get(Connection.CONNECT + "spp_akun.php")
                .addQueryParameter("TAG", "edit_petugas")
                .addQueryParameter("idsiswa", idpetugas)
                .addQueryParameter("nama", et_nama.getText().toString().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        successDialog(ProfilPetugasActivity.this, response.optString("pesan"));
                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(ProfilPetugasActivity.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(ProfilPetugasActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    private void HapusData(String idpetugas) {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_akun.php")
                .addQueryParameter("TAG", "hapus_petugas")
                .addQueryParameter("idpetugas", idpetugas)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        customProgress.hideProgress();
                        successDialog(ProfilPetugasActivity.this, response.optString("pesan"));
                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(ProfilPetugasActivity.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(ProfilPetugasActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public void successDialog(final Context context, final String alertText) {
        final View inflater = LayoutInflater.from(context).inflate(R.layout.custom_success_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(inflater);
        builder.setCancelable(false);
        final TextView ket = inflater.findViewById(R.id.keterangan);
        ket.setText(alertText);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparan);
        inflater.findViewById(R.id.ok).setOnClickListener(v -> {
            finish();
            alertDialog.dismiss();
        });
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}