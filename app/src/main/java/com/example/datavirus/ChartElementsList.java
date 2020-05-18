package com.example.datavirus;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChartElementsList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartElementsList extends Fragment {

    private ChartElementsAdapter adapter;

    public ChartElementsList() {}

    /**
     * Creates a new instance of fragment ChartElementsList.
     * @return A new instance of fragment ChartElementsList.
     */
    public static ChartElementsList newInstance() {
        ChartElementsList fragment = new ChartElementsList();
        return fragment;
    }

    /**
     * Populates the recyclerview, for the list of the chart elements
     * @param v the yet created view
     */
    private void populateRecycler(View v) {
        if (v == null) v = getView();
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.chart_elements_recycler);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager layoutManager = linearLayoutManager;
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        this.adapter = new ChartElementsAdapter();
        recyclerView.setAdapter(this.adapter);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chart_elements_list, container, false);
        populateRecycler(v);
        return v;
    }

    /**
     * Adapter for the Recyclerview of the list of elements plotted in chart
     */
    public class ChartElementsAdapter extends RecyclerView.Adapter<ChartElementsAdapter.ChartElementsHolder> {

        private ArrayList<String> elements;
        private ArrayList<Integer> colors;
        private ArrayList<Boolean> visible;

        /**
         * Constructor of the adapter. Uses the ChartModel singleton
         */
        public ChartElementsAdapter() {
            this.elements = ChartModel.getInstance().getElementsName();
            this.colors = ChartModel.getInstance().getElementsColor();
            this.visible = ChartModel.getInstance().getElementsVisible();
        }

        @NonNull
        @Override

        public ChartElementsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chart_element_recycler_item, parent, false);
            ChartElementsHolder vh = new ChartElementsHolder(v, (OnChartElementActions) getActivity());
            return vh;
        }

        /**
         * Binds recyclerview items with data
         * @param holder the holder of the recyclerview element
         * @param position the position of the recyclerview element
         */
        @Override
        public void onBindViewHolder(@NonNull ChartElementsHolder holder, int position) {
            holder.setDenominazione(this.elements.get(position));
            holder.setColor(this.colors.get(position));
            holder.setPos(position);
            holder.setChecked(this.visible.get(position));
        }

        /**
         * Returns the count of elements in the recyclerview
         * @return the count of elements in the recyclerview
         */
        @Override
        public int getItemCount() {
            return this.elements.size();
        }

        /**
         * Holder for the Chart Elements Recyclerview
         */
        public class ChartElementsHolder extends RecyclerView.ViewHolder {

            private TextView denominazione;
            private CheckBox visible;
            private Integer pos;
            private CardView color;

            /**
             * Constructor for the holder. It also sets listener for the checkbox click and delete button
             * @param v
             * @param listener
             */
            public ChartElementsHolder(View v, final OnChartElementActions listener) {
                super(v);
                this.denominazione = v.findViewById(R.id.chart_element_recycler_denominazione);
                this.visible = v.findViewById(R.id.chart_element_recycler_visible_checkbox);
                this.setChecked(true);
                this.color = v.findViewById(R.id.chart_element_recycler_color);
                ImageButton delete = v.findViewById(R.id.chart_element_recycler_delete);
                this.visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ChartModel cm = ChartModel.getInstance();
                        cm.setElementVisible(pos, isChecked);
                        listener.onChartElementActions();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChartModel cm = ChartModel.getInstance();
                        cm.deleteItem(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, cm.getSize());
                        listener.onChartElementActions();
                    }
                });
            }

            /**
             * Sets the title of the recyclerview item
             * @param denominazione
             */
            public void setDenominazione(String denominazione) {
                this.denominazione.setText(DPCData.trimTitleString(denominazione));
            }

            /**
             * Set visible checkbox
             * @param check the checkbox status
             */
            public void setChecked(Boolean check) {
                this.visible.setChecked(check);
            }

            /**
             * Set color of the line representing a data in chart
             * @param color color for the dataset
             */
            public void setColor(Integer color) {
                this.color.setBackgroundColor(color);
            }

            /**
             * Sets the position of the holder
             * @param pos position of the holder
             */
            public void setPos(Integer pos) {
                this.pos = pos;
            }
        }
    }
}
