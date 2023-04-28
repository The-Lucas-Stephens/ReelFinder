package com.example.movieapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import com.example.movieapp.MovieDetailsBottomSheetDialogFragment;




public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickListener {

    //important string variables
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String API_KEY = "5134b3f56c2cae575bb0ad435f0be5ee";
    private static final String API_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;

    //creating variables
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private ProgressBar progressBar;

    private EditText searchEditText;
    private Button searchButton;
    private Button showWatchListButton;


    //creating list of movie objects
    private List<Movie> movieList = new ArrayList<>();

    private List<Movie> watchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting show watch list button

        showWatchListButton = findViewById(R.id.showWatchListButton);

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ProgressBar
        progressBar = findViewById(R.id.progress_bar);

        // Set up the adapter
        movieAdapter = new MovieAdapter(this, movieList, this);
        recyclerView.setAdapter(movieAdapter);

        // Make API call to fetch movies
        fetchMovies();


        //listener to show the watch list
        showWatchListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the instance of the WatchListService that was used to add movies
                WatchListService watchListService = WatchListService.getInstance();
                List<Movie> watchList = watchListService.getWatchList();

                if (watchList.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Watch list is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a StringBuilder object to build a string of movie titles from the watch list
                StringBuilder sb = new StringBuilder();
                for (Movie movie : watchList) {
                    sb.append(movie.getTitle()).append("\n");
                }

                // Make toast to the screen to show watch list to the user
                Toast.makeText(getApplicationContext(), "Watch List:\n" + sb.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        //listener for if search button is clicked and user wants to search movies
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSearchButtonClick();
            }
        });


    }

//method to fetch movies when the app first loads
    private void fetchMovies() {
        // Show ProgressBar before making API call
        progressBar.setVisibility(View.VISIBLE);

            //creating the JSON request to the movie db api using teh Volley Library
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                response -> {
                    try {
                        //using GSON to parse the json response
                        Gson gson = new Gson();
                        JSONArray results = response.getJSONArray("results");
                        //for every object in the response run this code
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject movieObject = results.getJSONObject(i);
                            Movie movie = gson.fromJson(movieObject.toString(), Movie.class);
                            movieList.add(movie);

                            Log.d(TAG, "Title: " + movie.getTitle());
                            Log.d(TAG, "Release date: " + movie.getReleaseDate());
                            Log.d(TAG, "Overview: " + movie.getOverview());
                            Log.d(TAG, "Poster path: " + movie.getPosterPath());
                            Log.d(TAG, "ID: " + movie.getId());






                            // Notify adapter of data change
                            movieAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON error: " + e.getMessage());
                    }

                    // Hide ProgressBar after API call is finished
                    progressBar.setVisibility(View.GONE);
                },
                error -> {
                    Log.e(TAG, "Volley error: " + error.getMessage());
                    // Hide ProgressBar on error
                    progressBar.setVisibility(View.GONE);
                }
        );

        // Add request to Volley request queue
        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    // On movie item click, create and show the movie details bottom sheet
    @Override
    public void onMovieClick(Movie movie) {
        // Create a new instance of the MovieDetailsBottomSheetDialogFragment and pass it the movie ID as an argument
        MovieDetailsBottomSheetDialogFragment bottomSheetDialogFragment = MovieDetailsBottomSheetDialogFragment.newInstance(movie.getId());

        // Show the bottom sheet dialog fragment
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    //method to handle click event for the button at the top when user wants to search movies

    //method to handle user clicking the search button
    private void handleSearchButtonClick() {
        //if there is nothing in the search bar let the user know to enter query
        String query = searchEditText.getText().toString();
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show();
            return;
        }

        //URL of the api call using to pull results based on user search
        String url = "https://api.themoviedb.org/3/search/movie?api_key=5134b3f56c2cae575bb0ad435f0be5ee&query=" + query;

        //getting the JSON response using Volley
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // Define a new List to hold the new data
                        List<Movie> newMovies = new ArrayList<>();
                            try {
                                //Using GSON to parse json response
                                Gson gson = new Gson();
                                //for every object in the list parse the JSON array and adding them to movie list
                                JSONArray results = response.getJSONArray("results");
                                for (int i = 0; i < results.length(); i++) {
                                    JSONObject movieObject = results.getJSONObject(i);
                                    Movie movie = gson.fromJson(movieObject.toString(), Movie.class);
                                    movieList.add(movie);

                                    Log.d(TAG, "Title: " + movie.getTitle());
                                    Log.d(TAG, "Release date: " + movie.getReleaseDate());
                                    Log.d(TAG, "Overview: " + movie.getOverview());
                                    Log.d(TAG, "Poster path: " + movie.getPosterPath());
                                    Log.d(TAG, "ID: " + movie.getId());

                                    //add new movies to the new search list
                                    newMovies.add(movie);


                                    // Set the new data in your adapter
                                    movieAdapter.setMovies(newMovies);

                                    // Notify adapter of data change
                                    movieAdapter.notifyDataSetChanged();

                                }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error fetching movies", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add request to Volley request queue
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }


    //update movie list with new movies

    public void setMovies(List<Movie> movies) {
        this.movieList = movies;
    }


    private BroadcastReceiver watchListUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


// ...
            MainActivity.this.startService(intent);

            if (intent.getAction().equals(WatchListService.ACTION_WATCH_LIST_UPDATED)) {
                // Update the watch list UI here
                WatchListService watchListService = WatchListService.getInstance();
                List<Movie> watchList = watchListService.showWatchList();
                // Create a StringBuilder object to build a string of movie titles from the watch list
                StringBuilder sb = new StringBuilder();
                for (Movie movie : watchList) {
                    sb.append(movie.getTitle()).append("\n");
                }
                // Show the watch list in a Toast
                Toast.makeText(MainActivity.this, "Watch List Updated:\n" + sb.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    //on resume method for broadcaster
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(watchListUpdateReceiver, new IntentFilter(WatchListService.ACTION_WATCH_LIST_UPDATED));
    }


    //on pause method for broadcaster
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(watchListUpdateReceiver);
    }





}

