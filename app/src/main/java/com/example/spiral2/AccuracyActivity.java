package com.example.spiral2;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.evrencoskun.tableview.TableView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccuracyActivity extends AppCompatActivity {
    private ImageView rface, mface;
    private TextView name, accuracy;

    private Button back;
    private ProgressBar progressBar;
    private Bitmap graph1;
    private Bitmap graph2;
    private FirebaseFirestore mfirestore;
    private FirebaseStorage storage;
    private static final String TAG = "unitt";

    private StorageReference sReference;
    private ArrayList<String> uidlist = new ArrayList<String>();
    private ArrayList<String> scorelist = new ArrayList<String>();
    private ArrayList<String> countlist = new ArrayList<String>();
    private ArrayList<String> labellist = new ArrayList<String>();
    private ArrayList<String> clabellist = new ArrayList<String>();
    private ArrayList<String> cscorelist = new ArrayList<String>();
    private ArrayList<String> resultlist = new ArrayList<String>();
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private int position;
    private int number;
    private AccuracyActivity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accuracy);
        storage = FirebaseStorage.getInstance();
        sReference = storage.getReference();
        mfirestore = FirebaseFirestore.getInstance();
        initializeView();
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        Intent i = getIntent();
        uidlist = i.getStringArrayListExtra("uidlist");
        scorelist = i.getStringArrayListExtra("scorelist");
        labellist = i.getStringArrayListExtra("labellist");
        position = i.getIntExtra("position", 0);



        Log.d(TAG, Integer.toString(number));
        //show test image
        String path = "face/" + uidlist.get(position) + ".jpg";
        Log.d(TAG, path);
        StorageReference photoRef = sReference.child(path);
        final long ONE_MEGABYTE = 1024 * 1024;
        photoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                                 @Override
                                                                 public void onSuccess(byte[] bytes) {
                                                                     // Data for "images/island.jpg" is returns, use this as needed
                                                                     graph1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                                     final DocumentReference ref = mfirestore.collection("images").document(uidlist.get(position));
                                                                     ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                         @Override
                                                                         public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                             if (task.isSuccessful()) {
                                                                                 DocumentSnapshot document = task.getResult();
                                                                                 String check = document.getString("check");
                                                                                 if (check.equals("1")) {
                                                                                     ref.update("check", "0");
                                                                                     cutFace(graph1);

                                                                                 } else {
                                                                                     mface.setImageBitmap(graph1);

                                                                                 }

                                                                             } else {
                                                                                 Log.d(TAG, "get failed with ", task.getException());
                                                                             }
                                                                         }
                                                                     });

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

        path = "raw_images/" + uidlist.get(position) + ".jpg";
        Log.d(TAG, path);
        photoRef = sReference.child(path);
        photoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                                 @Override
                                                                 public void onSuccess(byte[] bytes) {
                                                                     // Data for "images/island.jpg" is returns, use this as needed
                                                                     graph2 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                                     rface.setImageBitmap(graph2);
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
        int score = Integer.parseInt(scorelist.get(position));
        accuracy.setText("Current Accuracy: " + scorelist.get(position) + "%");

        if (score < 31) {
            accuracy.setTextColor(Color.RED);
        } else if (score < 61) {
            accuracy.setTextColor(Color.BLUE);
        } else {
            accuracy.setTextColor(Color.GREEN);
        }

        mfirestore.collection("images").document(uidlist.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    int total = Integer.parseInt(scorelist.get(position));
                    int count = Integer.parseInt(document.getString("count"));
                    total = (count * (100 - total)) / 100;
                    int guess = Integer.parseInt(document.getString("guess"));
                    while (guess > 0) {
                        guess = guess - 1;
                        int cscore = Integer.parseInt(document.getString("numbername" + Integer.toString(guess)));
                        cscore = (cscore * 100) / total;
                        if (cscore == 0)
                            continue;
                        cscorelist.add(Integer.toString(cscore));
                        String name = document.getString("name" + Integer.toString(guess));
                        clabellist.add(name);
                    }
                    //Custom3Adapter customAdapter = new Custom3Adapter(AccuracyActivity.this, clabellist, cscorelist, labellist, uidlist, scorelist);
                    //recyclerView.setAdapter(customAdapter);
                    setTable();

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void setTable() {

        List<RowHeader> mRowHeaderList  = new ArrayList<RowHeader>();
        List<ColumnHeader> mColumnHeaderList = new ArrayList<ColumnHeader>();
        List<List<Cell>> mCellList=new ArrayList<List<Cell>>();

        mColumnHeaderList.add(new ColumnHeader("wrong answer"));
        mColumnHeaderList.add(new ColumnHeader("confusion rate"));

        mRowHeaderList.add(new RowHeader("row1"));
        mRowHeaderList.add(new RowHeader("row2"));

        for(int i = 0;i<clabellist.size();i++){
            List<Cell> cellList = new ArrayList<Cell>();
            cellList.add(new Cell(clabellist.get(i)));
            cellList.add(new Cell(cscorelist.get(i)));
            mCellList.add(cellList);
        }



        TableView tableView = this.findViewById(R.id.accuracy_list);

        // Create our custom TableView Adapter
        AccuracyConfuseAdapter adapter = new AccuracyConfuseAdapter(this,labellist,position,uidlist,scorelist);

        // Set this adapter to the our TableView
        tableView.setAdapter(adapter);

        // Let's set datas of the TableView on the Adapter
        adapter.setAllItems(mColumnHeaderList, mRowHeaderList, mCellList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void back() {
        /*
        Intent intent = new Intent(AccuracyActivity.this, PhotoLibraryActivity.class);
        intent.putStringArrayListExtra("labellist",labellist);
        intent.putStringArrayListExtra("uidlist",uidlist);
        intent.putStringArrayListExtra("scorelist",scorelist);
        startActivity(intent);
        */
        onBackPressed();

    }

    private void initializeView() {
        rface = (ImageView) findViewById(R.id.rface);
        mface = (ImageView) findViewById(R.id.mface);

        name = (TextView) findViewById(R.id.label);
        accuracy = (TextView) findViewById(R.id.score);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

    }

    public void cutFace(final Bitmap bitmap) {
        FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .build();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(highAccuracyOpts);


        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        // Task completed successfully
                                        // ...
                                        for (FirebaseVisionFace face : faces) {
                                            Rect bounds = face.getBoundingBox();
                                            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                            // nose available):
                                            FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                                            if (leftEar != null) {
                                                FirebaseVisionPoint leftEarPos = leftEar.getPosition();
                                            }

                                            // If contour detection was enabled:
                                            List<FirebaseVisionPoint> leftEyeContour =
                                                    face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                                            List<FirebaseVisionPoint> upperLipBottomContour =
                                                    face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();
                                            List<FirebaseVisionPoint> faceContour =
                                                    face.getContour(FirebaseVisionFaceContour.FACE).getPoints();


                                            //Bitmap src = BitmapFactory.decodeResource(getResources(), cropDrawable);
                                            Bitmap output =
                                                    Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                                            Canvas canvas = new Canvas(output);
                                            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                                            paint.setColor(Color.RED);
                                            Path path = new Path();
                                            path.moveTo(faceContour.get(0).getX(), faceContour.get(0).getY());
                                            for (int i = 1; i < faceContour.size(); i++) {
                                                Log.d("facex", String.valueOf(faceContour.get(i).getX()));
                                                Log.d("facey", String.valueOf(faceContour.get(i).getY()));
                                                path.lineTo(faceContour.get(i).getX(), faceContour.get(i).getY());
                                            }
                                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
                                            //canvas.drawPath(path, paint);
                                            canvas.clipPath(path, Region.Op.INTERSECT);
                                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
                                            canvas.drawBitmap(bitmap, 0, 0, paint);

                                            mface.setImageBitmap(output);

                                            String filepath = "face/" + uidlist.get(position) + ".jpg";
                                            StorageReference imagesRef = sReference.child(filepath);
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            output.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            byte[] data = baos.toByteArray();
                                            UploadTask uploadTask = imagesRef.putBytes(data);
                                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Handle unsuccessful uploads
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    //showToast("image uploaded");
                                                }
                                            });
                                            // If classification was enabled:
                                            if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                float smileProb = face.getSmilingProbability();
                                                Log.d("smileProp", String.valueOf(smileProb));
                                                //file:///storage/emulated/0/Download/th.jpeg
                                            }
                                            if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                            }

                                            // If face tracking was enabled:
                                            if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                                int id = face.getTrackingId();
                                                Log.d("fid", String.valueOf(id));
                                            }
                                        }

                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        e.printStackTrace();
                                    }
                                });

    }

}