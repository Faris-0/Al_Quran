package com.yuuna.alquran;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class DetailActivity extends Activity {

    private TextView tvSurat, tvDeskripsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        findViewById(R.id.dBack).setOnClickListener(v -> onBackPressed());

        tvSurat = findViewById(R.id.dSurat);
        tvDeskripsi = findViewById(R.id.dDeskripsi);

        tvSurat.setText(getIntent().getStringExtra("nama") + " | " + getIntent().getStringExtra("namaLatin"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvDeskripsi.setText(Html.fromHtml(getIntent().getStringExtra("deskripsi"), Html.FROM_HTML_MODE_COMPACT));
        } else tvDeskripsi.setText(Html.fromHtml(getIntent().getStringExtra("deskripsi")));
    }
}
