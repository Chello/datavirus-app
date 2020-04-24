package com.example.datavirus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity implements OnDPCDataReady {

    DataParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.parser = new DataParser(getSupportFragmentManager(), this);

    }

    @Override
    public void updateData(DPCData data) {
        Log.d("Blabla", "BleBle1111");
        DataTilesFragment fragment = (DataTilesFragment) getSupportFragmentManager().findFragmentById(R.id.data_tiles);
        fragment.updateData(data);
    }
}
