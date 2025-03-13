package com.yuuna.alquran.adapter;

import static com.yuuna.alquran.MainActivity.qori_api;
import static com.yuuna.alquran.MainActivity.spAlQuran;
import static com.yuuna.alquran.adapter.AudioAdapter.epAudioFull;
import static com.yuuna.alquran.MainActivity.iSurat;
import static com.yuuna.alquran.MainActivity.isAlFatihah;
import static com.yuuna.alquran.MainActivity.isBasmalah;
import static com.yuuna.alquran.MainActivity.nsvAyatLayout;
import static com.yuuna.alquran.MainActivity.rvAyat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.RecyclerView;

import com.yuuna.alquran.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AyatAdapter extends RecyclerView.Adapter<AyatAdapter.Holder> {

    private ArrayList<JSONObject> jsonObjectDataList;
    private ArrayList<ImageView> iconDataList = new ArrayList<>();

    public static ExoPlayer epAudioPartial;

    private Long lPosition;
    private Integer iNext;
    private Boolean isPlaying = false;

    public AyatAdapter(ArrayList<JSONObject> jsonObjectArrayList) {
        this.jsonObjectDataList = jsonObjectArrayList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ayat, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        epAudioPartial = new ExoPlayer.Builder(holder.itemView.getContext()).build();
        JSONObject jsonObject = jsonObjectDataList.get(position);
        try {
            holder.tvNomorAyat.setVisibility(!isAlFatihah && isBasmalah && position == 0 ? View.GONE : View.VISIBLE);
            holder.tvNomorAyat.setText("\u06DD" + NumberFormat.getInstance(Locale.forLanguageTag("AR")).format(jsonObject.getInt("nomorAyat")));
            iconDataList.add(holder.ivIcon);
            holder.llAudio.setVisibility(isAlFatihah && position == 0 ? View.VISIBLE : (isBasmalah && position == 0 ? View.INVISIBLE : View.VISIBLE));
            holder.llAudio.setOnClickListener(v -> {
                if (epAudioFull != null) epAudioFull.stop();
                if (isPlaying) {
                    if (position != iNext - (!isAlFatihah && isBasmalah ? 1 : 2)) {
                        for (ImageView ivIcon : iconDataList) ivIcon.setImageResource(R.drawable.ic_play);
                        iNext = position + (!isAlFatihah && isBasmalah ? 1 : 2);
                        setPlayer(position, holder.itemView.getContext());
                    } else {
                        isPlaying = false;
                        lPosition = epAudioPartial.getCurrentPosition();
                        epAudioPartial.stop();
                        iconDataList.get(position).setImageResource(R.drawable.ic_play);
                    }
                } else {
                    if (iNext != null && position != iNext - (!isAlFatihah && isBasmalah ? 1 : 2)) {
                        for (ImageView ivIcon : iconDataList) ivIcon.setImageResource(R.drawable.ic_play);
                        iNext = position + (!isAlFatihah && isBasmalah ? 1 : 2);
                        setPlayer(position, holder.itemView.getContext());
                    } else {
                        iNext = position + (!isAlFatihah && isBasmalah ? 1 : 2);
                        setPlayer(position, holder.itemView.getContext());
                        if (lPosition != null) epAudioPartial.seekTo(lPosition);
                    }
                }
            });
            holder.tvTextArab.setText(jsonObject.getString("teksArab"));
            holder.tvTextIndo.setText(jsonObject.getString("teksIndonesia"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setPlayer(Integer position, Context context) {
        epAudioPartial.stop();
        epAudioPartial = new ExoPlayer.Builder(context).build();
        epAudioPartial.setMediaItem(MediaItem.fromUri("https://equran.nos.wjv-1.neo.id/audio-partial/" + qori_api[spAlQuran.getInt("Pengisi Suara", 0)] + "/" + String.format("%03d", iSurat) + String.format("%03d", !isAlFatihah && isBasmalah ? position : position + 1) + ".mp3"));
        epAudioPartial.prepare();
        epAudioPartial.setPlayWhenReady(true);
        iconDataList.get(position).setImageResource(R.drawable.ic_pause);
        epAudioPartial.addListener(new Player.Listener() {
            @Override
            public void onEvents(Player player, Player.Events events) {
                Player.Listener.super.onEvents(player, events);
                if (epAudioPartial.getPlaybackState() == Player.STATE_READY) {
                    isPlaying = true;
                    iconDataList.get(iNext - (!isAlFatihah && isBasmalah ? 1 : 2)).setImageResource(R.drawable.ic_pause);
                    nsvAyatLayout.smoothScrollTo(0, (int) rvAyat.getChildAt(iNext - (!isAlFatihah && isBasmalah ? 1 : 2)).getY());
                } else if (epAudioPartial.getPlaybackState() == Player.STATE_ENDED) {
                    isPlaying = false;
                    iconDataList.get(iNext - (!isAlFatihah && isBasmalah ? 1 : 2)).setImageResource(R.drawable.ic_play);
                    if ((!isAlFatihah && isBasmalah ? iNext : iNext - 1) < jsonObjectDataList.size()) {
                        epAudioPartial.setMediaItem(MediaItem.fromUri("https://equran.nos.wjv-1.neo.id/audio-partial/" + qori_api[spAlQuran.getInt("Pengisi Suara", 0)] + "/"+ String.format("%03d", iSurat) + String.format("%03d", iNext++) + ".mp3"));
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return jsonObjectDataList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvNomorAyat, tvTextArab, tvTextIndo;
        private LinearLayout llAudio;
        private ImageView ivIcon;

        public Holder(View itemView) {
            super(itemView);
            tvNomorAyat = itemView.findViewById(R.id.aNomorAyat);
            llAudio = itemView.findViewById(R.id.aAudio);
            ivIcon = itemView.findViewById(R.id.aIcon);
            tvTextArab = itemView.findViewById(R.id.aTextArab);
            tvTextIndo = itemView.findViewById(R.id.aTextIndo);
        }
    }
}
