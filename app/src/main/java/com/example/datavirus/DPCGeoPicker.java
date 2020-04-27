package com.example.datavirus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
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
 * A simple {@link Fragment} subclass.
 * Use the {@link DPCGeoPicker#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DPCGeoPicker extends DialogFragment {

    private DPCGeoAdapter adapter;

    private DPCData covidData;

    public DPCGeoPicker() {
        // Required empty public constructor
    }

    /**
     * This method sets the covidData for
     * @param covidData
     */
    public void setCovidData(DPCData covidData) {
        this.covidData = covidData;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment DPCDataPicker.
     */
    // TODO: Rename and change types and number of parameters
    public static DPCGeoPicker newInstance(DPCData covidData) {
        DPCGeoPicker fragment = new DPCGeoPicker();
        fragment.setCovidData(covidData);
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
        this.setUpSearchView(v);
        return v;

    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_d_p_c_data_picker, null, false);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setView(v).setTitle(R.string.geo_picker_title);
//
//        this.populateRecycler(v);
//        this.setUpSearchView(v);
//        return builder.create();
//    }

    private void populateRecycler(View v) {
        if (v == null) v = getView();
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        this.adapter = new DPCGeoAdapter(this.covidData.getOrderedGeoElements(), (OnDPCGeoListener) getActivity());
        recyclerView.setAdapter(this.adapter);
    }

    private void setUpSearchView(View v) {
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
        private ArrayList<DPCData.GeographicElement> geoElements;
        /**
         * List for searching elements
         */
        private ArrayList<DPCData.GeographicElement> searchList;
        /**
         * Listener for onclick items
         */
        private OnDPCGeoListener listener;

        /**
         * Filter for search an item by SearchView
         */
        private Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<DPCData.GeographicElement> filteredList = new ArrayList<>();
                Resources res = getResources();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(searchList);
                } else {
                    //Adjusting filter pattern
                    String filterPatter = constraint.toString().toLowerCase().trim();
                    //for each item check if searched pattern corresponds
                    for (DPCData.GeographicElement elem : searchList) {
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

        public DPCGeoAdapter(ArrayList<DPCData.GeographicElement> list, OnDPCGeoListener listener) {
            this.geoElements = list;
            this.searchList = new ArrayList<>(list);
            this.listener = listener;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public DPCGeoHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            // create a new view
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate( R.layout.dialog_recycler_item, parent, false);

            DPCGeoHolder vh = new DPCGeoHolder(v, this.listener);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(DPCGeoHolder holder, int position) {
            Resources res = getResources();
            DPCData.GeographicElement current = geoElements.get(position);
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

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return this.geoElements.size();
        }

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
