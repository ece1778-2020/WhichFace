package com.example.spiral2;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
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

public class Custom3Adapter extends RecyclerView.Adapter <Custom3Adapter.MyViewHolder>{


    ArrayList <String> label;
    ArrayList <String> score;
    ArrayList <String> labellist;
    ArrayList <String> uidlist;
    ArrayList <String> scorelist;


    Context context;

    public Custom3Adapter(Context context, ArrayList <String> label,ArrayList <String> score,ArrayList <String> labellist,ArrayList <String> uidlist,ArrayList <String> scorelist ) {
        this.context = context;
        this.label=label;
        this.score=score;
        this.labellist=labellist;
        this.uidlist=uidlist;
        this.scorelist=scorelist;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row3layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder( MyViewHolder holder, final int position) {
        // set the data in items

        holder.confuse.setText(label.get(position)+":            "+score.get(position)+"% confussion rate");
        int i=0;
        while(i<labellist.size()){
            if(label.get(position).equals(labellist.get(i))){
                holder.confuse.setTextColor(Color.BLUE);
                final int check=i;
                holder.confuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, AccuracyActivity.class);
                        intent.putExtra("uidlist",uidlist);
                        intent.putExtra("labellist",labellist);
                        intent.putExtra("scorelist",scorelist);
                        intent.putExtra("position",check);
                        context.startActivity(intent);
                    }
                });
                break;
            }
            else{
                i=i+1;
            }
        }
    }
    @Override
    public int getItemCount() {
        return label.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's

        TextView confuse;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            confuse= (TextView) itemView.findViewById(R.id.confuse);
        }
    }
}
