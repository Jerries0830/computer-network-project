package com.example.ftpserver.FileAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ftpserver.R;

import java.util.List;

public abstract class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder> {
    protected final List<FileItem> fileItemList;

    public FileAdapter(List<FileItem> fileItemList) {
        this.fileItemList = fileItemList;
    }

    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, int position) {
        FileItem fileItem = fileItemList.get(position);
        holder.fileName.setText(fileItem.getName());
        holder.fileIcon.setImageResource(fileItem.getImageID());
    }

    @Override
    public int getItemCount() {
        return fileItemList.size();
    }

    public abstract void setFiles(String dirPath);

    protected static class FileHolder extends RecyclerView.ViewHolder {
        private final ImageView fileIcon;
        private final TextView fileName;

        public FileHolder(@NonNull View itemView) {
            super(itemView);
            fileIcon = itemView.findViewById(R.id.fileIcon);
            fileName = itemView.findViewById(R.id.fileName);
        }
    }
}