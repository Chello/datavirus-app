package com.example.datavirus;

import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Objects from this class will contain data from Nazionale, Regionale and Provinciale sections.
 * These data are structured as the italian Dipartimento della Protezione Civile JSONs are structured
 */

public class DPCData implements Serializable {

    enum Field {
        NAZIONALE, REGIONALE, PROVINCIALE
    }

    private Gson gson;
    //Reports for territorial data
    private DailyReport[] nazionale;
    private HashMap<String, ArrayList<DailyReport>> regionale;
    private HashMap<String, ArrayList<DailyReport>> provinciale;

    /**
     * Returns the Nazionale report
     * @return the Nazionale report
     */
    public DailyReport[] getNazionale() {
        return this.nazionale;
    }

    /**
     * Returns the Regionale report, by giving it's regione
     * @param regione the regione to obtain
     * @return the Regiona report
     */
    public DailyReport[] getRegionaleReport(String regione) {
        DailyReport[] arr = new DailyReport[this.regionale.size()];
        if (this.regionale.containsKey(regione))
            return this.regionale.get(regione).toArray(arr);
        return null;
    }

    /**
     * Returns the Provinciale report, by giving it's provincia
     * @param provincia the provincia to obtain
     * @return the Provincia report
     */
    public DailyReport[] getProvincialeReport(String provincia) {
        DailyReport[] arr = new DailyReport[this.provinciale.size()];
        if (this.provinciale.containsKey(provincia))
            return this.provinciale.get(provincia).toArray(arr);
        return null;
    }

    public DPCData(String nazionaleJson, String regionaleJson, String provincialeJson) {
        this.gson = new Gson();

        this.nazionale = gson.fromJson(nazionaleJson, DailyReport[].class);
        DailyReport[] regionale = gson.fromJson(regionaleJson, DailyReport[].class);
        DailyReport[] provinciale = gson.fromJson(provincialeJson, DailyReport[].class);

        this.regionale = createKeyValueList(regionale);
        this.provinciale = createKeyValueList(provinciale);

        Log.d("DPCParse", "Data parsed");
    }

    /**
     * Creates the key-value Hash Table, useful for organizing data into field of interest.
     * The field used is DPCData's 'denominazione', obtained via getDenominazione()
     * @param reports the row array to order
     * @return an ordered Hash Table by field ('denominazione')
     */
    private HashMap<String, ArrayList<DailyReport>> createKeyValueList(DailyReport[] reports) {
        //This HashMap contains the ArrayList
        HashMap<String, ArrayList<DailyReport>> build = new HashMap<String, ArrayList<DailyReport>>();
        //Build the reports, by field
        for (DailyReport report : reports) {
            String currentDenominazione = report.getDenominazione();
            //if the array does not yet contains the current 'denominazione'
            if (!build.containsKey(currentDenominazione))
                //create it
                build.put(currentDenominazione, new ArrayList<DailyReport>());
            //put the current report into the hashmap in the right array
            build.get(currentDenominazione).add(report);
        }
        return build;
    }

    public class DailyReport {
        private String data;
        private String denominazione_regione;
        private String denominazione_provincia;
        private Integer ricoverati_con_sintomi;
        private Integer terapia_intensiva;
        private Integer isolamento_domiciliare;
        private Integer totale_positivi;
        private Integer variazione_totale_positivi;
        private Integer nuovi_positivi;
        private Integer dimessi_guariti;
        private Integer deceduti;
        private Integer totale_casi;
        private Integer tamponi;
        private Integer casi_testati;

        public String getDenominazione() {
            return (this.denominazione_provincia != null) ? this.denominazione_provincia : this.denominazione_regione;
        }

        public Integer getRicoverati_con_sintomi() {
            return ricoverati_con_sintomi;
        }

        public Integer getTerapia_intensiva() {
            return terapia_intensiva;
        }

        public Integer getIsolamento_domiciliare() {
            return isolamento_domiciliare;
        }

        public Integer getTotale_positivi() {
            return totale_positivi;
        }

        public Integer getVariazione_totale_positivi() {
            return variazione_totale_positivi;
        }

        public Integer getNuovi_positivi() {
            return nuovi_positivi;
        }

        public Integer getDimessi_guariti() {
            return dimessi_guariti;
        }

        public Integer getDeceduti() {
            return deceduti;
        }

        public Integer getTotale_casi() {
            return totale_casi;
        }

        public Integer getTamponi() {
            return tamponi;
        }

        public Integer getCasi_testati() {
            return casi_testati;
        }

//        public DailyReport(String denominazione_regione, String denominazione_provincia, Integer ricoverati_con_sintomi, Integer terapia_intensiva, Integer isolamento_domiciliare, Integer totale_positivi, Integer variazione_totale_positivi, Integer nuovi_positivi, Integer dimessi_guariti, Integer deceduti, Integer totale_casi, Integer tamponi, Integer casi_testati) {
//            this.ricoverati_con_sintomi = ricoverati_con_sintomi;
//            this.terapia_intensiva = terapia_intensiva;
//            this.isolamento_domiciliare = isolamento_domiciliare;
//            this.totale_positivi = totale_positivi;
//            this.variazione_totale_positivi = variazione_totale_positivi;
//            this.nuovi_positivi = nuovi_positivi;
//            this.dimessi_guariti = dimessi_guariti;
//            this.deceduti = deceduti;
//            this.totale_casi = totale_casi;
//            this.tamponi = tamponi;
//            this.casi_testati = casi_testati;
//        }
    }

}
