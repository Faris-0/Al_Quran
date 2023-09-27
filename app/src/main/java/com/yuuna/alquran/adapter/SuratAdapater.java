package com.yuuna.alquran.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yuuna.alquran.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuratAdapater extends RecyclerView.Adapter<SuratAdapater.Holder> implements Filterable {

    private ArrayList<JSONObject> jsonObjectDataList, listSurat;

    private ItemClickListener clickListener;

    public SuratAdapater(ArrayList<JSONObject> jsonObjectArrayList) {
        this.jsonObjectDataList = jsonObjectArrayList;
        this.listSurat = jsonObjectArrayList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_surat, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        JSONObject jsonObject = listSurat.get(position);
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
        return listSurat.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString().toLowerCase().trim();

                if (searchString.isEmpty()) {
                    listSurat = jsonObjectDataList;
                } else {
                    ArrayList<JSONObject> tempFilteredList = new ArrayList<>();
                    for (JSONObject jsonObject : jsonObjectDataList) {
                        try {
                            if (jsonObject.getString("namaLatin").toLowerCase().contains(searchString)) tempFilteredList.add(jsonObject);
                            else if (jsonObject.getString("arti").toLowerCase().contains(searchString)) tempFilteredList.add(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    listSurat = tempFilteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listSurat;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listSurat = (ArrayList<JSONObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ItemClickListener {
        void onItemClick(JSONObject jsonObject);
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
                if (clickListener != null) clickListener.onItemClick(listSurat.get(getAdapterPosition()));
            });
        }
    }
}
