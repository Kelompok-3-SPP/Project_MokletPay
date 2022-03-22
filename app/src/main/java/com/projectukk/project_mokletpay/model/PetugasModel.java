package com.projectukk.project_mokletpay.model;

public class PetugasModel {
    String idpetugas;
    String nama_petugas;

    public PetugasModel(String idpetugas, String nama_petugas) {
        this.idpetugas = idpetugas;
        this.nama_petugas = nama_petugas;
    }
    public String getIdpetugas() {
        return idpetugas;
    }

    public String getNama_petugas() {
        return nama_petugas;
    }
}
