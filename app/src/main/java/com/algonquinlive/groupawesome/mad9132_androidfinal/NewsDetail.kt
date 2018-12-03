package com.algonquinlive.groupawesome.mad9132_androidfinal

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
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_news_detail.*

class NewsDetail : AppCompatActivity() {

    var receivedStory = NewsList.Story(null, null, null, null, null, null)
    lateinit var db: SQLiteDatabase
    lateinit var dbHelper: ArticleDatabaseHelper
    lateinit var results: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        val addFavouriteButton = news_detail_addToFavButton

        val toolBar = nav_toolbar
        setSupportActionBar(toolBar)

        dbHelper = ArticleDatabaseHelper()
        db = dbHelper.writableDatabase

        addFavouriteButton.setOnClickListener {
            val values = ContentValues().apply {
                put(FavouriteArticleContract.FavArticle.COLUMN_NAME_TITLE, receivedStory.title)
                put(FavouriteArticleContract.FavArticle.COLUMN_NAME_AUTHOR, receivedStory.author)
                put(FavouriteArticleContract.FavArticle.COLUMN_NAME_DATE, receivedStory.date)
                put(FavouriteArticleContract.FavArticle.COLUMN_NAME_LINK, receivedStory.link)
                put(FavouriteArticleContract.FavArticle.COLUMN_NAME_DESCRIPTION, receivedStory.description)
                put(FavouriteArticleContract.FavArticle.COLUMN_NAME_IMAGELINK, receivedStory.imageLink)
            }
            db.insert(FavouriteArticleContract.FavArticle.TABLE_NAME, null, values)
            Toast.makeText(this@NewsDetail, "Added Article to Favourites", Toast.LENGTH_SHORT)
                .show()
        }

        onActivityResult(50, 2, intent)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)

        receivedStory.title = data?.getStringExtra("title")
        receivedStory.author = data?.getStringExtra("author")
        receivedStory.date = data?.getStringExtra("date")
        receivedStory.link = data?.getStringExtra("link")
        receivedStory.description = data?.getStringExtra("description")
        receivedStory.imageLink = data?.getStringExtra("imageLink")

        val articleTitleView = articleTitle
        val articleAuthorView = authorName
        val articleDateView = articleDate
        val articleLinkView = articleLink
        val articleDescView = articleContent
        val articleImageView = articleImage

        articleTitleView.text = receivedStory.title
        articleAuthorView.text = receivedStory.author
        articleDateView.text = receivedStory.date
        articleLinkView.text = receivedStory.link
        articleDescView.text = receivedStory.description

        val htmlString = "<img src='${receivedStory.imageLink}'/>"

//        articleImageView.loadData(htmlString, "text/html", null)
        articleImageView.loadDataWithBaseURL(null, "<style>img{display: inline;max-height: 100%;max-width: 100%;}</style>" + htmlString, "text/html", "UTF-8", null);

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

    inner class ArticleDatabaseHelper : SQLiteOpenHelper(this@NewsDetail, DATABASE_NAME, null, VERSION_NUM) {
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
