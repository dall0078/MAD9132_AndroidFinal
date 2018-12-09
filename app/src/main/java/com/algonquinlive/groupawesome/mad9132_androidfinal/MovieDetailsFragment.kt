package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_movie_details.*
import org.w3c.dom.Text

class MovieDetailsFragment : Fragment() {

//    snackbar variable
    lateinit var movieSnackbarMessage:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //Inflate layout for movieDetails fragment

        //Retrieve data from arguments
        var movieDataPassed = arguments

//
        var movieTitleFragment = movieDataPassed?.getString("movie-title")
        var movieReleaseFragment = movieDataPassed?.getString("movie-released")
        var movieRatingFragment = movieDataPassed?.getString("movie-rated")
        var movieRuntimeFragment = movieDataPassed?.getString("movie-runtime")
        var movieStarringFragment = movieDataPassed?.getString("movie-actors")
        var moviePlotFragment = movieDataPassed?.getString("movie-plot")
        //var moviePosterFragment = movieDataPassed?.get("Movie-Poster")

//        inflate layout
        var movieFragmentScreen = inflater?.inflate(R.layout.activity_movie_details, container, false)

//        assign findViewById's
//        TODO: movieposter image and URL

        var movieTitle = movieFragmentScreen.findViewById<TextView>(R.id.movieDetailsTitle)
        var movieRelease = movieFragmentScreen.findViewById<TextView>(R.id.movieDetailsRelease)
        var movieRating = movieFragmentScreen.findViewById<TextView>(R.id.movieDetailsRating)
        var movieRuntime = movieFragmentScreen.findViewById<TextView>(R.id.movieDetailsRuntime)
        var movieStarring = movieFragmentScreen.findViewById<TextView>(R.id.movieDetailsStarring)
        var moviePlot = movieFragmentScreen.findViewById<TextView>(R.id.movieDetailsDescription)
        //var moviePosterFragment = movieFragmentScreen.findViewById<ImageView>(R.id.movieDetailsPoster)


//        snackbar

        movieSnackbarMessage = movieTitleFragment.toString()

//        fill containers
        movieTitle.text = "Movie Title: $movieTitleFragment"
        movieRelease.text = movieReleaseFragment
        movieRating.text = movieRatingFragment
        movieRuntime.text = movieRuntimeFragment
        movieStarring.text = movieStarringFragment
        moviePlot.text = moviePlotFragment


//        Cancel button to return to list of saved movies.
        var movieDetailsCancelButton = movieFragmentScreen.findViewById<Button>(R.id.movieDetailsCancel)
        movieDetailsCancelButton.setOnClickListener {
            movieFragmentScreen.visibility = View.VISIBLE
        }

//       Delete button to remove movie from list of saved movies
        var movieDeleteButton = movieFragmentScreen.findViewById<Button>(R.id.movieDeleteButton)
        movieDeleteButton.setOnClickListener {

            //movieSnackbarMessage should hold movie title
            Snackbar.make(movieDeleteButton, "Do you still want to delete $movieSnackbarMessage +?", Snackbar.LENGTH_LONG)
                .setAction("Yes", {
                        e -> Toast.makeText(getView()?.getContext(), "$movieSnackbarMessage Deleted Successfully", Toast.LENGTH_SHORT).show()
                    movieFragmentScreen.visibility = View.INVISIBLE})
                .show()
            



        }




        //return results
        return movieFragmentScreen
    }
}