package com.example.test;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

    private NavController navController;
    private ImageView listButton, recordButton;

    private boolean isRecording = false;


    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
    public void onClick(View view) {
        switch (view.getId())
        {
            // if list button is clicked
            case R.id.record_fragment_listButton:
                navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                break;
            // if record button is clicked
            case R.id.record_fragment_recordButton:
                // if recording --> switch to recordStop image
                if (isRecording)
                {
                    recordButton.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped, null));
                    isRecording = false;
                }
                // if not recording --> switch to record image
                else
                {
                    recordButton.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording, null));
                    isRecording = true;
                }
                break;
        }

    }
}