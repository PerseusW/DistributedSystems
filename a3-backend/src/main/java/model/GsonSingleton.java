package model;

import com.google.gson.Gson;

import java.util.Arrays;

/**
 * Singleton wrapper for Gson.
 */
public class GsonSingleton {

    private static Gson GSON = null;

    public static Gson getInstance() {
        if (GSON == null) {
            GSON = new Gson();
        }
        return GSON;
    }
}
