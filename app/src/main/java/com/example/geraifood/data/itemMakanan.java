package com.example.geraifood.data;

public class itemMakanan {


    private String NamaMakanan, Harga, Gambar;

    public itemMakanan() {

    }

    public itemMakanan(String NamaMakanan, String Harga, String Gambar) {
        this.NamaMakanan = NamaMakanan;
        this.Harga = Harga;
        this.Gambar = Gambar;
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
}


