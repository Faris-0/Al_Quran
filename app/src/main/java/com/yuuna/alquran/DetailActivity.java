package com.yuuna.alquran;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuuna.alquran.adapter.AudioAdapater;
import com.yuuna.alquran.adapter.SuratAdapater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class DetailActivity extends Activity implements AudioAdapater.ItemClickListener {

    private TextView tvSurat, tvDeskripsi;
    private RecyclerView rvAudio;

    private ArrayList<String> stringArrayList;

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

        rvAudio = findViewById(R.id.dAudioFull);
        rvAudio.setLayoutManager(new LinearLayoutManager(this));
        try {
            JSONObject jsonObject =  new JSONObject(getIntent().getStringExtra("audioFull"));
            stringArrayList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                stringArrayList.add(jsonObject.getString("0" + (i + 1)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AudioAdapater audioAdapater = new AudioAdapater(stringArrayList);
        rvAudio.setAdapter(audioAdapater);
        audioAdapater.setClickListener(DetailActivity.this);
    }

    @Override
    public void onItemClick(String s, View view, int position) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        if (Build.VERSION.SDK_INT >= 26) {
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
        } else mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(s);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
