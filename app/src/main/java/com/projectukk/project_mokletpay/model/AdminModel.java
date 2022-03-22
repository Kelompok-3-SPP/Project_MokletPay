package com.projectukk.project_mokletpay.model;

public class AdminModel {
    String idadmin;
    String nama_admin;

    public AdminModel(String idadmin, String nama_admin) {
        this.idadmin = idadmin;
        this.nama_admin = nama_admin;
    }
    public String getIdadmin() {
        return idadmin;
    }

    public String getNama_admin() {
        return nama_admin;
    }
}
