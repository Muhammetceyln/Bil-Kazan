package com.rexmuhammet.bilkazan;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    // Bekleme süresi: 3000 milisaniye = 3 saniye
    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onStart(){
        super.onStart();
        Toast.makeText(this,"start",Toast.LENGTH_LONG).show();
        Toast.makeText(this,"naber",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Toast.makeText(this,"mesaj",Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPause(){
        super.onPause();
        AlertDialog.Builder diyalogolustur = new AlertDialog.Builder(SplashActivity.this);
        diyalogolustur.setTitle("MESAJ");
        diyalogolustur.setMessage("uyhbeğendin mi");
        diyalogolustur.setIcon(R.mipmap.ic_launcher_round);
        diyalogolustur.setCancelable(false);
        diyalogolustur.setPositiveButton("evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SplashActivity.this.setTitle("uyg beğendin öş");
            }
        });
        diyalogolustur.show();
    }
    protected void method(){
        AlertDialog.Builder diya = new AlertDialog.Builder(SplashActivity.this);
        diya.setTitle("selam");
        diya.setMessage("selam");
        diya.setIcon(R.mipmap.ic_launcher_round);
        diya.setCancelable(false);
        diya.setPositiveButton("yes", new DialogInterface.OnClickListener(){
           @Override
           public void onClick(DialogInterface dialog, int which) {
               SplashActivity.this.setTitle("uygbependinmi");
           }
        });
        diya.show();
    }

    protected void method2(){
        AlertDialog.Builder yen = new AlertDialog.Builder(SplashActivity.this);
        yen.setTitle("asd");
        yen.setMessage("asd");
        yen.setIcon(R.mipmap.ic_launcher_round);
        yen.setCancelable(false);
        yen.setPositiveButton("yes", new DialogInterface.OnClickListener(){
           @Override
           public void onClick(DialogInterface dialog, int which) {
               SplashActivity.this.setTitle("yes");
           }
        });
        yen.show();
    }

    public void method4(){
        //örnek sayfalar arası  geçiş
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        startActivity(new Intent("android.intent.action.MainActivity"));

    }

    public void firstExam() {
        AlertDialog.Builder sinav = new AlertDialog.Builder(SplashActivity.this);
        sinav.setTitle("Sınav");
        sinav.setMessage("Sınava başlamak istiyor musunuz?");
        sinav.setIcon(R.mipmap.ic_launcher_round);
        sinav.setCancelable(false);

        // HAYIR BUTONU (İptal Senaryosu)
        sinav.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("İPTAL", "Kullanıcı vazgeçti");

                Toast i = Toast.makeText(SplashActivity.this, "Sınav İptal edildi", Toast.LENGTH_SHORT);
                i.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                i.show(); // Göstermeyi unutmuyoruz!
            }
        });

        // EVET BUTONU (Onay ve Geçiş Senaryosu)
        sinav.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Yükleme ekranı
                ProgressDialog prog = new ProgressDialog(SplashActivity.this);
                prog.setTitle("Hazırlanıyor");
                prog.setMessage("Sorular çekiliyor..");
                prog.setMax(100);
                prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Doğru sabit
                prog.show(); // Göstermeyi unutmuyoruz!

                // Diğer sayfaya geçiş (onClick İÇİNDE olmalı)
                Intent gecis = new Intent(SplashActivity.this, GameActivity.class);
                startActivity(gecis);
            }
        });

        // Diyaloğu oluştur ve göster
        sinav.create().show();
    }

    protected void mothod3(){
        ProgressDialog progressDialog= new ProgressDialog(SplashActivity.this);
        progressDialog.setTitle("d");
        progressDialog.setMessage("slm");
        progressDialog.setMax(10);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
    }

    public void mainA(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Handler kullanarak arka planda zamanlayıcı oluşturuyoruz
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Süre dolduğunda LoginActivity'e geçiş yapacak Intent
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);

                // finish() metodu çok önemlidir!
                // Splash ekranını kapatır ki kullanıcı geri tuşuna bastığında tekrar bu ekrana dönmesin.
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}