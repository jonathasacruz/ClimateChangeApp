package com.example.climatechangeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    public String location;

    TextView
            //Textos do app
            // TEMPO AGORA
            conditionText, temp_c, precip_mm, feelslike_c, uv, co, pm2_5, pm10,
    //PREVISAO HOJE
    d0_maxtemp_c, d0_mintemp_c, d0_avgtemp_c, d0_totalprecip_mm, d0_uv,
    //PREVISAO AMANHA
    d1_maxtemp_c, d1_mintemp_c, d1_avgtemp_c, d1_totalprecip_mm, d1_uv,
    //PREVISAO DEPOIS
    d2_maxtemp_c, d2_mintemp_c, d2_avgtemp_c, d2_totalprecip_mm, d2_uv;

    ImageView //Ícones do app
            icon, d0_icon, d1_icon, d2_icon;

    public void setLocation(String location) {
        this.location = location;
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        conditionText = findViewById(R.id.condition);
        temp_c = findViewById(R.id.temp_c);
        precip_mm = findViewById(R.id.precip_mm);
        feelslike_c = findViewById(R.id.feelslike_c);
        uv = findViewById(R.id.uv);
        co = findViewById(R.id.co);
        pm2_5 = findViewById(R.id.pm2_5);
        pm10 = findViewById(R.id.pm10);
        icon = findViewById(R.id.icon);

        d0_maxtemp_c = findViewById(R.id.d0_maxtemp_c);
        d0_mintemp_c = findViewById(R.id.d0_mintemp_c);
        d0_avgtemp_c = findViewById(R.id.d0_avgtemp_c);
        d0_totalprecip_mm = findViewById(R.id.d0_totalprecip_mm);
        d0_uv = findViewById(R.id.d0_uv);
        d0_icon = findViewById(R.id.d0_icon);

        d1_maxtemp_c = findViewById(R.id.d1_maxtemp_c);
        d1_mintemp_c = findViewById(R.id.d1_mintemp_c);
        d1_avgtemp_c = findViewById(R.id.d1_avgtemp_c);
        d1_totalprecip_mm = findViewById(R.id.d1_totalprecip_mm);
        d1_uv = findViewById(R.id.d1_uv);
        d1_icon = findViewById(R.id.d1_icon);

        d2_maxtemp_c = findViewById(R.id.d2_maxtemp_c);
        d2_mintemp_c = findViewById(R.id.d2_mintemp_c);
        d2_avgtemp_c = findViewById(R.id.d2_avgtemp_c);
        d2_totalprecip_mm = findViewById(R.id.d2_totalprecip_mm);
        d2_uv = findViewById(R.id.d2_uv);
        d2_icon = findViewById(R.id.d2_icon);

        getLocation();
//        tempoagora(location);

    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        class MyLocationListener implements LocationListener {

            @Override
            public void onLocationChanged(Location loc) {
                //Obtem localização GPS
                //Toast só pra ver se está funcionando

                Toast.makeText(getApplicationContext(), String.valueOf(loc.getLatitude() + "," + loc.getLongitude()), Toast.LENGTH_SHORT).show();
                setLocation(String.valueOf(loc.getLatitude() + "," + loc.getLongitude()));

                tempoagora(location);
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }


        }
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(
                LocationManager.FUSED_PROVIDER, 1000, 1000, locationListener);

    }


    private void tempoagora(String latLong) {


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://api.weatherapi.com/v1/forecast.json?key=5357ea18adc543e3b2b102716212811&q=" + location + "&days=3&aqi=yes&alerts=no&lang=pt";

        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Transformando a stringRequest em JSON
                        try {
                            //Destrinchando o JSON
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject current = new JSONObject(jsonObject.getJSONObject("current").toString()); //Tempo agora
                            JSONObject condition = new JSONObject(current.getJSONObject("condition").toString());
                            JSONObject currentAirQuality = new JSONObject(current.getJSONObject("air_quality").toString());
                            JSONObject forecast = new JSONObject(jsonObject.getJSONObject("forecast").toString());
                            JSONArray forecastday = new JSONArray(forecast.getJSONArray("forecastday").toString());
                            JSONObject todayArray = new JSONObject(forecastday.getJSONObject(0).toString());
                            JSONObject tomorrowArray = new JSONObject(forecastday.getJSONObject(1).toString());
                            JSONObject afterTomorrowArray = new JSONObject(forecastday.getJSONObject(2).toString());
                            JSONObject today = new JSONObject(todayArray.getJSONObject("day").toString()); //Previsão para hoje
                            JSONObject tomorrow = new JSONObject(tomorrowArray.getJSONObject("day").toString()); //Previsão para amanhã
                            JSONObject afterTomorrow = new JSONObject(afterTomorrowArray.getJSONObject("day").toString()); //Previsão para depois de amanhã

                            //Tempo agora
                            conditionText.setText(condition.getString("text"));
                            temp_c.setText(numeroDecimal(current.getString("temp_c")) + "°C");
                            feelslike_c.setText(numeroDecimal(current.getString("feelslike_c")) + "ºC");
                            precip_mm.setText(current.getString("precip_mm") + " mm");
                            uv.setText(numeroDecimal(current.getString("uv")) + indiceUv(current.getString("uv")));
                            co.setText(numeroDecimal(currentAirQuality.getString("co")));
                            pm2_5.setText(numeroDecimal(currentAirQuality.getString("pm2_5")));
                            pm10.setText(numeroDecimal(currentAirQuality.getString("pm10")));
                            String iconUrl = current.getJSONObject("condition").getString("icon");
                            Picasso.get().load(new StringBuilder().append("https:").append(iconUrl).toString()).into(icon);

                            //Previsão para hoje
                            d0_maxtemp_c.setText(numeroDecimal(today.getString("maxtemp_c")) + "ºC");
                            d0_mintemp_c.setText(numeroDecimal(today.getString("mintemp_c")) + "ºC");
                            d0_avgtemp_c.setText(numeroDecimal(today.getString("avgtemp_c")) + "ºC");
                            d0_totalprecip_mm.setText(numeroDecimal(today.getString("totalprecip_mm")) + " mm");
                            d0_uv.setText(numeroDecimal(today.getString("uv")) + indiceUv(today.getString("uv")));
                            String d0iconUrl = today.getJSONObject("condition").getString("icon");
                            Picasso.get().load(new StringBuilder().append("https:").append(d0iconUrl).toString()).into(d0_icon);

                            //Previsão para amanhã
                            d1_maxtemp_c.setText(numeroDecimal(tomorrow.getString("maxtemp_c")) + "ºC");
                            d1_mintemp_c.setText(numeroDecimal(tomorrow.getString("mintemp_c")) + "ºC");
                            d1_avgtemp_c.setText(numeroDecimal(tomorrow.getString("avgtemp_c")) + "ºC");
                            d1_totalprecip_mm.setText(numeroDecimal(tomorrow.getString("totalprecip_mm")) + " mm");
                            d1_uv.setText(numeroDecimal(tomorrow.getString("uv")) + indiceUv(tomorrow.getString("uv")));
                            String d1iconUrl = tomorrow.getJSONObject("condition").getString("icon");
                            Picasso.get().load(new StringBuilder().append("https:").append(d1iconUrl).toString()).into(d1_icon);

                            //Previsão para depois de amanhã
                            d2_maxtemp_c.setText(numeroDecimal(afterTomorrow.getString("maxtemp_c")) + "ºC");
                            d2_mintemp_c.setText(numeroDecimal(afterTomorrow.getString("mintemp_c")) + "ºC");
                            d2_avgtemp_c.setText(numeroDecimal(afterTomorrow.getString("avgtemp_c")) + "ºC");
                            d2_totalprecip_mm.setText(numeroDecimal(afterTomorrow.getString("totalprecip_mm")) + " mm");
                            d2_uv.setText(numeroDecimal(afterTomorrow.getString("uv")) + indiceUv(afterTomorrow.getString("uv")));
                            String d2iconUrl = afterTomorrow.getJSONObject("condition").getString("icon");
                            Picasso.get().load(new StringBuilder().append("https:").append(d2iconUrl).toString()).into(d2_icon);
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                temp_c.setText(location);

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    //Parse de índice UV para texto
    private String indiceUv(String indiceUv) {
        String indiceText;
        int indiceInt = Integer.parseInt(indiceUv);
        if (indiceInt <= 2) {
            return " (Baixo)";
        } else if (indiceInt <= 5) {
            return " (Moderado)";
        } else if (indiceInt <= 7) {
            return " (Alto)";
        } else if (indiceInt <= 10) {
            return " (Muito Alto)";
        } else {
            return " (Extremo)";
        }
    }

    //Formatar números decimais
    private String numeroDecimal(String num) {
        String pattern = "#,##0.0";
        Locale locale = new Locale("pt_BR");
        DecimalFormat numeroDecimal = new DecimalFormat(pattern);
        return numeroDecimal.format(Double.valueOf(num)).replace(".", ",");
    }
}
