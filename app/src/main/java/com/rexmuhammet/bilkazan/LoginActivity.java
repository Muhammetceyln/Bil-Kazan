package com.rexmuhammet.bilkazan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {


    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        dbHelper = new DatabaseHelper(this);


        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = etUsername.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();


                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
                } else {

                    boolean checkUserPass = dbHelper.checkUser(user, pass);

                    if (checkUserPass) {

                        Toast.makeText(LoginActivity.this, "Giriş Başarılı! Hoş Geldiniz.", Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);


                        intent.putExtra("USERNAME", user);
                        startActivity(intent);


                        finish();
                    } else {
                        // Veritabanında eşleşme bulunamadıysa:
                        Toast.makeText(LoginActivity.this, "Hatalı Kullanıcı Adı veya Şifre!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // "Kaydolun" Yazısına Tıklanma Olayı
        tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kayıt ekranına yönlendir
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}