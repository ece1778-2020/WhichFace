package com.example.spiral2;

import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;

public class CustomAdapter extends RecyclerView.Adapter <CustomAdapter.MyViewHolder>{

    ArrayList <Bitmap> mface;
    ArrayList <Bitmap> rface;
    ArrayList <String> result;
    ArrayList <String> label;
    Context context;

    public CustomAdapter(Context context,  ArrayList <Bitmap> mface,  ArrayList <Bitmap> rface,ArrayList <String> result, ArrayList <String> label) {
        this.context = context;
        this.mface = mface;
        this.rface=rface;
        this.result=result;
        this.label=label;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // set the data in items
        Log.d("rface", String.valueOf(rface.size()));
        holder.rimage.setImageBitmap(rface.get(position));
        holder.mimage.setImageBitmap(mface.get(position));
        if(result.get(position).equals("yes")){
            holder.text.setText(Integer.toString(position+1)+". Congratualtions the result is correct for this image");
            holder.text.setTextColor(Color.GREEN);
        }else{
            holder.text.setText(Integer.toString(position+1)+". Sorry your answer is wrong the real name is "+label.get(position));
            holder.text.setTextColor(Color.RED);
        }

    }
    @Override
    public int getItemCount() {
        return mface.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's

        ImageView rimage;
        ImageView mimage;
        TextView text;

        String imagecontent;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            rimage = (ImageView) itemView.findViewById(R.id.rface);
            mimage = (ImageView) itemView.findViewById(R.id.mface);
            text=(TextView) itemView.findViewById(R.id.comment);

        }
    }
}
