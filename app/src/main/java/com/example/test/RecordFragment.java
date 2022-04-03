package com.example.test;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */

public class RecordFragment extends Fragment implements View.OnClickListener {

    private NavController navController;
    private ImageView listButton, recordButton;

    // Recording stuff
    private boolean isRecording = false;
    private static int MIC_PERMISSION_CODE = 2434;
    MediaRecorder mediaRecorder;
    // File name
    private String filePath = "";
    private String fileName = "";
    // Timer
    private Chronometer timer;
    // Display filename
    private TextView display_fileName;


    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    // Will override the onCreate method
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // Initialize + set onclick function to list button (ImageView)
        listButton = view.findViewById(R.id.record_fragment_listButton);
        listButton.setOnClickListener(this);
        // Initialize + set onclick function to record button (ImageView)
        recordButton = view.findViewById(R.id.record_fragment_recordButton);
        recordButton.setOnClickListener(this);
        // Timer
        timer = view.findViewById(R.id.record_fragment_timer);
        // Display filename
        display_fileName = view.findViewById(R.id.record_fragment_displayFilename);

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            // if list button is clicked
            case R.id.record_fragment_listButton:
                if (isRecording == true)
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    // Buttons for Alert Dialogs
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                        }
                    });
                    alertDialog.setNegativeButton("No", null);

                    alertDialog.setTitle("Audio recording is in progress");
                    alertDialog.setMessage("Do you want to stop the recording?");
                    alertDialog.create().show();
                }
                else
                {
                    navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                }
                break;

            // if record button is clicked
            case R.id.record_fragment_recordButton:
                if (isMicPresent())
                {
                    // if recording --> switch to recordStop image
                    if (isRecording == true) {
                        stopRecording();
                        recordButton.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording, null));
                        isRecording = false;
                        Toast.makeText(getContext(), "Recording has stopped!", Toast.LENGTH_SHORT).show();
                    }
                    // if not recording --> switch to record image
                    else {
                        // if getMicPermission == true => start recording -> set to Mic off
                        if (getMicPermission() == true) {
                            startRecording();
                            recordButton.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped, null));
                            isRecording = true;
                            Toast.makeText(getContext(), "Recording has started!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                    Toast.makeText(getContext(), "Mic is not present! Please try again!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // check whether the system has a mic or not
    private boolean isMicPresent()
    {
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // Getting mic permission
    private boolean getMicPermission()
    {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_CODE);
            return false;
        }
    }


    // Start recording --> start timer --> save file (formatted file name) to system --> display file name
    private void startRecording()
    {
        // allows timer to start at 0:00
        timer.setBase(SystemClock.elapsedRealtime());
        // start the time when we call startRecording
        timer.start();

        // formatting filenames + save to a location
        // Make sure the name does not contain any special characters
        SimpleDateFormat fileNameFormatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date currentDate = new Date();
        filePath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        fileName = fileNameFormatter.format(currentDate) + ".aac";

        // Displays file name
        display_fileName.setText("File name: " + fileName);

        // start the recording
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(filePath + "/" + fileName);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    // Stop recording --> stop timer
    private void stopRecording()
    {
        // stop timer
        timer.stop();
        // stop recording
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }
    @Override
    public void onStop()
    {
        super.onStop();
        // Stop audio + avoid crash + avoid corrupted file when exiting while recording
        if (isRecording == true)
        {
            stopRecording();
        }
    }
}