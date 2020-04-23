package com.example.datavirus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity  {

    DataParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.parser = new DataParser();

        DataTilesFragment fragment = (DataTilesFragment) getSupportFragmentManager().findFragmentById(R.id.data_tiles);
        fragment.setParser(parser);
    }

}
