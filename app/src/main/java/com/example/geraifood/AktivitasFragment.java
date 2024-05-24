package com.example.geraifood;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.geraifood.adapter.riwayatAdapter;
import com.example.geraifood.data.RiwayatCart;
import com.example.geraifood.databinding.FragmentAktivitasBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class AktivitasFragment extends Fragment {
    private FragmentAktivitasBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ArrayList<RiwayatCart> RiwayatCarrs;
    private riwayatAdapter riwayatAdapters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAktivitasBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getItem();
    }

    private void getItem() {
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        RiwayatCarrs = new ArrayList<RiwayatCart>();
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        String uid = firebaseAuth.getCurrentUser().getUid();

        riwayatAdapters = new riwayatAdapter(RiwayatCarrs, getContext());
        binding.recyclerview.setAdapter(riwayatAdapters);
        firestore.collection("users").document(uid).collection("riwayat").orderBy("tanggal").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            binding.progressB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                RiwayatCart c = d.toObject(RiwayatCart.class);
                                RiwayatCarrs.add(c);
                            }
                            riwayatAdapters.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext().getApplicationContext(), "No data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext().getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}