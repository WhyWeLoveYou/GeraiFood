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

import com.example.geraifood.databinding.ActivityLoginPageBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {
    private ActivityLoginPageBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            if (auth.getCurrentUser().isEmailVerified()){
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        listener();
    }

    private void listener() {
        binding.ButtonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, RegisterPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        binding.buttonLogin.setOnClickListener(v -> {
            if (validator()) {
                binding.progressB.setVisibility(View.VISIBLE);
                login();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void login() {
        auth = FirebaseAuth.getInstance();
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if (!auth.getCurrentUser().isEmailVerified()) {
                    binding.progressB.setVisibility(View.GONE);
                    showToast("Silahkan verifikasi email dulu");
                } else {
                    binding.progressB.setVisibility(View.GONE);
                    Intent intent = new Intent(LoginPage.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.progressB.setVisibility(View.GONE);
                Toast.makeText(LoginPage.this, "Credensitial tidak valid", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Boolean validator() {
        String Email = binding.email.getText().toString();
        String Password = binding.password.getText().toString();
        if (Password.isEmpty()) {
            showToast("Password kosong");
            return false;
        }
        if (Email.isEmpty()) {
            showToast("Email Kosong");
            return false;
        }
        return true;
    }
}