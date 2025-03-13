package com.yuuna.alquran.adapter;

import static com.yuuna.alquran.adapter.AyatAdapter.epAudioPartial;
import static com.yuuna.alquran.MainActivity.qori;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.RecyclerView;

import com.yuuna.alquran.R;

import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.Holder> {

    private ArrayList<String> stringDataList;
    private ArrayList<Boolean> booleanDataList;
    private ArrayList<ImageView> iconDataList = new ArrayList<>();

    private ArrayList<LinearLayout> linearLayoutDataList = new ArrayList<>();
    private ArrayList<TextView> textViewDataList = new ArrayList<>();
    private ArrayList<ProgressBar> progressBarDataList = new ArrayList<>();

    private ItemClickListener clickListener;
    public static ExoPlayer epAudioFull;

    private Long lPosition;
    private Integer iPause;
    private Boolean isPlaying = false;

    public AudioAdapter(ArrayList<String> stringArrayList) {
        this.stringDataList = stringArrayList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        epAudioFull = new ExoPlayer.Builder(holder.itemView.getContext()).build();
        holder.tvQori.setText(qori[position]);
        iconDataList.add(holder.ivIcon);
        linearLayoutDataList.add(holder.llPercent);
        textViewDataList.add(holder.tvProgress);
        progressBarDataList.add(holder.pbLoading);
        holder.llAudio.setOnClickListener(v -> {
            if (epAudioPartial != null) epAudioPartial.stop();
            if (isPlaying) {
                isPlaying = false;
                lPosition = epAudioFull.getCurrentPosition();
                epAudioFull.stop();
                if (!booleanDataList.get(position)) {
                    isPlaying = true;
                    setPlayer(position, holder.itemView.getContext());
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
                        setPlayer(position, holder.itemView.getContext());
                    } else {
                        iPause = position;
                        setPlayer(position, holder.itemView.getContext());
                        epAudioFull.seekTo(lPosition);
                    }
                } else {
                    iPause = position;
                    setPlayer(position, holder.itemView.getContext());
                }
            }
        });
    }

    private void setPlayer(Integer position, Context context) {
        booleanDataList = new ArrayList<>();
        for (int i = 0; i < stringDataList.size(); i++) {
            booleanDataList.add(position == i);
            iconDataList.get(i).setImageResource(position == i ? R.drawable.ic_pause : R.drawable.ic_play);
        }
        epAudioFull.stop();
        epAudioFull = new ExoPlayer.Builder(context).build();
        epAudioFull.setMediaItem(MediaItem.fromUri(stringDataList.get(position)));
        epAudioFull.prepare();
        epAudioFull.setPlayWhenReady(true);
        epAudioFull.addListener(new Player.Listener() {
            @Override
            public void onEvents(Player player, Player.Events events) {
                Player.Listener.super.onEvents(player, events);
                if (epAudioFull.getPlaybackState() == Player.STATE_ENDED) {
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
