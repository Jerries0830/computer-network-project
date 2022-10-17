package com.example.ftpclient.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.ftpclient.LogAdapter.LogAdapter;
import com.example.ftpclient.LogAdapter.LogItem;
import com.example.ftpclient.R;
import com.example.ftpclient.Service.InteractionService;

import java.util.ArrayList;

public class TerminalActivity extends AppCompatActivity {
    private final ArrayList<LogItem> logItems = new ArrayList<>();
    private final LogAdapter logAdapter = new LogAdapter(logItems);

    private EditText editText;
    private Button submit;
    private RecyclerView recyclerView;

    private TerminalReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        initContent();

        submit.setOnClickListener(view -> InteractionService.doCommand(this, editText.getText().toString()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(logAdapter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.action_command));
        receiver = new TerminalReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initContent() {
        editText = findViewById(R.id.editCommand);
        submit = findViewById(R.id.submit);
        recyclerView = findViewById(R.id.responseView);
    }

    private class TerminalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra(InteractionService.RESPONSE);
            LogItem logItem = new LogItem(response);
            logItems.add(logItem);
            logAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(logItems.size() - 1);
            editText.getText().clear();
        }
    }
}