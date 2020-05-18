package com.example.datavirus;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Singleton class for read and write the tiles starred
 */
public class ManageStarredTiles {


    private static ManageStarredTiles instance;

    private Context context;

    private ArrayList<FieldGeographicElement> savedTiles;

    /**
     * Returns the list of starred tiles
     * @return the list of starred tiles
     */
    public ArrayList<FieldGeographicElement> getSavedTiles() {
        return savedTiles;
    }

    /**
     * Constructor. It requires a context for getting strings
     * @param context the context
     */
    private ManageStarredTiles(Context context) {
        this.context = context;
        this.savedTiles = new ArrayList<>();
        this.loadFile();
    }

    /**
     * Returns the instance of the singleton ManageStarredTiles
     * @param context the context for initializing the object
     * @return a new instance of ManageStarredTiles
     */
    public static ManageStarredTiles getInstance(Context context) {
        if (instance == null)
            instance = new ManageStarredTiles(context);
        return instance;
    }

    /**
     * Saves an element to the starred list
     * @param geographicElement the FieldGeographicElement of the element to save in starred
     */
    public void saveElement(FieldGeographicElement geographicElement) {

        if (this.getPos(geographicElement) != -1) {
            return;
        }
        this.savedTiles.add(geographicElement);
        this.savetoFile();
        this.loadFile();
    }

    /**
     * Return the position of the searched element, if exist
     * @param geographicElement the element to search
     * @return the position of the element if exist, otherwise returns -1
     */
    public Integer getPos(FieldGeographicElement geographicElement) {
        Integer i;
        for (i = 0; i < this.savedTiles.size(); i++) {
            if (this.savedTiles.get(i).compareTo(geographicElement) == 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Deletes the passed element from the starred list, if exists
     * @param geographicElement the element to delete from starred list
     */
    public void deleteElement(FieldGeographicElement geographicElement) {
        Integer i = this.getPos(geographicElement);
        if (i != -1) {
            Log.d("Salvatore ", "Remosso");
            this.savedTiles.remove((int) i);
        }
        this.savetoFile();
        this.loadFile();
    }

    /**
     * Function that commits to file what it kepy by this instance
     */
    private void savetoFile() {
        try {
            File testFile = new File(this.context.getExternalFilesDir(null), this.context.getString(R.string.starred_file));
            if (!testFile.exists())
                testFile.createNewFile();

            // Adds a line to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, false /*append*/));
            writer.write(new Gson().toJson(this.savedTiles));
            writer.close();

        } catch (IOException e) {
            Log.e("ReadWriteFile", "Unable to write to the starred file.");
        }
    }

    /**
     * Loads data from file and builds the ManageStarredTiles object
     */
    private void loadFile() {
        String textFromFile = "";
        File testFile = new File(this.context.getExternalFilesDir(null), this.context.getString(R.string.starred_file));
        if (testFile != null) {
            StringBuilder stringBuilder = new StringBuilder();
            // Reads the data from the file
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;

                while ((line = reader.readLine()) != null) {
                    textFromFile += line;
                    textFromFile += "\n";
                }
                reader.close();
                FieldGeographicElement[] userArray = new Gson().fromJson(textFromFile, FieldGeographicElement[].class);
                Log.d("Lettura file", textFromFile);
                this.savedTiles = new ArrayList<FieldGeographicElement>(Arrays.asList(userArray));
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the starred file.");
            }
        }
    }

}
