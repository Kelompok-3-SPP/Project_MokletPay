package com.projectukk.project_mokletpay.transaksi;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.content.Context;
import android.content.Intent;
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
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
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

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class DetailTransaksi extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();
//    public static SessionManager SessionManager;
//    public static String iduser, username;

    String fName = String.valueOf(System.currentTimeMillis());

    private TextView et_cari;
    String idtransaksi, iduser;
    String nis, nama, nama_kelas, tahun_ajaran, bulan, petugas, jumlah_pembayaran;
    TextView text_nis, text_nama, text_kelas, text_tahun, text_bulan, text_total, text_petugas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi);
        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Detail Pembayaran");

//        SessionManager = new SessionManager(DetailTransaksi.this);
//        SessionManager.checkLogin();
//        HashMap<String, String> user = SessionManager.getUserDetails();
//        iduser = user.get(SessionManager.KEY_ID);
//        username = user.get(SessionManager.KEY_USERNAME);




//
//        if (!checkPermission()){
//
//            requestPermission();
//        }

        //add strict mode code on app intialize

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        findViewById(R.id.unduhButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                layoutToImage();

                layoutTOimageConverter();
            }
        });

        Intent i = getIntent();
        idtransaksi = i.getStringExtra("idtransaksi");


        text_nis = findViewById(R.id.text_nis);
        text_nama = findViewById(R.id.text_nama);
        text_kelas = findViewById(R.id.text_kelas);
        text_tahun = findViewById(R.id.text_tahun);
        text_bulan = findViewById(R.id.text_bulan);
        text_total = findViewById(R.id.text_total);
        text_petugas = findViewById(R.id.text_petugas);



        LoadData();
    }



    private void LoadData() {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_transaksi.php")
                .addQueryParameter("TAG", "detail")
                .addQueryParameter("idtransaksi", idtransaksi)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        customProgress.hideProgress();
                        nis = response.optString("nis");
                        nama = response.optString("nama");
                        nama_kelas = response.optString("nama_kelas");
                        tahun_ajaran = response.optString("tahun_ajaran");
                        bulan = response.optString("bulan");
                        petugas = response.optString("nama_petugas");
                        jumlah_pembayaran = response.optString("jumlah_pembayaran");

                        text_nis.setText(nis);
                        text_nama.setText(nama);
                        text_kelas.setText(nama_kelas);
                        text_tahun.setText(tahun_ajaran);
                        text_bulan.setText(bulan);
                        text_petugas.setText(petugas);
                        text_total.setText(jumlah_pembayaran);

                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            customProgress.hideProgress();
                        } else {
                            customProgress.hideProgress();
                            CustomDialog.errorDialog(DetailTransaksi.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
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
                            FrameLayout layout  = findViewById(R.id.sllayout);


                            File file = saveBitMap(DetailTransaksi.this, layout);    //which view you want to pass that view as parameter
                            if (file != null) {
                                Log.i("TAG", "Drawing saved to the gallery!");
                                Toast.makeText(DetailTransaksi.this, "Mengunduh", Toast.LENGTH_SHORT).show();


                                try {
                                    imageToPDF();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }


                            } else {
                                Log.i("TAG", "Oops! Image could not be saved.");
                                Toast.makeText(DetailTransaksi.this, "Click Again !", Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            Toast.makeText(DetailTransaksi.this, "Permissions are not granted!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "PDF Generated successfully!..", Toast.LENGTH_SHORT).show();
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
//    private boolean checkPermission() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageManager();
//        } else {
//            int result = ContextCompat.checkSelfPermission(DetailTransaksi.this, READ_EXTERNAL_STORAGE);
//            int result1 = ContextCompat.checkSelfPermission(DetailTransaksi.this, WRITE_EXTERNAL_STORAGE);
//            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
//        }
//    }


    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(DetailTransaksi.this, new String[]{WRITE_EXTERNAL_STORAGE}, 1100);
        }
    }
}