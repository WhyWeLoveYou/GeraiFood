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
import androidx.recyclerview.widget.RecyclerView;

import com.example.geraifood.adapter.cartAdapter;
import com.example.geraifood.data.RiwayatCart;
import com.example.geraifood.data.itemCart;
import com.example.geraifood.databinding.ActivityKeranjangBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class Keranjang extends AppCompatActivity implements com.example.geraifood.adapter.cartAdapter.OnItemDeletedListener, cartAdapter.OnItemChangeListener  {

    private ActivityKeranjangBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private ArrayList<RiwayatCart> riwayatCarts;
    private ArrayList<itemCart> itemCarts;
    private cartAdapter cartAdapter;
    private String uid;
    private String formattedData;
    private ArrayList<itemCart> cartitemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKeranjangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showData();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        listener();
    }

    private void listener() {
        binding.backa.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });
        binding.Keranjang.setOnClickListener(v -> {
            binding.progressB.setVisibility(View.VISIBLE);
            addData();
        });
    }

    private void addData() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        firestore.collection("users").document(uid).collection("item").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    if (!result.isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String nama = document.getString("namaMakanan");
                            String gambar = document.getString("gambar");
                            String harga = document.getString("harga");
                            Object jumlah = document.getLong("jumlah");

                            String formattedDate = "";
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                LocalDateTime myDateObj = LocalDateTime.now();
                                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                                formattedDate = myDateObj.format(myFormatObj);
                            }
                            RiwayatCart riwayatnya = new RiwayatCart(nama, harga, gambar, formattedDate, jumlah);

                            firestore.collection("users").document(uid).collection("riwayat").add(riwayatnya)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(Keranjang.this, "Berhasil", Toast.LENGTH_SHORT).show();
                                            firestore.collection("users").document(uid).collection("item").get().addOnSuccessListener((querySnapshot) -> {
                                                        WriteBatch batch = firestore.batch();
                                                        binding.progressB.setVisibility(View.GONE);
                                                        for (QueryDocumentSnapshot doc : querySnapshot) {
                                                            batch.delete(doc.getReference());
                                                        }

                                                        batch
                                                                .commit()
                                                                .addOnSuccessListener((result) -> {
                                                                    showData();
                                                                })
                                                                .addOnFailureListener((error) -> {
                                                                    Toast.makeText(Keranjang.this, "Gagal", Toast.LENGTH_SHORT).show();
                                                                });
                                                    })
                                                    .addOnFailureListener((error) -> {
                                                        Toast.makeText(Keranjang.this, "Gagal", Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Keranjang.this, "Gagal", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        binding.progressB.setVisibility(View.GONE);
                        Toast.makeText(Keranjang.this, "Anda belum memesan apapun", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    binding.progressB.setVisibility(View.GONE);
                    Toast.makeText(Keranjang.this, "Gagal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showData() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        itemCarts = new ArrayList<>();
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

        cartAdapter = new cartAdapter(itemCarts, this);
        cartAdapter.setOnItemDeletedListener(this);
        cartAdapter.setOnItemChangeListener(this);
        binding.recyclerview.setAdapter(cartAdapter);
        firestore.collection("users").document(uid).collection("item").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                binding.progressB.setVisibility(View.GONE);
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                countHarga();
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

    private void countHarga() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        binding.progressB.setVisibility(View.VISIBLE);
        firestore.collection("users").document(uid).collection("item").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                binding.progressB.setVisibility(View.GONE);
                int totalHarga = 0;
                String totalhargaa = "0";
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {
                    int jumlah = Integer.parseInt(d.get("jumlah").toString());
                    if (d.get("harga") != null) {
                        String hargaa = d.get("harga").toString();
                        int harga = Integer.parseInt(hargaa);
                        totalHarga += harga * jumlah;
                        totalhargaa = String.valueOf(totalHarga);
                    } else {
                        totalHarga = 0;
                        totalhargaa = String.valueOf(totalHarga);
                    }
                }
                binding.Hargatotal.setText(totalhargaa);
            }
        });
    }

    public void onItemDeleted(int position) {
        if (position != RecyclerView.NO_POSITION && position < itemCarts.size()) {
            itemCarts.remove(position);
            cartAdapter.notifyItemRemoved(position);
            cartAdapter.notifyItemRangeChanged(position, itemCarts.size());
            countHarga();
        } else {
            // Handle invalid position
            Log.e(TAG, "Invalid position in onItemDeleted");
        }
    }

    public void onItemChange() {
        countHarga();
    }
}