package com.yuuna.alquran.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.yuuna.alquran.R;

import java.util.ArrayList;

public class AudioAdapater extends RecyclerView.Adapter<AudioAdapater.Holder> {

    private ArrayList<String> stringDataList;
    private ArrayList<Boolean> booleanDataList;
    private Context mContext;

    private ItemClickListener clickListener;

    private ExoPlayer exoPlayer;

    private Integer iCheck;

    public AudioAdapater(ArrayList<String> stringArrayList, Context context) {
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
        exoPlayer.stop();
//        for (int i = 0; i < stringDataList.size(); i++) booleanDataList.add(false);
        if (position == 0) holder.tvQori.setText("Abdullah Al-Juhany");
        if (position == 1) holder.tvQori.setText("Abdul Muhsin Al-Qasim");
        if (position == 2) holder.tvQori.setText("Abdurrahman as-Sudais");
        if (position == 3) holder.tvQori.setText("Ibrahim Al-Dossari");
        if (position == 4) holder.tvQori.setText("Misyari Rasyid Al-Afasi");
        if (booleanDataList != null && booleanDataList.get(position)) {
            holder.ivIcon.setImageResource(R.drawable.ic_pause);
            exoPlayer.stop();
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_play);
            exoPlayer.stop();
        }
        holder.llAudio.setOnClickListener(v -> {
            booleanDataList = new ArrayList<>();
            for (int i = 0; i < stringDataList.size(); i++) {
                if (position == i) booleanDataList.add(true);
                else booleanDataList.add(false);
            }
            exoPlayer.stop();
            exoPlayer = new ExoPlayer.Builder(mContext).build();
            exoPlayer.addMediaItem(MediaItem.fromUri(stringDataList.get(position)));
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
//            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return stringDataList.size();
    }

    public interface ItemClickListener {
        void onItemClick(String jsonObject, View view, int position);
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvQori;
        private LinearLayout llDownload, llAudio;
        private ImageView ivIcon;

        public Holder(View itemView) {
            super(itemView);
            tvQori = itemView.findViewById(R.id.aQori);
            llDownload = itemView.findViewById(R.id.aDownload);
            llAudio = itemView.findViewById(R.id.aAudio);
            ivIcon = itemView.findViewById(R.id.aIcon);

            llAudio.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onItemClick(stringDataList.get(getAdapterPosition()), v, getAdapterPosition());
            });
        }
    }
}
