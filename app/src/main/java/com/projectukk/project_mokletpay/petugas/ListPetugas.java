package com.projectukk.project_mokletpay.petugas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.projectukk.project_mokletpay.R;
import com.projectukk.project_mokletpay.auth.ProfilPetugasActivity;
import com.projectukk.project_mokletpay.helper.Connection;
import com.projectukk.project_mokletpay.helper.utils.CekKoneksi;
import com.projectukk.project_mokletpay.helper.utils.CustomDialog;
import com.projectukk.project_mokletpay.helper.utils.CustomProgressbar;
import com.projectukk.project_mokletpay.model.PetugasModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListPetugas extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    private LinearLayout ly00, ly11, ly22;
    private RecyclerView rv_data;
    List<PetugasModel> PetugasModel;
    int limit = 0, offset = 1000;
    private TextView text_more;
    private SwipeRefreshLayout swipe_refresh;
    private EditText text_search;
    private TextView et_cari;
    String idpetugas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_petugas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Daftar Petugas");

        ly00 = findViewById(R.id.ly00);
        ly11 = findViewById(R.id.ly11);
        ly22 = findViewById(R.id.ly22);
        rv_data = findViewById(R.id.rv_data);
        text_more = findViewById(R.id.text_more);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        text_search = findViewById(R.id.text_search);

        PetugasModel = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        rv_data.setHasFixedSize(true);
        rv_data.setLayoutManager(x);
        rv_data.setNestedScrollingEnabled(true);

        ActiomButton();
    }

    private void ActiomButton() {
        findViewById(R.id.add).setOnClickListener(v -> {
            Intent i = new Intent(ListPetugas.this, TambahPetugas.class);
//            i.putExtra("idpetugas", idpetugas);
            startActivity(i);
        });
        findViewById(R.id.back).setOnClickListener(v -> finish());
        text_search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                ly11.setVisibility(View.GONE);
                ly00.setVisibility(View.VISIBLE);
                ly22.setVisibility(View.GONE);
                limit = 0;
                PetugasModel.clear();
                LoadPegawai(limit, offset, text_search.getText().toString().trim());
                return true;
            }
            return false;
        });
        text_more.setOnClickListener(v -> {
            limit = limit + offset;
            LoadPegawai(limit, offset, text_search.getText().toString().trim());
        });
        swipe_refresh.setOnRefreshListener(() -> {
            ly11.setVisibility(View.GONE);
            ly00.setVisibility(View.VISIBLE);
            ly22.setVisibility(View.GONE);
            text_search.setText("");
            limit = 0;
            PetugasModel.clear();
            LoadPegawai(limit, offset, text_search.getText().toString().trim());
        });
    }

    @Override
    protected void onResume() {
        ly11.setVisibility(View.GONE);
        ly00.setVisibility(View.VISIBLE);
        ly22.setVisibility(View.GONE);
        PetugasModel.clear();
        text_search.setText("");
        limit = 0;
        LoadPegawai(limit, offset, text_search.getText().toString().trim());
        super.onResume();
    }

    private void LoadPegawai(int limit, int offset, String cari) {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_petugas.php")
                .addQueryParameter("TAG", "listsemua")
                .addQueryParameter("limit", String.valueOf(limit))
                .addQueryParameter("offset", String.valueOf(offset))
                .addQueryParameter("q", cari)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                PetugasModel bk = new PetugasModel(
                                        responses.getString("idpetugas"),
                                        responses.getString("nama_petugas"));
                                PetugasModel.add(bk);
                            }

                            ListPetugas.PegawaiAdapter adapter = new ListPetugas.PegawaiAdapter(getApplicationContext(), PetugasModel);
                            rv_data.setAdapter(adapter);

                            ly00.setVisibility(View.GONE);
                            ly11.setVisibility(View.VISIBLE);
                            ly22.setVisibility(View.GONE);
                            if (adapter.getItemCount() < offset) {
                                text_more.setVisibility(View.GONE);
                            } else {
                                text_more.setVisibility(View.VISIBLE);
                            }

                            swipe_refresh.setRefreshing(false);
                            customProgress.hideProgress();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ly11.setVisibility(View.GONE);
                            ly00.setVisibility(View.GONE);
                            ly22.setVisibility(View.GONE);
                            swipe_refresh.setRefreshing(false);
//                            hideDialog();
                            customProgress.hideProgress();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                String kode = body.optString("kode");
                                if (kode.equals("0")) {
                                    //tidak ada data
                                    customProgress.hideProgress();
                                    swipe_refresh.setRefreshing(false);
                                    ly00.setVisibility(View.GONE);
                                    ly11.setVisibility(View.GONE);
                                    ly22.setVisibility(View.VISIBLE);
                                    text_more.setVisibility(View.GONE);
                                    CustomDialog.errorDialog(ListPetugas.this, body.optString("pesan"));
                                } else if (kode.equals("1")) {
                                    //mencapai batas limit
                                    customProgress.hideProgress();
                                    swipe_refresh.setRefreshing(false);
                                    ly00.setVisibility(View.GONE);
                                    ly11.setVisibility(View.VISIBLE);
                                    ly22.setVisibility(View.GONE);
                                    text_more.setVisibility(View.GONE);
                                    CustomDialog.errorDialog(ListPetugas.this, body.optString("pesan"));
                                } else {
                                    //2 tiket dibatalkan
                                    customProgress.hideProgress();
                                    swipe_refresh.setRefreshing(false);
                                    ly00.setVisibility(View.GONE);
                                    ly11.setVisibility(View.GONE);
                                    ly22.setVisibility(View.VISIBLE);
                                    text_more.setVisibility(View.GONE);
                                    CustomDialog.errorDialog(ListPetugas.this, body.optString("pesan"));
                                }
                            } catch (JSONException ignored) {
                            }
                        } else {
                            customProgress.hideProgress();
                            CustomDialog.errorDialog(ListPetugas.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public class PegawaiAdapter extends RecyclerView.Adapter<ListPetugas.PegawaiAdapter.ProductViewHolder> {
        private final Context mCtx;
        private final List<PetugasModel> PetugasModel;

        PegawaiAdapter(Context mCtx, List<PetugasModel> PetugasModel) {
            this.mCtx = mCtx;
            this.PetugasModel = PetugasModel;
        }

        @Override
        public ListPetugas.PegawaiAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.model_list_petugas, null);
            return new ListPetugas.PegawaiAdapter.ProductViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ListPetugas.PegawaiAdapter.ProductViewHolder holder, int i) {
            final PetugasModel petugass = PetugasModel.get(i);
            holder.text_nama.setText(petugass.getNama_petugas());
            holder.text_id.setText(petugass.getIdpetugas());
            holder.cv.setOnClickListener(view -> {
                Intent x = new Intent(mCtx, ProfilPetugasActivity.class);
                x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                x.putExtra("idpetugas", petugass.getIdpetugas());
                mCtx.startActivity(x);
                finish();
            });
        }

        @Override
        public int getItemCount() {
            return PetugasModel.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_nama, text_id;
            CardView cv;

            ProductViewHolder(View itemView) {
                super(itemView);
                text_nama = itemView.findViewById(R.id.text_nama);
                text_id = itemView.findViewById(R.id.text_id);
                cv = itemView.findViewById(R.id.cv);
            }
        }
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
            onResume();
            alertDialog.dismiss();
        });
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}