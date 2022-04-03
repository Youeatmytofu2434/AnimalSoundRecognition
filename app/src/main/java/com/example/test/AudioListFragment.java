package com.example.test;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioListFragment extends Fragment implements FileListAdapter.onItemListClick{

    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView fileList;

    // Stuff for file list
    private File[] fileArray;
    private FileListAdapter fileListAdapter;

    // Stuff to play audio
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;

    // Store file to be played when clicked
    private File fileToPlay = null;

    // Player Sheet Stuff
    private ImageButton playIcon;
    private TextView audioStatus, display_file_name;
    //Seekbar
    private SeekBar seekBar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;

    public AudioListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize + calling behavior by referencing playerSheet
        playerSheet = view.findViewById(R.id.player_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        fileList = view.findViewById(R.id.record_list_fragment_recyclerView);

        // Player Sheet declaration
        playIcon = view.findViewById(R.id.player_sheet_play_icon);
        display_file_name = view.findViewById(R.id.player_sheet_file_name);
        audioStatus = view.findViewById(R.id.player_sheet_not_playing);
        seekBar = view.findViewById(R.id.player_sheet_seekbar);

        // Saving files to an array
        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File dir = new File(path);
        fileArray = dir.listFiles();

        fileListAdapter = new FileListAdapter(fileArray, this);

        fileList.setHasFixedSize(true);
        fileList.setLayoutManager(new LinearLayoutManager(getContext()));
        fileList.setAdapter(fileListAdapter);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Fix the bug where player sheet disappears if dragged all the way down to the bottom
                if (newState == BottomSheetBehavior.STATE_HIDDEN)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Can't delete this since it'll give an error
            }
        });

        // Pause + resume
        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying == true)
                {
                    pauseAudio();
                }
                else {
                    if (fileToPlay != null)
                    {
                        resumeAudio();
                    }
                }
            }
        });

        // Clicking on seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (fileToPlay != null)
                {
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }
            }
        });
    }



    @Override
    public void onClickListener(File file, int position)
    {
        fileToPlay = file;
        //Log.d("AUDIO_PLAYER", "File: " + file.getName() + " is playing!");
        if (isPlaying == true)
        {
            // Stop current file
            stopAudio();
            // Play the file on click
            playAudio(fileToPlay);
        }
        else
        {
            playAudio(fileToPlay);
        }

    }

    // Play audio function
    public void playAudio (File fileToPlay)
    {
        mediaPlayer = new MediaPlayer();

        // Change state of BottomBehavior
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        // Try playing the audio file
        try {
            Log.d("AUDIO_PLAYING", "File: " + fileToPlay.getName() + " is playing!");
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.d("AUDIO_PLAYING", "Failed to play: " + fileToPlay.getName());
            e.printStackTrace();
        }

        // Changing state of buttons + file name for player_sheet
        playIcon.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn, null));
        display_file_name.setText("Playing: " + fileToPlay.getName());
        audioStatus.setText("Playing!");

        // Play audio
        isPlaying = true;

        //Seekbar
        seekBar.setMax(mediaPlayer.getDuration());

        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

        // Change status to "Done!" and stop the audio once finished
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer)
            {
                stopAudio();
                audioStatus.setText("Done!");
            }
        });
    }

    private void updateRunnable()
    {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);
            }
        };
    }

    // When stopped, change player icon, change status, isPlay == false, pause mediaPlayer, stop seekbar
    public void stopAudio ()
    {
        // Changing state of buttons + file name for player_sheet
        playIcon.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn, null));
        audioStatus.setText("Stopped");

        isPlaying = false;
        // Stop audio
        mediaPlayer.pause();
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    // When paused, set mediaplayer == pause, change play icon image, stop seekbar
    private void pauseAudio()
    {
        playIcon.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn, null));
        audioStatus.setText("Paused!");
        mediaPlayer.pause();
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }
    // When paused, set mediaplayer == start, change play icon image, update seekbar
    private void resumeAudio()
    {
        playIcon.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn, null));
        audioStatus.setText("Playing!");
        mediaPlayer.start();
        isPlaying = true;
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }
    // When exiting the fragment, completely stop the audio
    public void onStop()
    {
        super.onStop();
        if (isPlaying == true)
        {
            mediaPlayer.stop();
            stopAudio();
        }
    }
}