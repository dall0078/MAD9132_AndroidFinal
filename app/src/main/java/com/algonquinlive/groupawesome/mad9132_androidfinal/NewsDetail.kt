package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_news_detail.*

class NewsDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        val addFavouriteButton = news_detail_addToFavButton
        addFavouriteButton.setOnClickListener {
            Toast.makeText(this@NewsDetail, "Added Article to Favourites", Toast.LENGTH_SHORT)
                .show()
        }

        onActivityResult(50, 2, intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val title = data?.getStringExtra("title")
        val author = data?.getStringExtra("author")
        val date = data?.getStringExtra("date")
        val link = data?.getStringExtra("link")
        val description = data?.getStringExtra("description")
        val imgSrc = data?.getStringExtra("imageLink")

        val articleTitleView = articleTitle
        val articleAuthorView = authorName
        val articleDateView = articleDate
        val articleLinkView = articleLink
        val articleDescView = articleContent
        val articleImageView = articleImage

        articleTitleView.text = title
        articleAuthorView.text = author
        articleDateView.text = date
        articleLinkView.text = link
        articleDescView.text = description

        val htmlString = "<img src='$imgSrc'/>"

//        articleImageView.loadData(htmlString, "text/html", null)
        articleImageView.loadDataWithBaseURL(null, "<style>img{display: inline;max-height: 100%;max-width: 100%;}</style>" + htmlString, "text/html", "UTF-8", null);

    }
}
