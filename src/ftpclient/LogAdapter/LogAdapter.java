package com.example.ftpclient.LogAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ftpclient.R;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogHolder> {
    protected final List<LogItem> logItemList;

    public LogAdapter(List<LogItem> logItemList) {
        this.logItemList = logItemList;
    }

    @NonNull
    @Override
    public LogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new LogHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogHolder holder, int position) {
        LogItem logItem = logItemList.get(position);
        holder.time.setText(logItem.getTime());
        holder.content.setText(logItem.getContent());
    }

    @Override
    public int getItemCount() {
        return logItemList.size();
    }

    protected static class LogHolder extends RecyclerView.ViewHolder {
        private final TextView time;
        private final TextView content;

        public LogHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            content = itemView.findViewById(R.id.content);
        }
    }
}