package com.example.spiral2;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
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

public class PhotoLibraryActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mfirestore;
    private FirebaseStorage storage;
    private StorageReference sReference;
    private Button back;
    private String path;
    private String text;
    private int number;
    private int i;
    private static final String TAG = "result";
    private Bitmap graph;

    private ArrayList<Bitmap> rface = new ArrayList<Bitmap>();
    private ArrayList<Bitmap> mface = new ArrayList<Bitmap>();
    private ArrayList<String> scorelist = new ArrayList<String>();
    private ArrayList<String> uidlist = new ArrayList<String>();
    private ArrayList<String> labellist = new ArrayList<String>();

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photolibrary);
        i = 0;
        path = null;
        graph = null;
        mAuth = FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        sReference = storage.getReference();
        initializeView();
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        Intent intent = getIntent();
        uidlist = intent.getStringArrayListExtra("uidlist");
        labellist = intent.getStringArrayListExtra("labellist");
        scorelist = intent.getStringArrayListExtra("scorelist");
        progressDialog = new ProgressDialog(PhotoLibraryActivity.this);
        progressDialog.setMessage("loading photos");
        progressDialog.setCancelable(false); // 加载完成消失
        progressDialog.show();
        display();
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

    public void backToMain(){
        onBackPressed();
        finish();
    }


    private void display() {
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
                                                                         Custom2Adapter customAdapter = new Custom2Adapter(PhotoLibraryActivity.this, rface, labellist, uidlist, scorelist);
                                                                         recyclerView.setAdapter(customAdapter);
                                                                         progressDialog.cancel();
                                                                     } else {
                                                                         display();
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
        back = findViewById(R.id.back);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }


}
