package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import kotlinx.android.synthetic.main.news_list_item.view.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern


class NewsList : AppCompatActivity() {

    var newsStoryArray = ArrayList<Story?>()

    lateinit var storyAdapter: NewsAdapter

    data class Story(var title: String?, var author: String?, var date: String?, var link: String?, var imageLink: String?, var description: String?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_list)

        val listItems: ListView = newsItemsListView

        listItems.setOnItemClickListener {_, _, position, _ ->

            val intent = Intent(this, NewsDetail::class.java)

            intent.putExtra("title",newsStoryArray[position]?.title)
            intent.putExtra("author",newsStoryArray[position]?.author)
            intent.putExtra("date",newsStoryArray[position]?.date)
            intent.putExtra("link",newsStoryArray[position]?.link)
            intent.putExtra("description",newsStoryArray[position]?.description)
            intent.putExtra("imageLink",newsStoryArray[position]?.imageLink)

            startActivity(intent)
        }

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
            val storyTitle = result.storyListItemTitle
            val story = getItem(position)
            storyTitle.text = story?.title

            val articleImageView = result.storyListItemImage
            val imgSrc = story?.imageLink
            val htmlString = "<img src='$imgSrc'/>"
            articleImageView.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;}</style>" + htmlString, "text/html", "UTF-8", null)

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
                            this.story = Story(null, null,null,null, null, null )
                        } else if (this.story != null) {
                            when {
                                xpp.name == "title" -> this.story?.title = xpp.nextText()
                                xpp.name == "link" -> this.story?.link = xpp.nextText()
                                xpp.name == "pubDate" -> this.story?.date = xpp.nextText()
                                xpp.name == "author" -> this.story?.author == xpp.nextText()
                                xpp.name == "description" -> {
                                    this.story?.description = xpp.nextText()
                                    this.story?.imageLink = PatternMatcherGroupHtml.main(story!!.description!!)
                                    this.story?.description = PatternMatcherGroupHtmlText.main(story!!.description!!)
                                }
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

        override fun onPostExecute(result: String?) {
            storyAdapter.notifyDataSetChanged()
        }
    }

    object PatternMatcherGroupHtml {

        @JvmStatic
        fun main(arg: String): String {
            val p = Pattern.compile("src='(\\S+)'")
            val m = p.matcher(arg)
            // if we find a match, get the group
            if (m.find()) {
                // get the matching group
                val codeGroup = m.group(1)
                // print the group
                System.out.format("'%s'\n", codeGroup)
                return codeGroup
            }
            return arg
        }
    }
    object PatternMatcherGroupHtmlText {

        @JvmStatic
        fun main(arg: String): String {
            val p = Pattern.compile("<p>(.*?)</p>")
            val m = p.matcher(arg)
            // if we find a match, get the group
            if (m.find()) {
                // get the matching group
                val codeGroup = m.group(1)
                // print the group
                System.out.format("'%s'\n", codeGroup)
                return codeGroup
            }
            return arg
        }
    }
}
