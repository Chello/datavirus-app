package com.example.datavirus;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class DataTilesFragment extends DataSingleTileFragment {

//    private static final String TOTAL = "total";
//    private static final String TOTAL_DELTA = "total_delta";
//    private static final String ACTIVE = "active";
//    private static final String ACTIVE_DELTA = "active_delta";
//    private static final String HEALED = "healed";
//    private static final String HEALED_DELTA = "healed_delta";
//    private static final String DEATHS = "deaths";
//    private static final String DEATHS_DELTA = "deaths_delta";
    private DataTilesAdapter adapter;

    private DPCData.DailyReport[] reports;

    public void setReports(DPCData.DailyReport[] reports) {
        this.reports = reports;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.data_tiles, container, false);
        populateRecycler(v);

//        if (getArguments() != null) {
//            updateTiles(v, getArguments());
//        }
        return v;
    }

    private void populateRecycler(View v) {
        if (v == null) v = getView();
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_tiles);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        this.adapter = new DataTilesAdapter(this.reports);
        recyclerView.setAdapter(this.adapter);
    }

    public static DataTilesFragment newInstance(DPCData.DailyReport[] reports) {
        Bundle args = new Bundle();
        Integer last = reports.length -1;
        Integer lastLast = reports.length -2;

        for (String key : reports[last].getKeys()) {
            if (reports[last].getInt(key) != null) {
                args.putInt(key, reports[last].getInt(key));
                args.putInt(key + "_delta", reports[last].getInt(key) - reports[lastLast].getInt(key));
            }
        }
//        args.putInt(TOTAL, report[report.length -1].getInt("totale_casi"));
//        args.putInt(TOTAL_DELTA, report[report.length -1].getInt("totale_casi") - report[report.length-2].getInt("totale_casi"));
//
//        args.putInt(ACTIVE, report[report.length -1].getInt("totale_positivi"));
//        args.putInt(ACTIVE_DELTA, report[report.length -1].getInt("totale_positivi") - report[report.length-2].getInt("totale_positivi"));
//
//        args.putInt(HEALED, report[report.length -1].getInt("dimessi_guariti"));
//        args.putInt(HEALED_DELTA, report[report.length -1].getInt("dimessi_guariti") - report[report.length-2].getInt("dimessi_guariti"));

//        args.putInt(DEATHS, report[report.length -1].getInt("deceduti"));
//        args.putInt(DEATHS_DELTA, report[report.length -1].getInt("deceduti") - report[report.length-2].getInt("deceduti"));

        DataTilesFragment fragment = new DataTilesFragment();
        fragment.setArguments(args);
        fragment.setReports(reports);
        return fragment;
    }

    /**
     * Update tiles, using data passed.
     * It only uses the last two days for getting the current data and the delta
     * @param v the view containing objects to set
     */
//    protected void updateTiles(View v, Bundle b) {
//        super.updateTiles(v, b);
//        Resources res = getResources();
//
//        //Set active value of last day
//        TextView active = (TextView) v.findViewById(R.id.tile_active);
//        active.setText(String.format(res.getString(R.string.placeholder_decimal), b.getInt("totale_positivi")));
//        //Set active delta of last day
//        TextView activeDelta = (TextView) v.findViewById(R.id.tile_active_delta);
//        activeDelta.setText(String.format(res.getString(R.string.placeholder_delta), b.getInt("totale_positivi_delta")));
//
//        //Set healed value of last day
//        TextView healed = (TextView) v.findViewById(R.id.tile_healed);
//        healed.setText(String.format(res.getString(R.string.placeholder_decimal), b.getInt("dimessi_guariti")));
//        //Set healed delta of last day
//        TextView healedDelta = (TextView) v.findViewById(R.id.tile_healed_delta);
//        healedDelta.setText(String.format(res.getString(R.string.placeholder_delta), b.getInt("dimessi_guariti_delta")));
//
//        //Set deaths value of last day
//        TextView deaths = (TextView) v.findViewById(R.id.tile_deaths);
//        deaths.setText(String.format(res.getString(R.string.placeholder_decimal), b.getInt("deceduti")));
//        //Set deaths delta of last day
//        TextView deathsDelta = (TextView) v.findViewById(R.id.tile_deaths_delta);
//        deathsDelta.setText(String.format(res.getString(R.string.placeholder_delta), b.getInt("deceduti_delta")));
//    }

    public class DataTilesAdapter extends RecyclerView.Adapter<DataTilesAdapter.DataTilesHolder> {
        private DPCData.DailyReport[] reports;
        private String[] fields;
        private Integer last;
        private Integer lastLast;

        public class DataTilesHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            private CardView container;
            private TextView qty;
            private TextView qty_delta;
            private TextView tile_head;

            public void setQty(Integer qty) {
                this.qty.setText(qty.toString());
            }

            public void setQtyDelta(Integer qty_delta) {
                this.qty_delta.setText(qty_delta.toString());
            }

            public void setTileHead(String tile_head) {
                this.tile_head.setText(this.adjustTitleString(tile_head));
                //setting colors
                Log.d("ColorTest", getResources().getText(R.string.data_total).toString());
                if (tile_head.equals(getResources().getText(R.string.data_total)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorBlue, null));
                else if (tile_head.equals(getResources().getText(R.string.data_active)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorOrange, null));
                else if (tile_head.equals(getResources().getText(R.string.data_healed)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorGreen, null));
                else if (tile_head.equals(getResources().getText(R.string.data_deaths)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorRed, null));
                else this.container.setBackgroundColor(getResources().getColor(R.color.colorGrey, null));
            }

            private String adjustTitleString(String s) {
                s = s.trim().replace('_', ' '); //replace bad characters
                return s.substring(0, 1).toUpperCase() + s.substring(1); //first capital letter
            }

            public DataTilesHolder(CardView v) {
                super(v);
                this.container = v;
                this.qty = v.findViewById(R.id.tile_qty);
                this.qty_delta = v.findViewById(R.id.tile_qty_delta);
                this.tile_head = v.findViewById(R.id.tile_head);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public DataTilesAdapter(DPCData.DailyReport[] myDataset) {
            this.reports = myDataset;
            this.last = myDataset.length -1;
            this.lastLast = myDataset.length -2;
            Integer keysLenght = myDataset[this.last].getKeys().size();
            ArrayList<String> fields = new ArrayList<String>();
            //For each key beautify the string
            for (int i = 0; i < keysLenght; i++) {
                String s = myDataset[this.last].getKeys().get(i); //get the string
                if (myDataset[this.last].getInt(s) != null)
                    fields.add(s);
            }
            this.fields = fields.toArray(new String[0]);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public DataTilesHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
            // create a new view
            CardView v = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_tile, parent, false);
            DataTilesHolder vh = new DataTilesHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(DataTilesHolder holder, int position) {
            String key = this.fields[position];
            holder.setTileHead(key);
            holder.setQty(this.reports[this.last].getInt(key));
            holder.setQtyDelta(this.reports[this.last].getInt(key) - this.reports[this.lastLast].getInt(key));

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return this.fields.length;
        }
    }


}
