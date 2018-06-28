package com.cloudwalk.joseemmanuel.cerelacphotolive.feature;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.String.format;

public class MyCameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraDemo";
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            cameraIntent();
        }

        Log.d(TAG, "onCreate'd");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                cameraIntent();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void cameraIntent() {
        final CameraPreview cameraPreview = new CameraPreview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(cameraPreview);

        Button buttonClick = (Button) findViewById(R.id.buttonClick);
        buttonClick.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                cameraPreview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };

    /** Handles data for raw picture */
    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
//            if(!data === null) {
//                stream = getContentResolver().openInputStream(data.getData());
//                bitmap = BitmapFactory.decodeStream(stream);
//
//                imageView.setImageBitmap(bitmap);
//            }
            Log.d(TAG, "onPictureTaken - raw");
        }
    };

    /** Handles data for jpeg picture */
    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        Context context;
        public void onPictureTaken(byte[] byteArray, Camera camera) {
            Bitmap photoBm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            saveToInternalStorage(photoBm);

            Intent getExtras = getIntent();
            String name = getExtras.getStringExtra("InputName");

            Intent newIntent = new Intent(getApplicationContext(), PrintActivity.class);
            newIntent.putExtra("InputName", name);
            startActivity(newIntent);

            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

}
