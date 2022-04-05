package com.projectukk.project_mokletpay.admintransaksi;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.projectukk.project_mokletpay.R;
import com.projectukk.project_mokletpay.helper.Connection;
import com.projectukk.project_mokletpay.helper.utils.CekKoneksi;
import com.projectukk.project_mokletpay.helper.utils.CustomDialog;
import com.projectukk.project_mokletpay.helper.utils.CustomProgressbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RekapitulasiAdminActivity extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();
    private TextView et_cari;
    private EditText et_tahun, et_bulan;
    private TextView text_siswa, text_transaksi, text_total, text_tahun, text_bulan;

    ArrayList<HashMap<String, String>> dataTahun = new ArrayList<>();
    ArrayList<HashMap<String, String>> dataBulan = new ArrayList<>();

    String fName = String.valueOf(System.currentTimeMillis());

    String jumlah_transaksi, jumlah_siswa, total_pembayaran_format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R .layout.activity_rekapitulasi_admin);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Rekapitulasi SPP");
        et_tahun = findViewById(R.id.et_tahun);
        et_bulan = findViewById(R.id.et_bulan);
        text_siswa = findViewById(R.id.text_siswa);
        text_transaksi = findViewById(R.id.text_transaksi);
        text_total = findViewById(R.id.text_total);
        text_tahun = findViewById(R.id.text_tahun);
        text_bulan = findViewById(R.id.text_bulan);

        LoadLaporan();
        LoadProvinsi();

        et_tahun.setOnClickListener(v -> {
            popup_provinsi();
        });
        et_bulan.setOnClickListener(v -> {
            popup_bulan();
        });

        findViewById(R.id.text_simpan).setOnClickListener(v -> {
            if(koneksi.isConnected(RekapitulasiAdminActivity.this)){
                LoadLaporan();
            } else {
                CustomDialog.noInternet(RekapitulasiAdminActivity.this);
            }
        });


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        findViewById(R.id.unduhButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                layoutToImage();

                layoutTOimageConverter();
            }
        });
    }

    private void LoadProvinsi() {
        dataTahun.clear();
        AndroidNetworking.get(Connection.CONNECT + "spp_laporan.php")
                .addQueryParameter("TAG", "list_tahun")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();

                                map.put("tahun_ajaran", responses.optString("tahun_ajaran"));

                                dataTahun.add(map);
                            }

                            customProgress.hideProgress();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                    }
                });
    }

    private void popup_provinsi() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RekapitulasiAdminActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.list_kategori, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = dialogBuilder.create();
        ListView lv_kategori = dialogView.findViewById(R.id.lv_kategori);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, dataTahun, R.layout.custom_list_jenis,
                new String[]{"tahun_ajaran"},
                new int[]{R.id.text_nama});
        lv_kategori.setAdapter(simpleAdapter);
        lv_kategori.setOnItemClickListener((parent, view, position, id) -> {
            String nama_kategori = ((TextView) view.findViewById(R.id.text_nama)).getText().toString();
            et_tahun.setText(nama_kategori);

            LoadBulan(nama_kategori);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void LoadBulan(String tahun) {
        customProgress.showProgress(this, false);
        dataBulan.clear();
        AndroidNetworking.get(Connection.CONNECT + "spp_laporan.php")
                .addQueryParameter("TAG", "list_bulan")
                .addQueryParameter("tahun", tahun)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();

                                map.put("bulan", responses.optString("bulan"));

                                dataBulan.add(map);
                            }

                            customProgress.hideProgress();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                    }
                });
    }

    private void popup_bulan() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RekapitulasiAdminActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.list_kategori, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = dialogBuilder.create();
        ListView lv_kategori = dialogView.findViewById(R.id.lv_kategori);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, dataBulan, R.layout.custom_list_jenis,
                new String[]{"bulan"},
                new int[]{R.id.text_nama});
        lv_kategori.setAdapter(simpleAdapter);
        lv_kategori.setOnItemClickListener((parent, view, position, id) -> {
            String nama_kategori = ((TextView) view.findViewById(R.id.text_nama)).getText().toString();
            et_bulan.setText(nama_kategori);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void LoadLaporan() {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_laporan.php")
                .addQueryParameter("TAG", "laporan")
                .addQueryParameter("tahun_ajaran", et_tahun.getText().toString().trim())
                .addQueryParameter("bulan", et_bulan.getText().toString().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        customProgress.hideProgress();
                        jumlah_transaksi = response.optString("jumlah_transaksi");
                        jumlah_siswa = response.optString("jumlah_siswa");
                        total_pembayaran_format = response.optString("total_pembayaran_format");

                        text_siswa.setText(jumlah_siswa + " Siswa");
                        text_transaksi.setText(jumlah_transaksi + " Transaksi");
                        text_total.setText(total_pembayaran_format);
                        text_tahun.setText(et_tahun.getText().toString().trim());
                        text_bulan.setText(et_bulan.getText().toString().trim());
                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                    }
                });
    }

    private void layoutTOimageConverter() {


        Dexter.withContext(this).withPermissions(WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

//                            ConstraintLayout layout = findViewById(R.id.lay);

//                            ScrollView layout = findViewById(R.id.sView);
                            LinearLayout layout  = findViewById(R.id.cetaklaporan);


                            File file = saveBitMap(RekapitulasiAdminActivity.this, layout);    //which view you want to pass that view as parameter
                            if (file != null) {
                                Log.i("TAG", "Drawing saved to the gallery!");
                                Toast.makeText(RekapitulasiAdminActivity.this, "Mengunduh", Toast.LENGTH_SHORT).show();


                                try {
                                    imageToPDF();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }


                            } else {
                                Log.i("TAG", "Oops! Image could not be saved.");
                                Toast.makeText(RekapitulasiAdminActivity.this, "Click Again !", Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            Toast.makeText(RekapitulasiAdminActivity.this, "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();


    }

    public void imageToPDF() throws FileNotFoundException {
        try {
            Document document = new Document();
            String dirpath = android.os.Environment.getExternalStorageDirectory().toString();
            PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/"+fName+".pdf")); //  Change pdf's name.
            document.open();
            Image img = Image.getInstance(Environment.getExternalStorageDirectory() + File.separator +"/Pictures/Download/"+ fName+".jpg");
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / img.getWidth()) * 100;
            img.scalePercent(scaler);
            img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            document.add(img);
            document.close();
            Toast.makeText(this, "Berhasil mengunduh", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

    private File saveBitMap(Context context, View drawView) {
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Download"); // enter folder name to save image
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() + File.separator +fName + ".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery(context, pictureFile.getAbsolutePath());
        return pictureFile;
    }

    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}