package com.example.music_xuzhaocheng;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private Dialog privacyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isFirstRun = sharedPref.getBoolean("is_first_run", true);

        if (!isFirstRun) {
            startMainActivity();
            return;
        }

        // 显示隐私协议对话框
        showPrivacyDialog();
    }

    private void showPrivacyDialog() {
        privacyDialog = new Dialog(this);
        privacyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        privacyDialog.setContentView(R.layout.dialog_privacy);
        privacyDialog.setCancelable(false);

        Button btnAgree = privacyDialog.findViewById(R.id.btn_agree);
        Button btnDisagree = privacyDialog.findViewById(R.id.btn_disagree);
        TextView tvUserAgreement = privacyDialog.findViewById(R.id.tv_user_agreement);
        TextView tvPrivacyPolicy = privacyDialog.findViewById(R.id.tv_privacy_policy);

        btnAgree.setOnClickListener(v -> {
            sharedPref.edit().putBoolean("is_first_run", false).apply();
            privacyDialog.dismiss();
            startMainActivity();
        });

        btnDisagree.setOnClickListener(v -> {
            privacyDialog.dismiss();
            finishAffinity();
        });

        tvUserAgreement.setOnClickListener(v ->
                openUrl("https://www.mi.com"));

        tvPrivacyPolicy.setOnClickListener(v ->
                openUrl("https://www.xiaomiev.com/"));

        privacyDialog.show();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}