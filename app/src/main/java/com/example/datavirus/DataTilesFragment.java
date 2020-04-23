package com.example.datavirus;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DataTilesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataTilesFragment extends Fragment implements UpdateUI {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DataParser parser;

    public DataTilesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DataTilesFragment newInstance(String param1, String param2) {
        DataTilesFragment fragment = new DataTilesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.data_tiles, container, false);
        //Populating spinner
//        Spinner macrofield = (Spinner) v.getView().findViewById(R.id.spinner_macrofield_spinner);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R., android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        macrofield.setAdapter(adapter);
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void updateData(DPCData data) {
        DPCData.DailyReport[] report = data.get(DPCData.Field.NAZIONALE);

        //Set total value of last day
        TextView total = (TextView) getView().findViewById(R.id.tile_total);
        total.setText(report[report.length -1].getTotale_casi().toString());
        //Set total delta of last day
        TextView totalDelta = (TextView) getView().findViewById(R.id.tile_total_delta);
        totalDelta.setText("Δ" + (report[report.length -1].getTotale_casi() - report[report.length-2].getTotale_casi()));

        //Set active value of last day
        TextView active = (TextView) getView().findViewById(R.id.tile_active);
        active.setText(report[report.length -1].getTotale_positivi().toString());
        //Set active delta of last day
        TextView activeDelta = (TextView) getView().findViewById(R.id.tile_active_delta);
        activeDelta.setText("Δ" + (report[report.length -1].getTotale_positivi() - report[report.length-2].getTotale_positivi()));

        //Set healed value of last day
        TextView healed = (TextView) getView().findViewById(R.id.tile_healed);
        healed.setText(report[report.length -1].getDimessi_guariti().toString());
        //Set healed delta of last day
        TextView healedDelta = (TextView) getView().findViewById(R.id.tile_healed_delta);
        healedDelta.setText("Δ" + (report[report.length -1].getDimessi_guariti() - report[report.length-2].getDimessi_guariti()));

        //Set deaths value of last day
        TextView deaths = (TextView) getView().findViewById(R.id.tile_deaths);
        deaths.setText(report[report.length -1].getDeceduti().toString());
        //Set deaths delta of last day
        TextView deathsDelta = (TextView) getView().findViewById(R.id.tile_deaths_delta);
        deathsDelta.setText("Δ" + (report[report.length -1].getDeceduti() - report[report.length-2].getDeceduti()));


    }

    @Override
    public void setParser(DataParser parser) {
        this.parser = parser;
        parser.setUI(this);
    }
}
