package com.rexmuhammet.bilkazan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db"; // Assets içindeki dosya adı
    private static final int DATABASE_VERSION = 1;
    private final Context context;
    private final String DB_PATH;


    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_SCORE = "score";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        // Cihazın içinde veritabanının kopyalanacağı sistem yolu
        this.DB_PATH = context.getApplicationInfo().dataDir + "/databases/" + DATABASE_NAME;

        // Veritabanını kontrol et ve yoksa kopyala
        initializeDatabase();
    }

    private void initializeDatabase() {
        if (!checkDatabaseExists()) {

            this.getReadableDatabase();
            this.close();
            try {
                copyDatabaseFromAssets();
            } catch (IOException e) {
                throw new RuntimeException("Veritabanı kopyalanırken hata oluştu!", e);
            }
        }
    }

    // Veritabanı daha önce kopyalanmış mı kontrolü
    private boolean checkDatabaseExists() {
        java.io.File dbFile = new java.io.File(DB_PATH);
        return dbFile.exists();
    }

    // Assets klasöründeki dosyayı cihaz hafızasına byte byte yazan metot
    private void copyDatabaseFromAssets() throws IOException {
        InputStream inputStream = context.getAssets().open("database/" + DATABASE_NAME);
        OutputStream outputStream = new FileOutputStream(DB_PATH);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // --- KULLANICI İŞLEMLERİ ---

    public boolean insertUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_SCORE, 0);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    public void updateUserScore(String username, int newScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_USERS + " SET " + COLUMN_SCORE + " = " + COLUMN_SCORE + " + " + newScore + " WHERE " + COLUMN_USERNAME + " = '" + username + "'";
        db.execSQL(updateQuery);
        db.close();
    }

    public Cursor getAllUsersByScore() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " ORDER BY " + COLUMN_SCORE + " DESC", null);
    }

    public Cursor searchUsers(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " LIKE '%" + keyword + "%' ORDER BY " + COLUMN_SCORE + " DESC";
        return db.rawQuery(query, null);
    }

    // --- SORU ÇEKME İŞLEMİ (Yeni Eklenen Örnek Metot) ---

    public Cursor getRandomQuestions(int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Hazır veritabanındaki 'questions' tablosundan rastgele soruları çeker
        return db.rawQuery("SELECT * FROM questions ORDER BY RANDOM() LIMIT " + limit, null);
    }

    public int getUserScore(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int score = 0;
        Cursor cursor = db.rawQuery("SELECT score FROM " + TABLE_USERS + " WHERE username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            score = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return score;
    }

    // Kullanıcının puanına göre genel sıralamadaki yerini hesaplar
    public int getUserRank(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int rank = 1;
        int userScore = 0;

        // 1. AŞAMA: Önce kullanıcının kendi puanını buluyoruz (Bağımsız sorgu)
        Cursor cursorScore = db.rawQuery("SELECT score FROM " + TABLE_USERS + " WHERE username = ?", new String[]{username});
        if (cursorScore.moveToFirst()) {
            userScore = cursorScore.getInt(0);
        }
        cursorScore.close(); // Sadece okuyucuyu kapatıyoruz, veritabanını değil!

        // 2. AŞAMA: Puanı bu kullanıcıdan daha büyük olan kaç kişi var sayıyoruz
        Cursor cursorRank = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE score > ?", new String[]{String.valueOf(userScore)});
        if (cursorRank.moveToFirst()) {
            // Bizden yüksek puanlı kişi sayısına 1 eklersek, bizim sıramız çıkar
            rank = cursorRank.getInt(0) + 1;
        }
        cursorRank.close();

        db.close(); // Her iki işlem de bitince veritabanını güvenle kapatıyoruz
        return rank;
    }
}