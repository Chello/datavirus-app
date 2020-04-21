package com.example.datavirus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity implements UpdateUI<DPCData> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataParser parser = new DataParser(this);
    }

    @Override
    public void updateData(DPCData data) {

    }
}
