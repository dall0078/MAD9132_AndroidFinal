package com.algonquinlive.groupawesome.mad9132_androidfinal


//----------------------------------------------------------------//
//                        MAD9132 Android Final
//                      Movie Saved List Activity
//                              Nov 2018
//----------------------------------------------------------------//

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_movie_saved_list.*
import kotlinx.android.synthetic.main.activity_start.*


class MovieSavedList : AppCompatActivity() {


    lateinit var favoriteMoviesAdapter: FavoriteMoviesAdapter
    var favoriteMoviesArray = mutableListOf<FavoriteMovies>()
    private lateinit var cursor: Cursor

    //create data class
    data class FavoriteMovies(var movieTitle: String?,
                              var movieReleaseDate: String?,
                              var movieRating: String?,
                              var movieRuntime: String?,
                              var movieActors: String?,
                              var moviePlot: String?,
                              var moviePosterUrl: String?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_saved_list)

        //setup toolbar

        val toolBar = nav_toolbar
        setSupportActionBar(toolBar)


        NavigationClickHandler(this).initializePage()






        val favMovieListView = movie_saved_listView

        val dbHelper = MovieDataBaseHelper()
        val db = dbHelper.readableDatabase
        val projection = arrayOf(BaseColumns._ID,
            FavoriteMovieContract.FavMovie.COLUMN_NAME_TITLE,
            FavoriteMovieContract.FavMovie.COLUMN_NAME_RELEASE,
            FavoriteMovieContract.FavMovie.COLUMN_NAME_RATING,
            FavoriteMovieContract.FavMovie.COLUMN_NAME_RUNTIME,
            FavoriteMovieContract.FavMovie.COLUMN_NAME_ACTORS,
            FavoriteMovieContract.FavMovie.COLUMN_NAME_PLOT,
            FavoriteMovieContract.FavMovie.COLUMN_NAME_POSTER)

        cursor = db.query(FavoriteMovieContract.FavMovie.TABLE_NAME, projection, null, null, null, null, null)

        var movieRow: FavoriteMovies?
        with(cursor) {
            while (moveToNext()) {
                movieRow = FavoriteMovies(null, null, null, null, null, null, null)
                movieRow?.movieTitle = getString(getColumnIndexOrThrow(FavoriteMovieContract.FavMovie.COLUMN_NAME_TITLE))
                movieRow?.movieReleaseDate = getString(getColumnIndexOrThrow(FavoriteMovieContract.FavMovie.COLUMN_NAME_RELEASE))
                movieRow?.movieRating = getString(getColumnIndexOrThrow(FavoriteMovieContract.FavMovie.COLUMN_NAME_RATING))
                movieRow?.movieRuntime = getString(getColumnIndexOrThrow(FavoriteMovieContract.FavMovie.COLUMN_NAME_RUNTIME))
                movieRow?.movieActors = getString(getColumnIndexOrThrow(FavoriteMovieContract.FavMovie.COLUMN_NAME_ACTORS))
                movieRow?.moviePlot = getString(getColumnIndexOrThrow(FavoriteMovieContract.FavMovie.COLUMN_NAME_PLOT))
                movieRow?.moviePosterUrl = getString(getColumnIndexOrThrow(FavoriteMovieContract.FavMovie.COLUMN_NAME_POSTER))
                favoriteMoviesArray.add(movieRow!!)
            }
            cursor.close()
        }

        favMovieListView.setOnItemClickListener {_,_, position, _ ->

            val intent = Intent(this, MovieDetails::class.java)

            intent.putExtra("title", favoriteMoviesArray[position].movieTitle)
            intent.putExtra("released", favoriteMoviesArray[position].movieReleaseDate)
            intent.putExtra("rated", favoriteMoviesArray[position].movieRating)
            intent.putExtra("runtime", favoriteMoviesArray[position].movieRuntime)
            intent.putExtra("actors", favoriteMoviesArray[position].movieActors)
            intent.putExtra("plot", favoriteMoviesArray[position].moviePlot)
            intent.putExtra("poster", favoriteMoviesArray[position].moviePosterUrl)

            startActivity(intent)
        }
        favoriteMoviesAdapter = FavoriteMoviesAdapter(this)
        favMovieListView.adapter = favoriteMoviesAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.news_list_tool_bar_menu, menu)
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




    inner class FavoriteMoviesAdapter(ctx: Context): ArrayAdapter<FavoriteMovies>(ctx, 0) {

        override fun getCount(): Int {
            return favoriteMoviesArray.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

//            TODO: finish showing list item data

            val inflater = LayoutInflater.from(parent.context)

            val result: View


            result = inflater.inflate(R.layout.activity_movie_saved_list, parent, false)



            return result
        }

        override fun getItem(position: Int): FavoriteMovies {
            return favoriteMoviesArray[position]
        }


        override fun getItemId(position: Int): Long {
            return 0
        }
    }

    val DATABASE_NAME = "FavoriteMovies.db"
    val VERSION_NUM = 1


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

inner class MovieDataBaseHelper : SQLiteOpenHelper(this@MovieSavedList, DATABASE_NAME, null, VERSION_NUM ) {



    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
}

    //---------- Delete Movie Function -----------//
//    TODO: Remove movie from list logic
//    fun deleteMovie(id: Long)
//    {
//        db.delete(TABLE_NAME, "_ID=$id", null)
//        results = db.query(TABLE_NAME, arrayOf("_id", KEY_MESSAGES), null, null, null, null, null, null)
//        chatMessages.removeAt(messagePosition)
//        myAdapter.notifyDataSetChanged()//update
//    }

}