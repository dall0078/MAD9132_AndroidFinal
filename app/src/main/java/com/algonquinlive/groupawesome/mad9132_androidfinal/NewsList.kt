package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_news_list.*
import kotlinx.android.synthetic.main.news_list_item.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.HttpURLConnection
import java.net.URL

class NewsList : AppCompatActivity() {

    var newsStoryArray = ArrayList<Story?>()

    lateinit var storyAdapter: NewsAdapter

    data class Story(var title: String?, var author: String?, var date: String?, var link: String?, var description: String?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_list)

        Log.d("TEST", "TEST")

        val listItems: ListView = this.addedFavouritesListView

        val myQuery = StoryQuery()
        myQuery.execute()

        storyAdapter = NewsAdapter(this)
        listItems.adapter = storyAdapter
    }

    inner class NewsAdapter(ctx: Context): ArrayAdapter<Story>(ctx, 0) {

        override fun getCount(): Int {
            return newsStoryArray.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(parent.context)
            val result: View
            result = inflater.inflate(R.layout.news_list_item, parent, false)
            val storyTitle = storyListItemTitle
            val story = getItem(position)
            storyTitle.text = story?.title

            return result
        }

        override fun getItem(position: Int): Story? {
            return newsStoryArray[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class StoryQuery : AsyncTask<String, Int, String>() {
        var story: Story? = null
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
            var eventType = xpp.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                Log.d("PARSING: ", "Event Type: ${xpp.eventType}, Event Name: ${xpp.name}")
                when(eventType) {
                    XmlPullParser.START_TAG -> {
                        if (xpp.name == "item") {
                            Log.d("FOUND ITEM TAG", "Creating Story Object")
                            this.story = Story(null, null,null,null, null)
                        } else if (this.story != null) {
                            when {
                                xpp.name == "title" -> this.story?.title = xpp.nextText()
                                xpp.name == "link" -> this.story?.link = xpp.nextText()
                                xpp.name == "pubDate" -> this.story?.date = xpp.nextText()
                                xpp.name == "author" -> this.story?.author == xpp.nextText()
                                xpp.name == "description" -> this.story?.description = xpp.nextText()
                            }
                            Log.d("Added Fields: ", "${this.story?.title}")
                        }
                        publishProgress()
                    }
                    XmlPullParser.END_TAG -> {
                        if (xpp.name == "item") {
                            newsStoryArray.add(this.story)
                            this.story = null
                        }
                    }
                }
                eventType = xpp.next()
            }
            Log.d("Array: ", "$newsStoryArray")
            return "Done"
        }
    }
}
