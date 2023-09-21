package com.yuuna.alquran;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuuna.alquran.adapter.AyatAdapater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private TextView tvSurat;
    private RecyclerView rvAyat;
    private ProgressBar pbLoad;

    private String BASE_URL = "https://equran.id/api/v2/surat/", nama, namaLatin, tempatTurun, arti, deskripsi;
    private Integer i = 1, nomor, jumlahAyat;
    private Boolean doubleBackToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbLoad = findViewById(R.id.mLoading);
//        Drawable drawable = pbLoad.getIndeterminateDrawable().mutate();
//        drawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
//        pbLoad.setProgressDrawable(drawable);

        findViewById(R.id.mBack).setOnClickListener(v -> {
            i--;
            if (i == 0) i = 114;
            detailSurat();
        });
        findViewById(R.id.mNext).setOnClickListener(v -> {
            i++;
            if (i == 115) i = 1;
            detailSurat();
        });
        findViewById(R.id.mDetail).setOnClickListener(v -> {
            startActivity(new Intent(this, DetailActivity.class)
                    .putExtra("nomor", nomor)
                    .putExtra("nama", nama)
                    .putExtra("namaLatin", namaLatin)
                    .putExtra("jumlahAyat", jumlahAyat)
                    .putExtra("tempatTurun", tempatTurun)
                    .putExtra("arti", arti)
                    .putExtra("deskripsi", deskripsi)
            );
        });

        tvSurat = findViewById(R.id.mSurat);
        rvAyat = findViewById(R.id.mAyat);
        rvAyat.setLayoutManager(new LinearLayoutManager(this));

        surat();
        detailSurat();
    }

    private void surat() {
//        spbLoading.progressiveStart();
        try {
            new Client().getOkHttpClient(BASE_URL, new Client.OKHttpNetwork() {
                @Override
                public void onSuccess(String response) {
                    // Set to Data
                    try {
                        JSONArray jsonArray = new JSONObject(response).getJSONArray("data");
                        String[] surat = new String[jsonArray.length()];
                        for (int a = 0; a < jsonArray.length(); a++) {
                            surat[a] = jsonArray.getJSONObject(a).getString("namaLatin") + " ("
                                    + jsonArray.getJSONObject(a).getString("jumlahAyat") + " Ayat)\n"
                                    + jsonArray.getJSONObject(a).getString("tempatTurun") + " • "
                                    + jsonArray.getJSONObject(a).getString("arti");
                        }
                        runOnUiThread(() -> tvSurat.setOnClickListener(v -> new AlertDialog
                                .Builder(MainActivity.this)
                                .setTitle("Surat")
                                .setItems(surat, (d, w) -> {
                                    d.dismiss();
                                    i = w + 1;
                                    detailSurat();
                                })
                                .show()
                            )
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void detailSurat() {
//        spbLoading.progressiveStart();
        pbLoad.setVisibility(View.VISIBLE);
        try {
            new Client().getOkHttpClient(BASE_URL + i, new Client.OKHttpNetwork() {
                @Override
                public void onSuccess(String response) {
                    // Set to Data
                    try {
                        JSONObject jsonObject = new JSONObject(response).getJSONObject("data");
                        nomor = jsonObject.getInt("nomor");
                        nama = jsonObject.getString("nama");
                        namaLatin = jsonObject.getString("namaLatin");
                        jumlahAyat = jsonObject.getInt("jumlahAyat");
                        tempatTurun = jsonObject.getString("tempatTurun");
                        arti = jsonObject.getString("arti");
                        deskripsi = jsonObject.getString("deskripsi");
                        JSONArray jsonArray = jsonObject.getJSONArray("ayat");
                        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
                        for (int a = 0; a < jsonArray.length(); a++) jsonObjectArrayList.add(jsonArray.getJSONObject(a));
                        runOnUiThread(() -> {
                            tvSurat.setText(namaLatin);
                            pbLoad.setVisibility(View.GONE);
                            rvAyat.setAdapter(new AyatAdapater(jsonObjectArrayList, getApplicationContext()));
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        pbLoad.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                pbLoad.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExit) finishAndRemoveTask();

        doubleBackToExit = true;
        Toast.makeText(this, "Tekan sekali lagi untuk keluar dari aplikasi", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExit = false, 2000);
    }
}