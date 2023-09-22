package com.yuuna.alquran.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yuuna.alquran.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuratAdapater extends RecyclerView.Adapter<SuratAdapater.Holder> {

    private ArrayList<JSONObject> jsonObjectDataList;

    private ItemClickListener clickListener;

    public SuratAdapater(ArrayList<JSONObject> jsonObjectArrayList) {
        this.jsonObjectDataList = jsonObjectArrayList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_surat, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        JSONObject jsonObject = jsonObjectDataList.get(position);
        try {
            holder.tvNomor.setText(jsonObject.getInt("nomor") + ".");
            holder.tvNamaLatin.setText(jsonObject.getString("namaLatin"));
            holder.tvTempatTurunArti.setText(jsonObject.getString("tempatTurun") + " • " + jsonObject.getString("arti"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonObjectDataList.size();
    }

    public interface ItemClickListener {
        void onItemClick(JSONObject jsonObject, View view, int position);
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvNomor, tvNamaLatin, tvTempatTurunArti;

        public Holder(View itemView) {
            super(itemView);
            tvNomor = itemView.findViewById(R.id.sNomor);
            tvNamaLatin = itemView.findViewById(R.id.sNamaLatin);
            tvTempatTurunArti = itemView.findViewById(R.id.sTempatTurunArti);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onItemClick(jsonObjectDataList.get(getAdapterPosition()), v, getAdapterPosition());
            });
        }
    }
}
