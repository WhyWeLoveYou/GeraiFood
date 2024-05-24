package com.example.geraifood.data;

public class itemCart {
    private String NamaMakanan, Harga, Gambar;
    private Object Jumlah;

    public itemCart() {

    }

    public itemCart(String NamaMakanan, String Harga, String Gambar, Object Jumlah) {
        this.NamaMakanan = NamaMakanan;
        this.Harga = Harga;
        this.Gambar = Gambar;
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

    public Object getJumlah() {return Jumlah;}
    public void setJumlah(Object Jumlah) {this.Jumlah = Jumlah;}
}
