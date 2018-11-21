package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toolbar
import kotlinx.android.synthetic.main.activity_movie_search.*


//----------------------------------------------------------------//
//                        MAD9132 Android Final
//                        Movie Search Activity
//                              Nov 2018
//----------------------------------------------------------------//


class MovieSearch : AppCompatActivity() {

    //----------- Declare Variables -------------//

    var favoriteMovieListArray = ArrayList<MovieData>()

    //Create movieListAdapter
    //lateinit var movieListAdapter: MovieAdapter

    //variable for search result pass/fail
    var successfulSearch = false



    //store required information in data class
    data class MovieData(var movieTitle: String?, var movieReleaseDate: String?, var movieRating: String?, var movieRuntime: String?, var movieActors: String?, var moviePlot: String?, var moviePosterUrl: String?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_search)

        //----------- Set ToolBar -------------//

        var movieSearchToolBar = findViewById<Toolbar>(R.id.movieSearchToolbar)
        setSupportActionBar(movieSearchToolbar)

    }

    //----------- Create OMDB API request -------------//





    //----------- Create Adapter -------------//


}
