package com.example.ftpclient.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.ftpclient.R;
import com.example.ftpclient.Service.InteractionService;

public class SettingActivity extends AppCompatActivity {
    private RadioGroup type;
    private RadioGroup mode;
    private RadioGroup structure;

    private RadioButton ascii;
    private RadioButton binary;

    private RadioButton stream;
    private RadioButton block;
    private RadioButton compressed;

    private RadioButton file;
    private RadioButton record;
    private RadioButton page;

    private SwitchCompat zip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initContent();

        if(InteractionService.getType() == 0) ascii.setChecked(true);
        else binary.setChecked(true);

        if(InteractionService.getMode() == 0) stream.setChecked(true);
        else if(InteractionService.getMode() == 1) block.setChecked(true);
        else compressed.setChecked(true);

        if(InteractionService.getStructure() == 0) file.setChecked(true);
        else if(InteractionService.getStructure() == 1)record.setChecked(true);
        else page.setChecked(true);

        zip.setText("zip mode: " + (InteractionService.getZipped() ? "on" : "off"));
        zip.setChecked(InteractionService.getZipped());
        zip.setOnCheckedChangeListener((view, isChecked) -> zip.setText("zip mode: " + (isChecked ? "on" : "off")));
    }

    @Override
    public void onBackPressed() {
        String temp;

        if (type.getCheckedRadioButtonId() == R.id.setting_ascii) temp = "a";
        else temp = "i";
        InteractionService.doCommand(this, "TYPE " + temp);

        if (mode.getCheckedRadioButtonId() == R.id.setting_stream) temp = "s";
        else if (mode.getCheckedRadioButtonId() == R.id.setting_block) temp = "b";
        else temp = "c";
        InteractionService.doCommand(this, "MODE " + temp);

        if (structure.getCheckedRadioButtonId() == R.id.setting_file) temp = "f";
        else if (structure.getCheckedRadioButtonId() == R.id.setting_record) temp = "r";
        else temp = "p";
        InteractionService.doCommand(this, "STRU " + temp);

        InteractionService.doCommand(this, "ZIP " + (zip.isChecked() ? "T" : "F"));

        super.onBackPressed();
    }

    private void initContent() {
        type = findViewById(R.id.setting_type);
        mode = findViewById(R.id.setting_mode);
        structure = findViewById(R.id.setting_structure);

        ascii = findViewById(R.id.setting_ascii);
        binary = findViewById(R.id.setting_binary);

        stream = findViewById(R.id.setting_stream);
        block = findViewById(R.id.setting_block);
        compressed = findViewById(R.id.setting_compressed);

        file = findViewById(R.id.setting_file);
        record = findViewById(R.id.setting_record);
        page = findViewById(R.id.setting_page);

        zip = findViewById(R.id.setting_zip);
    }
}