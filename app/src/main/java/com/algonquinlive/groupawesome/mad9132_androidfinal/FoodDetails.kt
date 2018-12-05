package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

/**
 * This page displays the fat content, calories count, food name and food tag if any
 * @param foodSnackbarMessage is used to hold the message to be displayed when snackbar is launched
 * @param onCreate is a life cycle function that gets called when the application loads up
 * @param foodCancelBtn is the cancel button on details page which is used to return to the food search page
 * @param foodDeleteBtn is used to delete food items from the database and favorites list. This button uses a Snackbar and Toast to confirm if users intend to delete an item
 * @param foodDetailsToolbar this references the toolbar item "search"
 **/

class FoodDetails : AppCompatActivity() {

    lateinit var foodSnackbarMessage:String
    lateinit var foodDB: SQLiteDatabase
    lateinit var foodResults: Cursor
    lateinit var foodDbHelper: FoodDatabaseHelper

    var foodId:Int? = null

    var tagArray = ArrayList<Double>()

    var total = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_details)

        foodDbHelper = FoodDatabaseHelper()
        foodDB = foodDbHelper.writableDatabase

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
                        e -> Toast.makeText(this@FoodDetails, "$foodSnackbarMessage Deleted Successfully", Toast.LENGTH_LONG).show()
                    var intent = Intent(this, FoodSearch::class.java)  //delete from array
                    var id = foodId!!
                    intent.putExtra("foodToDelete", id)
                    startActivity(intent)})
                .show()
        }

        onActivityResult(35, 2, intent)

    }


    /**
     * used to specify the options menu for this activity and is used to create the options menu when called
     * menu resource will be inflated in this function
     * */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.food_tool_bar_menu2, menu)

        return true
    }

    /**
     * used to specify what action you want each menu item to perform
     * the specific id of each menu items will be referenced and actions will be assigned to each of them
     * */
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

    /**
     * This function is used to set the GUI values. it checks if food tag is empty and set the value of foodTagDetails if not empty then displays the textview for food tag
     * @param foodname stores food name
     * @param foodcalories stores food calories
     * @param foodfat stores food fat
     * @param foodtag stores food tag
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)


        foodId = data?.getIntExtra("id", 0)
        val foodname = data?.getStringExtra("name")
        val foodcalories =data?.getIntExtra("calContent", 0)
        val foodfat = data?.getIntExtra("fatContent", 0)
        val foodtag = data?.getStringExtra("tag")

        val foodNameDetails = findViewById<TextView>(R.id.foodNameDetails)
        val foodCaloriesDetails = findViewById<TextView>(R.id.foodCaloriesContent)
        val foodFatDetails = findViewById<TextView>(R.id.foodFatContent)
        val foodTagDetails = findViewById<Button>(R.id.foodTagDetails)

        foodSnackbarMessage = foodname.toString()
        foodNameDetails.text = "Food Name:  $foodname"
        foodCaloriesDetails.text = foodcalories.toString()
        foodFatDetails.text = foodfat.toString()

        if(foodtag != null){

            foodTagDetails.text = foodtag
            foodTagDetails.visibility = View.VISIBLE
        }

        foodTagDetails.setOnClickListener {
            showFoodItemWithSameTag(foodtag)
        }
    }

    ///creating databases

    val DATABASE_NAME = "FoodFav.db"
    val VERSION_NUM = 6
    val TABLE_NAME = "FavoriteFoodItem"
    val FOODITEMKEY = "FoodName"
    val FOODFATKEY = "FoodFat"
    val FOODCALORIESKEY = "FoodCalories"
    val FOODTAGKEY = "FoodTag"

    inner class FoodDatabaseHelper : SQLiteOpenHelper(this@FoodDetails, DATABASE_NAME, null, VERSION_NUM) {

        override fun onCreate(db: SQLiteDatabase) {

            db.execSQL("CREATE TABLE $TABLE_NAME ( _id INTEGER PRIMARY KEY AUTOINCREMENT, $FOODITEMKEY TEXT, $FOODFATKEY INTEGER, $FOODCALORIESKEY INTEGER, $FOODTAGKEY TEXT)") //creates table
            Log.i("FoodDatabaseHelper", "Calling onCreate")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME") //deletes old data

            //creates new table
            onCreate(db)
            Log.i("FoodDatabaseHelper", "Calling onUpgrade, oldVersion=" + oldVersion + "newVersion=" + newVersion)
        }

    }

    fun showFoodItemWithSameTag(tag:String?){

        foodResults = foodDB.query(TABLE_NAME, arrayOf("_id", FOODITEMKEY, FOODFATKEY, FOODCALORIESKEY, FOODTAGKEY),
            "$FOODTAGKEY= ?", arrayOf(tag), null, null, null, null)

        foodResults.moveToFirst()
        val idIndex = foodResults.getColumnIndex("_id") //get index of id column
        val itemIndex = foodResults.getColumnIndex(FOODITEMKEY) //get index of name column
        val fatIndex = foodResults.getColumnIndex(FOODFATKEY) //get index of fat column
        val caloriesIndex = foodResults.getColumnIndex(FOODCALORIESKEY) //get index of calories column
        val tagIndex = foodResults.getColumnIndex(FOODTAGKEY) //get index of tag column


        var calories:Double? = null


        while (!foodResults.isAfterLast) {

            //this runs while you are not done reading
            calories = foodResults.getDouble(caloriesIndex)

            total += calories

            tagArray.add(calories)
            var index = 0
            for(s in tagArray)
            Log.i("Bla"+index++, "$s")

            foodResults.moveToNext()

        }
        Log.i("similar tags", "$tagArray")
        showSummary(tagArray, tag)
    }

    private fun showSummary(arr: ArrayList<Double>, text: String?){

        var arrCount = arr.count()
        var max = arr.max()
        var min = arr.min()
        var average =  total / arrCount

        var summary = findViewById<TextView>(R.id.summary)
        var minCal = findViewById<TextView>(R.id.minCal)
        var maxCal = findViewById<TextView>(R.id.maxCal)
        var aveCal = findViewById<TextView>(R.id.averageCal)
        var textTotal = findViewById<TextView>(R.id.totalCal)

        summary.text = "Summary for $text"
        minCal.text = "Minimum calories " + min.toString()
        maxCal.text = "Maximum calories " + max.toString()
        aveCal.text = "Average calories " + average.toString()
        textTotal.text = "Total calories " + total.toString()

        summary.visibility = View.VISIBLE
        minCal.visibility = View.VISIBLE
        maxCal.visibility = View.VISIBLE
        aveCal.visibility = View.VISIBLE
        textTotal.visibility = View.VISIBLE

    }

}
