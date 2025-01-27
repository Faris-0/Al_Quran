package com.yuuna.alquran.adapter;

import static com.yuuna.alquran.ui.MainActivity.isAlFatihah;
import static com.yuuna.alquran.ui.MainActivity.isBasmalah;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yuuna.alquran.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AyatAdapter extends RecyclerView.Adapter<AyatAdapter.Holder> {

    private ArrayList<JSONObject> jsonObjectDataList;

    public AyatAdapter(ArrayList<JSONObject> jsonObjectArrayList) {
        this.jsonObjectDataList = jsonObjectArrayList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ayat, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        JSONObject jsonObject = jsonObjectDataList.get(position);
        try {
            holder.tvNomorAyat.setVisibility(isAlFatihah && position == 0 ? View.VISIBLE : (isBasmalah && position == 0 ? View.INVISIBLE : View.VISIBLE));
            holder.tvNomorAyat.setText("\u06DD"+ NumberFormat.getInstance(Locale.forLanguageTag("AR")).format(jsonObject.getInt("nomorAyat")));
            holder.tvTextArab.setText(jsonObject.getString("teksArab"));
            holder.tvTextIndo.setText(jsonObject.getString("teksIndonesia"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonObjectDataList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvNomorAyat, tvTextArab, tvTextIndo;

        public Holder(View itemView) {
            super(itemView);
            tvNomorAyat = itemView.findViewById(R.id.aNomorAyat);
            tvTextArab = itemView.findViewById(R.id.aTextArab);
            tvTextIndo = itemView.findViewById(R.id.aTextIndo);
        }
    }
}
