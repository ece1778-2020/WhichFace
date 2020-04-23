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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class TestUnitActivity  extends AppCompatActivity {
    private ImageView View1;
    private EditText name;
    private Button submit;
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
    private ArrayList<String> guesslist = new ArrayList<String>();
    private int position1;
    private int position;
    private int number;
    private String label;
    int confuse=0;
    int flag=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_testunit);
        storage = FirebaseStorage.getInstance();
        sReference=storage.getReference();
        mfirestore=FirebaseFirestore.getInstance();
        initializeView();

        Intent i=getIntent();
        uidlist=i.getStringArrayListExtra("uidlist");
        scorelist=i.getStringArrayListExtra("scorelist");
        countlist=i.getStringArrayListExtra("countlist");
        labellist=i.getStringArrayListExtra("labellist");
        resultlist=i.getStringArrayListExtra("resultlist");
        guesslist=i.getStringArrayListExtra("guesslist");

        position=i.getIntExtra("position",0);
        position1=position;
        number=i.getIntExtra("number",0);

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
                                                                     View1.setImageBitmap(graph);
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


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submission();
            }
        });
    }


    private void submission()
    {
        if(position<=number){
            Log.d(TAG, uidlist.get(position)+"---"+scorelist.get(position));

            label=name.getText().toString();
            if(label.equals(labellist.get(position))){
                resultlist.add("yes");
                int score = Integer.parseInt(scorelist.get(position));
                int count = Integer.parseInt(countlist.get(position));
                score=score*count+100;
                count=count+1;
                score=score/count;
                mfirestore.collection("images").document(uidlist.get(position)).update("score",Integer.toString(score));
                mfirestore.collection("images").document(uidlist.get(position)).update("count",Integer.toString(count));
            }else{
                resultlist.add("no");
                int score = Integer.parseInt(scorelist.get(position));
                int count = Integer.parseInt(countlist.get(position));


                DocumentReference ref=mfirestore.collection("images").document(uidlist.get(position1));
                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                int guess=Integer.parseInt(guesslist.get(position1));
                                while(confuse<guess){
                                    String che=Integer.toString(confuse);
                                    String realname="name"+che;
                                    String checkname=document.getString("name0");
                                    Log.d(TAG, Integer.toString(guess)+": " + checkname + ": name"+che);

                                    if(label.equals(checkname)){
                                        int namecount= Integer.parseInt(document.getString("numbername"+che));
                                        namecount=namecount+1;
                                        flag=1;
                                        mfirestore.collection("images").document(uidlist.get(position1)).update("numbername"+che,Integer.toString(namecount));

                                    }
                                    confuse=confuse+1;
                                }

                                if(flag==-1){
                                    mfirestore.collection("images").document(uidlist.get(position1)).update("name"+Integer.toString(guess),label);
                                    mfirestore.collection("images").document(uidlist.get(position1)).update("numbername"+Integer.toString(guess),"1");

                                    guess=guess+1;
                                    mfirestore.collection("images").document(uidlist.get(position1)).update("guess",Integer.toString(guess));
                                }



                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

                score=score*count;
                count=count+1;
                score=score/count;
                mfirestore.collection("images").document(uidlist.get(position)).update("score",Integer.toString(score));
                mfirestore.collection("images").document(uidlist.get(position)).update("count",Integer.toString(count));
            }
            position=position+1;
            Log.d(TAG, Integer.toString(position)+"-check-"+Integer.toString(number));

            if(position > number)
            {
                Log.d(TAG, Integer.toString(position)+"-check2-"+Integer.toString(number));

                number=number+1;
                Intent intent=new Intent(TestUnitActivity.this,ResultActivity.class);
                intent.putStringArrayListExtra("uidlist",uidlist);
                intent.putStringArrayListExtra("labellist",labellist);
                intent.putStringArrayListExtra("resultlist",resultlist);
                intent.putStringArrayListExtra("guesslist",guesslist);

                intent.putExtra("number",number);
                startActivity(intent);
            }
           else{
            Intent intent=new Intent(TestUnitActivity.this,TestUnitActivity.class);
            intent.putStringArrayListExtra("uidlist",uidlist);
            intent.putStringArrayListExtra("scorelist",scorelist);
            intent.putStringArrayListExtra("countlist",countlist);
            intent.putStringArrayListExtra("labellist",labellist);
            intent.putStringArrayListExtra("resultlist",resultlist);
            intent.putStringArrayListExtra("guesslist",guesslist);

                intent.putExtra("position",position);
            intent.putExtra("number",number);
            startActivity(intent);}
        }else{
            number=number+1;
            Intent intent=new Intent(TestUnitActivity.this,ResultActivity.class);
            intent.putStringArrayListExtra("uidlist",uidlist);
            intent.putStringArrayListExtra("labellist",labellist);
            intent.putStringArrayListExtra("resultlist",resultlist);
            intent.putStringArrayListExtra("guesslist",guesslist);

            intent.putExtra("number",number);
            startActivity(intent);
        }



    }

    private void initializeView() {
        View1=(ImageView)findViewById(R.id.testimage);
        submit=findViewById(R.id.submit);
        name = findViewById(R.id.label);
        progressBar = findViewById(R.id.progressBar);
    }
}

