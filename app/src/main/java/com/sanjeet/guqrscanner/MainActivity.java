package com.sanjeet.guqrscanner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.Objects;


import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler  {
    private final static int requestcode_camera=100;
    private Button openSettings;
    private SharedPreferences sharedPreferences,sharedPreferences1;
    private ZXingScannerView mScannerView;
    private dialogShow ds=new dialogShow();
    boolean flash=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialize the Scanner Api
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        SharedPreferences prefs = getSharedPreferences("PerCount",MODE_PRIVATE);
        String count = prefs.getString("count", "");

        /*Flash Light Logic by storage*/
        SharedPreferences prefs1 = getSharedPreferences("Flash",MODE_PRIVATE);
        String f1 = prefs1.getString("state", "");

        changeColor(R.color.red);

        if(f1.equals("")){
            sharedPreferences1 = getSharedPreferences("Flash", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
            editor1.putString("state", "false");
            editor1.apply();
        }
        else{
            boolean flashState1=Boolean.parseBoolean(f1);
            mScannerView.setFlash(flashState1);
        }




        //For the Flash Light
//        SharedPreferences prefs1 = getSharedPreferences("Flash",MODE_PRIVATE);
//        String f1 = prefs1.getString("state", "");
//        boolean flashState1=Boolean.parseBoolean(f1);
//        mScannerView.setFlash(flashState1);

        // Toast.makeText(this, count+" Value", Toast.LENGTH_SHORT).show();
        if(count.equals("")) {
            sharedPreferences = getSharedPreferences("PerCount", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("count", "0");
            editor.apply();
        }
        else{
            int counter=Integer.parseInt(count);
           // Toast.makeText(getApplicationContext(), counter+"", Toast.LENGTH_SHORT).show();
            if(counter>=2){
                setPer();
            }
        }

//        openSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                SharedPreferences prefs = getSharedPreferences("PerCount",MODE_PRIVATE);
//                String count = prefs.getString("count", "");
//                Toast.makeText(getApplicationContext(), count, Toast.LENGTH_SHORT).show();
//
//            }
//        });
        checkPermission(Manifest.permission.CAMERA,requestcode_camera);
//        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,101);


    }



    private void setPer() {
        ds.show(getSupportFragmentManager(),"Permission");
    }

    private void checkPermission(String permission,int code){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                permission)
                == PackageManager.PERMISSION_DENIED)
        {
//            int counter=getPerCounter();
//            sharedPreferences = getSharedPreferences("PerCount", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("count", (++counter)+"");
//            editor.apply();

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {permission},code);

//            Toast.makeText(this, "Permission is not granted", Toast.LENGTH_LONG).show();
//            if(ContextCompat.checkSelfPermission(getApplicationContext(),
//                    permission)
//                    != PackageManager.PERMISSION_DENIED){
//                ds.dismiss();
//            }
        }
//        else{
////            sharedPreferences = getSharedPreferences("PerCount", Context.MODE_PRIVATE);
////            SharedPreferences.Editor editor = sharedPreferences.edit();
////            editor.putString("count", "0");
////            editor.apply();
//            //Toast.makeText(this, "Permission is Already Granted", Toast.LENGTH_SHORT).show();
//        }

    }

    int getPerCounter(){
        SharedPreferences prefs = getSharedPreferences("PerCount",MODE_PRIVATE);
        String count = prefs.getString("count", "");
//        Toast.makeText(getApplicationContext(), count, Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), "called", Toast.LENGTH_SHORT).show();
        return Integer.parseInt(count);

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(final Result rawResult) {
        // Do something with the result here
        Log.v("Qr", rawResult.getText()); // Prints scan results
        Log.v("Qr", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
       // Toast.makeText(getApplicationContext(), rawResult.getText()+"", Toast.LENGTH_SHORT).show();
        QrResult qr=new QrResult(rawResult.getText()+"");

        qr.show(getSupportFragmentManager(),"QR");
        getSupportFragmentManager().executePendingTransactions();
        qr.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mScannerView.startCamera();
               // Toast.makeText(getApplicationContext(), "Dismissed", Toast.LENGTH_SHORT).show();
            }
        });

        mScannerView.resumeCameraPreview(this);
        mScannerView.stopCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String share="This Application is developed by Vikas Kumar";
        switch (item.getItemId()) {
            case R.id.QRGenerator:
                Intent intent=new Intent(getApplicationContext(),QrGenerate.class);
                startActivity(intent);
                return true;
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, share+"\n"+"Sharing Link not available.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            case R.id.about:
                Intent intent2=new Intent(getApplicationContext(),About.class);
                startActivity(intent2);
                return true;
            case R.id.flash:
                //SharedPreferences sharedPreferences=
               // Toast.makeText(this, "Flash", Toast.LENGTH_SHORT).show();
                SharedPreferences prefs = getSharedPreferences("Flash",MODE_PRIVATE);
                String f = prefs.getString("state", "");
                boolean flashState=Boolean.parseBoolean(f);
               // Toast.makeText(this, flashState+"", Toast.LENGTH_SHORT).show();
                if (flashState){
                    sharedPreferences1 = getSharedPreferences("Flash", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                    editor1.putString("state", "false");
                    editor1.apply();
                }
                else{
                    sharedPreferences1 = getSharedPreferences("Flash", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                    editor1.putString("state", "true");
                    editor1.apply();
                }
                 prefs = getSharedPreferences("Flash",MODE_PRIVATE);
                String f1 = prefs.getString("state", "");
                boolean flashState1=Boolean.parseBoolean(f1);
                mScannerView.setFlash(flashState1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
    public void changeColor(int resourseColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), resourseColor));
        }
        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(Html.fromHtml("<font color='#ff0000'>GU QR Scanner </font>"));
        //bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(resourseColor)));


    }

}
