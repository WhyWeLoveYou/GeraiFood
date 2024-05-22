package com.example.geraifood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geraifood.databinding.ActivityRegisterPageBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterPage extends AppCompatActivity {

    private ActivityRegisterPageBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String encodedImage;
    ImageView imageView;
    FloatingActionButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listener();

        imageView = findViewById(R.id.imageView4);
        button = findViewById(R.id.floatingActionButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(RegisterPage.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        }); {

        }
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
        binding.zpindah.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterPage.this, LoginPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        binding.registerB.setOnClickListener(v -> {
            if (validator()) {
                binding.addingImage.setVisibility(View.VISIBLE);
                signUp();
            }
        });
    }

    private void signUp() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        String Nama = binding.Username.getText().toString();
        String Email = binding.email.getText().toString().toLowerCase();
        String Password = binding.password.getText().toString();
        auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(task -> {
            HashMap<String, Object> user = new HashMap<>();
            user.put("Nama", Nama);
            user.put("Email", Email);
            user.put("Password", Password);
            user.put("Image", encodedImage);
            String currentUser = auth.getCurrentUser().getUid();
            firestore.collection("users").document(currentUser).set(user).addOnCompleteListener(
                    documentReference -> {
                        Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
            );
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
        String Password = binding.password.getText().toString();
        String Username = binding.Username.getText().toString();
        if (Password.isEmpty()) {
            showToast("Password kosong");
            return false;
        }
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
        if (Password.length() < 6) {
            showToast("Password kurang dari 6");
            return false;
        }
        if (!validateEmailAddress(Email)) {
            showToast("Invalid Email");
            return false;
        }
        return true;
    }

    public Boolean validateEmailAddress(String emailAddress) {
        Pattern regexPattern = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
        Matcher regMatcher   = regexPattern.matcher(emailAddress);
        if(regMatcher.matches()) {
            return true;
        }
        return false;
    }

}