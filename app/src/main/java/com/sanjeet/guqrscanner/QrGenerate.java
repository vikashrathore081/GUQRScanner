package com.sanjeet.guqrscanner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class QrGenerate extends AppCompatActivity {
    private EditText text;
    private Button generate,save;
    private ImageView image;
    private Exception e;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generate);
        text=findViewById(R.id.Text);
        generate=findViewById(R.id.generate);
        save=findViewById(R.id.save);
        image=findViewById(R.id.image);
        changeColor(R.color.bgColor);
        Objects.requireNonNull(getSupportActionBar()).hide();
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        generate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String mytext = text.getText().toString();
                if (mytext.isEmpty()) {
                    Toast.makeText(QrGenerate.this, "Please Enter Text First", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        image.setImageBitmap(new BarcodeEncoder().createBitmap(new MultiFormatWriter().encode(mytext, BarcodeFormat.QR_CODE, 500, 500)));
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(QrGenerate.this, "android.permission.WRITE_EXTERNAL_STORAGE") == -1) {
                    Toast.makeText(QrGenerate.this, "Permission is denied please allow Permissions", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Bitmap img = ((BitmapDrawable) image.getDrawable()).getBitmap();
                        File dir = new File(Environment.getExternalStorageDirectory() + "/QR Codes/");
                        dir.mkdirs();
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(new File(dir, "QR " + new SimpleDateFormat("dd-mm-yyyy  HH-mm-ss").format(new Date()) + ".jpg"));
                            try {
                                img.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                Toast.makeText(QrGenerate.this, "Image Saved succesfully", Toast.LENGTH_SHORT).show();
                                fileOutputStream.flush();
                                fileOutputStream.close();
                            } catch (Exception e) {
                                e = e;
                                FileOutputStream fileOutputStream2 = fileOutputStream;
                            }
                        } catch (Exception e2) {
                            e = e2;
                            e.printStackTrace();

                        }
                    } catch (Exception e3) {
                        e3.printStackTrace();
                        Toast.makeText(QrGenerate.this, "Please Generate QR code First", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            });
        }
    public void changeColor(int resourseColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), resourseColor));
        }

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(resourseColor)));

    }
    }

