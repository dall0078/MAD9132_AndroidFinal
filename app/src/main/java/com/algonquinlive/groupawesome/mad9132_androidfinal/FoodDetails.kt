package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class FoodDetails : AppCompatActivity() {

    lateinit var foodSnackbarMessage:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_details)

        var foodCancelBtn = findViewById<Button>(R.id.foodCancelBtn)
        foodCancelBtn.setOnClickListener {
            finish()
        }

        var foodDeleteBtn = findViewById<Button>(R.id.foodDelBtn)
        foodDeleteBtn.setOnClickListener {

            Snackbar.make(foodDeleteBtn, "Do you still want to delete " + foodSnackbarMessage + "?", Snackbar.LENGTH_LONG)
                .setAction("Yes", {
                        e -> Toast.makeText(this@FoodDetails, foodSnackbarMessage + " Deleted Successfully", Toast.LENGTH_LONG).show() })
                .show()
            //delete from array
            //upload arrayList
        }

        onActivityResult(50, 2, intent)

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
        foodNameDetails.text = foodname
        foodCaloriesDetails.text = foodcalories
        foodFatDetails.text = foodfat
        foodTagDetails.text = foodtag
    }
}
