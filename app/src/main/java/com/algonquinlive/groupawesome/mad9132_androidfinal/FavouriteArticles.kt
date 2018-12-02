package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log

class FavouriteArticles : AppCompatActivity() {

    var favouriteArticlesArray = mutableListOf<NewsList.Story>()
    var storyRow = NewsList.Story(null, null, null, null, null, null)
    lateinit var cursor: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_articles)

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

        with(cursor) {
            while (moveToNext()) {
                storyRow.title = getString(getColumnIndexOrThrow(FavouriteArticleContract.FavArticle.COLUMN_NAME_TITLE))
                storyRow.author = getString(getColumnIndexOrThrow(FavouriteArticleContract.FavArticle.COLUMN_NAME_AUTHOR))
                storyRow.date = getString(getColumnIndexOrThrow(FavouriteArticleContract.FavArticle.COLUMN_NAME_DATE))
                storyRow.link = getString(getColumnIndexOrThrow(FavouriteArticleContract.FavArticle.COLUMN_NAME_LINK))
                storyRow.description = getString(getColumnIndexOrThrow(FavouriteArticleContract.FavArticle.COLUMN_NAME_DESCRIPTION))
                storyRow.imageLink = getString(getColumnIndexOrThrow(FavouriteArticleContract.FavArticle.COLUMN_NAME_IMAGELINK))
                favouriteArticlesArray.add(storyRow)
            }
        }
        Log.d("List of Fav Stories", "$favouriteArticlesArray")
    }

    val DATABASE_NAME = "FavouriteArticles.db"
    val VERSION_NUM = 1

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
