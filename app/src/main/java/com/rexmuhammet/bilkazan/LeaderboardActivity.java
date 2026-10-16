package com.rexmuhammet.bilkazan;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class LeaderboardActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String loggedInUser;


    private ListView listViewLeaderboard;
    private EditText etSearchUser;
    private TextView tvLeaderboardUserName, tvLeaderboardUserScore, tvLeaderboardUserRank;


    private TextView tvRank1Name, tvRank1Score, tvRank2Name, tvRank2Score, tvRank3Name, tvRank3Score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        dbHelper = new DatabaseHelper(this);
        loggedInUser = getIntent().getStringExtra("USERNAME");


        listViewLeaderboard = findViewById(R.id.listViewLeaderboard);
        etSearchUser = findViewById(R.id.etSearchUser);

        tvLeaderboardUserName = findViewById(R.id.tvLeaderboardUserName);
        tvLeaderboardUserScore = findViewById(R.id.tvLeaderboardUserScore);
        tvLeaderboardUserRank = findViewById(R.id.tvLeaderboardUserRank);

        tvRank1Name = findViewById(R.id.tvRank1Name); tvRank1Score = findViewById(R.id.tvRank1Score);
        tvRank2Name = findViewById(R.id.tvRank2Name); tvRank2Score = findViewById(R.id.tvRank2Score);
        tvRank3Name = findViewById(R.id.tvRank3Name); tvRank3Score = findViewById(R.id.tvRank3Score);


        registerForContextMenu(listViewLeaderboard);


        loadLeaderboardData("");


        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Her harf girildiğinde veritabanında sorgulama yapar
                loadLeaderboardData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Veritabanından verileri çekip arayüze dağıtan ana fonksiyon
    private void loadLeaderboardData(String keyword) {
        Cursor cursor;
        if (keyword.isEmpty()) {
            cursor = dbHelper.getAllUsersByScore(); // Arama boşsa herkesi getir
        } else {
            cursor = dbHelper.searchUsers(keyword); // Harf girildiyse isme göre ara
        }

        // ListView için verileri tutacağımız liste
        ArrayList<HashMap<String, String>> listData = new ArrayList<>();
        int rank = 1;


        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow("score"));

                // Geçerli kullanıcının (Siz) kendi kartını güncelle
                if (username.equals(loggedInUser)) {
                    tvLeaderboardUserName.setText(username + " (Siz)");
                    tvLeaderboardUserScore.setText("Toplam Puan: " + score);
                    tvLeaderboardUserRank.setText("Sıra: " + rank + ".");
                }

                // İlk 3 kişiyi podyuma, geri kalanları ListView'a ekle
                if (keyword.isEmpty()) { // Sadece genel sıralamada podyumu doldur
                    if (rank == 1) {
                        tvRank1Name.setText(username); tvRank1Score.setText(score + " Puan");
                    } else if (rank == 2) {
                        tvRank2Name.setText(username); tvRank2Score.setText(score + " Puan");
                    } else if (rank == 3) {
                        tvRank3Name.setText(username); tvRank3Score.setText(score + " Puan");
                    } else {
                        addToList(listData, rank, username, score);
                    }
                } else {
                    // Arama yapılıyorsa podyum mantığını atla, herkesi listeye koy
                    addToList(listData, rank, username, score);
                }
                rank++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        // ListView'ı Doldurma İşlemi
        String[] from = {"rank", "name", "score"};
        int[] to = {R.id.tvRowRank, R.id.tvRowName, R.id.tvRowScore};
        SimpleAdapter adapter = new SimpleAdapter(this, listData, R.layout.item_leaderboard, from, to);
        listViewLeaderboard.setAdapter(adapter);
    }

    // ListView için HashMap hazırlayan yardımcı fonksiyon
    private void addToList(ArrayList<HashMap<String, String>> list, int rank, String name, int score) {
        HashMap<String, String> map = new HashMap<>();
        map.put("rank", rank + ".");
        map.put("name", name);
        map.put("score", score + " Puan");
        list.add(map);
    }

    // --- CONTEXT MENU (UZUN TIKLAMA) BÖLÜMÜ ---

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listViewLeaderboard) {
            menu.setHeaderTitle("Oyuncu İşlemleri");
            menu.add(0, 1, 0, "Profili İncele");
            menu.add(0, 2, 0, "Skoru Şikayet Et");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // Hangi satıra tıklandığını bulmak istersen info.position kullanabilirsin
        if (item.getItemId() == 1) {
            Toast.makeText(this, "Oyuncu profili inceleniyor...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == 2) {
            Toast.makeText(this, "Şüpheli skor yetkililere bildirildi!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}