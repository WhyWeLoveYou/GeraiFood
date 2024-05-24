package com.example.geraifood.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geraifood.Keranjang;
import com.example.geraifood.R;
import com.example.geraifood.data.itemCart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class cartAdapter extends RecyclerView.Adapter<cartAdapter.ViewHolder> {

    private ArrayList<itemCart> cartitemArrayList;
    private Context context;
    private OnItemDeletedListener onItemDeletedListener;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    public cartAdapter(ArrayList<itemCart> coursesArrayList, Context context) {
        this.cartitemArrayList = coursesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public cartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_keranjang, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull cartAdapter.ViewHolder holder, int position) {
        itemCart cartItem = cartitemArrayList.get(position);
        holder.textViewMakanan.setText(cartItem.getNamaMakanan());
        holder.textViewHarga.setText(cartItem.getHarga());
        holder.textViewJumlah.setText("Jumlah: " + String.valueOf(cartItem.getJumlah()));
        if (cartItem.getGambar() == null) {
            holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
        } else {
            String bytea = cartItem.getGambar();
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
        private final TextView textViewJumlah;
        private final Button Buttom;

        private FirebaseFirestore firebaseFirestore;
        private FirebaseAuth auth;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMakanan = itemView.findViewById(R.id.makanan2);
            textViewHarga = itemView.findViewById(R.id.harga2);
            imageView = itemView.findViewById(R.id.imageviewK);
            Buttom = itemView.findViewById(R.id.hapus);
            auth = FirebaseAuth.getInstance();
            firebaseFirestore = FirebaseFirestore.getInstance();
            textViewJumlah = itemView.findViewById(R.id.jumlah);

            Buttom.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && position < cartitemArrayList.size()) {
                    if (auth.getCurrentUser() != null) {
                        String email = auth.getCurrentUser().getUid();
                        String namaMakanannya = cartitemArrayList.get(position).getNamaMakanan();
                        if (namaMakanannya != null) {
                            firebaseFirestore.collection("users").document(email).collection("item").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list) {
                                        if (d.get("harga") != null) {
                                            int jumlaha = Integer.parseInt(d.get("jumlah").toString());
                                            if (jumlaha <= 1) {
                                                firebaseFirestore.collection("users").document(email).collection("item").document(namaMakanannya)
                                                        .delete().addOnSuccessListener(task -> {
                                                            if (onItemDeletedListener != null) {
                                                                onItemDeletedListener.onItemDeleted(getAdapterPosition());
                                                            }
                                                        })
                                                        .addOnFailureListener(task -> {
                                                            Toast.makeText(context.getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                                                        });
                                            } else {
                                                firebaseFirestore.collection("users").document(email).collection("item").document(namaMakanannya).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot documentSnapshot = task.getResult();
                                                            if (documentSnapshot.exists()) {
                                                                firebaseFirestore.collection("users").document(email).collection("item").document(namaMakanannya).update("jumlah", FieldValue.increment(-1))    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            int position = getAdapterPosition();
                                                                            itemCart updatedItem = cartitemArrayList.get(position);
                                                                            updatedItem.setJumlah(Integer.parseInt(updatedItem.getJumlah().toString()) - 1);
                                                                            notifyItemChanged(position);
                                                                        }
                                                                    }
                                                                });

                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(context.getApplicationContext(), "Document ID is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context.getApplicationContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context.getApplicationContext(), "Invalid position", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public interface OnItemDeletedListener {
        void onItemDeleted(int position);
    }

    public void setOnItemDeletedListener(OnItemDeletedListener listener) {
        this.onItemDeletedListener = listener;
    }


}