package com.example.admybin.admybin;


import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static android.R.color.black;



public class BinAdapter extends RecyclerView.Adapter<BinAdapter.ViewHolder> {
    @SuppressWarnings("unused")

    private ArrayList<BinDetails> binlist;
    private ArrayList<Location> locationlist1;
    private String location;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView binname;
        TextView people;
        TextView level;
        CardView cardView;
        LinearLayout ll;
        FrameLayout frame;
        View highlight;
        Context context;



        public ViewHolder(View itemView) {
            super(itemView);

            binname = (TextView) itemView.findViewById(R.id.binId);
            people = (TextView) itemView.findViewById(R.id.people);
            level = (TextView) itemView.findViewById(R.id.level);
            cardView = (CardView) itemView.findViewById(R.id.card_viewBin);
            highlight = itemView.findViewById(R.id.percent_highlight);
            ll = (LinearLayout) itemView.findViewById(R.id.linearlayout);
            frame = (FrameLayout) itemView.findViewById(R.id.level_frame);
            context = itemView.getContext();



        }

    }


    public BinAdapter(ArrayList<BinDetails> binlist, String location, ArrayList<Location> locationList1) {
        this.binlist = binlist;
        this.location = location;
        this.locationlist1 = locationList1;

    }


    @Override
    public BinAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_bin, parent, false);

        return new BinAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(BinAdapter.ViewHolder holder, int position) {


        final ArrayList<String> tempbincode = new ArrayList<>();


        //System.out.println(locationlist1.size() + " CHECKING SIZE " + binlist.size());

        final Location info = locationlist1.get(position);

        String capitalize_binname = info.getBinName().substring(0, 1).toUpperCase() + info.getBinName().substring(1);
        holder.binname.setText(capitalize_binname);


        for (int z = 0; z < binlist.size(); z++) {
            tempbincode.add(binlist.get(z).getBincode());
        }

        System.out.println("bin code no check " + tempbincode);

        if (tempbincode.contains(info.getBinCode())) {

            int i = tempbincode.indexOf((info.getBinCode()));
            //System.out.println("yes " + i + "   " + binlist.get(i).getPeople());
            holder.people.setText(Integer.toString(binlist.get(i).getPeople()));
            holder.level.setText(Integer.toString(binlist.get(i).getLevel()) + "%");


            final int per = binlist.get(i).getLevel();

            if (per > 0 && per <= 30) {
                holder.highlight.setBackgroundResource(R.drawable.valbox_green);//Any color you want to set
                ViewGroup.LayoutParams layoutParams = holder.highlight.getLayoutParams();
                layoutParams.width = (int) (dpToPixel(100, holder.context) * per / 100.0f);//If you want to set 25%
                holder.highlight.setLayoutParams(layoutParams);
            } else if (per > 30 && per <= 70) {
                holder.highlight.setBackgroundResource(R.drawable.valbox_yellow);//Any color you want to set
                ViewGroup.LayoutParams layoutParams = holder.highlight.getLayoutParams();
                layoutParams.width = (int) (dpToPixel(100, holder.context) * per / 100.0f);//If you want to set 25%
                holder.highlight.setLayoutParams(layoutParams);
            } else if (per > 70) {
                holder.highlight.setBackgroundResource(R.drawable.valbox_red);//Any color you want to set
                ViewGroup.LayoutParams layoutParams = holder.highlight.getLayoutParams();
                layoutParams.width = (int) (dpToPixel(100, holder.context) * per / 100.0f);//If you want to set 25%
                holder.highlight.setLayoutParams(layoutParams);

                //Notification
                //might be needed
                /*AlarmManager alarmManager = (AlarmManager) holder.context.getSystemService(Context.ALARM_SERVICE);
                Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                notificationIntent.addCategory("android.intent.category.DEFAULT");
                PendingIntent broadcast = PendingIntent.getBroadcast(holder.context, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, 5);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);*/
            } else {
                holder.highlight.setBackgroundResource(R.drawable.valbox_percent);//Any color you want to set
                ViewGroup.LayoutParams layoutParams = holder.highlight.getLayoutParams();
                layoutParams.width = (int) (dpToPixel(100, holder.context) * per / 100.0f);//If you want to set 25%
                holder.highlight.setLayoutParams(layoutParams);
            }

        } else {
            holder.people.setText(null);
            holder.people.setBackgroundResource(0);
            holder.level.setText(null);
            holder.level.setBackgroundResource(0);

            ImageView i = new ImageView(holder.context);
            i.setBackgroundResource(R.drawable.info);
            Bitmap theBitmap = BitmapFactory.decodeFile(i.toString());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(50, 50);
            holder.frame.setLayoutParams(lp);
            i.setImageBitmap(theBitmap);

            i.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Toast.makeText(v.getContext(),info.getBinName().substring(0, 1).toUpperCase() + info.getBinName().substring(1) + " is a Non-technical bin. No data is available for Bin count and Bin level. ", Toast.LENGTH_LONG).show();
                }
            });

            holder.frame.addView(i);


        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement onClick


                final Dialog dialog = new Dialog(v.getContext());

                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bindialog);


                int width = (int) (v.getContext().getResources().getDisplayMetrics().widthPixels * 0.95);
                int height = (int) (v.getContext().getResources().getDisplayMetrics().heightPixels);

                dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);

                TextView binID_dialog = (TextView) dialog.findViewById(R.id.bin_id_dialog);
                TextView bin_location_dialog = (TextView) dialog.findViewById(R.id.bin_location_dialog);
                TextView bin_address_dialog = (TextView) dialog.findViewById(R.id.bin_address_dialog);
                TextView people = (TextView) dialog.findViewById(R.id.bin_people_dialog);
                TextView level = (TextView) dialog.findViewById(R.id.bin_level_dialog);
                ImageView level_bin = (ImageView) dialog.findViewById(R.id.level_bin_image);

                binID_dialog.setText(info.getBinName().substring(0, 1).toUpperCase() + info.getBinName().substring(1));
                bin_location_dialog.setText(location.substring(0, 1).toUpperCase() + location.substring(1));
                //capitalize the first letter of the string
                String address = info.getAddress();
                bin_address_dialog.setText(address.substring(0, 1).toUpperCase() + address.substring(1));
                //System.out.println(address.substring(0,1).toUpperCase() + address.substring(1));

                if (tempbincode.contains(info.getBinCode())) {

                    int j = tempbincode.indexOf((info.getBinCode()));

                    people.setText(Integer.toString(binlist.get(j).getPeople()));
                    level.setText(Integer.toString(binlist.get(j).getLevel()) + "%");

                    int per = binlist.get(j).getLevel();
                    if (per >= 0 && per <= 30) {
                        level_bin.setBackgroundResource(R.drawable.greenbin);
                    } else if (per > 30 && per <= 70) {
                        level_bin.setBackgroundResource(R.drawable.yellowbin);
                    } else {
                        level_bin.setBackgroundResource(R.drawable.redbin);
                    }

                } else {
                    people.setText("NA");
                    level.setText("NA");
                    level_bin.setBackgroundResource(R.drawable.logo);
                }


                ImageView dialogclose = (ImageView) dialog.findViewById(R.id.dialogclose);


                dialogclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button show_in_map = (Button) dialog.findViewById(R.id.show_in_map);
                show_in_map.setAllCaps(false);

                show_in_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //System.out.println(info.getBinName()+" HUIHUIHUI1 "+info.getLatitude() +" HUIHUIHUIHUIHUI "+info.getLongitude());
                            Intent intent = new Intent(v.getContext(), MapsActivity.class);
                            Bundle value = new Bundle();
                            value.putDouble("latitude",info.getLatitude() );
                            value.putDouble("longitude",info.getLongitude() );

                            intent.putExtras(value);


                            v.getContext().startActivity(intent);



                    }
                });

                dialog.show();

            }
        });

    }

    public int dpToPixel(float dp, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    @Override
    public int getItemCount() {
        return locationlist1.size();
    }


}

