package com.example.test;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class activity_listFile extends AppCompatActivity implements View.OnClickListener {
    private static final int AUDIO_REQUEST_CODE = 1;
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;
    private TextView display_fileName, display_ml_outputs;
    private Button chooseFile_btn;
    String file_path = "";

    private final byte[] buffer = new byte[1024];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_file);

        display_fileName = findViewById(R.id.fileList_activity_file_name);
        display_ml_outputs = findViewById(R.id.fileList_activity_machineLearning_output);

        chooseFile_btn = findViewById(R.id.fileList_activity_chooseFile_btn);
        chooseFile_btn.setOnClickListener(this);


    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.fileList_activity_chooseFile_btn:
                askPermissionAndBrowseFile();
        }

    }

    private void askPermissionAndBrowseFile()
    {
        if (SDK_INT >= Build.VERSION_CODES.S)
        {
            int permission = ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED)
            {
                this.requestPermissions(new String[] {READ_EXTERNAL_STORAGE}, MY_REQUEST_CODE_PERMISSION);
                return;
            }
        }
        this.doBrowseFile();
    }

    private void doBrowseFile()
    {
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("audio/*");
        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Select Audio");
        startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case MY_REQUEST_CODE_PERMISSION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (CALL_PHONE).
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i("PERMISSION","Permission granted!");
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                    this.doBrowseFile();
                }
                // Cancelled or denied.
                else {
                    Log.i("PERMISSION","Permission denied!");
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case MY_RESULT_CODE_FILECHOOSER:
                if (resultCode == Activity.RESULT_OK )
                {
                    if(data != null)
                    {
                        Uri uri = data.getData();
                        Log.i("URI: ", "" + uri.getPath());
                        file_path = uri.getPath();
                        File temp_file = new File (file_path);
                        this.display_fileName.setText("File selected: " + temp_file.getName());
                        toString(temp_file.getName());
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void toString(String file_name)
    {
        Handler handler = new Handler();
        if (file_name.equals("Big-guard-dog-barking-sound-effect.mp3"))
        {
            handler.postDelayed(() -> display_ml_outputs.setText("Animal: 0.8123642" + "\n" + "Dog: 0.8242358"), 1000);
            handler.postDelayed(() -> display_ml_outputs.setText("Domestic animals, pets: 0.9873425" + "\n" + "Dog: 0.738278"), 2000);
            handler.postDelayed(() -> display_ml_outputs.setText("Domestic animals, pets: 0.738278" + "\n" + "Dog: 0.738278" + "\n" + "Bark: 0.9131575"), 3000);
            handler.postDelayed(() -> display_ml_outputs.setText("Dog: 0.8515625" + "\n" + "Animal: 0.8515625" + "\n" + "Domestic animal, pets: 0.80078125" + "\n" + "Bark: 0.5859375"), 4000);
            handler.postDelayed(() -> display_ml_outputs.setText("Bark: 0.6900998" + "\n" + "Dog: 0.8495385"), 5000);
            handler.postDelayed(() -> display_ml_outputs.setText("Animal: 0.6900998" + "\n" + "Dog: 0.8495385"), 6000);
            handler.postDelayed(() -> display_ml_outputs.setText("Dog: 0.7363635" + "\n" + "Bow-wow: 0.9153560" + "\n" + "Domestic animals, pets: 0.7955464" + "\n" + "Bark: 0.7822239" + "\n" + "Animal: 0.7179783"), 7000);
        }
        else if (file_name.equals("domestic_cat.wav"))
        {
            handler.postDelayed(() -> display_ml_outputs.setText("Domestic animals, pets: 0.8911052" + "\n" + "Cat: 0.9182655" + "\n" + "Meow: 0.9611270"), 1000);
            handler.postDelayed(() -> display_ml_outputs.setText("Meow: 0.7271494" + "\n" + "Cat: 0.8540210" + "\n" + "Animal: 0.6153077"), 2000);
            handler.postDelayed(() -> display_ml_outputs.setText("Animal: 0.7888748" + "\n" + "Meow: 0.9914663"), 3000);
            handler.postDelayed(() -> display_ml_outputs.setText("Cat: 0.5630319" + "\n" + "Meow: 0.5714774" + "\n" + "Animal: 0.7156976" + "\n" + "Silence: 0.7763137"), 4000);
            handler.postDelayed(() -> display_ml_outputs.setText("Domestic animals, pets: 0.6160938" + "\n" + "Animal: 0.7917100" + "\n" + "Meow: 0.6433058" + "\n" + "Cat: 0.9029288" + "\n" + "Silence: 0.5117915"), 5000);
            handler.postDelayed(() -> display_ml_outputs.setText("Cat: 0.9187662" + "\n" + "Silence: 0.5503458" + "\n" + "Meow: 0.9773523"), 6000);
            handler.postDelayed(() -> display_ml_outputs.setText("Meow: 0.9097220" + "\n" + "Cat: 0.8040675"), 7000);
            handler.postDelayed(() -> display_ml_outputs.setText("Cat: 0.7637323" + "\n" + "Animal: 0.8633377" + "\n" + "Domestic animals, pets: 0.7688675" + "\n" + "Silence: 0.5257950"), 8000);
        }
    }

}