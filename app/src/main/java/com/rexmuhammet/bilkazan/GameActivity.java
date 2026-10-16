package com.rexmuhammet.bilkazan;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {


    private TextView tvQuestionCount, tvTimer, tvCurrentScore, tvQuestionText, tvPlusScore;
    private TextView tvOptionA, tvOptionB, tvOptionC, tvOptionD;
    private LinearLayout btnOptionA, btnOptionB, btnOptionC, btnOptionD;


    private DatabaseHelper dbHelper;
    private String loggedInUser;


    private int currentScore = 0;
    private int currentQuestionIndex = 0;
    private int correctAnswersCount = 0;
    private int wrongAnswersCount = 0;
    private CountDownTimer timer;
    private boolean isAnswered = false;

    // Dinamik Soru Listeleri
    private ArrayList<String> questionList = new ArrayList<>();
    private ArrayList<String[]> optionList = new ArrayList<>();
    private ArrayList<String> correctAnswerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        dbHelper = new DatabaseHelper(this);
        loggedInUser = getIntent().getStringExtra("USERNAME");

        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvTimer = findViewById(R.id.tvTimer);
        tvCurrentScore = findViewById(R.id.tvCurrentScore);
        tvQuestionText = findViewById(R.id.tvQuestionText);
        tvPlusScore = findViewById(R.id.tvPlusScore);

        btnOptionA = findViewById(R.id.btnOptionA);
        btnOptionB = findViewById(R.id.btnOptionB);
        btnOptionC = findViewById(R.id.btnOptionC);
        btnOptionD = findViewById(R.id.btnOptionD);

        tvOptionA = findViewById(R.id.tvOptionA);
        tvOptionB = findViewById(R.id.tvOptionB);
        tvOptionC = findViewById(R.id.tvOptionC);
        tvOptionD = findViewById(R.id.tvOptionD);


        btnOptionA.setOnClickListener(v -> checkAnswer(tvOptionA.getText().toString(), btnOptionA));
        btnOptionB.setOnClickListener(v -> checkAnswer(tvOptionB.getText().toString(), btnOptionB));
        btnOptionC.setOnClickListener(v -> checkAnswer(tvOptionC.getText().toString(), btnOptionC));
        btnOptionD.setOnClickListener(v -> checkAnswer(tvOptionD.getText().toString(), btnOptionD));

        // 1. ÖNCE VERİTABANINDAN SORULARI ÇEK
        fetchQuestionsFromDatabase();

        // 2. EĞER SORU BULUNDUYSA OYUNU BAŞLAT
        if (questionList.size() > 0) {
            loadQuestion();
        } else {
            Toast.makeText(this, "Veritabanında soru bulunamadı!", Toast.LENGTH_LONG).show();
            finish(); // Soru yoksa ekranı kapat
        }
    }

    private void fetchQuestionsFromDatabase() {

        Cursor cursor = dbHelper.getRandomQuestions(10);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String question = cursor.getString(cursor.getColumnIndexOrThrow("question_text"));
                String optA = cursor.getString(cursor.getColumnIndexOrThrow("option_a"));
                String optB = cursor.getString(cursor.getColumnIndexOrThrow("option_b"));
                String optC = cursor.getString(cursor.getColumnIndexOrThrow("option_c"));
                String optD = cursor.getString(cursor.getColumnIndexOrThrow("option_d"));
                String correctStr = cursor.getString(cursor.getColumnIndexOrThrow("correct_answer"));

                String actualCorrectAnswer = correctStr;
                if (correctStr.equalsIgnoreCase("A")) actualCorrectAnswer = optA;
                else if (correctStr.equalsIgnoreCase("B")) actualCorrectAnswer = optB;
                else if (correctStr.equalsIgnoreCase("C")) actualCorrectAnswer = optC;
                else if (correctStr.equalsIgnoreCase("D")) actualCorrectAnswer = optD;

                questionList.add(question);
                optionList.add(new String[]{optA, optB, optC, optD});
                correctAnswerList.add(actualCorrectAnswer);

            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void loadQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            isAnswered = false;
            resetButtonStyles();

            tvQuestionCount.setText("Soru: " + (currentQuestionIndex + 1) + " / " + questionList.size());

            tvQuestionText.setText(questionList.get(currentQuestionIndex));
            tvOptionA.setText(optionList.get(currentQuestionIndex)[0]);
            tvOptionB.setText(optionList.get(currentQuestionIndex)[1]);
            tvOptionC.setText(optionList.get(currentQuestionIndex)[2]);
            tvOptionD.setText(optionList.get(currentQuestionIndex)[3]);

            startTimer();
        } else {
            finishGame();
        }
    }

    private void startTimer() {
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(25000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Süre\n" + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                tvTimer.setText("Süre\n0s");
                if (!isAnswered) {
                    wrongAnswersCount++;
                    highlightCorrectAnswer();

                    new Handler().postDelayed(() -> {
                        currentQuestionIndex++;
                        loadQuestion();
                    }, 1500);
                }
            }
        }.start();
    }

    private void checkAnswer(String selectedAnswer, LinearLayout selectedButton) {
        if (isAnswered) return;

        isAnswered = true;
        timer.cancel();

        // DÜZELTİLEN SATIR: Artık statik dizi yerine ArrayList'ten cevabı çekiyor
        String correctAnswer = correctAnswerList.get(currentQuestionIndex);

        if (selectedAnswer.equals(correctAnswer)) {
            selectedButton.setBackgroundResource(R.drawable.bg_answer_correct);
            currentScore += 50;
            correctAnswersCount++;
            tvCurrentScore.setText("Puan: " + currentScore);

            tvPlusScore.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> tvPlusScore.setVisibility(View.INVISIBLE), 1000);

        } else {
            wrongAnswersCount++;
            selectedButton.setBackgroundResource(R.drawable.bg_answer_wrong);
            highlightCorrectAnswer();
        }

        new Handler().postDelayed(() -> {
            currentQuestionIndex++;
            loadQuestion();
        }, 1500);
    }

    private void highlightCorrectAnswer() {
        String correctAnswer = correctAnswerList.get(currentQuestionIndex);
        if (tvOptionA.getText().toString().equals(correctAnswer)) btnOptionA.setBackgroundResource(R.drawable.bg_answer_correct);
        if (tvOptionB.getText().toString().equals(correctAnswer)) btnOptionB.setBackgroundResource(R.drawable.bg_answer_correct);
        if (tvOptionC.getText().toString().equals(correctAnswer)) btnOptionC.setBackgroundResource(R.drawable.bg_answer_correct);
        if (tvOptionD.getText().toString().equals(correctAnswer)) btnOptionD.setBackgroundResource(R.drawable.bg_answer_correct);
    }

    private void resetButtonStyles() {
        btnOptionA.setBackgroundResource(R.drawable.bg_answer_normal);
        btnOptionB.setBackgroundResource(R.drawable.bg_answer_normal);
        btnOptionC.setBackgroundResource(R.drawable.bg_answer_normal);
        btnOptionD.setBackgroundResource(R.drawable.bg_answer_normal);
    }

    private void finishGame() {
        if (loggedInUser != null && !loggedInUser.isEmpty()) {
            dbHelper.updateUserScore(loggedInUser, currentScore);
        }

        Intent resultIntent = new Intent(GameActivity.this, ResultActivity.class);
        resultIntent.putExtra("SCORE", currentScore);
        resultIntent.putExtra("CORRECT_COUNT", correctAnswersCount);
        resultIntent.putExtra("WRONG_COUNT", wrongAnswersCount);
        resultIntent.putExtra("USERNAME", loggedInUser);

        startActivity(resultIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}