package com.example.spiral2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class TestActivity  extends AppCompatActivity {
    private EditText testnumber;
    private Button test;
    private ProgressBar progressBar;
    private FirebaseFirestore mfirestore;
    private static final String TAG = "test choose";
    private ArrayList<String> uidlist = new ArrayList<String>();
    private ArrayList<String> scorelist = new ArrayList<String>();
    private ArrayList<String> countlist = new ArrayList<String>();
    private ArrayList<String> labellist = new ArrayList<String>();
    private ArrayList<String> resultlist = new ArrayList<String>();
    private ArrayList<String> guesslist = new ArrayList<String>();



    private int check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_test);
        initializeView();
        mfirestore=FirebaseFirestore.getInstance();

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageselector();
            }
        });
    }
    private void imageselector() {
       final int number = Integer.parseInt(testnumber.getText().toString());
        check = 0;
        mfirestore.collection("images")
                .orderBy("score", Query.Direction.ASCENDING)
                .limit(number)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {

                                                   for (QueryDocumentSnapshot document : task.getResult()) {
                                                       check=check+1;
                                                       String count = document.getString("count");
                                                       String label=document.getString("name");
                                                       String score = document.getString("score");
                                                       String guess=document.getString("guess");
                                                       String uid=document.getId();
                                                       uidlist.add(uid);
                                                       labellist.add(label);
                                                       scorelist.add(score);
                                                       countlist.add(count);
                                                       guesslist.add(guess);



                                                       Log.d(TAG, uid + " => " + label+" score: "+score+" count:"+count);

                                                       if(check==number){
                                                           Intent intent=new Intent(TestActivity.this,TestUnitActivity.class);
                                                           intent.putStringArrayListExtra("uidlist",uidlist);
                                                           intent.putStringArrayListExtra("scorelist",scorelist);
                                                           intent.putStringArrayListExtra("countlist",countlist);
                                                           intent.putStringArrayListExtra("labellist",labellist);
                                                           intent.putStringArrayListExtra("resultlist",resultlist);
                                                           intent.putStringArrayListExtra("guesslist",guesslist);
                                                           check=check-1;
                                                           intent.putExtra("position",0);
                                                           intent.putExtra("number",check);
                                                           startActivity(intent);
                                                       }







                                                   }



                                               } else {
                                                   Log.d(TAG, "Error getting documents: ", task.getException());
                                               }
                                           }
                                       }

                );


    }



    private void initializeView() {

        test=findViewById(R.id.test);
        testnumber=findViewById(R.id.amount);
        progressBar = findViewById(R.id.progressBar);
    }
}

