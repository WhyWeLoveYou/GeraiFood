package com.example.geraifood;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.geraifood.adapter.cartAdapter;
import com.example.geraifood.data.RiwayatCart;
import com.example.geraifood.data.itemCart;
import com.example.geraifood.databinding.ActivityKeranjangBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Keranjang extends AppCompatActivity implements com.example.geraifood.adapter.cartAdapter.OnItemDeletedListener {

    private ActivityKeranjangBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private ArrayList<RiwayatCart> riwayatCarts;
    private ArrayList<itemCart> itemCarts;
    private cartAdapter cartAdapter;
    private String formattedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityKeranjangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showData();

        listener();
    }

    private void listener() {
        binding.backa.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });
        binding.Keranjang.setOnClickListener(v -> {
            addData();
        });
    }

    private void addData() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        firestore.collection("users").document(uid).collection("item").document().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String nama = document.getString("namaMakanan");
                        String gambar = document.getString("gambar");
                        String harga = document.getString("harga");
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            LocalDateTime myDateObj = LocalDateTime.now();
                            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                            String formattedDate = myDateObj.format(myFormatObj);
                        }
                        RiwayatCart riwayatnya = new RiwayatCart(nama, harga, gambar, formattedData);

                        firestore.collection("users").document(uid).collection("riwayat").document().set(riwayatnya)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Keranjang.this, "Berhaasil Checkour=t", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Keranjang.this, "Gagal Checkout", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(Keranjang.this, "Data tidak ada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Keranjang.this, "hem", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showData() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        itemCarts = new ArrayList<>();
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

        cartAdapter = new cartAdapter(itemCarts, this);
        cartAdapter.setOnItemDeletedListener(this);
        binding.recyclerview.setAdapter(cartAdapter);
        firestore.collection("users").document(uid).collection("item").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                binding.progressB.setVisibility(View.GONE);
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {
                    itemCart c = d.toObject(itemCart.class);
                    if (c != null) {
                        itemCarts.add(c);
                    }
                }
                cartAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Keranjang.this, "Gagal", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onItemDeleted(int position) {
        // Remove the item from the dataset and notify the adapter
        itemCarts.remove(position);
        cartAdapter.notifyItemRemoved(position);
        cartAdapter.notifyItemRangeChanged(position, itemCarts.size());
    }
}