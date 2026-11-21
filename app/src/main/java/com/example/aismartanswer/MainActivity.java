package com.example.aismartanswer;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

public class MainActivity extends Activity {
    private WebView browser;
    private EditText address, question;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 18, 18, 12);
        root.setBackgroundColor(0xFFF7F8FA);

        TextView title = new TextView(this);
        title.setText("AI SMART ANSWER");
        title.setTextSize(22);
        title.setTextColor(0xFF126B3B);
        title.setTypeface(null, 1);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Browser pembelajaran + bantuan analisis soal latihan");
        subtitle.setTextSize(12);
        root.addView(subtitle);

        LinearLayout nav = new LinearLayout(this);
        address = new EditText(this);
        address.setSingleLine(true);
        address.setText("https://www.google.com");
        address.setHint("Masukkan alamat website");
        nav.addView(address, new LinearLayout.LayoutParams(0, dp(48), 1));
        Button go = new Button(this);
        go.setText("BUKA");
        nav.addView(go);
        root.addView(nav);

        LinearLayout controls = new LinearLayout(this);
        Button back = new Button(this); back.setText("Kembali");
        Button forward = new Button(this); forward.setText("Lanjut");
        Button refresh = new Button(this); refresh.setText("Muat ulang");
        controls.addView(back, new LinearLayout.LayoutParams(0, dp(44), 1));
        controls.addView(forward, new LinearLayout.LayoutParams(0, dp(44), 1));
        controls.addView(refresh, new LinearLayout.LayoutParams(0, dp(44), 1));
        root.addView(controls);

        browser = new WebView(this);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setDomStorageEnabled(true);
        browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        browser.getSettings().setSupportMultipleWindows(false);
        browser.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
            @Override public void onPageStarted(WebView view, String url, Bitmap icon) {
                address.setText(url);
            }
        });
        browser.setWebChromeClient(new WebChromeClient());
        root.addView(browser, new LinearLayout.LayoutParams(-1, 0, 1));

        TextView note = new TextView(this);
        note.setText("Bantuan AI untuk latihan: tempel teks soal secara manual. Jangan gunakan untuk mengambil alih ulangan aktif atau melewati pengamanan situs.");
        note.setTextSize(11);
        note.setTextColor(0xFF555555);
        root.addView(note);

        question = new EditText(this);
        question.setHint("Tempel atau ketik soal latihan di sini...");
        question.setMinLines(2);
        question.setMaxLines(4);
        root.addView(question);

        Button ask = new Button(this);
        ask.setText("KIRIM SOAL KE AI");
        root.addView(ask);

        setContentView(root);

        go.setOnClickListener(v -> loadAddress());
        address.setOnEditorActionListener((v, actionId, event) -> { loadAddress(); return true; });
        back.setOnClickListener(v -> { if (browser.canGoBack()) browser.goBack(); });
        forward.setOnClickListener(v -> { if (browser.canGoForward()) browser.goForward(); });
        refresh.setOnClickListener(v -> browser.reload());
        ask.setOnClickListener(v -> shareQuestion());
        browser.loadUrl("https://www.google.com");
    }

    private void loadAddress() {
        String url = address.getText().toString().trim();
        if (url.isEmpty()) return;
        if (!url.matches("(?i)^https?://.*")) url = "https://" + url;
        try {
            Uri uri = Uri.parse(url);
            if (uri.getHost() == null) {
                Toast.makeText(this, "Alamat website tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }
            browser.loadUrl(url);
        } catch (Exception e) {
            Toast.makeText(this, "Tidak dapat membuka alamat", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareQuestion() {
        String q = question.getText().toString().trim();
        if (q.isEmpty()) {
            Toast.makeText(this, "Masukkan soal latihan terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT,
            "Tolong analisis soal latihan ini. Berikan jawaban, alasan, dan langkah pengerjaan bila diperlukan:\n\n" + q);
        startActivity(Intent.createChooser(send, "Kirim soal ke aplikasi AI"));
    }

    private int dp(int value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override public void onBackPressed() {
        if (browser != null && browser.canGoBack()) browser.goBack();
        else super.onBackPressed();
    }
}
