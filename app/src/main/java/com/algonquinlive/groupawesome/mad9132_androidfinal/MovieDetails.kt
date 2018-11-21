package com.algonquinlive.groupawesome.mad9132_androidfinal


//----------------------------------------------------------------//
//                        MAD9132 Android Final
//                       Movie Details Activity
//                              Nov 2018
//----------------------------------------------------------------//

//Not functional, using it to store code for now

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_movie_details.view.*
import org.w3c.dom.Text
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MovieDetails : AppCompatActivity() {


    //Declare Lateinit Variables
   lateinit var title: TextView
    lateinit var release: TextView
    lateinit var rating: TextView
    lateinit var runtime: TextView
    lateinit var starring: TextView
    lateinit var plot: TextView
    lateinit var poster: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var imageBitmap: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        title = findViewById(R.id.movieDetailsTitle)
        release = findViewById(R.id.movieDetailsRelease)
        rating = findViewById(R.id.movieDetailsRating)
        runtime = findViewById(R.id.movieDetailsRuntime)
        starring = findViewById(R.id.movieDetailsStarring)
        plot = findViewById(R.id.movieDetailsDescription)
        poster = findViewById(R.id.movieDetailsPoster)

        var progressBar: ProgressBar = findViewById(R.id.movieDetailsProgressBar)
        val myQuery = MovieDataQuery()
        myQuery.execute()
        progressBar.visibility = View.VISIBLE
    }


    inner class MovieDataQuery : AsyncTask<String, kotlin.Int, String>() {


//movie title, year, rating, runtime, main actors, plot, and URL of the movie poster.
//        create variables
        var movieTitle: String? = null
        var movieRelease: String? = null
        var movieRating: String? = null
        var movieRuntime: String? = null
        var movieStarring: String? = null
        var moviePlot: String? = null

        //query variables
        //convert url to encoded
        var userQuery: String? = null
        var moviePosterUrl = "http://www.omdbapi.com/?apikey=6c9862c2&r=xml&t=" + URLEncoder.encode(userQuery, "UTF-8")

        var iconName: String? = null

        //Progress Bar Progress calculation variable
        var progress = 0


        //9.	Each activity must use an AsyncTask to retrieve data from an http server.
        override fun doInBackground(vararg params: String): String {


//---------------- XMLPullParserRequest and URL Fetch -----------------//

            //still need to validate I manipulated API properly. Pretty Sure I Didnt.
            //mostly boiler plate conversion from lab 6 of AndroidLabs

            val url = URL("http://www.omdbapi.com/?apikey=6c9862c2&r=xml")
            val urlConnection = url.openConnection() as HttpURLConnection
            val stream = urlConnection.getInputStream()


            val factory = XmlPullParserFactory.newInstance()
            factory.setNamespaceAware(false)
            val xpp = factory.newPullParser()
            xpp.setInput(stream, "UTF-8") //set input stream

            while (xpp.eventType != XmlPullParser.END_DOCUMENT) {


                //*FIXED* NEED TO GET API WORKING TO VALIDATE NAMING SCHEME FOR XML PULL *FIXED*
                when (xpp.eventType) {
                    XmlPullParser.START_TAG -> {
                        if (xpp.name.equals("movie")) {
                            movieTitle = xpp.getAttributeValue(null, "title")
                            //title accounts for 20% progress
                            progress += 20
                        } else if (xpp.name.equals("movie")) {
                            movieRelease = xpp.getAttributeValue(null, "released")
                            movieRating = xpp.getAttributeValue(null, "rated")
                            movieRuntime = xpp.getAttributeValue(null, "runtime")
                            moviePlot = xpp.getAttributeValue(null, "plot")
                            //movie data accounts for 60% progress
                            progress += 60
                        }

                        //Grab images
                        if (xpp.name.equals("movie")) {
                            iconName = xpp.getAttributeValue(null, "poster")

                            if (fileExistence("iconName.png")) {

                                var fis: FileInputStream? = null
                                try {
                                    fis = openFileInput("iconName.png")
                                } catch (e: FileNotFoundException) {
                                    e.printStackTrace()
                                }
                                imageBitmap = BitmapFactory.decodeStream(fis)


                            } else {
                                moviePosterUrl = "https://omdbapi.com/img/w/$iconName.png"
                                var bitmap = getImage(moviePosterUrl)
                                val outputStream = openFileOutput("$iconName.png", Context.MODE_PRIVATE);
                                imageBitmap?.compress(Bitmap.CompressFormat.PNG, 80, outputStream)
                                outputStream.flush()
                                outputStream.close()
                            }
                            //image accounts for 20% progress
                            progress += 20
                        }
                        publishProgress()
                    }
                    XmlPullParser.TEXT -> {
                    }
                }

                xpp.next()
            }
            return "Done"
        }


        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            progressBar.setProgress(progress)
            progressBar.visibility = View.VISIBLE
            title.text = movieTitle
            release.text = movieRelease
            rating.text = movieRating
            runtime.text = movieRuntime
            starring.text = movieStarring
            plot.text = moviePlot
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            //set bitmap
            poster.setImageBitmap(imageBitmap)
            // hide progress bar
            progressBar.visibility = View.INVISIBLE
        }

    }


    fun fileExistence(fname: String): Boolean {
        val file = getBaseContext().getFileStreamPath(fname)
        return file.exists()
    }

//Need to replace try catch statements, dont exist in kotlin

    //------------- Get Image Functions ---------------//
    fun getImage(url: URL): Bitmap? {
        var connection: HttpURLConnection? = null
        try {
            connection = url.openConnection() as HttpURLConnection
            connection!!.connect()
            val responseCode = connection!!.getResponseCode()
            return if (responseCode == 200) {
                BitmapFactory.decodeStream(connection!!.getInputStream())
            } else
                null
        } catch (e: Exception) {
            return null
        } finally {
            if (connection != null) {
                connection!!.disconnect()
            }
        }
    }

    fun getImage(urlString: String): Bitmap? {
        try {
            val url = URL(urlString)
            return getImage(url)
        } catch (e: Exception) {
            return null
        }

    }
}
