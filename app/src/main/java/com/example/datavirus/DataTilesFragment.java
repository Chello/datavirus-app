package com.example.datavirus;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DataTilesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataTilesFragment extends Fragment implements OnDPCDataReady {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DataParser parser;
    private DPCData covidData;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.data_tiles, container, false);

        //spinnerHandlers(v);
        return v;
    }

    private void setRegionaleSpinner() {

    }

    /**
     * Update tiles, using data passed.
     * It only uses the last two days for getting the current data and the delta
     * @param report report to use for the update
     */
    public void updateTiles(DPCData.DailyReport[] report) {
        Resources res = getResources();
        //Set total value of last day
        TextView total = (TextView) getView().findViewById(R.id.tile_total);
        total.setText(report[report.length -1].getInt("totale_casi").toString());
        //Set total delta of last day
        TextView totalDelta = (TextView) getView().findViewById(R.id.tile_total_delta);

        Integer deltaTotal = report[report.length -1].getInt("totale_casi") - report[report.length-2].getInt("totale_casi");
        totalDelta.setText(String.format(res.getString(R.string.placeholder_delta), deltaTotal));

        //Set active value of last day
        TextView active = (TextView) getView().findViewById(R.id.tile_active);
        active.setText(report[report.length -1].getInt("totale_positivi").toString());
        //Set active delta of last day
        TextView activeDelta = (TextView) getView().findViewById(R.id.tile_active_delta);
        Integer deltaActive = report[report.length -1].getInt("totale_positivi") - report[report.length-2].getInt("totale_positivi");
        activeDelta.setText(String.format(res.getString(R.string.placeholder_delta), deltaActive));

        //Set healed value of last day
        TextView healed = (TextView) getView().findViewById(R.id.tile_healed);
        healed.setText(report[report.length -1].getInt("dimessi_guariti").toString());
        //Set healed delta of last day
        TextView healedDelta = (TextView) getView().findViewById(R.id.tile_healed_delta);
        Integer deltaHealed = report[report.length -1].getInt("dimessi_guariti") - report[report.length-2].getInt("dimessi_guariti");
        healedDelta.setText(String.format(res.getString(R.string.placeholder_delta), deltaHealed));

        //Set deaths value of last day
        TextView deaths = (TextView) getView().findViewById(R.id.tile_deaths);
        deaths.setText(report[report.length -1].getInt("deceduti").toString());
        //Set deaths delta of last day
        TextView deathsDelta = (TextView) getView().findViewById(R.id.tile_deaths_delta);
        Integer deltaDeaths = report[report.length -1].getInt("deceduti") - report[report.length-2].getInt("deceduti");
        deathsDelta.setText(String.format(res.getString(R.string.placeholder_delta), deltaDeaths));
    }



    @Override
    public void updateData(DPCData data) {
        this.covidData = data;
        this.updateTiles(data.getNazionale());
    }

}
