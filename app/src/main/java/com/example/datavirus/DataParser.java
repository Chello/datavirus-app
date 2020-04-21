/**
 * This class downloads and parses the data from https://github.com/pcm-dpc/COVID-19
 */

package com.example.datavirus;

import android.os.AsyncTask;
import android.util.Log;

import org.json.*;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class DataParser {

    enum Field {
        NAZIONALE, REGIONALE, PROVINCIALE
    }

    private UpdateUI UI;

    private static String JSONUrl[] = new String[] {
        "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json",
        "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json",
        "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-province.json"};

    public DataParser(UpdateUI toUpdate) {
        this.setUI(toUpdate);
    }

    public void setUI(UpdateUI UI) {
        this.UI = UI;
    }

    public boolean refreshData() {
        AsyncTask<String, Integer, ArrayList<String>> data = new AsyncDownloader().execute(JSONUrl);
        return true;
    }

    private class AsyncDownloader  extends AsyncTask<String, Integer, ArrayList<String>> {

        @Override
        protected void onPostExecute(ArrayList<String> jsonArrays) {
            super.onPostExecute(jsonArrays);

            for (String str : jsonArrays) {
                UI.updateData(new DPCData(str));
            }
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            try {
                ArrayList<String> arr = new ArrayList<String>();
                for (String url: strings) {
                    arr.add(retrieveJSONFromUrl(url));
                }
                Log.d("DataParser", "All JSON download successful");

                return arr;
            } catch (IOException e) {
                Log.e("DataParser", "JSON download error");
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                Log.e("DataParser", "JSON parse error");
                e.printStackTrace();
                return null;
            }
        }

        private String retrieveJSONFromUrl(String Url) throws IOException, JSONException {
            //Download json from source URL
            Scanner scanner = new Scanner(new URL(Url).openStream(), StandardCharsets.UTF_8.toString());
            scanner.useDelimiter("\\A");
            String scanned = scanner.hasNext() ? scanner.next() : "";
            //Parse into json
            return scanned;

        }
    }
}
