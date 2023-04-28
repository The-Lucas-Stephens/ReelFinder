package com.example.movieapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;


//************* Tried to get watch list function to work but could not **************************

//Class used to add movies to watch list
public class WatchListService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // You don't need to bind to this service, so return null
        return null;
    }

    public static final String ACTION_WATCH_LIST_UPDATED = "com.example.movieapp.ACTION_WATCH_LIST_UPDATED";

    public static final String ACTION_ADD_MOVIE = "com.example.movieapp.ACTION_ADD_MOVIE";
    private static WatchListService instance;
    private ArrayList<Movie> watchList = new ArrayList<>();

    // Private constructor to prevent instantiation
    public WatchListService() {}

    // Singleton pattern to ensure only one instance of the class is created
    public static WatchListService getInstance() {
        if (instance == null) {
            instance = new WatchListService();
        }
        return instance;
    }

    public List<Movie> showWatchList() {
        return watchList;
    }


    // Add a movie to the watchlist
    public void addMovieToWatchList(String title) {
        // Create a new Movie object with the given title
        Movie movie = new Movie(title);

        // Add the movie to the watch list
        watchList.add(movie);

        // Send a broadcast message to notify the app that the watch list has been updated
        Intent intent = new Intent(ACTION_WATCH_LIST_UPDATED);
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.sendBroadcast(intent);
    }





    // Get the watchlist
    public ArrayList<Movie> getWatchList() {
        return watchList;
    }

    // Check if a movie is in the watchlist
    public boolean isMovieInWatchList(Movie movie) {
        for (Movie m : watchList) {
            if (m.getId() == movie.getId()) {
                return true;
            }
        }
        return false;
    }

    //get watch list as a string to be displayed to user
    public String getWatchListAsString() {
        StringBuilder sb = new StringBuilder();
        for (Movie m : watchList) {
            sb.append(m.getTitle()).append(", ");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length()); // Remove last comma and space
            return sb.toString();
        } else {
            return "Watchlist is empty";
        }
    }

}
