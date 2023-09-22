package com.yuuna.alquran.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yuuna.alquran.R;

import java.util.ArrayList;

public class AudioAdapater extends RecyclerView.Adapter<AudioAdapater.Holder> {

    private ArrayList<String> stringDataList;

    private ItemClickListener clickListener;

    public AudioAdapater(ArrayList<String> stringArrayList) {
        this.stringDataList = stringArrayList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (position == 0) holder.tvQori.setText("Abdullah Al-Juhany");
        if (position == 1) holder.tvQori.setText("Abdul Muhsin Al-Qasim");
        if (position == 2) holder.tvQori.setText("Abdurrahman as-Sudais");
        if (position == 3) holder.tvQori.setText("Ibrahim Al-Dossari");
        if (position == 4) holder.tvQori.setText("Misyari Rasyid Al-Afasi");
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

        public Holder(View itemView) {
            super(itemView);
            tvQori = itemView.findViewById(R.id.aQori);
            llDownload = itemView.findViewById(R.id.aDownload);
            llAudio = itemView.findViewById(R.id.aAudio);

            llAudio.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onItemClick(stringDataList.get(getAdapterPosition()), v, getAdapterPosition());
            });
        }
    }
}
