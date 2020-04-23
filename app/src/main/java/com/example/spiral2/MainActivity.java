package com.example.spiral2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageButton test, photo;
    private FirebaseFirestore mfirestore;
    private int check;
    private static final String TAG = "start";
    private ArrayList<String> uidlist = new ArrayList<String>();
    private ArrayList<String> scorelist = new ArrayList<String>();
    private ArrayList<String> labellist = new ArrayList<String>();
    public int RESULT_LOAD_IMG = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mfirestore=FirebaseFirestore.getInstance();
        check=0;
        initializeViews();
        //hideBar();

        //go to test mode
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    photolibrary();
            }
        });
    }

    public void hideBar(){
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }

    private void photolibrary(){
        if(labellist.size()<=1){
        mfirestore.collection("images")
                .orderBy("score", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {

                                                   for (QueryDocumentSnapshot document : task.getResult()) {
                                                       check=check+1;
                                                       String label=document.getString("name");
                                                       String score = document.getString("score");
                                                       String uid=document.getId();
                                                       uidlist.add(uid);
                                                       labellist.add(label);
                                                       scorelist.add(score);

                                                   }
                                                   Intent intent = new Intent(MainActivity.this, PhotoLibraryActivity.class);
                                                   intent.putStringArrayListExtra("labellist",labellist);
                                                   intent.putStringArrayListExtra("uidlist",uidlist);
                                                   intent.putStringArrayListExtra("scorelist",scorelist);
                                                   intent.putExtra("check",check);
                                                   startActivity(intent);

                                               } else {
                                                   Log.d(TAG, "Error getting documents: ", task.getException());
                                               }
                                           }
                                       }

                );}
        else{
            Intent intent = new Intent(MainActivity.this, PhotoLibraryActivity.class);
            intent.putStringArrayListExtra("labellist",labellist);
            intent.putStringArrayListExtra("uidlist",uidlist);
            intent.putStringArrayListExtra("scorelist",scorelist);
            intent.putExtra("check",check);
            startActivity(intent);
        }

    }

    private void initializeViews() {
        test = findViewById(R.id.test);
        photo=findViewById(R.id.photo);
    }

    public void uploadOnClick(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        if (reqCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            Intent i = new Intent(this, Display.class);
            i.putExtra("imageUri", imageUri.toString());
            startActivity(i);
            //finish();
        }
    }
}
