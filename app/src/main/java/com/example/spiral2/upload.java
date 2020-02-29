package com.example.spiral2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class upload extends AppCompatActivity {
    public int RESULT_LOAD_IMG = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
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
            ImageView image = (ImageView)findViewById(R.id.imageView);
            Bitmap bitmap= null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                Intent i = new Intent(this, Display.class);
                i.putExtra("bitmap", bitmap);
                startActivity(i);
                finish();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
