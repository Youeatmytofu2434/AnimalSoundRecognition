package com.example.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Date;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileListViewHolder>
{

    private File[] fileArray;
    private modifiedDateToString timeToString;

    private onItemListClick onItemListClick;

    // Constructor
    public FileListAdapter(File[] fileArray, onItemListClick onItemListClick)
    {
        this.fileArray = fileArray;
        this.onItemListClick = onItemListClick;
    }

    @NonNull
    @Override
    public FileListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_file_list, parent, false);
        return new FileListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileListViewHolder holder, int position)
    {
        Date lastModified = new Date(fileArray[position].lastModified());
        timeToString = new modifiedDateToString();
        holder.display_name.setText(fileArray[position].getName());
        holder.display_date.setText(timeToString.getCurrentTime(fileArray[position].lastModified()));

    }

    @Override
    public int getItemCount()
    {
        return fileArray.length;
    }

    public class FileListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView single_file_list_play_button;
        private TextView display_name, display_date;

        public FileListViewHolder(@NonNull View itemView)
        {
            super(itemView);

            single_file_list_play_button = itemView.findViewById(R.id.single_file_list_playButton);
            display_name = itemView.findViewById(R.id.single_file_list_fileName);
            display_date = itemView.findViewById(R.id.single_file_list_date);

            // Set on click to each file list
            itemView.setOnClickListener(this);
        }

        // On click functionality for item in the file list
        @Override
        public void onClick(View view) {
            onItemListClick.onClickListener(fileArray[getAdapterPosition()], getAdapterPosition());
        }
    }

    // Onclick declaration
    public interface onItemListClick
    {
        void onClickListener(File file, int position);
    }
}
