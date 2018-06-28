package com.cloudwalk.joseemmanuel.cerelacphotolive.feature;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.String.format;

public class PrintActivity extends AppCompatActivity {
    private static final String TAG = "PrintActivity";
    Bitmap photo;
    Button button;
    ImageView cImage;
    Intent newIntent;

    private static Bitmap getScaledBitmap(Bitmap bm, int bmOriginalWidth, int bmOriginalHeight, double originalWidthToHeightRatio, double originalHeightToWidthRatio, int maxHeight, int maxWidth) {
        if (bmOriginalWidth > maxWidth || bmOriginalHeight > maxHeight) {
            Log.v(TAG, format("RESIZING bitmap FROM %sx%s ", bmOriginalWidth, bmOriginalHeight));

            if (bmOriginalWidth > bmOriginalHeight) {
                bm = scaleDeminsFromWidth(bm, maxWidth, bmOriginalHeight, originalHeightToWidthRatio);
            } else {
                bm = scaleDeminsFromHeight(bm, maxHeight, bmOriginalHeight, originalWidthToHeightRatio);
            }

            Log.v(TAG, format("RESIZED bitmap TO %sx%s ", bm.getWidth(), bm.getHeight()));
        }
        return bm;
    }

    private static Bitmap scaleDeminsFromHeight(Bitmap bm, int maxHeight, int bmOriginalHeight, double originalWidthToHeightRatio) {
        int newHeight = (int) Math.min(maxHeight, bmOriginalHeight * .55);
        int newWidth = (int) (newHeight * originalWidthToHeightRatio);
        bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return bm;
    }

    private static Bitmap scaleDeminsFromWidth(Bitmap bm, int maxWidth, int bmOriginalWidth, double originalHeightToWidthRatio) {
        //scale the width
        int newWidth = (int) Math.min(maxWidth, bmOriginalWidth);
        int newHeight = (int) (newWidth * originalHeightToWidthRatio);
//        int newWidth = (int) 4 * 250;
//        int newHeight = (int) 3 * 250;
        bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return bm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        photo = ProcessingBitmap();

        saveImage(photo);

        cImage = findViewById(R.id.combinedImageView);
//        int bmOriginalWidth = photo.getWidth();
//        int bmOriginalHeight = photo.getHeight();
//        double originalWidthToHeightRatio = 1.0 * bmOriginalWidth / bmOriginalHeight;
//        double originalHeightToWidthRatio = 1.0 * bmOriginalHeight / bmOriginalWidth;
//        photo = getScaledBitmap(photo, bmOriginalWidth, bmOriginalHeight, originalWidthToHeightRatio, originalHeightToWidthRatio);
        cImage.setImageBitmap(photo);

        button = findViewById(R.id.home);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(newIntent);
                finish();
            }
        });

    }

    public Bitmap ProcessingBitmap() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        Bitmap bm1 = null;
        Bitmap newBitmap = null;
        try {
//            Toast.makeText(MainActivity.this, pickedImage.getPath(), Toast.LENGTH_LONG).show();
            InputStream instream = getAssets().open("template.png");
            bm1 = BitmapFactory.decodeStream(instream);
            Bitmap.Config config = bm1.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }
            newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);
            Canvas canvas = new Canvas(newBitmap);

            Bundle extras = getIntent().getExtras();
            String captionString = extras.getString("InputName") + '!';

            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f = new File(directory, "profile.jpg");
            Bitmap photoBm = BitmapFactory.decodeStream(new FileInputStream(f));
            int bmOriginalWidth = photoBm.getWidth();
            int bmOriginalHeight = photoBm.getHeight();
            double originalWidthToHeightRatio = 1.0 * bmOriginalWidth / bmOriginalHeight;
            double originalHeightToWidthRatio = 1.0 * bmOriginalHeight / bmOriginalWidth;
            //choose a maximum height
            int maxHeight = 1024;
            //choose a max width
            int maxWidth = 1110;

            photoBm = getScaledBitmap(photoBm, bmOriginalWidth, bmOriginalHeight,
                    originalWidthToHeightRatio, originalHeightToWidthRatio,
                    maxHeight, maxWidth);

            canvas.drawBitmap(photoBm, 30, 130, null);
            canvas.drawBitmap(bm1, 0, 0, null);

            Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintText.setTextAlign(Paint.Align.CENTER);
            paintText.setColor(Color.WHITE);
            paintText.setTextSize(110);
            paintText.setStyle(Paint.Style.FILL);
            paintText.setShadowLayer(1f, 1f, 1f, Color.BLACK);
            Typeface cocon = ResourcesCompat.getFont(getApplicationContext(), R.font.cocon_bold);
            paintText.setTypeface(cocon);
            Rect textRect = new Rect();
            paintText.getTextBounds(captionString, 0, captionString.length(), textRect);
            if (textRect.width() >= (canvas.getWidth() - 4))
                paintText.setTextSize(7);
            int xPos = 1470;
            int yPos = 800;
            canvas.drawText(captionString, xPos, yPos, paintText);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newBitmap;
    }

    BitmapDrawable flip(BitmapDrawable d) {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap src = d.getBitmap();
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return new BitmapDrawable(dst);
    }

    private void saveImage(Bitmap finalBitmap) {

        File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File myDir = new File(pictureDir, "/cerelac/");
        myDir.mkdirs();
        String fname = "Image-" + System.currentTimeMillis() + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", myDir + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
