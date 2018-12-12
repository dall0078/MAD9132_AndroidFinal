package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.bluetooth.BluetoothA2dp
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.Movie
import android.os.AsyncTask
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
import com.algonquinlive.groupawesome.mad9132_androidfinal.R.id.nav_toolbar
import kotlinx.android.synthetic.main.activity_movie_saved_list_main_row.*
import kotlinx.android.synthetic.main.activity_movie_search.*
import kotlinx.android.synthetic.main.activity_news_list.*
import org.json.JSONObject
import org.w3c.dom.Text
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


//----------------------------------------------------------------//
//                        MAD9132 Android Final
//                        Movie Search Activity
//                              Nov 2018
//----------------------------------------------------------------//
//TODO: Make progress bar invisible

class MovieSearch : AppCompatActivity() {

//----------- Declare lateinit Variables -------------//

//    TODO: Clean this up, causing uninitialized errors
    lateinit var movieTitle: TextView//textview for movie Title
    lateinit var movieReleaseDate: TextView
    lateinit var movieRating: TextView
    lateinit var movieRuntime: TextView
    lateinit var movieActors: TextView
    lateinit var moviePlot: TextView
    lateinit var userQuery: String




    var favoriteMovieListArray = ArrayList<MovieData?>()
    lateinit var movieAdapter: MovieAdapter

    //store required information in data class
    data class MovieData(var movieTitle: String?,
                         var movieReleaseDate: String?,
                         var movieRating: String?,
                         var movieRuntime: String?,
                         var movieActors: String?,
                         var moviePlot: String?,
                         var moviePosterUrl: String?)



//    database
    lateinit var movieDB: SQLiteDatabase
    lateinit var movieResults: Cursor


    var movieItemPosition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_search)

        var movieSearchProgressBar: ProgressBar = findViewById(R.id.movieSearchProgressBar)
        //movieSearchProgressBar = 0
        movieSearchProgressBar.visibility = View.VISIBLE
        movieTitle = findViewById(R.id.movie_search_input)



        val searchButton = findViewById<Button>(R.id.searchForMovie)
        searchForMovie.setOnClickListener {

            val intent = Intent ()

            intent.putExtra("MovieTitle", movieTitle.text.toString())
            //TODO: Capture user input

//            Toast
            var inputToast = findViewById(R.id.movie_search_input) as EditText
            Toast.makeText(this, inputToast.text, Toast.LENGTH_SHORT).show()
        }


//TODO: Fix navigation handler
        //Navigation handler function, uses NavigationClickHandler Activity
        //NavigationClickHandler(this).initializePage()



            movieTitle = findViewById(R.id.movie_Title)
            movieReleaseDate = findViewById(R.id.movie_ReleaseDate)
            movieRating = findViewById(R.id.movie_Rating)
            movieRuntime = findViewById(R.id.movie_Runtime)
            movieActors = findViewById(R.id.movie_Actors)
            moviePlot = findViewById(R.id.movie_Plot)


//        TODO: DELETE THIS
//        Send selected movie details to MovieDetails activity
//            addMovieButton.setOnItemClickListener {_, _, position, _ ->
//
//            val intent = Intent(this, MovieDetails::class.java)
//
//            //movie data to be sent
//            intent.putExtra("title",favoriteMovieListArray[position]?.movieTitle)
//            intent.putExtra("release",favoriteMovieListArray[position]?.movieReleaseDate)
//            intent.putExtra("rating",favoriteMovieListArray[position]?.movieRating)
//            intent.putExtra("runtime",favoriteMovieListArray[position]?.movieRuntime)
//            intent.putExtra("actors",favoriteMovieListArray[position]?.movieActors)
//            intent.putExtra("plot",favoriteMovieListArray[position]?.moviePlot)
//
//
//            startActivity(intent)
//    }


//        TODO: Finish this add movie intent
        //        Send selected movie details to MovieDetails activity
            addMovieButton.setOnClickListener {

            val intent = Intent(this, MovieDetails::class.java)
//
//            //movie data to be sent
//            intent.putExtra("title",favoriteMovieListArray)
//            intent.putExtra("release",favoriteMovieListArray[position]?.movieReleaseDate)
//            intent.putExtra("rating",favoriteMovieListArray[position]?.movieRating)
//            intent.putExtra("runtime",favoriteMovieListArray[position]?.movieRuntime)
//            intent.putExtra("actors",favoriteMovieListArray[position]?.movieActors)
//            intent.putExtra("plot",favoriteMovieListArray[position]?.moviePlot)
                val addConfirm = "Added movie to Favorites"
                Toast.makeText(this, addConfirm, Toast.LENGTH_SHORT).show()
//
            startActivity(intent)
    }


        //----------- Set ToolBar -------------//

        //Not fully implemented
        //Toolbar Appears on screen, shows each activity, doesn't navigate yet
        //searchtextview bleeds into it

       val toolBar = nav_toolbar
        setSupportActionBar(toolBar)



        val myQuery = MovieQuery()
        myQuery.execute()

        movieAdapter = MovieAdapter(this)
        //listItem.adapter = movieAdapter



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
           // movieTitle?.text = movie.name


            return result


        }

    }




//    Create inner class for Movie Query *ASYNC TASK*
//    XML PULL PARSER HERE

    inner class MovieQuery : AsyncTask<String, Integer, String>() {

        var movieTitle: String? = null
        var movieReleaseDate: String? = null
        var movieRating: String? = null
        var movieRuntime: String? = null
        var movieActors: String? = null
        var moviePlot: String? = null


        var movie: MovieData? = null
        var movieSearchProgress = 0

        lateinit var bitmap: Bitmap


        override fun doInBackground(vararg params: String?): String {
            //connect to the OMDB API with url

            //assign value to userQuery through user input search
            var userQuery = findViewById<EditText>(R.id.movie_search_input).toString()
            //creates URL encoded query for search
            var url = URL("http://www.omdbapi.com/?apikey=6c9862c2&r=xml&&t=" + URLEncoder.encode(userQuery, "UTF-8"))

            var connection = url.openConnection() as HttpURLConnection
            var response = connection.inputStream


            var factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            var xpp = factory.newPullParser()
            xpp.setInput(response, "UTF-8")
            var eventType = xpp.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                Log.d("Parsing: ", "Event Type: ${xpp.eventType}, Event Name: ${xpp.name}")
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (xpp.name == "movie") {

                            Log.d("Found movie tag", "Creating Movie Object")
                            this.movie = MovieData(null, null, null, null, null, null, null)
                        } else if (this.movie != null) {
                            when {

                                xpp.name == "title" -> this.movie?.movieTitle = xpp.nextText() //grabs title from OMDB
                                xpp.name == "year" -> this.movie?.movieReleaseDate = xpp.nextText() //grabs year from OMDB
                                xpp.name == "rated" -> this.movie?.movieRating = xpp.nextText() //grabs rating from OMDB
                                xpp.name == "runtime" -> this.movie?.movieRuntime = xpp.nextText() //grabs runtime from OMDB
                                xpp.name == "actors" -> this.movie?.movieActors = xpp.nextText() //grabs actors from OMDB
                                xpp.name == "plot" -> this.movie?.moviePlot = xpp.nextText() //grabs plot from OMDB
                                //xpp.name == "poster" -> this.movie?.moviePosterUrl = xpp.nextText() //grabs posterurl from OMDB

                            }
                            movieSearchProgress += 100
                        Log.d("Added Fields: ", "${this.movie?.movieTitle}")
                    }
                    publishProgress()
                }

                XmlPullParser.END_TAG -> {
                    if (xpp.name == "movie") {
                        favoriteMovieListArray.add(this.movie)
                        //this.movie = null
                    }
                }
            }

            eventType = xpp.next()
        }
            Log.d("Array: ", "$favoriteMovieListArray")
            return "Done"
        }


        override fun onPostExecute(result: String?) {
                movieAdapter.notifyDataSetChanged()
           // movieSearchProgressbar.visibility = View.INVISIBLE // hide progress bar
            movieSearchProgressBar.visibility = View.INVISIBLE





        }

        override fun onProgressUpdate(vararg values: Integer?) {
            super.onProgressUpdate(*values)
            movieSearchProgressBar.setProgress((movieSearchProgress))
            movieSearchProgressBar.visibility = View.VISIBLE

            movie_Title.text = "Title: $movieTitle"
            movie_ReleaseDate.text = "Release: $movieReleaseDate"
            movie_Actors.text = "Starring: $movieActors"
            movie_Plot.text = "Plot Summary: $moviePlot"
            movie_Rating.text = "Rating: $movieRating"
            movie_Runtime.text = "Runtime: $movieRuntime"

        }




    }

}
