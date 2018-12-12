package com.algonquinlive.groupawesome.mad9132_androidfinal


//----------------------------------------------------------------//
//                        MAD9132 Android Final
//                       Movie Details Activity
//                              Nov 2018
//----------------------------------------------------------------//



import android.content.ContentValues

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView

import android.widget.Toast
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.activity_start.*


class MovieDetails : AppCompatActivity() {


    lateinit var movieSnackbarMessage:String
    lateinit var db: SQLiteDatabase
    lateinit var dbHelper: MovieDatabaseHelper
    //lateinit var results: Cursor

    var receivedMovie = MovieSavedList.FavoriteMovies(null, null, null, null, null, null, null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)


//        Set Toolbar
      //  NavigationClickHandler(this).initializePage()
        val toolbar = nav_toolbar
        setSupportActionBar(toolbar)




        dbHelper = MovieDatabaseHelper()
        db = dbHelper.writableDatabase

//Cancel button login to return to previous activity
        var movieDetailsCancelBtn = findViewById<Button>(R.id.movieDetailsCancel)
        movieDetailsCancelBtn.setOnClickListener {

            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)

        val movieTitle = data?.getStringExtra("title")
        val movieReleaseDate = data?.getStringExtra("release")
        val movieRating = data?.getStringExtra("rated")
        val movieRuntime = data?.getStringExtra("runtime")
        val movieActors = data?.getStringExtra("actors")
        val moviePlot = data?.getStringExtra("plot")
        //val moviePosterUrl = data?.getStringExtra("poster")


        val movieTitleView = findViewById<TextView>(R.id.movieDetailsTitle)
        val movieReleaseDateView = findViewById<TextView>(R.id.movie_ReleaseDate)
        val movieRatingView = findViewById<TextView>(R.id.movie_Rating)
        val movieRuntimeView = findViewById<TextView>(R.id.movie_Runtime)
        val movieActorsView = findViewById<TextView>(R.id.movie_Actors)
        val moviePlotView = findViewById<TextView>(R.id.movie_Plot)
//        TODO: Need poster
        //need poster

        movieTitleView.text = movieTitle.toString()
        movieReleaseDateView.text = movieReleaseDate.toString()
        movieRatingView.text = movieRating.toString()
        movieRuntimeView.text = movieRuntime.toString()
        movieActorsView.text = movieActors.toString()
        moviePlotView.text = moviePlot.toString()
        //need poster


//        Snackbar assignment
        movieSnackbarMessage = movieTitle.toString()

    }

// ------------------- Create Menu Here, can recycle code. -----------------------//

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.movie_list_tool_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.favourite_news_toolbar_menu_button -> {
                var intent = Intent(this, FavouriteArticles::class.java)
                startActivity(intent)
            }

            R.id.foodHelpIcon -> {

                var dialogStuff = layoutInflater.inflate(R.layout.movie_help_dialog, null)

                var builder =  AlertDialog.Builder(this)
                builder.setTitle("About Movie Search")
                builder.setView(dialogStuff) //insert view into dialog

                // Add the buttons
                builder.setPositiveButton(R.string.food_help_dialog_done, {dialog, id -> })

                // Create the AlertDialog
                var dialog = builder.create()
                dialog.show()
            }

            R.id.item_cbc ->{

                var intent = Intent(this, NewsList::class.java)
                startActivity(intent)
            }

            R.id.item_movie ->{

                var intent = Intent(this, MovieSearch::class.java)
                startActivity(intent)
            }

            R.id.item_bus ->{

                var intent = Intent(this, BusSearch::class.java)
                startActivity(intent)
            }

        }
        return true
    }

//  ----------------------  DATABASE LOGIC HERE ------------------------//

//Database variables
    val DATABASE_NAME = "FavouriteMovies.db"
    val VERSION_NUM = 1

//    Anon object
object FavoriteMovieContract {

    object FavMovie : BaseColumns {
        const val TABLE_NAME = "Movies"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_RELEASE = "release"
        const val COLUMN_NAME_RATING = "rating"
        const val COLUMN_NAME_RUNTIME = "runtime"
        const val COLUMN_NAME_ACTORS = "actors"
        const val COLUMN_NAME_PLOT = "plot"
        const val COLUMN_NAME_POSTER = "poster"
    }
}


    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FavoriteMovieContract.FavMovie.TABLE_NAME}"




    inner class MovieDatabaseHelper : SQLiteOpenHelper(this@MovieDetails, DATABASE_NAME, null, VERSION_NUM) {

        override fun onCreate(db: SQLiteDatabase) {
           // db.execSQL(SQL_CREATE_ENTRIES) //create the table
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL(SQL_DELETE_ENTRIES) //deletes your old data
            //create new table
            onCreate(db)
        }

    }

}
