package com.example.movieapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;




//Class used to add movies to watch list
public class WatchListService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // You don't need to bind to this service, so return null
        return null;
    }


    private static WatchListService instance;
    private ArrayList<String> watchList = new ArrayList<>();

    // Private constructor to prevent instantiation
    public WatchListService() {}

    // Singleton pattern to ensure only one instance of the class is created
    public static WatchListService getInstance() {
        if (instance == null) {
            instance = new WatchListService();
        }
        return instance;
    }

    public List<String> showWatchList() {
        return watchList;
    }


    // Add a movie to the watchlist
    public void addMovieToWatchList(String title) {
        // Create a new Movie object with the given title
        //Movie movie = new Movie(title);

        // Add the movie to the watch list
        watchList.add(title);


    }





    // Get the watchlist
    public ArrayList<String> getWatchList() {
        return watchList;
    }

    // Check if a movie is in the watchlist



}
