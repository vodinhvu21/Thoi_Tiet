package com.example.thitit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class weatherRVapdater extends RecyclerView.Adapter<weatherRVapdater.ViewHolder>  {

    private final Context context;
    private final ArrayList<weatherRVmodal> weatherRVmodalArrayList;

    public weatherRVapdater(Context context, ArrayList<weatherRVmodal> weatherRVmodalArrayList) {
        this.context = context;
        this.weatherRVmodalArrayList = weatherRVmodalArrayList;
    }


    @NonNull
    @Override
    public weatherRVapdater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weater_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull weatherRVapdater.ViewHolder holder, int position) {
        weatherRVmodal weatherRVmodal = weatherRVmodalArrayList.get(position);
        holder.textView5.setText(weatherRVmodal.getTemperature()+"℃");
        Picasso.get().load("https:".concat(weatherRVmodal.getIcon())).into(holder.imageView3);
        holder.textView6.setText(weatherRVmodal.getWindSpeed()+"km/h");
        holder.textView4.setText(weatherRVmodal.getTime());



    }

    @Override
    public int getItemCount() {
        return weatherRVmodalArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView4;
        private final TextView textView5;
        private final TextView textView6;
        private final ImageView imageView3;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView4 = (TextView) itemView.findViewById(R.id.textview4);
            textView5 = (TextView) itemView.findViewById(R.id.textview5);
            textView6 = (TextView) itemView.findViewById(R.id.textview6);
            imageView3 = (ImageView) itemView.findViewById(R.id.imageView3);
        }
    }
}
