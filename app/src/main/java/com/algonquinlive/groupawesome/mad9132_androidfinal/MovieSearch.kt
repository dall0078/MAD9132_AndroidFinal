package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.bluetooth.BluetoothA2dp
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.*
import android.widget.*
import kotlinx.android.synthetic.main.activity_movie_saved_list_main_row.*
import kotlinx.android.synthetic.main.activity_movie_search.*
import org.w3c.dom.Text


//----------------------------------------------------------------//
//                        MAD9132 Android Final
//                        Movie Search Activity
//                              Nov 2018
//----------------------------------------------------------------//


class MovieSearch : AppCompatActivity() {

    //----------- Declare Variables -------------//

    var favoriteMovieListArray = ArrayList<MovieData>()

    //----------- Declare lateinit Variables -------------//

    lateinit var listItem: ListView
    lateinit var listAdapter: MovieAdapter
    lateinit var movieTitle: TextView
    lateinit var movieReleaseDate: TextView
    lateinit var movieRating: TextView
    lateinit var movieRuntime: TextView
    lateinit var movieActors: TextView
    lateinit var moviePlot: TextView
    lateinit var moviePosterUrl: TextView
    lateinit var movieSearchUserInput : String

//    progress bar
    lateinit var movieSearchProgressBar: ProgressBar

//    database
    lateinit var movieDB: SQLiteDatabase
    lateinit var movieResults: Cursor
    //lateinit var movieDbHelper: MovieDatabaseHelper //this will be an inner class of SQLite

    var movieItemPosition = 0




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

        val addButton = findViewById<ImageButton>(R.id.addMovieButton)

        addButton.setOnClickListener{
            Toast.makeText(this, "So Toasty", Toast.LENGTH_LONG).show()
        }


        //NavigationClickHandler(this).initializePage()

            val listItems: ListView = movieSearchListView

        listItems.setOnItemClickListener {_, _, position, _ ->

            val intent = Intent(this, MovieDetails::class.java)


        intent.putExtra("title", favoriteMovieListArray[position]?.movieTitle)
        intent.putExtra("release", favoriteMovieListArray[position]?.movieReleaseDate)
        intent.putExtra("rating", favoriteMovieListArray[position]?.movieRating)
        intent.putExtra("runtime", favoriteMovieListArray[position]?.movieRuntime)
        intent.putExtra("starring", favoriteMovieListArray[position]?.movieActors)
        intent.putExtra("plot", favoriteMovieListArray[position]?.moviePlot)
        intent.putExtra("poster", favoriteMovieListArray[position]?.moviePosterUrl)

            startActivity(intent)
    }


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

    //----------- Create menu -------------//
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.movie_list_tool_bar_menu, menu)
        return true
    }

    //-----------Handle selected items in menu -------------//

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.favourite_movies_toolbar_menu_button -> {
                var intent = Intent(this, MovieSavedList::class.java)
                startActivity(intent)
            }

            R.id.foodHelpIcon -> {
                var intent = Intent(this, FoodSearch::class.java)
                startActivity(intent)
            }

            R.id.item_cbc -> {
                var intent = Intent(this, NewsList::class.java)
                startActivity(intent)
            }

            R.id.item_movie -> {
                var intent = Intent(this, MovieSearch::class.java)
                startActivity(intent)
            }

            R.id.item_bus -> {
                var intent = Intent(this, BusSearch::class.java)
                startActivity(intent)
            }
        }
        return true
    }


    //----------- Create OMDB API request -------------//





    //----------- Create Movie Adapter -------------//

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

            //assign values here



            return result


        }

    }

//   ---------------------- Databases ---------------------//

    val DATABASE_NAME = "MovieFavorites.db"
    val VERSION_NUM= 1
    val TABLE_NAME= "FavoriteMovieItem"
    val MOVIEITEMKEY = "MovieItems"

//    Create inner class MovieDatabaseHelper

    inner class MovieDataBaseHelper : SQLiteOpenHelper(this@MovieSearch, DATABASE_NAME, null, VERSION_NUM) {


        override fun onCreate(db: SQLiteDatabase?) {

            //creates saved movie data base table

            db?.execSQL("CREATE TABLE $TABLE_NAME (_id INTEGER PRIMARY KEY AUTOINCREMENT, $MOVIEITEMKEY TEXT)")
            Log.i("MovieDataBaseHelper", "calling OnCreate")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            //delete old data from assigned database
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")

            //create new table from assigned database
            onCreate(db)
            Log.i("MovieDataBaseHelper", "Calling onUpgrade, oldVersion=" + oldVersion + "newVersion" + newVersion)
        }
    }


        //    delete item from array
        fun deleteMovieItem(id:Long) {

        movieDB.delete(TABLE_NAME, "_id=$id", null)

        //    find item in table
        movieResults = movieDB.query(TABLE_NAME, arrayOf("_id", MOVIEITEMKEY),
        null, null, null, null, null, null)


        favoriteMovieListArray.removeAt(movieItemPosition)
        Log.i("MovieSearch", favoriteMovieListArray.toString())

        //reload list of movies
        listAdapter.notifyDataSetChanged()
}

}
