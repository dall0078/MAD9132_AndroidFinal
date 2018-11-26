package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_news_list.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.HttpURLConnection
import java.net.URL

class NewsList : AppCompatActivity() {

    var newsStoryArray = ArrayList<Story>()

    data class Story(var title: String?, var author: String?, var date: String?, var link: String?, var content: String?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_list)

        Log.d("TEST", "TEST")

        var listItems: ListView?=null

        listItems = this.addedFavouritesListView

        val myQuery = StoryQuery()
        myQuery.execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class StoryQuery : AsyncTask<String, Int, String>() {
        var story: Story? = null
        var title: String? = null
        var author: String? = null
        var date: String? = null
        var content: String? = null
        var progress = 0
        lateinit var bitmap: Bitmap

        override fun doInBackground(vararg params: String?): String {
            val url = URL("https://www.cbc.ca/cmlink/rss-world")

            val connection = url.openConnection() as HttpURLConnection
            val response = connection.inputStream

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val xpp = factory.newPullParser()
            xpp.setInput(response, "UTF-8")

            while (xpp.eventType != XmlPullParser.END_DOCUMENT) {
                Log.d("STARTING PARSER", "STARTING PARSER")
                when(xpp.eventType) {
                    XmlPullParser.START_TAG -> {
                        if (xpp.name == "item") {
                            Log.d("FOUND ITEM TAG", "CREATING STORY CLASS")
                            this.story = Story(null, null,null,null, null)
                        } else if (this.story != null) {
                            when {
                                xpp.name == "title" -> this.story!!.title = xpp.nextText()
                                xpp.name == "link" -> this.story!!.link = xpp.nextText()
                                xpp.name == "pubDate" -> this.story!!.date = xpp.nextText()
                                xpp.name == "author" -> this.story!!.author == xpp.nextText()
                                xpp.name == "description" -> this.story!!.content = xpp.nextText()
                            }
                        }
                        publishProgress()
                    }
                }
            }
            return "Done"
        }
    }
}
