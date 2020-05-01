package com.example.datavirus;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

//TODO javadoc
public class DataTilesFragment extends Fragment {

    private DataTilesAdapter adapter;

    private DailyReport[] reports;

    protected GeographicElement geoField;

    public void setReports(DailyReport[] reports) {
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

    public static DataTilesFragment newInstance(DailyReport[] reports, GeographicElement geoField, Resources res) {
        Bundle args = new Bundle();
        Integer last = reports.length -1;
        Integer lastLast = reports.length -2;

        for (String key : reports[last].getKeys(res)) {
            if (reports[last].getInt(key) != null) {
                args.putInt(key, reports[last].getInt(key));
                args.putInt(key + "_delta", reports[last].getInt(key) - reports[lastLast].getInt(key));
            }
        }

        DataTilesFragment fragment = new DataTilesFragment();
        fragment.setArguments(args);
        fragment.setGeoField(geoField);
        fragment.setReports(reports);
        return fragment;
    }

    public void setGeoField(GeographicElement field) {
        this.geoField = field;
    }

    public class DataTilesAdapter extends RecyclerView.Adapter<DataTilesAdapter.DataTilesHolder> {
        private DailyReport[] reports;
        private String[] fields;
        private Integer last;
        private Integer lastLast;

        public DataTilesAdapter(DailyReport[] myDataset) {
            this.reports = myDataset;
            this.last = myDataset.length -1;
            this.lastLast = myDataset.length -2;
            Integer keysLenght = myDataset[this.last].getKeys(getResources()).size();
            ArrayList<String> fields = new ArrayList<String>();
            //For each key beautify the string
            for (int i = 0; i < keysLenght; i++) {
                String s = myDataset[this.last].getKeys(getResources()).get(i); //get the string
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
            DataTilesHolder vh = new DataTilesHolder(v, (OnTileClick) getActivity());
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

        public class DataTilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            // each data item is just a string in this case
            private CardView container;
            private TextView qty;
            private TextView qty_delta;
            private TextView tile_head;
            private OnTileClick listener;

            public void setQty(Integer qty) {
                this.qty.setText(qty.toString());
            }

            public void setQtyDelta(Integer qty_delta) {
                this.qty_delta.setText(qty_delta.toString());
            }

            public void setTileHead(String tile_head) {
                this.tile_head.setText(this.adjustTitleString(tile_head));
                //setting colors
                if (tile_head.equals(getResources().getText(R.string.denominazione_total)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorBlue, null));
                else if (tile_head.equals(getResources().getText(R.string.denominazione_active)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorOrange, null));
                else if (tile_head.equals(getResources().getText(R.string.denominazione_healed)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorGreen, null));
                else if (tile_head.equals(getResources().getText(R.string.denominazione_deaths)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorRed, null));
                else
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorGrey, null));
            }

            private String adjustTitleString(String s) {
                s = s.trim().replace('_', ' '); //replace bad characters
                return s.substring(0, 1).toUpperCase() + s.substring(1); //first capital letter
            }

            public DataTilesHolder(CardView v, OnTileClick listener) {
                super(v);
                this.listener = listener;
                this.container = v;
                this.qty = v.findViewById(R.id.tile_qty);
                this.qty_delta = v.findViewById(R.id.tile_qty_delta);
                this.tile_head = v.findViewById(R.id.tile_head);
                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                listener.onTileClick(geoField, fields[getAdapterPosition()]);
            }
        }
    }

}
