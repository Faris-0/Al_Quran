package com.yuuna.alquran.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.yuuna.alquran.R;

import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.Holder> {

    private ArrayList<String> stringDataList;
    private ArrayList<Boolean> booleanDataList;
    private ArrayList<ImageView> iconDataList = new ArrayList<>();

    private ArrayList<LinearLayout> linearLayoutDataList = new ArrayList<>();
    private ArrayList<TextView> textViewDataList = new ArrayList<>();
    private ArrayList<ProgressBar> progressBarDataList = new ArrayList<>();

    private Context mContext;
    private ItemClickListener clickListener;
    public static ExoPlayer exoPlayer;

    private Long lPosition;
    private Integer iPause;
    private Boolean isPlaying = false;

    public AudioAdapter(ArrayList<String> stringArrayList, Context context) {
        this.stringDataList = stringArrayList;
        this.mContext = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        exoPlayer = new ExoPlayer.Builder(mContext).build();
        if (position == 0) holder.tvQori.setText("Abdullah Al-Juhany");
        if (position == 1) holder.tvQori.setText("Abdul Muhsin Al-Qasim");
        if (position == 2) holder.tvQori.setText("Abdurrahman as-Sudais");
        if (position == 3) holder.tvQori.setText("Ibrahim Al-Dossari");
        if (position == 4) holder.tvQori.setText("Misyari Rasyid Al-Afasi");
        iconDataList.add(holder.ivIcon);
        linearLayoutDataList.add(holder.llPercent);
        textViewDataList.add(holder.tvProgress);
        progressBarDataList.add(holder.pbLoading);
        holder.llAudio.setOnClickListener(v -> {
            if (isPlaying) {
                isPlaying = false;
                lPosition = exoPlayer.getCurrentPosition();
                exoPlayer.stop();
                if (!booleanDataList.get(position)) {
                    isPlaying = true;
                    setPlayer(position);
                } else {
                    isPlaying = false;
                    booleanDataList = new ArrayList<>();
                    for (int i = 0; i < stringDataList.size(); i++) {
                        booleanDataList.add(false);
                        iconDataList.get(i).setImageResource(R.drawable.ic_play);
                    }
                }
            } else {
                isPlaying = true;
                if (iPause != null) {
                    if (iPause != position) {
                        iPause = position;
                        setPlayer(position);
                    } else {
                        iPause = position;
                        setPlayer(position);
                        exoPlayer.seekTo(lPosition);
                    }
                } else {
                    iPause = position;
                    setPlayer(position);
                }
            }
        });
    }

    private void setPlayer(Integer position) {
        booleanDataList = new ArrayList<>();
        for (int i = 0; i < stringDataList.size(); i++) {
            if (position == i) {
                booleanDataList.add(true);
                iconDataList.get(i).setImageResource(R.drawable.ic_pause);
            } else {
                booleanDataList.add(false);
                iconDataList.get(i).setImageResource(R.drawable.ic_play);
            }
        }
        exoPlayer.stop();
        exoPlayer = new ExoPlayer.Builder(mContext).build();
        exoPlayer.addMediaItem(MediaItem.fromUri(stringDataList.get(position)));
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onEvents(Player player, Player.Events events) {
                Player.Listener.super.onEvents(player, events);
                if (exoPlayer.getPlaybackState() == Player.STATE_ENDED) {
                    booleanDataList = new ArrayList<>();
                    for (int i = 0; i < stringDataList.size(); i++) {
                        booleanDataList.add(false);
                        iconDataList.get(i).setImageResource(R.drawable.ic_play);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stringDataList.size();
    }

    public interface ItemClickListener {
        void onItemClick(String s,
                         ArrayList<LinearLayout> lld,
                         ArrayList<TextView> tvd,
                         ArrayList<ProgressBar> pbd,
                         int position);
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvQori, tvProgress;
        private LinearLayout llDownload, llAudio, llPercent;
        private ImageView ivIcon;
        private ProgressBar pbLoading;

        public Holder(View itemView) {
            super(itemView);
            tvQori = itemView.findViewById(R.id.aQori);
            llDownload = itemView.findViewById(R.id.aDownload);
            llAudio = itemView.findViewById(R.id.aAudio);
            ivIcon = itemView.findViewById(R.id.aIcon);
            llPercent = itemView.findViewById(R.id.aPercent);
            tvProgress = itemView.findViewById(R.id.aProgress);
            pbLoading = itemView.findViewById(R.id.aLoading);

            llDownload.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onItemClick(
                        stringDataList.get(getBindingAdapterPosition()),
                        linearLayoutDataList,
                        textViewDataList,
                        progressBarDataList,
                        getBindingAdapterPosition()
                );
            });
        }
    }
}
