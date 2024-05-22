package com.example.geraifood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.geraifood.databinding.FragmentProfilPageBinding;
import com.example.geraifood.hm.DataMakanan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class profil_page extends Fragment {

    private FragmentProfilPageBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfilPageBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
        listener();
    }

    private void listener() {
        binding.button.setOnClickListener(v-> {
            if (validator()) {
                binding.progressB.setVisibility(View.VISIBLE);
                updateData();
            }
        });
        binding.buttonTambah.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), DataMakanan.class);
            startActivity(intent);
        });
    }


    private void updateData() {
        String nama = binding.editTextText4.getText().toString();
        String email = binding.editTextTextEmailAddress4.getText().toString().toLowerCase();
        String password = binding.editTextTextPassword4.getText().toString();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        String currentUser = firebaseAuth.getCurrentUser().getUid();
        String emailuser = firebaseAuth.getCurrentUser().getEmail().toLowerCase();
        firestore.collection("users").document(currentUser).update(
                "Email", email,
                "Nama", nama,
                "Password", password
        ).addOnSuccessListener(task -> {
            binding.progressB.setVisibility(View.GONE);
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.getCurrentUser().updateEmail(email);
            firebaseAuth.getCurrentUser().updatePassword(password);
            getData();
        }).addOnFailureListener(task -> {
            showToast("Gagal");
        });
    }


    private void getData() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        String email = firebaseAuth.getCurrentUser().getEmail().toLowerCase();
        Toast.makeText(getContext(), email, Toast.LENGTH_SHORT).show();
        String currentUser = firebaseAuth.getCurrentUser().getUid();
        firestore.collection("users").document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    binding.progressB.setVisibility(View.GONE);
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String bytea = documentSnapshot.getString("Image");
                        byte[] bytes = Base64.decode(bytea, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        binding.textView2.setText(documentSnapshot.getString("Email"));
                        binding.textView3.setText(documentSnapshot.getString("Nama"));
                        binding.imageView2.setImageBitmap(bitmap);
                    }
                }
            }
        }).addOnFailureListener(task -> {
            Toast.makeText(getContext(), "Gagal mendapatkan data user", Toast.LENGTH_SHORT).show();
        });
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean validator() {
        String nama = binding.editTextText4.getText().toString();
        String email = binding.editTextTextEmailAddress4.getText().toString();
        String password = binding.editTextTextPassword4.getText().toString();
        if (nama.isEmpty()) {
            showToast("Silahkan masukkan nama");
            return false;
        }
        if (email.isEmpty()) {
            showToast("Silahkan masukkan email");
            return false;
        }
        if (password.isEmpty()) {
            showToast("Silahkan masukkan password");
            return false;
        }
        return true;
    }
}