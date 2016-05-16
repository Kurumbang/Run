package com.example.bishal.fitness;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.List;

/**
 * Created by Bishal on 4/30/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    List<UserData>userData;
    Typeface mytypeface;
    public MyAdapter(Context context, List<UserData>userData) {
        this.context = context;
        this.userData = userData;
        mytypeface = Typeface.createFromAsset(context.getAssets(),"Oswald-Stencil.ttf");
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //populating the list in reverse oder...
        final UserData current = userData.get(getItemCount()- position - 1);
        holder.date.setText(current.getDate());
        holder.distance.setText(current.getDistance());
        YoYo.with(Techniques.FadeIn).playOn(holder.cardView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserHistoryDetail.class);
                intent.putExtra("date", current.getDate());
                intent.putExtra("distance", current.getDistance());
                intent.putExtra("startTime", current.getStartTime());
                intent.putExtra("stopTime", current.getStopTime());
                intent.putExtra("averageSpeed", current.getAverageSpeed());
                intent.putExtra("totalTime", current.getTotalTime());
                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return userData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, distance, kmlabel;
        CardView cardView;
        ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            date = (TextView)itemView.findViewById(R.id.date);
            date.setTypeface(mytypeface);
            distance = (TextView)itemView.findViewById(R.id.distance);
            distance.setTypeface(mytypeface);
            kmlabel = (TextView)itemView.findViewById(R.id.kmLabel);
            kmlabel.setTypeface(mytypeface);
            imageView = (ImageView)itemView.findViewById(R.id.imageViewArrow);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
        }

    }
}
