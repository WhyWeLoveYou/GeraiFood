package com.example.geraifood.hm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geraifood.LoginPage;
import com.example.geraifood.MainActivity;
import com.example.geraifood.data.itemMakanan;
import com.example.geraifood.databinding.ActivityRegisterPageBinding;
import com.example.geraifood.databinding.ActivitymakanantambahBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class DataMakanan extends AppCompatActivity {

    private ActivitymakanantambahBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitymakanantambahBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listener();
    }

    private void listener() {
        binding.zpindah.setOnClickListener(v -> {
            Intent intent = new Intent(DataMakanan.this, LoginPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        binding.imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.registerB.setOnClickListener(v -> {
            if (validator()) {
                TambahMakanan();
            }
        });
    }

    private void TambahMakanan() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        String Nama = binding.Username.getText().toString();
        String Email = binding.email.getText().toString().toLowerCase();
        itemMakanan item = new itemMakanan(Nama, Email, encodedImage);
        firestore.collection("makanan").document(Nama).set(item).addOnSuccessListener(task -> {
            Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
        });
    }

    private String encodeImage(Bitmap bitmap) {
        int maxWidth = 300;
        int maxHeight = 300;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        float aspectRatio = (float) originalWidth / originalHeight;
        int previewW, previewH;
        if (originalWidth > maxWidth || originalHeight > maxHeight) {
            if (aspectRatio > 1) {

                previewW = maxWidth;
                previewH = (int) (maxWidth / aspectRatio);
            } else {
                previewH = maxHeight;
                previewW = (int) (maxHeight * aspectRatio);
            }
        } else {
            previewW = originalWidth;
            previewH = originalHeight;
        }
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewW, previewH, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageButton.setImageBitmap(bitmap);
                            binding.addingImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    );

    private Boolean validator() {
        String Email = binding.email.getText().toString();
        String Username = binding.Username.getText().toString();
        if (Username.isEmpty()) {
            showToast("Username Kosong");
            return false;
        }
        if (Email.isEmpty()) {
            showToast("Email Kosong");
            return false;
        }
        if (encodedImage == null) {
            showToast("Gambar Kosong");
            return false;
        }
        return true;
    }

}