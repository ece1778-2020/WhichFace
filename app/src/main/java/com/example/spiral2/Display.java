package com.example.spiral2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Display extends AppCompatActivity {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Boolean rawImageSuccess = false;
    Boolean cutFaceSuccess = false;
    Boolean nameSuccess = false;

    ProgressDialog submitProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Intent intent = getIntent();
        ImageView image = (ImageView)findViewById(R.id.originImage);
        Uri myUri = Uri.parse(intent.getExtras().getString("imageUri"));

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(myUri));
            image.setImageBitmap(bitmap);

            cutFace(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

    public void cutFace(final Bitmap bitmap){
        final ProgressDialog cuttingProgress = showProgress("cutting face");
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
                                            Path path=new Path();
                                            path.moveTo(faceContour.get(0).getX(),faceContour.get(0).getY());
                                            for(int i=1;i<faceContour.size();i++){
                                                Log.d("facex",String.valueOf(faceContour.get(i).getX()));
                                                Log.d("facey",String.valueOf(faceContour.get(i).getY()));
                                                path.lineTo(faceContour.get(i).getX(),faceContour.get(i).getY());
                                            }
                                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
                                            //canvas.drawPath(path, paint);
                                            canvas.clipPath(path, Region.Op.INTERSECT);
                                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
                                            canvas.drawBitmap(bitmap, 0, 0, paint);

                                            ImageView cover = (ImageView) findViewById(R.id.cutFace);
                                            cuttingProgress.cancel();
                                            cover.setImageBitmap(output);
                                            showToast("face cut");
                                            // If classification was enabled:
                                            if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                float smileProb = face.getSmilingProbability();
                                                Log.d("smileProp",String.valueOf(smileProb));
                                                //file:///storage/emulated/0/Download/th.jpeg
                                            }
                                            if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                            }

                                            // If face tracking was enabled:
                                            if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                                int id = face.getTrackingId();
                                                Log.d("fid",String.valueOf(id));
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
                                        showToast("Fail to cut face");
                                    }
                                });

    }

    public void onSubmit(View view){

        EditText name = (EditText)findViewById(R.id.name);

        ImageView originImage = (ImageView)findViewById(R.id.originImage);
        Bitmap originBitmap = ((BitmapDrawable)originImage.getDrawable()).getBitmap();

        ImageView cutFace = (ImageView)findViewById(R.id.cutFace);
        Bitmap cutFaceBitmap = ((BitmapDrawable)cutFace.getDrawable()).getBitmap();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String id = Long.toString(timestamp.getTime());
        submitProgress = showProgress("submitting images");
        upload(originBitmap,id,true);
        upload(cutFaceBitmap,id,false);
        writeDatabase(id,name.getText().toString(),"0","0");
    }

    public void upload(Bitmap bitmap, String id, final Boolean isRawImage){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String filePath;
        if(isRawImage){
            filePath = "raw_images/".concat(id).concat(".jpg");
        }else {
            filePath = "face/".concat(id).concat(".jpg");
        }

        StorageReference imagesRef = storageRef.child(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                showToast("uploading image failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //showToast("image uploaded");
                if(isRawImage){
                    rawImageSuccess = true;
                }else{
                    cutFaceSuccess = true;
                }
                checkSuccess();
            }
        });
    }

    public void writeDatabase(String id,String name,String count,String score){
        Map<String, Object> image = new HashMap<>();
        CheckBox rglassCheckbox =(CheckBox)findViewById(R.id.rglassCheckbox);
        Boolean rglassChecked = rglassCheckbox.isChecked();
        String glass;
        if(rglassChecked){
            glass = "1";
        }else{
            glass = "0";
        }
        image.put("name", name);
        image.put("count",count);
        image.put("score",score);
        String check="0";
        image.put("glass",glass);
        image.put("check",check);
        db.collection("images").document(id)
                .set(image)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("database", "database successfully written!");
                        nameSuccess = true;
                        checkSuccess();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("database", "fail to write the database");
                    }
                });

    }

    public void showToast(String msg){
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void checkSuccess(){
        if(cutFaceSuccess&&rawImageSuccess&&nameSuccess){
            showToast("upload succeed");
            Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(myIntent);
            submitProgress.cancel();
            finish();
        }
    }

    public ProgressDialog showProgress(String msg)
    {
        ProgressDialog progressDialog = new ProgressDialog(Display.this);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false); // 加载完成消失
        progressDialog.show();
        return progressDialog;
        //progressDialog.cancel(); to cancel the dialog
    }



}