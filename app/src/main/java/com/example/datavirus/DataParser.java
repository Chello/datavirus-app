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

/**
 * This class downloads JSONs from the Dipartimento della Protezione Civile's GitHub repository.
 * It also handles the instance for the downloaded data structure, kept by a DPCData object
 */
public class DataParser {

    private LoadingDialog dialog;
    private static DPCData repositoryData;
    private FragmentManager fm;
    private OnDPCDataReady UI;

    /**
     * Returns the instance of the DPCData downloades
     * @return the DPCData instance
     */
    public static DPCData getDPCDataInstance() {
        return repositoryData;
    }

    private static String[] JSONUrl = new String[] {
        "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json",
        "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json",
        "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-province.json"};

    /**
     * Initiallizes the object as frontend.
     * Shows progress and errors in frontend.
     * @param fm FragmentManager, useful for opening LoadingDialog
     * @param UI listener for when DPCData are obtained
     */
    public DataParser(FragmentManager fm, OnDPCDataReady UI) {
        this.UI = UI;
        this.dialog = new LoadingDialog();
        this.fm = fm;
        this.refreshData();
    }

    /**
     * Initializes the object as backend.
     * @param UI
     */
    public DataParser(OnDPCDataReady UI) {
        this.UI = UI;
        this.refreshData();
    }

    /**
     * Refreshes the data.
     * It will be called the OnDPCDataReady::onDPCDataReady() when finishes
     */
    public void refreshData() {
        AsyncTask<String, Integer, String[]> data = new AsyncDownloader().execute(JSONUrl);
    }

    /**
     * Downloads Json Strings asyncronusly
     */
    private class AsyncDownloader  extends AsyncTask<String, Integer, String[]> {

        private Exception excp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog != null)
                dialog.show(fm, "Loading");
        }

        @Override
        protected void onPostExecute(String[] jsonArray) {
            super.onPostExecute(jsonArray);
            //if download has failed
            if (this.excp != null && dialog != null) {
                dialog.setError(this.excp);
                return;
            } else {
                if (dialog != null)
                    dialog.dismiss();
                repositoryData = new DPCData(jsonArray[0], jsonArray[1], jsonArray[2]);
                UI.onDPCDataReady(repositoryData);
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

        /**
         * Returns Json from URL
         * @param Url Url where Json is
         * @return a Json string
         * @throws IOException If error on network
         */
        private String retrieveJSONFromUrl(String Url) throws IOException {
            //Download json from source URL
            Scanner scanner = new Scanner(new URL(Url).openStream(), StandardCharsets.UTF_8.toString());
            scanner.useDelimiter("\\A");
            String scanned = scanner.hasNext() ? scanner.next() : "";
            return scanned;

        }
    }
}
