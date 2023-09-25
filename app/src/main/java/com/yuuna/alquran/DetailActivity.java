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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.yuuna.alquran.adapter.AudioAdapater;
import com.yuuna.alquran.adapter.SuratAdapater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class DetailActivity extends Activity implements AudioAdapater.ItemClickListener {
//public class DetailActivity extends Activity {

    private TextView tvSurat, tvDeskripsi;
    private RecyclerView rvAudio;
    private LinearLayout llDownload1, llAudio1, llDownload2, llAudio2, llDownload3, llAudio3, llDownload4, llAudio4, llDownload5, llAudio5;

//    private ExoPlayer exoPlayer;

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
        AudioAdapater audioAdapater = new AudioAdapater(stringArrayList, this);
        rvAudio.setAdapter(audioAdapater);
        audioAdapater.setClickListener(DetailActivity.this);
    }

    @Override
    public void onItemClick(String s, View view, int position) {
//        exoPlayer = new ExoPlayer.Builder(this).build();
//        exoPlayer.addMediaItem(MediaItem.fromUri(s));
//        exoPlayer.prepare();
//        exoPlayer.setPlayWhenReady(true);
    }
}
