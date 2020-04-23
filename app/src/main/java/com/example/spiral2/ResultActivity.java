package com.example.spiral2;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.graphics.Bitmap;

import com.google.firebase.firestore.Query.Direction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.QuerySnapshot;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class ResultActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mfirestore;
    private FirebaseStorage storage;
    private StorageReference sReference;
    private Button back;
    private String path;
    private String text;
    private int number;
    private int i, j;
    private static final String TAG = "finalre";
    private Bitmap graph;

    private ArrayList<Bitmap> rface = new ArrayList<Bitmap>();
    private ArrayList<Bitmap> mface = new ArrayList<Bitmap>();
    private ArrayList<String> resultlist = new ArrayList<String>();
    private ArrayList<String> uidlist = new ArrayList<String>();
    private ArrayList<String> labellist = new ArrayList<String>();

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        i = 0;
        j = 0;
        path = null;
        mAuth = FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        sReference = storage.getReference();
        initializeView();
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        Intent intent = getIntent();
        uidlist = intent.getStringArrayListExtra("uidlist");
        labellist = intent.getStringArrayListExtra("labellist");
        resultlist = intent.getStringArrayListExtra("resultlist");
        number = intent.getIntExtra("number", 0);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
        display1();

        display2();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backToMain();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void backToMain() {
        Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(myIntent);
        finish();
    }

    private void display1() {
        path = "raw_images/" + uidlist.get(rface.size()) + ".jpg";
        Log.d(TAG, path);
        StorageReference photoRef = sReference.child(path);
        final long ONE_MEGABYTE = 1024 * 1024;
        photoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                                 @Override
                                                                 public void onSuccess(byte[] bytes) {
                                                                     // Data for "images/island.jpg" is returns, use this as needed
                                                                     graph = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                                     rface.add(graph);
                                                                     Log.d(TAG, "zheli:" + Integer.toString(i) + "--" + Integer.toString(number));
                                                                     i = i + 1;
                                                                     if (i == uidlist.size()) {


                                                                     } else {
                                                                         display1();
                                                                     }


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
    }

    private void display2() {
        path = "face/" + uidlist.get(mface.size()) + ".jpg";


        StorageReference photoRef = sReference.child(path);
        final long ONE_MEGABYTE = 1024 * 1024;
        photoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                                 @Override
                                                                 public void onSuccess(byte[] bytes) {
                                                                     // Data for "images/island.jpg" is returns, use this as needed
                                                                     graph = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                                     mface.add(graph);
                                                                     Log.d(TAG, "zheli:" + Integer.toString(j) + "--" + Integer.toString(number));
                                                                     j = j + 1;
                                                                     if (j == uidlist.size()) {
                                                                         Log.d(TAG, Integer.toString(rface.size()));
                                                                         Log.d(TAG, Integer.toString(mface.size()));
                                                                         CustomAdapter customAdapter = new CustomAdapter(ResultActivity.this, mface, rface, resultlist, labellist);
                                                                         recyclerView.setAdapter(customAdapter);
                                                                     } else {
                                                                         display2();
                                                                     }


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
    }

    private void initializeView() {


        progressBar = findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }


}
