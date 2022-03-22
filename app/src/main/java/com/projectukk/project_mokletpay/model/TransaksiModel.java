package com.projectukk.project_mokletpay.model;

public class TransaksiModel {
    String idtransaksi;
    String invoice;
    String nama;
    String bulan;
    String tahun;
    String jumlah_pembayaran;
    String file_pembayaran;
    String tgl_create;

    public TransaksiModel(String idtransaksi, String nama, String bulan, String tahun, String jumlah_pembayaran, String tgl_create) {
        this.idtransaksi = idtransaksi;
//        this.invoice = invoice;
        this.nama = nama;
        this.bulan = bulan;
        this.tahun = tahun;
        this.jumlah_pembayaran = jumlah_pembayaran;
        this.tgl_create = tgl_create;
    }

    public String getIdtransaksi() {
        return idtransaksi;
    }

//    public String getInvoice() {
//        return invoice;
//    }

    public String getNama() {
        return nama;
    }

    public String getBulan() {
        return bulan;
    }

    public String getTahun() {
        return tahun;
    }

    public String getJumlah_pembayaran() {
        return jumlah_pembayaran;
    }

}
