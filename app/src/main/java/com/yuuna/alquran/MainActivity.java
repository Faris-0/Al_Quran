package com.yuuna.alquran;

import static com.yuuna.alquran.adapter.AudioAdapter.epAudioFull;
import static com.yuuna.alquran.adapter.AyatAdapter.epAudioPartial;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.yuuna.alquran.adapter.AudioAdapter;
import com.yuuna.alquran.adapter.AyatAdapter;
import com.yuuna.alquran.adapter.SuratAdapter;
import com.yuuna.alquran.util.Client;
import com.yuuna.alquran.util.CustomLinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends Activity implements SuratAdapter.ItemClickListener, AudioAdapter.ItemClickListener {

    private TextView tvSurat, tvDeskripsi;
    public static NestedScrollView nsvAyatLayout;
    public static RecyclerView rvAyat;
    private RecyclerView rvAudio;
    private Spinner sPengisiSuara, sUkuranFont;
    private ProgressBar pbLoad;

    private Dialog dSurat;

    private ArrayList<String> stringArrayList;
    private ArrayList<LinearLayout> linearLayoutArrayList;
    private ArrayList<TextView> textViewArrayList;
    private ArrayList<ProgressBar> progressBarArrayList;

    public static SharedPreferences spAlQuran;
    private String BASE_URL = "https://equran.id/api/v2/surat/", namaLatin, deskripsi, audio, namaFile;
    public static String[] qori = new String[]{"Abdullah Al-Juhany", "Abdul Muhsin Al-Qasim", "Abdurrahman as-Sudais", "Ibrahim Al-Dossari", "Misyari Rasyid Al-Afasi"};
    public static String[] qori_api = new String[]{"Abdullah-Al-Juhany", "Abdul-Muhsin-Al-Qasim", "Abdurrahman-as-Sudais", "Ibrahim-Al-Dossari", "Misyari-Rasyid-Al-Afasi"};
    private String[] ukuranFont = new String[]{"11", "12", "13", "14", "15", "16"};
    public static Integer iSurat = 1;
    private Integer iPosition;
    private Boolean doubleBackToExit = false;
    public static Boolean isAlFatihah = false, isBasmalah = false;

    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);

        initID();
        initClick();
        initData();
    }

    private void initData() {
        spAlQuran = getSharedPreferences("Al Qur'an", MODE_PRIVATE);
        surat();
        detailSurat();
        sPengisiSuara.setAdapter(new ArrayAdapter<>(this, R.layout.custom_spinner, qori));
        sPengisiSuara.setSelection(spAlQuran.getInt("Pengisi Suara", 0));
        sUkuranFont.setAdapter(new ArrayAdapter<>(this, R.layout.custom_spinner, ukuranFont));
        sUkuranFont.setSelection(spAlQuran.getInt("Ukuran Font", 0));
    }

    private void initClick() {
        findViewById(R.id.mBack).setOnClickListener(v -> {
            iSurat--;
            if (iSurat == 0) iSurat = 114;
            ubahSurat();
        });
        findViewById(R.id.mNext).setOnClickListener(v -> {
            iSurat++;
            if (iSurat == 115) iSurat = 1;
            ubahSurat();
        });
        findViewById(R.id.mBeranda).setOnClickListener(v -> setLayout(0));
        findViewById(R.id.mDetail).setOnClickListener(v -> setLayout(1));
        findViewById(R.id.mSettings).setOnClickListener(v -> setLayout(2));
        sPengisiSuara.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spAlQuran.edit().putInt("Pengisi Suara", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sUkuranFont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spAlQuran.edit().putInt("Ukuran Font", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setLayout(int i) {
        nsvAyatLayout.setVisibility(i == 0 ? View.VISIBLE : View.GONE);
        findViewById(R.id.mDetailLayout).setVisibility(i == 1 ? View.VISIBLE : View.GONE);
        findViewById(R.id.mSettingsLayout).setVisibility(i == 2 ? View.VISIBLE : View.GONE);
    }

    private void initID() {
        pbLoad = findViewById(R.id.mLoading);
        nsvAyatLayout = findViewById(R.id.mAyatLayout);
        tvSurat = findViewById(R.id.mSurat);
        rvAyat = findViewById(R.id.mAyat);
        tvDeskripsi = findViewById(R.id.mDeskripsi);
        rvAudio = findViewById(R.id.mAudioFull);
        sPengisiSuara = findViewById(R.id.mPengisiSuara);
        sUkuranFont = findViewById(R.id.mUkuranFont);
    }

    private void ubahSurat() {
        surat();
        detailSurat();
        if (epAudioFull != null) epAudioFull.stop();
        if (epAudioPartial != null) epAudioPartial.stop();
    }

    private void surat() {
        try {
            new Client().getOkHttpClient(BASE_URL, new Client.OKHttpNetwork() {
                @Override
                public void onSuccess(String response) {
                    // Set to Data
                    try {
                        JSONArray jsonArray = new JSONObject(response).getJSONArray("data");
                        audio = String.valueOf(jsonArray.getJSONObject(iSurat - 1).getJSONObject("audioFull"));
                        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
                        for (int a = 0; a < jsonArray.length(); a++) jsonObjectArrayList.add(jsonArray.getJSONObject(a));
                        runOnUiThread(() -> {
                            tvSurat.setOnClickListener(v -> {
                                dSurat = new Dialog(MainActivity.this);
                                dSurat.setContentView(R.layout.dialog_surat);
                                dSurat.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                dSurat.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                dSurat.findViewById(R.id.sClose).setOnClickListener(v1 -> dSurat.dismiss());

                                SuratAdapter suratAdapter = new SuratAdapter(jsonObjectArrayList);

                                EditText etSearch = dSurat.findViewById(R.id.sFind);
                                LinearLayout llClear = dSurat.findViewById(R.id.sClear);
                                etSearch.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                        suratAdapter.getFilter().filter(editable);
                                        llClear.setVisibility(editable.toString().isEmpty() ? View.GONE : View.VISIBLE);
                                    }
                                });

                                llClear.setOnClickListener(v1 -> {
                                    etSearch.setText("");
                                    llClear.setVisibility(View.GONE);
                                    suratAdapter.getFilter().filter("");
                                });

                                RecyclerView rvSurat = dSurat.findViewById(R.id.sSurat);
                                rvSurat.setLayoutManager(new CustomLinearLayoutManager(MainActivity.this));
                                rvSurat.setAdapter(suratAdapter);
                                suratAdapter.setClickListener(MainActivity.this);

                                dSurat.show();
                            });
                            // Set RV Audio
                            try {
                                JSONObject jsonObject =  new JSONObject(audio);
                                stringArrayList = new ArrayList<>();
                                for (int i = 0; i < 5; i++) stringArrayList.add(jsonObject.getString("0" + (i + 1)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            AudioAdapter audioAdapter = new AudioAdapter(stringArrayList);
                            rvAudio.setLayoutManager(new CustomLinearLayoutManager(MainActivity.this));
                            rvAudio.setAdapter(audioAdapter);
                            audioAdapter.setClickListener(MainActivity.this);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void detailSurat() {
        pbLoad.setVisibility(View.VISIBLE);
        try {
            new Client().getOkHttpClient(BASE_URL + iSurat, new Client.OKHttpNetwork() {
                @Override
                public void onSuccess(String response) {
                    // Set to Data
                    try {
                        JSONObject jsonObject = new JSONObject(response).getJSONObject("data");
                        namaLatin = jsonObject.getString("namaLatin");
                        deskripsi = jsonObject.getString("deskripsi");
                        JSONArray jsonArray = jsonObject.getJSONArray("ayat");
                        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
                        if (namaLatin.equals("Al-Fatihah")) {
                            isAlFatihah = true;
                            isBasmalah = true;
                            spAlQuran.edit().putString("Basmalah", String.valueOf(jsonArray.getJSONObject(0))).apply();
                        } else if (namaLatin.equals("At-Taubah")) {
                            isAlFatihah = false;
                            isBasmalah = false;
                        } else {
                            isAlFatihah = false;
                            isBasmalah = true;
                            jsonObjectArrayList.add(new JSONObject(spAlQuran.getString("Basmalah", "")));
                        }
                        for (int a = 0; a < jsonArray.length(); a++) jsonObjectArrayList.add(jsonArray.getJSONObject(a));
                        runOnUiThread(() -> {
                            tvSurat.setText(namaLatin);
                            pbLoad.setVisibility(View.GONE);
                            rvAyat.setLayoutManager(new CustomLinearLayoutManager(MainActivity.this));
                            rvAyat.setAdapter(new AyatAdapter(jsonObjectArrayList));
                            nsvAyatLayout.smoothScrollTo(0, 0);
                            // Set Deskripsi
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) tvDeskripsi.setText(Html.fromHtml(deskripsi, Html.FROM_HTML_MODE_COMPACT));
                            else tvDeskripsi.setText(Html.fromHtml(deskripsi));
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        pbLoad.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                pbLoad.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExit) finishAndRemoveTask();
        doubleBackToExit = true;
        Toast.makeText(this, "Tekan sekali lagi untuk keluar dari aplikasi", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExit = false, 2000);
    }

    @Override
    public void onItemClick(JSONObject jsonObject) {
        try {
            iSurat = jsonObject.getInt("nomor");
            surat();
            detailSurat();
            if (epAudioFull != null) epAudioFull.stop();
            if (epAudioPartial != null) epAudioPartial.stop();
            dSurat.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(String s, ArrayList<LinearLayout> lld, ArrayList<TextView> tvd, ArrayList<ProgressBar> pbd, int position) {
        linearLayoutArrayList = new ArrayList<>(lld);
        textViewArrayList = new ArrayList<>(tvd);
        progressBarArrayList = new ArrayList<>(pbd);
        iPosition = position;
        namaFile = namaLatin + "_" + qori[position] + ".mp3";
        new DownloadFileFromURL().execute(s);
    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + namaFile);

                byte[] data = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            linearLayoutArrayList.get(iPosition).setVisibility(View.VISIBLE);
            textViewArrayList.get(iPosition).setText(String.valueOf(Integer.parseInt(progress[0])));
            progressBarArrayList.get(iPosition).setVisibility(View.VISIBLE);
            progressBarArrayList.get(iPosition).setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            linearLayoutArrayList.get(iPosition).setVisibility(View.GONE);
            progressBarArrayList.get(iPosition).setVisibility(View.GONE);
        }
    }
}