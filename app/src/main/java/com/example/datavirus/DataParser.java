/**
 * This class downloads and parses the data from https://github.com/pcm-dpc/COVID-19
 */

package com.example.datavirus;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DataParser {

    private LoadingDialog dialog;
    private DPCData repositoryData;
    private FragmentManager fm;
    private OnDPCDataReady UI;
    private Resources res;

    private static String JSONUrl[] = new String[] {
        "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json",
        "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json",
        "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-province.json"};

    public DataParser(Resources res, FragmentManager fm, OnDPCDataReady UI) {
        this.UI = UI;
        this.dialog = new LoadingDialog();
        this.fm = fm;
        this.res = res;
        this.refreshData();
    }

    public boolean refreshData() {
        AsyncTask<String, Integer, String[]> data = new AsyncDownloader().execute(JSONUrl);
        return true;
    }

    public void setUI(OnDPCDataReady UI) {
        this.UI = UI;
        if (this.repositoryData == null) {
            this.refreshData();
        } else {
            this.UI.setDPCData(this.repositoryData);
        }
    }

    private class AsyncDownloader  extends AsyncTask<String, Integer, String[]> {

        private Exception excp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show(fm, "Loading");
        }

        @Override
        protected void onPostExecute(String[] jsonArray) {
            super.onPostExecute(jsonArray);
            //if download has failed
            if (this.excp != null) {
                dialog.setError(this.excp);
                return;
            } else {
                dialog.dismiss();
                repositoryData = new DPCData(res, jsonArray[0], jsonArray[1], jsonArray[2]);
                UI.setDPCData(repositoryData);
            }
        }

        @Override
        protected String[] doInBackground(String... strings) {
            try {
                String[] arr = new String[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    arr[i] = this.retrieveJSONFromUrl(strings[i]);
                }
                Log.d("DataParser", "All JSON download successful");

                return arr;
            } catch (IOException e) {
                this.excp = e;
                Log.e("DataParser", "JSON download error");
                e.printStackTrace();
                return null;
            }
        }

        private String retrieveJSONFromUrl(String Url) throws IOException {
            //Download json from source URL
            Scanner scanner = new Scanner(new URL(Url).openStream(), StandardCharsets.UTF_8.toString());
            scanner.useDelimiter("\\A");
            String scanned = scanner.hasNext() ? scanner.next() : "";
            //Parse into json
            return scanned;

        }
    }
}
