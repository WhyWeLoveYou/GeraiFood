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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geraifood.LoginPage;
import com.example.geraifood.MainActivity;
import com.example.geraifood.RegisterPage;
import com.example.geraifood.data.itemMakanan;
import com.example.geraifood.databinding.ActivityRegisterPageBinding;
import com.example.geraifood.databinding.ActivitymakanantambahBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.StringUtils;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            binding.imageView4.setImageBitmap(bitmap);
            binding.addingImage.setVisibility(View.GONE);
            encodedImage = encodeImage(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();
        }
    }

    private void listener() {
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(DataMakanan.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        }); {

        }
        binding.registerB.setOnClickListener(v -> {
            if (validator()) {
                binding.progressB.setVisibility(View.VISIBLE);
                TambahMakanan();
            }
        });
    }

    private void TambahMakanan() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        String Nama = StringUtils.capitalize(binding.Username.getText().toString());
        String Email = binding.email.getText().toString().toLowerCase();
        itemMakanan item = new itemMakanan(Nama, Email, encodedImage);
        firestore.collection("makanan").document(Nama).set(item).addOnSuccessListener(task -> {
            binding.progressB.setVisibility(View.GONE);
            Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

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