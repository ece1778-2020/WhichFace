package com.example.spiral2;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import android.content.Intent;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;

public class Custom2Adapter extends RecyclerView.Adapter <Custom2Adapter.MyViewHolder>{


    ArrayList <Bitmap> face;
    ArrayList <String> label;
    ArrayList <String> uid;
    ArrayList <String> score;
    Context context;

    public Custom2Adapter(Context context, ArrayList <Bitmap> face, ArrayList <String> label,ArrayList <String> uid,ArrayList <String> score) {
        this.context = context;
        this.face=face;
        this.label=label;
        this.uid=uid;
        this.score=score;
    }





    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row2layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder( MyViewHolder holder, final int position) {
        // set the data in items

        holder.image.setImageBitmap(face.get(position));
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // int pos=getAdapterPosition();
                Intent intent = new Intent(context, AccuracyActivity.class);
                intent.putExtra("uidlist",uid);
                intent.putExtra("labellist",label);
                intent.putExtra("scorelist",score);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return uid.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's

        ImageView image;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
             image= (ImageView) itemView.findViewById(R.id.face);
        }
    }
}
