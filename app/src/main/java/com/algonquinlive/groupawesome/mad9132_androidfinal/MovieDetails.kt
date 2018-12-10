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

import android.widget.Toast
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.activity_start.*


class MovieDetails : AppCompatActivity() {


    lateinit var db: SQLiteDatabase
    lateinit var dbHelper: MovieDatabaseHelper
    lateinit var results: Cursor

    var receivedMovie = MovieSavedList.FavoriteMovies(null, null, null, null, null, null, null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

//        Set Toolbar
        val toolbar = nav_toolbar
        setSupportActionBar(toolbar)

//        Add to favs button assignment
        val addFavButton = movie_detail_AddButton


        dbHelper = MovieDatabaseHelper()
        db = dbHelper.writableDatabase

//        Create click listener for addFavButton to store selected movie.
        addFavButton.setOnClickListener {
            val movieValues = ContentValues().apply {
                put(MovieSavedList.FavoriteMovieContract.FavMovie.COLUMN_NAME_TITLE, receivedMovie.movieTitle)
                put(MovieSavedList.FavoriteMovieContract.FavMovie.COLUMN_NAME_RELEASE, receivedMovie.movieReleaseDate)
                put(MovieSavedList.FavoriteMovieContract.FavMovie.COLUMN_NAME_RATING, receivedMovie.movieRating)
                put(MovieSavedList.FavoriteMovieContract.FavMovie.COLUMN_NAME_RUNTIME, receivedMovie.movieRuntime)
                put(MovieSavedList.FavoriteMovieContract.FavMovie.COLUMN_NAME_ACTORS, receivedMovie.movieActors)
                put(MovieSavedList.FavoriteMovieContract.FavMovie.COLUMN_NAME_PLOT, receivedMovie.moviePlot)
                put(MovieSavedList.FavoriteMovieContract.FavMovie.COLUMN_NAME_POSTER, receivedMovie.moviePosterUrl)
            }

            db.insert(MovieSavedList.FavoriteMovieContract.FavMovie.TABLE_NAME, null, movieValues)

//            Create toast for user feedback
            Toast.makeText(this@MovieDetails, "Added Movie to list of Favourites", Toast.LENGTH_SHORT)
                .show()
        }
//        Create click listener for Cancel button
        movieDetailsCancel.setOnClickListener {
//            TODO: CANCEL CLICK LISTENER
        }

        onActivityResult(50,2,intent)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)

        receivedMovie.movieTitle = data?.getStringExtra("title")
        receivedMovie.movieReleaseDate = data?.getStringExtra("release")
        receivedMovie.movieRating = data?.getStringExtra("rated")
        receivedMovie.movieRuntime = data?.getStringExtra("runtime")
        receivedMovie.movieActors = data?.getStringExtra("actors")
        receivedMovie.moviePlot = data?.getStringExtra("plot")
        receivedMovie.moviePosterUrl = data?.getStringExtra("poster")


        val movieTitleView = movieDetailsTitle
        val movieReleaseDateView = movieDetailsRelease
        val movieRatingView = movieDetailsRating
        val movieRuntimeView = movieDetailsRuntime
        val movieActorsView = movieDetailsStarring
        val moviePlotView = movieDetailsDescription
//        TODO: Need poster
        //need poster

        movieTitleView.text = receivedMovie.movieTitle
        movieReleaseDateView.text = receivedMovie.movieReleaseDate
        movieRatingView.text = receivedMovie.movieRating
        movieRuntimeView.text = receivedMovie.movieRuntime
        movieActorsView.text = receivedMovie.movieActors
        moviePlotView.text = receivedMovie.moviePlot
        //need poster

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

    private val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${FavoriteMovieContract.FavMovie.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${FavoriteMovieContract.FavMovie.COLUMN_NAME_TITLE} TEXT," +
                "${FavoriteMovieContract.FavMovie.COLUMN_NAME_RELEASE} TEXT," +
                "${FavoriteMovieContract.FavMovie.COLUMN_NAME_RATING} TEXT," +
                "${FavoriteMovieContract.FavMovie.COLUMN_NAME_RUNTIME} TEXT," +
                "${FavoriteMovieContract.FavMovie.COLUMN_NAME_ACTORS} TEXT," +
                "${FavoriteMovieContract.FavMovie.COLUMN_NAME_PLOT} TEXT," +
                "${FavoriteMovieContract.FavMovie.COLUMN_NAME_POSTER} TEXT)"

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FavoriteMovieContract.FavMovie.TABLE_NAME}"




    inner class MovieDatabaseHelper : SQLiteOpenHelper(this@MovieDetails, DATABASE_NAME, null, VERSION_NUM) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(SQL_CREATE_ENTRIES) //create the table
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL(SQL_DELETE_ENTRIES) //deletes your old data
            //create new table
            onCreate(db)
        }

    }

}
