package com.example.datavirus;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Date;

/**
 * Objects from this class will contain data from Nazionale, Regionale and Provinciale sections.
 * These data are structured as the italian Dipartimento della Protezione Civile JSONs are structured
 */

public class DPCData {

    enum Field {
        NAZIONALE, REGIONALE, PROVINCIALE
    }

    private Gson gson;
    //Reports for territorial data
    private DailyReport[] nazionale;
    private DailyReport[] regionale;
    private DailyReport[] provinciale;

    public DailyReport[] get(Field field) {
        if (field == Field.NAZIONALE)
            return this.nazionale;
        if (field == Field.REGIONALE)
            return this.regionale;
        if (field == Field.PROVINCIALE)
            return this.provinciale;
        return null;
    }

    public DPCData(String nazionaleJson, String regionaleJson, String provincialeJson) {
        this.gson = new Gson();

        this.nazionale = gson.fromJson(nazionaleJson, DailyReport[].class);
        this.regionale = gson.fromJson(regionaleJson, DailyReport[].class);
        this.provinciale = gson.fromJson(provincialeJson, DailyReport[].class);
        Log.d("DPCParse", "Here we are");
    }

    public class DailyReport {

        //private Date data;
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

        public DailyReport(Integer ricoverati_con_sintomi, Integer terapia_intensiva, Integer isolamento_domiciliare, Integer totale_positivi, Integer variazione_totale_positivi, Integer nuovi_positivi, Integer dimessi_guariti, Integer deceduti, Integer totale_casi, Integer tamponi, Integer casi_testati) {
            this.ricoverati_con_sintomi = ricoverati_con_sintomi;
            this.terapia_intensiva = terapia_intensiva;
            this.isolamento_domiciliare = isolamento_domiciliare;
            this.totale_positivi = totale_positivi;
            this.variazione_totale_positivi = variazione_totale_positivi;
            this.nuovi_positivi = nuovi_positivi;
            this.dimessi_guariti = dimessi_guariti;
            this.deceduti = deceduti;
            this.totale_casi = totale_casi;
            this.tamponi = tamponi;
            this.casi_testati = casi_testati;
        }
    }

}
