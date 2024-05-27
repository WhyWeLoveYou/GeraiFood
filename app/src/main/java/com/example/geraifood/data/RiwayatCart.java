package com.example.geraifood.data;

public class RiwayatCart {


    private String NamaMakanan, Harga, Gambar, Tanggal;
    private Object Jumlah;

    public RiwayatCart() {

    }

    public RiwayatCart(String NamaMakanan, String Harga, String Gambar, String Tanggal, Object Jumlah) {
        this.NamaMakanan = NamaMakanan;
        this.Harga = Harga;
        this.Gambar = Gambar;
        this.Tanggal = Tanggal;
        this.Jumlah = Jumlah;
    }

    public String getNamaMakanan() {
        return NamaMakanan;
    }

    public void setNamaMakanan(String NamaMakanan) {
        this.NamaMakanan = NamaMakanan;
    }

    public String getHarga() {
        return Harga;
    }

    public void setHarga(String Harga) {
        this.Harga = Harga;
    }

    public String getGambar() {
        return Gambar;
    }

    public void setGambar(String Gambar) {
        this.Gambar = Gambar;
    }
    public String getTanggal() {return Tanggal;}
    public void setTanggal(String Tanggal) { this.Tanggal = Tanggal;}
    public Object getJumlah() {return Jumlah;}
    public void setJumlah(Object Jumlah) {this.Jumlah = Jumlah;}
}



