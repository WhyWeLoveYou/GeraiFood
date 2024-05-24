package com.example.geraifood;

import static android.app.PendingIntent.getActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.geraifood.data.itemCart;
import com.example.geraifood.databinding.ActivityDeskripsiBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.UUID;

public class deskripsi extends AppCompatActivity {

    private ActivityDeskripsiBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeskripsiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listener();
        getData();
    }

    private void listener() {
        binding.deskbutton.setOnClickListener(v -> {
            addData();
        });
        binding.backer.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });
    }

    private void getData() {
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        String namnya = getIntent().getStringExtra("jeneng");

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
        Object Jumlah = 1;
        itemCart ITEM = new itemCart(NamaMakanan, Harga, Gambar, Jumlah);
        String uid = firebaseAuth.getCurrentUser().getUid();

        DocumentReference path = firestore.collection("users").document(uid).collection("item").document(NamaMakanan);
        path.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        path.update("jumlah" , FieldValue.increment(1));
                    } else {
                        path.set(ITEM).addOnSuccessListener(task1 -> {
                            Toast.makeText(getApplicationContext(), "Berhasil Menambahkan ke Cart", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Gagal \n" + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        }).addOnFailureListener(task -> {
            Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show();
        });
    }
}