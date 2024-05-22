package com.example.geraifood.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geraifood.R;
import com.example.geraifood.data.itemMakanan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String itunya;
    private ArrayList<itemMakanan> ItemArrayList;

    public HomeAdapter(ArrayList<itemMakanan> itemArrayList, Context context) {
        this.ItemArrayList = itemArrayList;
        this.context = context;
    }
    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_makanan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {
        itemMakanan itemMakanan = ItemArrayList.get(position);
        holder.namaMakanan.setText(itemMakanan.getNamaMakanan());
        holder.hargamakanan.setText(itemMakanan.getHarga());

        if (itemMakanan.getGambar() == null) {
            holder.imageview.setImageResource(R.drawable.ic_launcher_foreground);
        } else {
            String bytea = itemMakanan.getGambar();
            byte[] bytes = Base64.decode(bytea, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bitmap != null) {
                holder.imageview.setImageBitmap(bitmap);
            } else {
                holder.imageview.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }
    }

    @Override
    public int getItemCount() {
        return ItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView namaMakanan;
        private final TextView hargamakanan;
        private final ImageView imageview;
        private final RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namaMakanan = itemView.findViewById(R.id.makanan);
            hargamakanan = itemView.findViewById(R.id.harga);
            imageview = itemView.findViewById(R.id.imageview);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);

            relativeLayout.setOnClickListener(v -> {
                itemMakanan item = ItemArrayList.get(getAdapterPosition());
                Intent val = new Intent(context.getApplicationContext(), null);
                val.putExtra("jeneng", item.getNamaMakanan());
                context.startActivity(val);
            });

        }
    }
}
