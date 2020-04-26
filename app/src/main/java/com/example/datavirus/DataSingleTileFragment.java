package com.example.datavirus;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DataSingleTileFragment extends Fragment {

    private DataSingleTileViewModel mViewModel;
    DPCData.DailyReport[] report;

    private static final String TOTAL = "total";
    private static final String TOTAL_DELTA = "total_delta";


    // TODO: Rename and change types of parameters
    private Integer total;
    private Integer total_delta;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.data_single_tile_fragment, container, false);
        updateTiles(v, getArguments());
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static DataSingleTileFragment newInstance(DPCData.DailyReport[] report) {
        
        Bundle args = new Bundle();
        args.putInt(TOTAL, report[report.length -1].getInt("totale_casi"));
        args.putInt(TOTAL_DELTA, report[report.length -1].getInt("totale_casi") - report[report.length-2].getInt("totale_casi"));
        
        DataSingleTileFragment fragment = new DataSingleTileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * Update tiles, using data passed in constructor.
     * It only uses the last two days for getting the current data and the delta
     */
    protected void updateTiles(View v, Bundle b) {
        Resources res = getResources();
        //Set total value of last day
        TextView total = (TextView) v.findViewById(R.id.tile_total);
        total.setText(String.format(res.getString(R.string.placeholder_decimal), b.getInt(TOTAL)));
        //Set total delta of last day
        TextView totalDelta = (TextView) v.findViewById(R.id.tile_total_delta);
        totalDelta.setText(String.format(res.getString(R.string.placeholder_delta), b.getInt(TOTAL_DELTA)));
    }

}
