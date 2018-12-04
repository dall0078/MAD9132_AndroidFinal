package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_favourite_articles.*
import kotlinx.android.synthetic.main.news_list_item.view.*

class FavouriteArticles : AppCompatActivity() {

    lateinit var favouritesAdapter: FavouritesAdapter
    var favouriteArticlesArray = mutableListOf<FavouriteStory>()
    private lateinit var cursor: Cursor

    data class FavouriteStory(var title: String?, var author: String?, var date: String?, var link: String?, var imageLink: String?, var description: String?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_articles)

        val toolBar = nav_toolbar
        setSupportActionBar(toolBar)

        val favouritesListView = activity_favourite_articles_favouritesListView

        val dbHelper = ArticleDatabaseHelper()
        val db = dbHelper.readableDatabase
        val projection = arrayOf(BaseColumns._ID,
            FavouriteArticleContract.FavArticle.COLUMN_NAME_TITLE,
            FavouriteArticleContract.FavArticle.COLUMN_NAME_AUTHOR,
            FavouriteArticleContract.FavArticle.COLUMN_NAME_DATE,
            FavouriteArticleContract.FavArticle.COLUMN_NAME_LINK,
            FavouriteArticleContract.FavArticle.COLUMN_NAME_DESCRIPTION,
            FavouriteArticleContract.FavArticle.COLUMN_NAME_IMAGELINK)

        cursor = db.query(FavouriteArticleContract.FavArticle.TABLE_NAME, projection, null, null, null, null, null)

        var storyRow: FavouriteStory?
        with(cursor) {
            while (moveToNext()) {
                storyRow = FavouriteStory(null, null, null, null, null, null)
                storyRow?.title = getString(getColumnIndexOrThrow(FavouriteArticleContract.FavArticle.COLUMN_NAME_TITLE))
                storyRow?.author = getString(getColumnIndexOrThrow(FavouriteArticleContract.FavArticle.COLUMN_NAME_AUTHOR))
                storyRow?.date = getString(getColumnIndexOrThrow(FavouriteArticleContract.FavArticle.COLUMN_NAME_DATE))
                storyRow?.link = getString(getColumnIndexOrThrow(FavouriteArticleContract.FavArticle.COLUMN_NAME_LINK))
                storyRow?.description = getString(getColumnIndexOrThrow(FavouriteArticleContract.FavArticle.COLUMN_NAME_DESCRIPTION))
                storyRow?.imageLink = getString(getColumnIndexOrThrow(FavouriteArticleContract.FavArticle.COLUMN_NAME_IMAGELINK))
                favouriteArticlesArray.add(storyRow!!)
                Log.d("Current Story", "$storyRow")
                Log.d("Current Array:", "$favouriteArticlesArray")
            }
            cursor.close()
        }
        Log.d("List of Fav Stories", "$favouriteArticlesArray")

        favouritesListView.setOnItemClickListener {_, _, position, _ ->

            val intent = Intent(this, NewsDetail::class.java)

            intent.putExtra("title",favouriteArticlesArray[position].title)
            intent.putExtra("author",favouriteArticlesArray[position].author)
            intent.putExtra("date",favouriteArticlesArray[position].date)
            intent.putExtra("link",favouriteArticlesArray[position].link)
            intent.putExtra("description",favouriteArticlesArray[position].description)
            intent.putExtra("imageLink",favouriteArticlesArray[position].imageLink)

            startActivity(intent)
        }
        favouritesAdapter = FavouritesAdapter(this)
        favouritesListView.adapter = favouritesAdapter
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

                var dialogStuff = layoutInflater.inflate(R.layout.food_help_dialog, null)

                var builder =  AlertDialog.Builder(this)
                builder.setTitle("About Food Analysis")
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

    inner class FavouritesAdapter(ctx: Context): ArrayAdapter<FavouriteStory>(ctx, 0) {

        override fun getCount(): Int {
            return favouriteArticlesArray.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(parent.context)
            val result: View
            result = inflater.inflate(R.layout.news_list_item, parent, false)
            val storyTitle = result.storyListItemTitle
            val story = getItem(position)
            storyTitle.text = story.title

            val articleImageView = result.storyListItemImage
            val imgSrc = story.imageLink
            val htmlString = "<img src='$imgSrc'/>"
            articleImageView.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;}</style>" + htmlString, "text/html", "UTF-8", null)

            return result
        }

        override fun getItem(position: Int): FavouriteStory {
            return favouriteArticlesArray[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }
    }

    val DATABASE_NAME = "FavouriteArticles.db"
    val VERSION_NUM = 3

    object FavouriteArticleContract {
        // Table contents are grouped together in an anonymous object.
        object FavArticle : BaseColumns {
            const val TABLE_NAME = "Articles"
            const val COLUMN_NAME_TITLE = "title"
            const val COLUMN_NAME_AUTHOR = "author"
            const val COLUMN_NAME_DATE = "date"
            const val COLUMN_NAME_LINK = "link"
            const val COLUMN_NAME_DESCRIPTION = "description"
            const val COLUMN_NAME_IMAGELINK = "imageLink"
        }
    }

    private val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${FavouriteArticleContract.FavArticle.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${FavouriteArticleContract.FavArticle.COLUMN_NAME_TITLE} TEXT," +
                "${FavouriteArticleContract.FavArticle.COLUMN_NAME_AUTHOR} TEXT," +
                "${FavouriteArticleContract.FavArticle.COLUMN_NAME_DATE} TEXT," +
                "${FavouriteArticleContract.FavArticle.COLUMN_NAME_LINK} TEXT," +
                "${FavouriteArticleContract.FavArticle.COLUMN_NAME_DESCRIPTION} TEXT," +
                "${FavouriteArticleContract.FavArticle.COLUMN_NAME_IMAGELINK} TEXT)"

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FavouriteArticleContract.FavArticle.TABLE_NAME}"

    inner class ArticleDatabaseHelper : SQLiteOpenHelper(this@FavouriteArticles, DATABASE_NAME, null, VERSION_NUM) {
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
