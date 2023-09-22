package com.yuuna.alquran;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuuna.alquran.adapter.AyatAdapater;
import com.yuuna.alquran.adapter.SuratAdapater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity implements SuratAdapater.ItemClickListener {

    private TextView tvSurat;
    private RecyclerView rvAyat;
    private ProgressBar pbLoad;

    private Dialog dSurat;

    private String BASE_URL = "https://equran.id/api/v2/surat/", nama, namaLatin, deskripsi, audio;
    private Integer i = 1, jumlahAyat;
    private Boolean doubleBackToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbLoad = findViewById(R.id.mLoading);

        findViewById(R.id.mBack).setOnClickListener(v -> {
            i--;
            if (i == 0) i = 114;
            surat();
            detailSurat();
        });
        findViewById(R.id.mNext).setOnClickListener(v -> {
            i++;
            if (i == 115) i = 1;
            surat();
            detailSurat();
        });
        findViewById(R.id.mDetail).setOnClickListener(v -> {
            startActivity(new Intent(this, DetailActivity.class)
                    .putExtra("nama", nama)
                    .putExtra("namaLatin", namaLatin)
                    .putExtra("jumlahAyat", jumlahAyat)
                    .putExtra("deskripsi", deskripsi)
                    .putExtra("audioFull", audio)
            );
        });

        tvSurat = findViewById(R.id.mSurat);
        rvAyat = findViewById(R.id.mAyat);
        rvAyat.setLayoutManager(new LinearLayoutManager(this));

        surat();
        detailSurat();
    }

    private void surat() {
        findViewById(R.id.mDetail).setEnabled(false);
        try {
            new Client().getOkHttpClient(BASE_URL, new Client.OKHttpNetwork() {
                @Override
                public void onSuccess(String response) {
                    // Set to Data
                    try {
                        JSONArray jsonArray = new JSONObject(response).getJSONArray("data");
                        audio = String.valueOf(jsonArray.getJSONObject(i - 1).getJSONObject("audioFull"));
                        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
                        for (int a = 0; a < jsonArray.length(); a++) jsonObjectArrayList.add(jsonArray.getJSONObject(a));
                        runOnUiThread(() -> {
                            tvSurat.setOnClickListener(v -> {
                                        dSurat = new Dialog(MainActivity.this);
                                        dSurat.setContentView(R.layout.dialog_surat);
                                        dSurat.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                        dSurat.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                        dSurat.findViewById(R.id.sClose).setOnClickListener(v1 -> dSurat.dismiss());

                                        RecyclerView rvSurat = dSurat.findViewById(R.id.sSurat);
                                        rvSurat.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                        SuratAdapater suratAdapater = new SuratAdapater(jsonObjectArrayList);
                                        rvSurat.setAdapter(suratAdapater);
                                        suratAdapater.setClickListener(MainActivity.this);

                                        dSurat.show();
                                    });
                            findViewById(R.id.mDetail).setEnabled(true);
                        });
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
        pbLoad.setVisibility(View.VISIBLE);
        try {
            new Client().getOkHttpClient(BASE_URL + i, new Client.OKHttpNetwork() {
                @Override
                public void onSuccess(String response) {
                    // Set to Data
                    try {
                        JSONObject jsonObject = new JSONObject(response).getJSONObject("data");
                        nama = jsonObject.getString("nama");
                        namaLatin = jsonObject.getString("namaLatin");
                        jumlahAyat = jsonObject.getInt("jumlahAyat");
                        deskripsi = jsonObject.getString("deskripsi");
                        JSONArray jsonArray = jsonObject.getJSONArray("ayat");
                        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
                        for (int a = 0; a < jsonArray.length(); a++) jsonObjectArrayList.add(jsonArray.getJSONObject(a));
                        runOnUiThread(() -> {
                            tvSurat.setText(namaLatin);
                            pbLoad.setVisibility(View.GONE);
                            rvAyat.setAdapter(new AyatAdapater(jsonObjectArrayList));
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

    @Override
    public void onItemClick(JSONObject jsonObject, View view, int position) {
        try {
            i = jsonObject.getInt("nomor");
            surat();
            detailSurat();
            dSurat.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}