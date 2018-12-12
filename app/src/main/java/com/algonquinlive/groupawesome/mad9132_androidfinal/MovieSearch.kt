package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.app.Activity
import android.bluetooth.BluetoothA2dp
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.*
import android.widget.*
import kotlinx.android.synthetic.main.activity_movie_search.*
import kotlinx.android.synthetic.main.activity_news_list.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import android.support.v7.widget.Toolbar
import kotlinx.android.synthetic.main.activity_movie_details.*


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

//        Toolbar
        var movieSearchToolbar = findViewById<Toolbar>(R.id.movieSearchToolbar)
        setSupportActionBar(movieSearchToolbar)


//Progress bar
        var movieSearchProgressBar: ProgressBar = findViewById(R.id.movieSearchProgressBar)
        //movieSearchProgressBar = 0
        movieSearchProgressBar.visibility = View.INVISIBLE
        movieTitle = findViewById(R.id.movie_search_input)



//        Movie Search Button Click Listener
//        Fires when user enters movie title and presses search
        searchForMovie.setOnClickListener {

            val intent = Intent ()

            intent.putExtra("MovieTitle", movieTitle.text.toString())
            //TODO: Capture user input

//            Toast
            var inputToast = findViewById(R.id.movie_search_input) as EditText
            Toast.makeText(this, inputToast.text, Toast.LENGTH_SHORT).show()

            val myQuery = MovieQuery()
            myQuery.execute()

        }

        show_saved_movies_button.setOnClickListener {
            val intent = Intent(this, MovieSavedList::class.java)

            val showFavMovies = "Sent To Saved List"
            Toast.makeText(this, showFavMovies, Toast.LENGTH_SHORT).show()

            startActivity(intent)


            Log.d("Starting Search", "Hopefully.")
        }



            //initialize text views
            movieTitle = findViewById(R.id.movie_Title)
            movieReleaseDate = findViewById(R.id.movie_ReleaseDate)
            movieRating = findViewById(R.id.movie_Rating)
            movieRuntime = findViewById(R.id.movie_Runtime)
            movieActors = findViewById(R.id.movie_Actors)
            moviePlot = findViewById(R.id.movie_Plot)

        //        Send selected movie details to MovieDetails activity
            addMovieButton.setOnClickListener {

                val intent = Intent(this, MovieDetails::class.java)

//                Send data to movie favorite list
                intent.putExtra("title",movieTitle.text.toString())
                intent.putExtra("release",movieReleaseDate.text.toString())
                intent.putExtra("rating", movieRating.text.toString())
                intent.putExtra("runtime", movieRuntime.text.toString())
                intent.putExtra("actors", movieActors.text.toString())
                intent.putExtra("plot", moviePlot.text.toString())
                setResult(Activity.RESULT_OK, intent)

                finish()

//                Toast to show addition confirmation
                val addConfirm = "Added movie to Favorites"
                Toast.makeText(this, addConfirm, Toast.LENGTH_SHORT).show()
//
            startActivity(intent)
    }

        movieAdapter = MovieAdapter(this)

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
                var intent = Intent(this, FoodSearch::class.java)
                startActivity(intent)
            }

            R.id.foodHelpIcon -> {
                var dialogStuff = layoutInflater.inflate(R.layout.movie_help_dialog, null)

                var builder =  AlertDialog.Builder(this)
                builder.setTitle("About Movie Search")
                builder.setView(dialogStuff) //insert view into dialog

                // Add the buttons
                builder.setPositiveButton(R.string.movie_help_dialog_done, {dialog, id -> })

                // Create the AlertDialog
                var dialog = builder.create()
                dialog.show()
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

         //var movieTitle: TextView? = null


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


        var movie: MovieData? = null
        var movieSearchProgress = 0

        lateinit var bitmap: Bitmap



        override fun doInBackground(vararg params: String?): String {
            //connect to the OMDB API with url

            //assign value to userQuery through user input search
            var userQuery = findViewById<EditText>(R.id.movie_search_input).text.toString()
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
                        if (xpp.name.equals( "movie")) {

                            Log.d("Found movie tag", "Creating Movie Object")
                            this.movie = MovieData(null, null, null, null, null, null, null)

                            when {

                                xpp.name == "title" -> this.movie?.movieTitle = xpp.getAttributeValue(null, "title") //grabs title from OMDB
                                xpp.name == "year" -> this.movie?.movieReleaseDate = xpp.getAttributeValue(null, "year") //grabs year from OMDB
                                xpp.name == "rated" -> this.movie?.movieRating = xpp.getAttributeValue(null, "rated") //grabs rating from OMDB
                                xpp.name == "runtime" -> this.movie?.movieRuntime = xpp.getAttributeValue(null, "runtime")//grabs runtime from OMDB
                                xpp.name == "actors" -> this.movie?.movieActors = xpp.getAttributeValue(null, "actors") //grabs actors from OMDB
                                //xpp.name == "plot" -> this.movie?.moviePlot = xpp.getText(null, "plot") //grabs plot from OMDB
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

            movie_Title.text = "Title: " + this.movie?.movieTitle
            movie_ReleaseDate.text = "Release:  ${movieReleaseDate}"
            movie_Actors.text = "Starring:  ${movieActors}"
            movie_Plot.text = "Plot Summary: ${moviePlot} "
            movie_Rating.text = "Rating: ${movieRating} "
            movie_Runtime.text = "Runtime: ${movieRuntime} "
            movieSearchProgressBar.visibility = View.INVISIBLE




        }

        override fun onProgressUpdate(vararg values: Integer?) {
            super.onProgressUpdate(*values)
            movieSearchProgressBar.setProgress((movieSearchProgress))
            movieSearchProgressBar.visibility = View.VISIBLE

        }




    }

}
