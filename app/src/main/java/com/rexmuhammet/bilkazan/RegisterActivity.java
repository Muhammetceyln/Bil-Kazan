package com.rexmuhammet.bilkazan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {


    private EditText etRegUsername, etRegEmail, etRegPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;


    private RadioGroup rgUserType;
    private CheckBox cbAgreement;


    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Veritabanı Bağlantısını Başlat
        dbHelper = new DatabaseHelper(this);

        etRegUsername = findViewById(R.id.etRegUsername);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        rgUserType = findViewById(R.id.rgUserType);
        cbAgreement = findViewById(R.id.cbAgreement);


        cbAgreement.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showAgreementWebView();
                return true;
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText'lerdeki yazıları al ve boşlukları temizle (trim)
                String user = etRegUsername.getText().toString().trim();
                String email = etRegEmail.getText().toString().trim();
                String pass = etRegPassword.getText().toString().trim();

                // 1. Boş Alan Kontrolü
                if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!cbAgreement.isChecked()) {
                    Toast.makeText(RegisterActivity.this, "Kayıt olmak için Kullanıcı Sözleşmesini kabul etmelisiniz!", Toast.LENGTH_SHORT).show();
                    return;
                }


                int selectedId = rgUserType.getCheckedRadioButtonId();
                String userType = "";
                if (selectedId != -1) { // Eğer bir seçim yapılmışsa (Çökme engellemesi)
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    userType = selectedRadioButton.getText().toString();
                }

                // 4. Veritabanına Kayıt İşlemi (DatabaseHelper sınıfındaki insertUser metodu)
                // DİKKAT: userType verisini çekiyoruz ama veritabanı tablonda bu sütun olmadığı için
                // kaydetme metoduna sadece 3 parametre (user, pass, email) gönderiyoruz.
                boolean isInserted = dbHelper.insertUser(user, pass, email);

                if (isInserted) {
                    // 5. HOCANIN İSTEDİĞİ: Kayıt Başarılı Olunca AlertDialog Gösterme
                    showSuccessDialog();
                } else {
                    // Kullanıcı adı veritabanında UNIQUE olduğu için aynı isimle kayıt olunamaz
                    Toast.makeText(RegisterActivity.this, "Bu kullanıcı adı zaten alınmış! Lütfen başka bir ad deneyin.", Toast.LENGTH_LONG).show();
                }
            }
        });


        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
    }


    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kayıt Başarılı! 🎉");
        builder.setMessage("Aramıza hoş geldin! BilKazan hesabın başarıyla oluşturuldu. Şimdi giriş yaparak yarışmaya başlayabilirsin.");


        builder.setCancelable(false);

        builder.setPositiveButton("Giriş Yap", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                goToLogin();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // --- HOCANIN İSTEDİĞİ: WebView (Kullanıcı Sözleşmesi Penceresi) ---
    private void showAgreementWebView() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Kullanıcı Sözleşmesi ve Gizlilik Politikası");

        // Kod ile sanal bir WebView oluşturuyoruz
        WebView wv = new WebView(this);

        // Uygulamanın dışına (Chrome vb. tarayıcıya) atmasını engeller, uygulamanın içinde açar
        wv.setWebViewClient(new WebViewClient());

        // Sözleşmenin çekileceği örnek bir web adresi
        wv.loadUrl("https://policies.google.com/terms?hl=tr");
        alert.setView(wv);


        alert.setNegativeButton("Kapat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    // Login (Giriş) Sayfasına Geçiş Yapan Ortak Metot
    private void goToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Geri tuşuna basıldığında tekrar Kayıt ekranına dönülmesini engeller
    }
}