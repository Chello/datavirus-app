package com.example.datavirus;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.service.quicksettings.Tile;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

//TODO javadoc
public class DataTilesFragment extends Fragment {

    private TilesAdapter adapter;

    private DPCData covidData;


    public void setStaticGeoField(GeographicElement geographicElement, DPCData reports) {
        this.covidData = reports;
        populateRecycler(geographicElement);
    }

    public void setSavedTilesReport(DPCData covidData) {
        this.covidData = covidData;
        this.populateRecycler(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.data_tiles, container, false);
        //populateRecycler(v);
        return v;
    }

    /**
     * Populates the RecylerView of tiles.
     * It sets up fragment with global GeographicElement if set.
     * If geographicElement is set null, it will shown all saved tiles
     * @param geographicElement tiles showing data from this geographicElement
     */
    private void populateRecycler(@Nullable GeographicElement geographicElement) {
        View v = getView();
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_tiles);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        if (geographicElement != null)
            this.adapter = new TilesAdapter(this.covidData, geographicElement);
        else this.adapter = new TilesAdapter(this.covidData);

        recyclerView.setAdapter(this.adapter);
    }

    public static DataTilesFragment newInstance() {
        return new DataTilesFragment();
    }

    /**
     * Adapter for tiles with static GeographicField
     */
    public class TilesAdapter extends RecyclerView.Adapter<TilesAdapter.DataTilesHolder> {
//        private DailyReport[] reports;
//        private String[] fields;
//        private Integer last;
//        private Integer lastLast;

        private ArrayList<Integer> last;
        private ArrayList<Integer> lastLast;
        private ArrayList<FieldGeographicElement> elements;
        private Boolean staticGeoField;

        /**
         * Constructor for creating a tile list of saved tiles
         * @param covidData DPCData report handler
         */
        public TilesAdapter(DPCData covidData) {
            StarredTileSaver saver = StarredTileSaver.getInstance(getContext());

            this.staticGeoField = false;
            this.last = new ArrayList<>();
            this.lastLast = new ArrayList<>();
            this.elements = saver.getSavedTiles();

            for (FieldGeographicElement fge : this.elements) {
                Integer last = covidData.getValuesFromGeoField(fge).get(covidData.getReportsNumber() -1);
                if (last != null) {
                    Integer lastLast = last - covidData.getValuesFromGeoField(fge).get(covidData.getReportsNumber() -2);
                    this.last.add(last);
                    this.lastLast.add(lastLast);
                }
            }

        }
        /**
         * Constructor for creating a tile list from a global static GeographicElement
         * @param covidData DPCData report handler
         * @param global the global GeographicElement for obtain reports
         */
        public TilesAdapter(DPCData covidData, GeographicElement global) {
            DailyReport[] reports = covidData.getReportFromGeoElement(global);

            this.staticGeoField = true;
            this.elements = new ArrayList<>();
            this.last = new ArrayList<>();
            this.lastLast = new ArrayList<>();

            ArrayList<String> keys = reports[reports.length - 1].getKeys(getResources());

            //For each key beautify the string
            for (int i = 0; i < keys.size(); i++) {
                FieldGeographicElement fge = new FieldGeographicElement(global, keys.get(i));
                Integer last = covidData.getValuesFromGeoField(fge).get(reports.length -1);
                //String s = this.elements.add();
                if (last != null) {
                    Integer lastLast = last - covidData.getValuesFromGeoField(fge).get(reports.length -2);
                    this.elements.add(fge);
                    this.last.add(last);
                    this.lastLast.add(lastLast);
                }
            }
            //this.fields = fields.toArray(new String[0]);
        }

        @Override
        public DataTilesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView v = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_tile, parent, false);
            DataTilesHolder vh = new DataTilesHolder(v, (OnTileClick) getActivity());
            return vh;
        }

        @Override
        public void onBindViewHolder(DataTilesHolder holder, int position) {
            holder.setTile(this.elements.get(position), this.staticGeoField);
            holder.setQty(this.last.get(position));
            holder.setQtyDelta(this.lastLast.get(position));
        }

        @Override
        public int getItemCount() {
            return this.last.size();
        }

        public class DataTilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            // each data item is just a string in this case
            private CardView container;
            private TextView qty;
            private TextView qty_delta;
            private TextView tile_head;
            private CheckBox star;

            private FieldGeographicElement fieldGeographicElement;

            private OnTileClick listener;

            public void setQty(Integer qty) {
                this.qty.setText(qty.toString());
            }

            public void setQtyDelta(Integer qty_delta) {
                this.qty_delta.setText(qty_delta.toString());
            }

            public void setTile(final FieldGeographicElement fieldGeographicElement, Boolean specifyRegion) {
                this.fieldGeographicElement = fieldGeographicElement;
                //If this element exists
                if (StarredTileSaver.getInstance(getContext()).exists(fieldGeographicElement) != -1)
                    //tick the tick
                    this.star.setChecked(true);
                //If there's not a denominazione
                if (specifyRegion)
                    this.tile_head.setText(this.adjustTitleString(fieldGeographicElement.getField()));//only set header text with the tile_head
                else this.tile_head.setText(String.format((String) getResources().getText(R.string.placeholder_concat),
                        this.adjustTitleString(fieldGeographicElement.getField()),
                        fieldGeographicElement.getDenominazione()));

                //setting colors
                if (fieldGeographicElement.getField().equals(getResources().getText(R.string.denominazione_total)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorBlue, null));
                else if (fieldGeographicElement.getField().equals(getResources().getText(R.string.denominazione_active)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorOrange, null));
                else if (fieldGeographicElement.getField().equals(getResources().getText(R.string.denominazione_healed)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorGreen, null));
                else if (fieldGeographicElement.getField().equals(getResources().getText(R.string.denominazione_deaths)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorRed, null));
                else
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorGrey, null));

                //set listener
                this.star.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //star.setBackgroundResource(R.drawable.star);
                        if (isChecked)
                            StarredTileSaver.getInstance(getContext()).saveElement(fieldGeographicElement);
                        else StarredTileSaver.getInstance(getContext()).deleteElement(fieldGeographicElement);

                    }
                });
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
                this.star = v.findViewById(R.id.recycler_tile_star);
                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                listener.onTileClick(fieldGeographicElement);
            }
        }
    }

}
