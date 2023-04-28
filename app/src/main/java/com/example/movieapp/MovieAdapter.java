package com.example.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movieList;
    private OnMovieClickListener onMovieClickListener; // Listener for movie clicks

    private OnWatchListClickListener OnWatchListClickListener;

    // Define interface for watchlist click listener
    public interface OnWatchListClickListener {
        void onWatchlistClicked(Movie movie);
    }

    // Setter method for movie click listener
    public void setOnMovieClickListener(OnMovieClickListener onMovieClickListener) {
        this.onMovieClickListener = onMovieClickListener;
    }

    // Define interface for watchlist click listener



    // Constructor with context, list of movies, and listener for movie clicks

    //creating constructor for Movie adapter for if the user searches for the movie to update movie list
    public void setMovies(List<Movie> movies) {
        this.movieList = movies;
        notifyDataSetChanged();
    }

    //creating adapter giving it the context ,list of movies,and the on click event for each movie in the recycler view
    public MovieAdapter(Context context, List<Movie> movieList, OnMovieClickListener onMovieClickListener) {
        this.context = context;
        this.movieList = movieList;
        this.onMovieClickListener = onMovieClickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the movie item layout
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item_layout, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        // Bind movie data to view
        Movie movie = movieList.get(position);
        holder.titleTextView.setText(movie.getTitle());
        holder.releaseDateTextView.setText(movie.getReleaseDate());
        holder.overviewTextView.setText(movie.getOverview());

        // Load movie poster using Picasso
        String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
        Picasso.get().load(posterUrl).into(holder.posterImageView);



        // Set click listener for movie view
        holder.itemView.setOnClickListener(v -> {
            if (onMovieClickListener != null) {
                onMovieClickListener.onMovieClick(movie); // Call onMovieClick method on listener
            }
        });




    }

    //getting count of movie list
    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        //creating variables
        public ImageView posterImageView;
        public TextView titleTextView;
        public TextView releaseDateTextView;
        public TextView overviewTextView;

        public Button watchlistButton;

        //getting the image views from the layout file
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.moviePoster);
            titleTextView = itemView.findViewById(R.id.movieTitle);
            releaseDateTextView = itemView.findViewById(R.id.movieReleaseDate);
            overviewTextView = itemView.findViewById(R.id.movieOverview);
        }
    }

    // Interface for movie click listener
    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public interface WatchListClickListener {
        void onWatchlistClicked(Movie movie);
    }
}

