package com.rexmuhammet.bilkazan;

import static android.content.Intent.getIntent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity1 extends AppCompatActivity {

    public EditText etAdSoyad = findViewById(R.id.etAdSoyad);

    RadioGroup rgBolum = findViewById(R.id.rgBolum);
    RadioButton rbBilgisayar = findViewById(R.id.rbBilgisayar);
    RadioButton rbMakine = findViewById(R.id.rbMakine);

    CheckBox cbKotlin = findViewById(R.id.rbMakine);

    Button btnKayit = findViewById(R.id.btnKayit);


    public void BtnKayit(View view) {
        String secimler;
        if (rgBolum.getCheckedRadioButtonId() == rbBilgisayar.getId()) {
            secimler = rbBilgisayar.getText().toString();
        } else {
            secimler = rbMakine.getText().toString();
        }
        secimler += " " + cbKotlin.getText().toString();

        //alertdialog
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity1.this);
        alert.setTitle("Kayıt");
        alert.setMessage("Onaylıyor musun?");
        alert.setCancelable(false);
        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int white) {
                Log.e("HATA", "Kayıt ipatl edildi");
            }
        });
        alert.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int white) {
                ProgressDialog prog = new ProgressDialog(MainActivity1.this);
                prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                prog.setMax(100);
                prog.setTitle("Yükleniyor");
                prog.show();

                Intent ins = new Intent(MainActivity1.this, SonucActivity.class);
                ins.putExtra("İsim", etAdSoyad.getText().toString());
                ins.putExtra("KullanıcıSecimleri", secimler);
                startActivity(ins);
            }
        });
        alert.create().show();
    }

    // Değişkenleri burada tanımlıyoruz
    TextView tvSonuc;
    WebView wvSite;
    RatingBar ratingBar;
    ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sonuc); // Arayüzü bağlıyoruz

        // Eşleştirmeleri mutlaka setContentView'dan SONRA yapıyoruz
        tvSonuc = findViewById(R.id.tvSonuc);
        wvSite = findViewById(R.id.wvSite);
        ratingBar = findViewById(R.id.ratingBar);
        ivLogo = findViewById(R.id.ivLogo);

        // 1. Intent ile Gelen Verileri Alma ve Yazdırma
        String gelenAd = getIntent().getStringExtra("gelen_ad_soyad"); // 1. sorudaki etiket
        String gelenSecimler = getIntent().getStringExtra("gelen_secimler"); // 1. sorudaki etiket
        tvSonuc.setText(gelenAd + " - " + gelenSecimler);

        // 2. Ortalanmış Toast Mesajı
        Toast i = Toast.makeText(SonucActivity.this, "Hoşgeldiniz", Toast.LENGTH_LONG);
        i.setGravity(Gravity.CENTER, 0, 0); // X ve Y sapmaları 0
        i.show(); // Göstermeyi unutmuyoruz!

        // 3. WebView Kullanımı
        wvSite.loadUrl("https://ebyu.edu.tr/");
        /* NOT: Bu kodun çalışması için AndroidManifest.xml dosyasına
           <uses-permission android:name="android.permission.INTERNET"/>
           izni eklenmelidir! */

        // 4. RatingBar Kullanımı
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(SonucActivity.this, "Puanınız: " + rating, Toast.LENGTH_SHORT).show();
            }
        });

        // 5. ImageView Tıklama ve Resim Değiştirme
        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivLogo.setImageResource(R.drawable.onay_ikonu);
            }
        });
    }
    ProgressBar pb1 = findViewById(R.id.progressBar2);
    TextView tw1 = findViewById(R.id.textView3);
    Thread t =new Thread(){
        @Override
        public void run(){
            int sayac = 0;
            while(sayac < pb1.getMax(pb1.getMax()){
                try{
                    sleep(50);
                    sayac +=1;
                    pb1.setProgress(sayac);
                    tw1.setText(pb1.getProgress()+"/100");
                }catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }    t.start();
        }


    };

}
