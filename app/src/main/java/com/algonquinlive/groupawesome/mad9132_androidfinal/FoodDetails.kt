package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class FoodDetails : AppCompatActivity() {

    /**
     * this page displays the fat content, calories count, food name and food tag if any
     * @params foodSnackbarMessage is used to hold the message to be displayed when snackbar is launched
     * onCreate is a life cycle function that gets called when the application loads up
     * foodCancelBtn is the cancel button on details page and is used to return to the food search page
     * foodDeleteBtn is used to delete food items from favorites list
    **/

    lateinit var foodSnackbarMessage:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_details)

        var foodDetailsToolbar = findViewById<Toolbar>(R.id.foodDetailsToolbar)
        setSupportActionBar(foodDetailsToolbar)

        var foodCancelBtn = findViewById<Button>(R.id.foodCancelBtn)
        foodCancelBtn.setOnClickListener {
            finish()
        }

        var foodDeleteBtn = findViewById<Button>(R.id.foodDelBtn)
        foodDeleteBtn.setOnClickListener {

            Snackbar.make(foodDeleteBtn, "Do you still want to delete $foodSnackbarMessage?", Snackbar.LENGTH_LONG)
                .setAction("Yes", {
                        e -> Toast.makeText(this@FoodDetails, "$foodSnackbarMessage Deleted Successfully", Toast.LENGTH_LONG).show()})
                .show()
            //delete from array
            //upload arrayList
        }

        onActivityResult(50, 2, intent)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.food_tool_bar_menu2, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val foodname = data?.getStringExtra("name")
        val foodcalories =data?.getStringExtra("calContent")
        val foodfat = data?.getStringExtra("fatContent")
        val foodtag = data?.getStringExtra("tag")

        val foodNameDetails = findViewById<TextView>(R.id.foodNameDetails)
        val foodCaloriesDetails = findViewById<TextView>(R.id.foodCaloriesContent)
        val foodFatDetails = findViewById<TextView>(R.id.foodFatContent)
        val foodTagDetails = findViewById<TextView>(R.id.foodTagDetails)

        foodSnackbarMessage = foodname.toString()
        foodNameDetails.text = "Food Name:  $foodname"
        foodCaloriesDetails.text = foodcalories
        foodFatDetails.text = foodfat

        if(foodtag != null){

            foodTagDetails.text = "Food Tag:  $foodtag"
            foodTagDetails.visibility = View.VISIBLE
        }
    }
}
