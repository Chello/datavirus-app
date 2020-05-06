package com.example.datavirus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SavedTilesActivity extends AppCompatActivity implements OnTileClick {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_chart_list);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataTilesFragment frg = (DataTilesFragment) getSupportFragmentManager().findFragmentById(R.id.list_saved_tiles_fragment);

        frg.setSavedTilesReport();
    }

    /**
     * Creates a new ChartActivity showing data in chart
     * @param fieldGeographicElement the data specification
     */
    @Override
    public void onTileClick(FieldGeographicElement fieldGeographicElement) {
        Intent i = new Intent(this, ChartActivity.class);
        Bundle b = new Bundle();

        b.putIntegerArrayList(ChartActivity.FIELD_DATA, DataParser.getDPCDataInstance().getValuesFromGeoField(fieldGeographicElement));

        i.putExtras(b);
        i.putExtra(ChartActivity.DATE, DataParser.getDPCDataInstance().getFirstDate());
        i.putExtra(ChartActivity.DENOMINAZIONE, fieldGeographicElement.getDenominazione());
        i.putExtra(ChartActivity.FIELD, fieldGeographicElement.getField());
        startActivity(i);
    }
}
