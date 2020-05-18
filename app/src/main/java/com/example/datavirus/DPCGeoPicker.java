package com.example.datavirus;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * The picker for a new GeographicField element
 */
public class DPCGeoPicker extends DialogFragment {

    private DPCGeoAdapter adapter;

    public DPCGeoPicker() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment DPCDataPicker.
     */
    public static DPCGeoPicker newInstance() {
        DPCGeoPicker fragment = new DPCGeoPicker();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_d_p_c_data_picker, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v).setTitle(R.string.geo_picker_title);

        this.populateRecycler(v);
        this.initSearchView(v);
        return v;

    }

    /**
     * Initializes and populates the RecyclerView
     * @param v the fragment view containing the Recycler
     */
    private void populateRecycler(View v) {
        if (v == null) v = getView();
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_geo_picker);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        this.adapter = new DPCGeoAdapter(DataParser.getDPCDataInstance().getOrderedGeoElements());
        recyclerView.setAdapter(this.adapter);
    }

    /**
     * Initializes the SearchView with a listener for the recyclerview
     * @param v the view containing the searchview
     */
    private void initSearchView(View v) {
        SearchView searchView = (SearchView) v.findViewById(R.id.action_search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    class DPCGeoAdapter extends RecyclerView.Adapter<DPCGeoAdapter.DPCGeoHolder> implements Filterable {
        /**
         * All geo elements
         */
        private ArrayList<GeographicElement> geoElements;
        /**
         * List for searching elements
         */
        private ArrayList<GeographicElement> searchList;

        /**
         * Filter for search an item by SearchView
         */
        private Filter filter = new Filter() {

            /**
             * Function that performs filerings in the RecyclerView given by a SearchView
             * @param constraint the searched text
             * @return a FilterResult instance
             */
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<GeographicElement> filteredList = new ArrayList<>();
                Resources res = getResources();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(searchList);
                } else {
                    //Adjusting filter pattern
                    String filterPatter = constraint.toString().toLowerCase().trim();
                    //for each item check if searched pattern corresponds
                    for (GeographicElement elem : searchList) {
                        //Creating string with both definizione and Geographic field concat
                        String toSearchFor = elem.getDenominazione().toLowerCase().trim();
                        if (elem.getGeoField() == DPCData.GeoField.REGIONALE)
                            toSearchFor += res.getString(R.string.region).toLowerCase();
                        else if (elem.getGeoField() == DPCData.GeoField.PROVINCIALE)
                            toSearchFor += res.getString(R.string.province).toLowerCase();

                        //If this string contains what user searched...
                        if (toSearchFor.contains(filterPatter)) {
                            filteredList.add(elem); //Add to return list
                        }
                    }
                }
                FilterResults toReturn = new FilterResults();
                toReturn.values = filteredList;
                return toReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                geoElements.clear();
                geoElements.addAll((ArrayList) results.values);
                notifyDataSetChanged();
            }
        };

        public DPCGeoAdapter(ArrayList<GeographicElement> list) {
            this.geoElements = list;
            this.searchList = new ArrayList<>(list);
        }

        /**
         * Creates a new ViewHolder for the DPCGeoAdapter
         * @param parent the parent ViewGroup
         * @param viewType the viewType
         * @return a new instance of the DPCGeoHolder
         */
        @Override
        public DPCGeoHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate( R.layout.dialog_recycler_item, parent, false);

            DPCGeoHolder vh = new DPCGeoHolder(v, (OnDPCGeoListener) getActivity());
            return vh;
        }

        /**
         * Binds RecyclerView holder with data
         * @param holder the holder to set up
         * @param position the position of the holder's tile
         */
        @Override
        public void onBindViewHolder(DPCGeoHolder holder, int position) {
            Resources res = getResources();
            GeographicElement current = geoElements.get(position);
            if (current.getGeoField() == DPCData.GeoField.REGIONALE) {
                holder.mainText.setText(res.getText(R.string.region));
                holder.mainText.setTextColor(Color.BLUE);
                holder.mainText.setTypeface(holder.mainText.getTypeface(), Typeface.NORMAL);
            } else if (current.getGeoField() == DPCData.GeoField.PROVINCIALE) {
                holder.mainText.setText(res.getText(R.string.province));
                holder.mainText.setTextColor(Color.GREEN);
                holder.mainText.setTypeface(holder.mainText.getTypeface(), Typeface.NORMAL);
            }
            else if (current.getGeoField() == DPCData.GeoField.NAZIONALE) {
                holder.mainText.setText(current.getDenominazione());
                holder.mainText.setTextColor(Color.BLACK);
                holder.mainText.setTypeface(holder.mainText.getTypeface(), Typeface.BOLD);
                holder.secText.setText("");
                return;
            }
            holder.secText.setText(current.getDenominazione());

        }

        /**
         * Returns the item count
         * @return the item count
         */
        @Override
        public int getItemCount() {
            return this.geoElements.size();
        }

        /**
         * Returns the filter for the RecyclerView
         * @return the filter
         */
        @Override
        public Filter getFilter() {
            return filter;
        }

        /**
         * Holder for the click listener
         */
        public class DPCGeoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView mainText;
            public TextView secText;
            public LinearLayout container;
            private OnDPCGeoListener listener;

            public DPCGeoHolder(LinearLayout v, OnDPCGeoListener listener) {
                super(v);
                this.listener = listener;
                v.setOnClickListener(this);
                this.mainText = (TextView) v.findViewById(R.id.dialog_recycler_main);
                this.secText = (TextView) v.findViewById(R.id.dialog_recycler_sec);
                this.container = (LinearLayout) v.findViewById(R.id.recycler_container);
            }

            @Override
            public void onClick(View v) {
                listener.onDPCGeoClick(geoElements.get(getAdapterPosition()));
                dismiss();
            }
        }
    }
}
