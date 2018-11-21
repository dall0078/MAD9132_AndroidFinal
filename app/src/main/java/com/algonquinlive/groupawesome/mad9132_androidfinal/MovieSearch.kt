package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.bluetooth.BluetoothA2dp
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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




    //store required information in data class
    data class MovieData(var movieTitle: String?,
                         var movieReleaseDate: String?,
                         var movieRating: String?,
                         var movieRuntime: String?,
                         var movieActors: String?,
                         var moviePlot: String?,
                         var moviePosterUrl: String?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_search)

        //----------- Set ToolBar -------------//

        //Not fully implemented
        //Toolbar Appears on screen, shows each activity, doesn't navigate yet
        //searchtextview bleeds into it

        var movieSearchToolBar = findViewById<Toolbar>(R.id.movieSearchToolbar)
        setSupportActionBar(movieSearchToolbar)

        //add navigation toolbar
        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        var toggle = ActionBarDrawerToggle(this, drawer, movieSearchToolbar, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        var navView = findViewById<View>(R.id.navigationView)
    }


    //----------- Create OMDB API request -------------//





    //----------- Create Adapter -------------//

    inner class MovieAdapter(ctx: Context) : ArrayAdapter<MovieData>(ctx, 0) {

        override fun getCount(): Int {
            //return size of array
            return favoriteMovieListArray.size
        }

        //inflate view
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var inflater = LayoutInflater.from(parent.getContext())
            var result: View

            result = inflater.inflate(R.layout.activity_movie_search, parent, false)


            return result


        }

    }
}
