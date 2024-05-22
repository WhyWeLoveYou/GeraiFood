package com.example.geraifood;

import static android.content.Intent.getIntent;
import static android.content.Intent.getIntentOld;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.geraifood.data.itemCart;
import com.example.geraifood.databinding.FragmentDeskripsiBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.UUID;


public class DeskripsiFragment extends Fragment {
    private FragmentDeskripsiBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDeskripsiBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
        listener();
    }

    private void listener() {
        binding.deskbutton.setOnClickListener(v -> {
            addData();
        });
    }

    private void getData() {
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String namnya = getActivity().getIntent().getStringExtra("jeneng");

        firestore.collection("makanan").document(namnya).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String bytea = documentSnapshot.getString("gambar");
                    byte[] bytes = Base64.decode(bytea, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    binding.deskmakanan.setText(documentSnapshot.getString("namaMakanan"));
                    binding.deskharga.setText(documentSnapshot.getString("harga"));
                    binding.imageview.setImageBitmap(bitmap);
                }
            }
        });
    }

    private String encodeImage(Bitmap bitmap) {
        int previewW = 150;
        int previewH = bitmap.getHeight() * previewW / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewW, previewH, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void addData() {
        String NamaMakanan = binding.deskmakanan.getText().toString();
        String Harga = binding.deskharga.getText().toString();
        Bitmap bitmap = ((BitmapDrawable)binding.imageview.getDrawable()).getBitmap();
        String Gambar = encodeImage(bitmap);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        UUID documentd = UUID.randomUUID();
        String documentId = String.valueOf(documentd);
        itemCart ITEM = new itemCart(NamaMakanan, Harga, Gambar);
        String uid = firebaseAuth.getCurrentUser().getUid();

        firestore.collection("users").document(uid).collection("item").document(NamaMakanan)
                .set(ITEM).addOnSuccessListener(task -> {
                    Toast.makeText(getContext(), "Berhasil Menambahkan ke Cart", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Fail to add course \n" + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}