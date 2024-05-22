package com.example.geraifood;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.geraifood.data.itemCart;
import com.example.geraifood.databinding.ActivityKeranjangBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private ArrayList<itemCart> itemCarts;
    private cartAdapter cartAdapter;

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