package com.rexmuhammet.bilkazan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class DashboardActivity extends AppCompatActivity {


    private TextView tvWelcomeUser, tvPoints, tvRank;
    private MaterialCardView btnStartGame, btnLeaderboard;

    private String loggedInUser;
    private DatabaseHelper dbHelper; // Veritabanı yardımcısı eklendi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Veritabanı bağlantısını başlat
        dbHelper = new DatabaseHelper(this);


        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        tvPoints = findViewById(R.id.tvPoints);
        tvRank = findViewById(R.id.tvRank);
        btnStartGame = findViewById(R.id.btnStartGame);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);


        Intent intent = getIntent();
        loggedInUser = intent.getStringExtra("USERNAME");


        if (loggedInUser != null && !loggedInUser.isEmpty()) {
            tvWelcomeUser.setText("Hoş Geldin, " + loggedInUser + "!");


            int currentScore = dbHelper.getUserScore(loggedInUser);
            int currentRank = dbHelper.getUserRank(loggedInUser);


            tvPoints.setText("Toplam Puan: " + currentScore);
            tvRank.setText("Sıralama: " + currentRank + ".");
        }


        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent(DashboardActivity.this, GameActivity.class);
                gameIntent.putExtra("USERNAME", loggedInUser);
                startActivity(gameIntent);
            }
        });


        btnLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent leaderboardIntent = new Intent(DashboardActivity.this, LeaderboardActivity.class);

                leaderboardIntent.putExtra("USERNAME", loggedInUser);
                startActivity(leaderboardIntent);
            }
        });
    }

    // Uygulama oyun ekranından Dashboard'a geri döndüğünde
    // puanların anında güncellenmesi için onResume metodunu kullanıyoruz.
    @Override
    protected void onResume() {
        super.onResume();
        if (loggedInUser != null && !loggedInUser.isEmpty()) {
            int updatedScore = dbHelper.getUserScore(loggedInUser);
            int updatedRank = dbHelper.getUserRank(loggedInUser);

            tvPoints.setText("Toplam Puan: " + updatedScore);
            tvRank.setText("Sıralama: " + updatedRank + ".");
        }
    }
}