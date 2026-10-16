package com.rexmuhammet.bilkazan;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    // Arayüz Elemanları
    private TextView tvFinalScore, tvCorrectAnswers, tvWrongAnswers, tvCurrentUserName;
    private TextView tvRankAbove, tvUserRankNumber, tvRankBelow; // Yeni eklenen sıralama kutuları
    private Button btnPlayAgain, btnBackToHome;

    private DatabaseHelper dbHelper;
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        dbHelper = new DatabaseHelper(this);


        loggedInUser = getIntent().getStringExtra("USERNAME");
        int matchScore = getIntent().getIntExtra("SCORE", 0);
        int correctCount = getIntent().getIntExtra("CORRECT_COUNT", 0);
        int wrongCount = getIntent().getIntExtra("WRONG_COUNT", 0);


        tvFinalScore = findViewById(R.id.tvFinalScore);
        tvCorrectAnswers = findViewById(R.id.tvCorrectAnswers);
        tvWrongAnswers = findViewById(R.id.tvWrongAnswers);
        tvCurrentUserName = findViewById(R.id.tvCurrentUserName);

        tvRankAbove = findViewById(R.id.tvRankAbove);
        tvUserRankNumber = findViewById(R.id.tvUserRankNumber);
        tvRankBelow = findViewById(R.id.tvRankBelow);

        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnBackToHome = findViewById(R.id.btnBackToHome);


        tvFinalScore.setText(matchScore + " Puan");
        tvCorrectAnswers.setText("✔️ Doğru: " + correctCount + "/10");
        tvWrongAnswers.setText("❌ Yanlış: " + wrongCount + "/10");
        tvCurrentUserName.setText(loggedInUser + " (Siz)");

        // VERİTABANINDAN CANLI SIRALAMA SORGUSUNU ÇALIŞTIR
        calculateLiveLeaderboard();


        btnPlayAgain.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, GameActivity.class);
            intent.putExtra("USERNAME", loggedInUser);
            startActivity(intent);
            finish();
        });

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, DashboardActivity.class);
            intent.putExtra("USERNAME", loggedInUser);
            startActivity(intent);
            finish();
        });
    }

    // --- VERİTABANINDAN ANLIK SIRALAMA HESAPLAMA ALGORİTMASI ---
    private void calculateLiveLeaderboard() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Tüm kullanıcıları yüksek puandan düşük puana göre sıralı getiriyoruz
        Cursor cursor = db.rawQuery("SELECT username, score FROM users ORDER BY score DESC", null);

        ArrayList<String> usersOrder = new ArrayList<>();
        ArrayList<Integer> scoresOrder = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                usersOrder.add(cursor.getString(cursor.getColumnIndexOrThrow("username")));
                scoresOrder.add(cursor.getInt(cursor.getColumnIndexOrThrow("score")));
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Giriş yapmış olan kullanıcının bu sıralamadaki indeksini (yerini) buluyoruz
        int myIndex = usersOrder.indexOf(loggedInUser);

        if (myIndex != -1) {
            int myRank = myIndex + 1; // Yazılımdaki indeks 0'dan başladığı için sıralama index + 1'dir.
            tvUserRankNumber.setText(myRank + ".");

            // 1. ÜSTÜNDEKİ RAKİBİ BULMA (Eğer kullanıcı 1. değilse üstünde biri vardır)
            if (myIndex > 0) {
                int aboveRank = myRank - 1;
                String aboveUser = usersOrder.get(myIndex - 1);
                int aboveScore = scoresOrder.get(myIndex - 1);
                tvRankAbove.setText(aboveRank + ". 👤 " + aboveUser + " (" + aboveScore + " Puan)");
            } else {
                tvRankAbove.setText("🏆 Zirvedesiniz! Üstünüzde kimse yok.");
            }

            // 2. ALTINDAKİ RAKİBİ BULMA (Eğer kullanıcı sonuncu değilse altında biri vardır)
            if (myIndex < usersOrder.size() - 1) {
                int belowRank = myRank + 1;
                String belowUser = usersOrder.get(myIndex + 1);
                int belowScore = scoresOrder.get(myIndex + 1);
                tvRankBelow.setText(belowRank + ". 👤 " + belowUser + " (" + belowScore + " Puan)");
            } else {
                tvRankBelow.setText("Listenin sonundasınız.");
            }
        }
    }
}