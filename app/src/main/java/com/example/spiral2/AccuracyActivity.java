package com.example.spiral2;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ProgressBar;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class AccuracyActivity  extends AppCompatActivity {
    private ImageView rface, mface;
    private TextView name, accuracy;

    private Button back;
    private ProgressBar progressBar;
    private Bitmap graph;
    private FirebaseFirestore mfirestore;
    private FirebaseStorage storage;
    private static final String TAG = "unitt";

    private StorageReference sReference;
    private ArrayList<String> uidlist = new ArrayList<String>();
    private ArrayList<String> scorelist = new ArrayList<String>();
    private ArrayList<String> countlist = new ArrayList<String>();
    private ArrayList<String> labellist = new ArrayList<String>();
    private ArrayList<String> resultlist = new ArrayList<String>();

    private int position;
    private int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accuracy);
        storage = FirebaseStorage.getInstance();
        sReference=storage.getReference();
        mfirestore=FirebaseFirestore.getInstance();
        initializeView();

        Intent i=getIntent();
        uidlist=i.getStringArrayListExtra("uidlist");
        scorelist=i.getStringArrayListExtra("scorelist");
        labellist=i.getStringArrayListExtra("labellist");
        position=i.getIntExtra("position",0);

        Log.d(TAG, Integer.toString(number));
        //show test image
        String path="face/"+uidlist.get(position)+".jpg";
        Log.d(TAG, path);
        StorageReference photoRef = sReference.child(path);
        final long ONE_MEGABYTE = 1024 * 1024;
        photoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                                 @Override
                                                                 public void onSuccess(byte[] bytes) {
                                                                     // Data for "images/island.jpg" is returns, use this as needed
                                                                     graph = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                                     mface.setImageBitmap(graph);
                                                                     Log.d(TAG, "zheli");

                                                                     //View2.setImageBitmap(graph);;
                                                                 }
                                                             }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(TAG, "zhe ge qing kuang");
            }
        });

        path="raw_images/"+uidlist.get(position)+".jpg";
        Log.d(TAG, path);
        photoRef = sReference.child(path);
        photoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                                 @Override
                                                                 public void onSuccess(byte[] bytes) {
                                                                     // Data for "images/island.jpg" is returns, use this as needed
                                                                     graph = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                                     rface.setImageBitmap(graph);
                                                                     Log.d(TAG, "zheli");

                                                                     //View2.setImageBitmap(graph);;
                                                                 }
                                                             }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(TAG, "zhe ge qing kuang");
            }
        });

        name.setText(labellist.get(position));
        int score=Integer.parseInt(scorelist.get(position));
        accuracy.setText("Current Accuracy: "+scorelist.get(position)+"%");

        if(score < 31){
            accuracy.setTextColor(Color.RED);
        }else if(score < 61){
            accuracy.setTextColor(Color.BLUE);
        }else{
            accuracy.setTextColor(Color.GREEN);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }


    private void back()
    {
        Intent intent = new Intent(AccuracyActivity.this, PhotoLibraryActivity.class);
        intent.putStringArrayListExtra("labellist",labellist);
        intent.putStringArrayListExtra("uidlist",uidlist);
        intent.putStringArrayListExtra("scorelist",scorelist);
        startActivity(intent);

    }

    private void initializeView() {
        rface=(ImageView)findViewById(R.id.rface);
        mface=(ImageView)findViewById(R.id.mface);

        back=findViewById(R.id.back);
        name = (TextView)findViewById(R.id.label);
        accuracy=(TextView)findViewById(R.id.score);
        progressBar = findViewById(R.id.progressBar);
    }
}

