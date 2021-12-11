package com.example.thitit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Magnifier;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout relativeLayout;
    private ProgressBar progressBar;
    private TextView textView,textView1,textView2;
    private TextInputEditText editText;
    private ImageView imageView,imageView1,imageView2,imageView4,imageView5;
    private RecyclerView recyclerView;
    private ArrayList<weatherRVmodal> weatherRVmodalArrayList;
    private weatherRVapdater weatherRVapdater;
    private final int PERMISSION_CODE = 1;
    private LocationManager locationManager;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        editText = (TextInputEditText) findViewById(R.id.editText);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayout =(LinearLayout) findViewById(R.id.linearLayout);
        linearLayout.setVisibility(LinearLayout.GONE);
        weatherRVmodalArrayList = new ArrayList<>();
        weatherRVapdater = new weatherRVapdater(this,weatherRVmodalArrayList);
        recyclerView.setAdapter(weatherRVapdater);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView5 = (ImageView) findViewById(R.id.imageView5);
        weathermylocation();
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(LinearLayout.VISIBLE);
                imageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String city = Objects.requireNonNull(editText.getText()).toString();// có đổi
                        editText.setText("");
                        if(!city.equals("")){
                            Geocoder geocoder= new Geocoder(getBaseContext(),Locale.getDefault());
                            List<Address> addressList = null;
                            try {
                                addressList = geocoder.getFromLocationName(city,1);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "Location not found", Toast.LENGTH_LONG).show();
                            }
                            assert addressList != null;
                            try {
                                textView.setText(String.valueOf(addressList.get(0).getFeatureName()));
                                getWeatherInfo(addressList.get(0).getLatitude(),addressList.get(0).getLongitude());
                                closeKeyboard();
                                linearLayout.setVisibility(LinearLayout.GONE);
                            }catch (Exception exception){
                                exception.printStackTrace();
                                Toast.makeText(MainActivity.this, "Tên nhập lỗi, hãy thử lại", Toast.LENGTH_SHORT).show();
                            }

                        }


                    }

                });
            }
        });
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weathermylocation();
            }
        });


    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    private void weathermylocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},99);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Geocoder geocoder = new Geocoder(getBaseContext(),Locale.getDefault());
        try {
            textView.setText(String.valueOf(geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1).get(0).getSubAdminArea()));
            getWeatherInfo(location.getLatitude(),location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getWeatherInfo(double latt, double lott){
        String url="https://api.weatherapi.com/v1/forecast.json?key=c1114643120740be89410350210611&q="+latt+","+lott+"&days=1&aqi=no&alerts=no&lang=vi";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                weatherRVmodalArrayList.clear();
                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    textView1.setText(temperature+"℃");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionicon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:".concat(conditionicon)).into(imageView2);
                    textView2.setText(condition);
                    if(isDay ==1 ){
                        //ban ngay
                        Picasso.get().load("https://toigingiuvedep.vn/wp-content/uploads/2021/01/hinh-anh-nang-choi-chang.jpg").into(imageView);
                    }
                    else {
                        Picasso.get().load("https://storage.googleapis.com/stateless-muahanghoa-com/2021/06/Cay-vao-ban-dem.jpg").into(imageView);

                    }
                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourarray = forecast0.getJSONArray("hour");
                    for(int i =0; i < hourarray.length();i++){
                        JSONObject hourObj = hourarray.getJSONObject(i);
                        String temper = hourObj.getString("temp_c");
                        String time = hourObj.getString("time");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRVmodalArrayList.add(new weatherRVmodal(time,temper,img,wind));

                    }
                    weatherRVapdater.notifyDataSetChanged();

                }catch (JSONException e){
                    Toast.makeText(MainActivity.this, "loi", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "lỗi tên thành phố", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}