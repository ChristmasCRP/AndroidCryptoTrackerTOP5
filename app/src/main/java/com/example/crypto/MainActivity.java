package com.example.crypto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView btcPriceView, marketCapView, dominanceView;
    private RecyclerView top5Recycler;
    private Button refreshButton;
    private final List<CryptoItem> cryptoList = new ArrayList<>();
    private CryptoAdapter adapter;
    private final DecimalFormat df2 = new DecimalFormat("#.##");
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btcPriceView = findViewById(R.id.bitcoinPrice);
        marketCapView = findViewById(R.id.marketCap);
        dominanceView = findViewById(R.id.btcDominance);
        refreshButton = findViewById(R.id.refreshButton);
        top5Recycler = findViewById(R.id.top5Recycler);

        top5Recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CryptoAdapter(cryptoList, this);
        top5Recycler.setAdapter(adapter);

        queue = Volley.newRequestQueue(this);
        fetchCryptoData();
        refreshButton.setOnClickListener(v -> fetchCryptoData());
    }

    private void fetchCryptoData() {
        String priceUrl = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd";
        JsonObjectRequest priceRequest = new JsonObjectRequest(
                Request.Method.GET, priceUrl, null,
                response -> {
                    try {
                        JSONObject bitcoin = response.getJSONObject("bitcoin");
                        double price = bitcoin.getDouble("usd");
                        String formattedPrice = "$" + df2.format(price);
                        btcPriceView.setText("Cena BTC: " + formattedPrice);
                        Log.d("CenaBitcoina", "Aktualna cena BTC: " + formattedPrice);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("VolleyError", "Błąd (cena BTC): " + error.toString())
        );
        queue.add(priceRequest);

        String globalUrl = "https://api.coingecko.com/api/v3/global";
        JsonObjectRequest marketCapRequest = new JsonObjectRequest(
                Request.Method.GET, globalUrl, null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");

                        JSONObject totalMarketCap = data.getJSONObject("total_market_cap");
                        JSONObject marketCapPercentage = data.getJSONObject("market_cap_percentage");

                        double totalCapUSD = totalMarketCap.getDouble("usd");
                        double btcCapPercent = marketCapPercentage.getDouble("btc");

                        String capText = "$" + df2.format(totalCapUSD / 1_000_000_000_000.0) + "T";
                        String domText = df2.format(btcCapPercent) + "%";

                        marketCapView.setText("Kapitalizacja rynku: " + capText);
                        dominanceView.setText("Udział BTC: " + domText);

                        Log.d("Kapitalizacja", "Całkowita kapitalizacja rynku: " + capText);
                        Log.d("Kapitalizacja", "Kapitalizacja BTC (udział %): " + domText);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("VolleyError", "Błąd (kapitalizacja): " + error.toString())
        );
        queue.add(marketCapRequest);

        String top5Url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=5&page=1";
        JsonArrayRequest top5Request = new JsonArrayRequest(
                Request.Method.GET, top5Url, null,
                response -> {
                    try {
                        cryptoList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject coin = response.getJSONObject(i);
                            String name = coin.getString("name");
                            double price = coin.getDouble("current_price");
                            double marketCap = coin.getDouble("market_cap");
                            String imageUrl = coin.getString("image");

                            String formattedPrice = df2.format(price);
                            String formattedCap;
                            if (marketCap >= 1_000_000_000_000.0) {
                                formattedCap = df2.format(marketCap / 1_000_000_000_000.0) + "T";
                            } else {
                                formattedCap = df2.format(marketCap / 1_000_000_000.0) + "B";
                            }

                            cryptoList.add(new CryptoItem(name, formattedPrice, formattedCap, imageUrl));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("VolleyError", "Błąd (top5): " + error.toString())
        );
        queue.add(top5Request);
    }
}
