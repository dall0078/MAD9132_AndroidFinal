package com.algonquinlive.groupawesome.mad9132_androidfinal


import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class StartActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        NavigationClickHandler(this)

    }
}
