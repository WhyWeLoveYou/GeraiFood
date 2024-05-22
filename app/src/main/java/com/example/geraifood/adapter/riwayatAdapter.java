package com.example.geraifood.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geraifood.R;
import com.example.geraifood.data.RiwayatCart;
import com.example.geraifood.data.itemCart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class riwayatAdapter extends RecyclerView.Adapter<riwayatAdapter.ViewHolder> {

    private ArrayList<RiwayatCart> cartitemArrayList;
    private Context context;

    public riwayatAdapter(ArrayList<RiwayatCart> coursesArrayList, Context context) {
        this.cartitemArrayList = coursesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public riwayatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_riwayat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull riwayatAdapter.ViewHolder holder, int position) {
        RiwayatCart riwayatAdapters = cartitemArrayList.get(position);
        holder.textViewMakanan.setText(riwayatAdapters.getNamaMakanan());
        holder.textViewHarga.setText(riwayatAdapters.getHarga());
        holder.textViewTanggal.setText(riwayatAdapters.getTanggal());
        if (riwayatAdapters.getGambar() == null) {
            holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
        } else {
            String bytea = riwayatAdapters.getGambar();
            byte[] bytes = Base64.decode(bytea, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return cartitemArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewMakanan;
        private final TextView textViewHarga;
        private final ImageView imageView;
        private final TextView textViewTanggal;

        private FirebaseFirestore firebaseFirestore;
        private FirebaseAuth auth;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMakanan = itemView.findViewById(R.id.makanan);
            textViewHarga = itemView.findViewById(R.id.harga);
            imageView = itemView.findViewById(R.id.imageviewA);
            textViewTanggal = itemView.findViewById(R.id.tanggal);
            auth = FirebaseAuth.getInstance();
        }
    }
}