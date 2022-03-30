package com.example.test;

import android.Manifest;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private String filePath = "";
    private String fileName = "";


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
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            // if list button is clicked
            case R.id.record_fragment_listButton:
                navController.navigate(R.id.action_recordFragment_to_audioListFragment);
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



    private void startRecording()
    {
        SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.US);
        Date now = new Date();
        filePath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        fileName = fileNameFormat.format(now) + ".mp3";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(filePath + "/" + fileName);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    private void stopRecording()
    {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }



}