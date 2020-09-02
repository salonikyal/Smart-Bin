package com.example.admybin.admybin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    @SuppressWarnings("unused")

    private ArrayList<Location> locationlist;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView location;
        private CardView cardView;



        public ViewHolder(View itemView) {
            super(itemView);

            location = (TextView) itemView.findViewById(R.id.location);
            cardView = (CardView) itemView.findViewById(R.id.card_view);


        }
    }

    public LocationAdapter(ArrayList<Location> locationlist) {
        this.locationlist = locationlist;


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Location location1 = locationlist.get(position);

        holder.location.setText(location1.getLocation().substring(0, 1).toUpperCase() + location1.getLocation().substring(1));
        System.out.println("hjh" + location1.getLocation());


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement onClick

                Intent intent = new Intent(v.getContext(), Bin.class);
                Bundle value = new Bundle();
                value.putString("location", location1.getLocation());

                intent.putExtras(value);
                v.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {

        return locationlist.size();
    }

    public void setFilter(ArrayList<Location> newlocationList) {

        locationlist = new ArrayList<>();
        locationlist.addAll(newlocationList);
        notifyDataSetChanged();

    }


}
