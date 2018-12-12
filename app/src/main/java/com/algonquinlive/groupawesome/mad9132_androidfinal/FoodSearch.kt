package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.*
import kotlinx.android.synthetic.main.activity_food_details.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Oluwakemi Mafe
 * You can search for food calories and fat content in this activity
 * you can save that in formation into the database to retrieve later
 * you can delete a foodItem from the database and the local foodArray
 * you can get foodItems with similar tag
 **/
class FoodSearch : AppCompatActivity() {

    /** favouritesListArray is an array of type Food*/
    var favouritesListArray = ArrayList<Food>()

    lateinit var listAdapter: FoodAdapter
    lateinit var foodName: TextView
    lateinit var foodCalories : TextView
    lateinit var foodFat : TextView
    lateinit var foodAddBtn : ImageButton
    lateinit var foodSearchInputValue : String
    lateinit var listItem: ListView
    lateinit var foodSearchProgressBar: ProgressBar
    lateinit var foodDB: SQLiteDatabase
    lateinit var foodResults: Cursor
    lateinit var foodDbHelper: FoodDatabaseHelper

    var foodItemPosition = 0
    var foodSearchCalories : Double? = null
    var foodSearchFat : Double? = null
    var foodSearchProgress = 0
    var searchIsSucessful = false
    var foodToDeleteId : Int? = null
    var changePosition = false
    var total = 0.00

    /**Food data class is a container used to hold food data
     * @param id references the id of a food item
     * @param name references the name of a food item
     * @param calContent references the calories content of a food item
     * @param fatContent references the fat content of a food item
     * @param tag references the tag of a food item*/
    data class Food(var id: Int, var name: String, var calContent: Int, var fatContent: Int, var tag: String?)

    /**called to initialize this activity
     * @param savedInstanceState references the bundle object to be passed to this activity*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_search)

        var foodSearchToolbar = findViewById<Toolbar>(R.id.foodSearchToolbar)
        setSupportActionBar(foodSearchToolbar)

        foodName = findViewById<TextView>(R.id.foodName)
        foodCalories = findViewById<TextView>(R.id.foodCalories)
        foodFat = findViewById<TextView>(R.id.foodFat)
        foodSearchProgressBar = findViewById(R.id.foodSearchProgress)

        foodDbHelper = FoodDatabaseHelper()
        foodDB = foodDbHelper.writableDatabase //open your database and allows you write into it
        foodResults = foodDB.query( TABLE_NAME, arrayOf("_id", FOODITEMKEY, FOODFATKEY, FOODCALORIESKEY, FOODTAGKEY),
            null, null,
            null, null, null,
            null)



        foodResults.moveToFirst()
        val idIndex = foodResults.getColumnIndex("_id") //get index of id column
        val itemIndex = foodResults.getColumnIndex(FOODITEMKEY) //get index of name column
        val fatIndex = foodResults.getColumnIndex(FOODFATKEY) //get index of fat column
        val caloriesIndex = foodResults.getColumnIndex(FOODCALORIESKEY) //get index of calories column
        val tagIndex = foodResults.getColumnIndex(FOODTAGKEY) //get index of tag column

        var id:Int? = null
        var item:String? = null
        var fat:Double? = null
        var calories:Double? = null
        var tag:String? = null

        while(!foodResults.isAfterLast){

            //this runs while you are not done reading

            id = foodResults.getInt(idIndex)
            item = foodResults.getString( itemIndex )
            fat = foodResults.getDouble( fatIndex )
            calories = foodResults.getDouble( caloriesIndex )
            tag = foodResults.getString( tagIndex )

            // add to arraylist
            var newFoodData: Food = Food( id, item, calories.toInt(), fat.toInt(), tag)
            favouritesListArray.add(newFoodData)

            foodResults.moveToNext()
            Log.i("from database", "id: $id, names: $item, fat: $fat, calories: $calories, tag: $tag")
        }


        Log.i("added", foodResults.getCount().toString())


        foodAddBtn = findViewById<ImageButton>(R.id.foodAddBtn)
        foodAddBtn.setOnClickListener {

            if(searchIsSucessful){

                var food = Food(
                        0,
                        foodName.text.toString().removePrefix("Food Name:  "),
                        foodSearchCalories.toString().toDouble().toInt(),
                        foodSearchFat.toString().toDouble().toInt(),
                        null
                    )

                //resetting the values of these text views
                foodName.text = "Name:"
                foodCalories.text = "Calories:"
                foodFat.text = "Fat:"

                //write to a database
                val foodNewRow = ContentValues()
                foodNewRow.put(FOODITEMKEY, food.name)
                foodNewRow.put(FOODFATKEY, food.fatContent)
                foodNewRow.put(FOODCALORIESKEY, food.calContent)
                foodNewRow.put(FOODTAGKEY, food.tag)

                foodDB.insert(TABLE_NAME, "", foodNewRow)

                foodResults = foodDB.query( TABLE_NAME, arrayOf("_id", FOODITEMKEY, FOODFATKEY, FOODCALORIESKEY, FOODTAGKEY),
                    null, null,
                    null, null, null,
                    null)



                foodResults.moveToFirst()
                val idIndex = foodResults.getColumnIndex("_id") //get index of id column
                val itemIndex = foodResults.getColumnIndex(FOODITEMKEY) //get index of name column
                val fatIndex = foodResults.getColumnIndex(FOODFATKEY) //get index of fat column
                val caloriesIndex = foodResults.getColumnIndex(FOODCALORIESKEY) //get index of calories column
                val tagIndex = foodResults.getColumnIndex(FOODTAGKEY) //get index of tag column

                while(!foodResults.isAfterLast){

                    //this runs while you are not done reading

                    var id = foodResults.getInt(idIndex)
                    var item = foodResults.getString( itemIndex )
                    var fat = foodResults.getDouble( fatIndex )
                    var calories = foodResults.getDouble( caloriesIndex )
                    var tag = foodResults.getString( tagIndex )

                    // add to arrayList
                    var newArray = ArrayList<Food>()

                    var newFoodData = Food( id, item, calories.toInt(), fat.toInt(), tag)
                    newArray.add(newFoodData)

                    for(item in newArray){

                        if(item.name.equals(food.name)){

                            var addToFoodArray = Food(item.id, item.name, item.calContent, item.fatContent, item.tag)

                            favouritesListArray.add(addToFoodArray)
                        }
                    }

                    foodResults.moveToNext()

                }

                listAdapter.notifyDataSetChanged()
                searchIsSucessful = false
            }
        }

        val foodTagBtn = findViewById(R.id.foodTagEditBtn) as? ImageButton

        /**
         * creating an event listener for food tag button*/
        foodTagBtn?.setOnClickListener{

            var foodDialogStuff = layoutInflater.inflate(R.layout.food_tag_dialog, null)
            var foodDialogAnswer = foodDialogStuff.findViewById<EditText>(R.id.foodDialogAnswer)
            var foodDialogTagName = foodDialogStuff.findViewById<EditText>(R.id.foodDialogTagNameEditText)


            var builder =  AlertDialog.Builder(this)
            builder.setTitle(R.string.food_dialog_title)
            builder.setView(foodDialogStuff) //insert view into dialog

            // Add the buttons
            builder.setPositiveButton(R.string.food_dialog_ok, {dialog, id ->

                for (item in favouritesListArray){

                    var name = item.name
                    var answer = foodDialogAnswer.text.toString()
                    var newTag = foodDialogTagName.text.toString()
                    var cv =  ContentValues()
                    cv.put(FOODTAGKEY, newTag)

                    if(name.equals(answer)){

                        foodDB.update(TABLE_NAME, cv, "$FOODITEMKEY=?", arrayOf(name))
                        Log.i("added tag", "$item")

                        item.tag = newTag
                    }
                }

            })
            builder.setNegativeButton(R.string.food_dialog_cancel, {dialog, id -> })

            // Create the AlertDialog
            var dialog = builder.create()
            dialog.show()
        }

        listItem = findViewById<ListView>(R.id.foodListView)
        listItem?.setOnItemClickListener {_, _, position, id ->

            val iAmTablet = findViewById<FrameLayout>(R.id.food_fragment_location) != null


            if(iAmTablet)
            {

                var foodArray = favouritesListArray.get(position)
                var dataToPass = Bundle()

                dataToPass.putInt("ID", foodArray.id)
                dataToPass.putString("Food-Name", foodArray.name)
                dataToPass.putInt("Food-Calories", foodArray.calContent)
                dataToPass.putInt("Food-Fat", foodArray.fatContent)
                dataToPass.putString("Food-Tag", foodArray.tag)
                Log.i("current data", "$foodArray")

                val newFoodFragment = FoodDetailsFragment()
                newFoodFragment.arguments = dataToPass //passing bundle to fragment
                supportFragmentManager.beginTransaction().replace(R.id.food_fragment_location, newFoodFragment).commit()
            }
            else{

                val intent = Intent(this, FoodDetails::class.java)

            intent.putExtra("id", favouritesListArray.get(position).id)
            intent.putExtra("name",favouritesListArray.get(position).name)
            intent.putExtra("calContent",favouritesListArray.get(position).calContent)
            intent.putExtra("fatContent",favouritesListArray.get(position).fatContent)
            intent.putExtra("tag",favouritesListArray.get(position).tag)

            startActivity(intent)
            }

            foodItemPosition = position
        }

        listAdapter = FoodAdapter(this)
        listItem?.adapter = listAdapter

        onActivityResult(50, 2, intent)
    }

    /**called to show menu on a page
     * @param menu this is a container for menu items
     * @return Boolean*/
    override fun  onCreateOptionsMenu (menu: Menu) : Boolean {

        menuInflater.inflate(R.menu.food_tool_bar_menu, menu )

        var foodSearchItem = menu.findItem(R.id.food_action_search)
        var foodSearchView = foodSearchItem.actionView as SearchView


        foodSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            /**called when query text is changed by user
             * @param newText reference to the value inputted by user
             * @return Boolean*/
            override fun onQueryTextChange(newText: String): Boolean {

                foodSearchInputValue = newText

                return false
            }

            /** called when user submits the query
             * @param query is the query value to be used for request */
            override fun onQueryTextSubmit(query: String): Boolean {

                //progress bar visible
                foodSearchProgressBar.visibility = View.VISIBLE

                //async task here to fetch data
                var query = FoodQuery()
                query.execute()

                return false
            }

        })
        return true
    }

    /**called when an item in the menu for this activity is selected
     * @param item reference to each menu items in available in the menu for this activity
     * @return Boolean */
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

    inner class FoodQuery : AsyncTask<String, Integer, String>(){

        /**used to perform background computation(in this case make url connection and get data back from the connection made)
         * @param params is the parameters of asynchronous task initiated in this activity
         * @return a result of type String at the end async task*/
        override fun doInBackground(vararg params: String?): String {

            try{

                //connect to server
                var foodSearchValue = Uri.encode(foodSearchInputValue)
                var url =
                    URL("https://api.edamam.com/api/food-database/parser?app_id=36c47bc9&app_key=f7383e1f243345dcd50c7e3aef8f9742&ingr=$foodSearchValue" )
                var urlConnection = url.openConnection() as HttpURLConnection
                var response = urlConnection.inputStream

                var reader = BufferedReader(InputStreamReader(response, "UTF-8"), 8)
                var stringBuilder = StringBuilder()

                var line: String? = reader.readLine()

                while(line != null){
                    stringBuilder.append(line + "\n")
                    line = reader.readLine()
                }

                var result = stringBuilder.toString()
                var root = JSONObject(result)

                var foodArray = root.getJSONArray("parsed")
                foodSearchProgress = 25

                var foodObject = foodArray.getJSONObject(0)
                foodObject = foodObject.getJSONObject("food").getJSONObject("nutrients")
                foodSearchProgress = 50

                foodSearchCalories = foodObject.getDouble("ENERC_KCAL")


                if(foodObject.has("FAT") ){

                    foodSearchFat = foodObject.getDouble("FAT")
                    foodSearchProgress = 75
                } else{
                    foodSearchProgress = 75
                    foodSearchFat = 0.00
                }

                foodSearchProgress = 100
                searchIsSucessful = true

                foodSearchProgressBar.setProgress(foodSearchProgress)

            }
            catch (e: Exception){

                Log.i("Exception", e.message)
            }

            return "Done"
        }

        /**is invoked on the UI thread after the background computation finishes
         * @param result is the result of background computation done in fun doInBackground*/
        override fun onPostExecute(result: String?) { //run when thread is done and going away

//            foodSearchProgressBar.setProgress(foodSearchProgress)

            foodName.text = "Food Name:  $foodSearchInputValue"
            foodCalories.text = "Calories Content:  " + foodSearchCalories.toString()
            foodFat.text = "Fat Content:  " + foodSearchFat.toString()

            foodSearchProgressBar.visibility = View.INVISIBLE
            foodAddBtn.visibility = View.VISIBLE

        }

    }

    /**creates a view by converting each object in Food data class to string and places the result into a text view
     * @param ctx specifies the context of the view created*/
    inner class FoodAdapter(val ctx: Context) : ArrayAdapter<Food>(ctx, 0) {

        /** called to know the number of items in the data set represented by FoodAdapter
         * @return the size of favouritesListArray*/
        override fun getCount(): Int {
            return favouritesListArray.size
        }

        /**called to get a view that displays data at the specified position tn the favouritesListArray
         * @param position reference the index of data in favouritesListArray
         * @param convertView reference the new view created
         * @param parent reference the parent view which new created view will attach to
         * @return created View*/
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var inflater = LayoutInflater.from(parent.getContext())
            var result: View
            result = inflater.inflate(R.layout.food_fav_list_item, parent, false)
            val foodName = result.findViewById(R.id.foodListItem) as? TextView

            val food = getItem(position)

            foodName?.text = food.name

            return result
        }

        /**called to get data item associated with specified position in favouritesListArray
         * @param position reference the index of data in favouritesListArray
         * @return the data at index specified by position in favouritesListArray*/
        override fun getItem(position: Int): Food {

            return favouritesListArray.get(position)
        }

        /**called to get row id associated with specified position in favouritesListArray
         * @param position reference the index of data in favouritesListArray
         * @return Long*/
        override fun getItemId(position: Int): Long {
            return 0
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

    /**creates a helper object to create, open, and/or manage a database for food*/
    inner class FoodDatabaseHelper : SQLiteOpenHelper(this@FoodSearch, DATABASE_NAME, null, VERSION_NUM) {

        /**called when the database is created
         * @param db reference the database created*/
        override fun onCreate(db: SQLiteDatabase) {

            db.execSQL("CREATE TABLE $TABLE_NAME ( _id INTEGER PRIMARY KEY AUTOINCREMENT, $FOODITEMKEY TEXT, $FOODFATKEY INTEGER, $FOODCALORIESKEY INTEGER, $FOODTAGKEY TEXT)") //creates table
            Log.i("FoodDatabaseHelper", "Calling onCreate")
        }

        /**called when database needs to be upgraded
         * @param db reference the database created
         * @param oldVersion reference the version number of old database
         * @param newVersion reference the version number of new database*/
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME") //deletes old data

            //creates new table
            onCreate(db)
            Log.i("FoodDatabaseHelper", "Calling onUpgrade, oldVersion=" + oldVersion + "newVersion=" + newVersion)
        }

    }

    /**called to delete food item from database and from favouritesListArray
     * @param id reference the id of food item to be deleted*/
    fun deleteFoodItem(id:Int?) {

            foodDB.delete(TABLE_NAME, "_id=$id", null)
            foodResults = foodDB.query( TABLE_NAME, arrayOf("_id", FOODITEMKEY, FOODFATKEY, FOODCALORIESKEY, FOODTAGKEY),
                null, null,
                null, null, null,
                null)

        if(changePosition){
            favouritesListArray.removeAt(foodItemPosition + 1)
        }else{
            favouritesListArray.removeAt(foodItemPosition)
        }

            Log.i("FoodSearch", favouritesListArray.toString())
            listAdapter.notifyDataSetChanged() //reload the list

    }

    /**called to show food items with similar tags
     * @param tag reference the food tag*/
    fun showFoodItemWithSameTag(tag:String?){

        var tagArray = ArrayList<Double>()

        foodResults = foodDB.query(TABLE_NAME, arrayOf("_id", FOODITEMKEY, FOODFATKEY, FOODCALORIESKEY, FOODTAGKEY),
            "$FOODTAGKEY= ?", arrayOf(tag), null, null, null, null)

        foodResults.moveToFirst()

        val caloriesIndex = foodResults.getColumnIndex(FOODCALORIESKEY) //get index of calories column

        var calories:Double? = null


        while (!foodResults.isAfterLast) {

            //this runs while you are not done reading

            calories = foodResults.getDouble(caloriesIndex)

            total += calories
            tagArray.add(calories)
            Log.i("Bla", "$tagArray")

            foodResults.moveToNext()

        }
        Log.i("similar tags", "$tagArray")
        showSummary(tagArray, tag)
    }

    /**called when this activity receives an activity result
     * @param requestCode reference the request code
     * @param resultCode reference the result code
     * @param data reference data passed by intent*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        foodToDeleteId = data?.getIntExtra("foodToDelete", 0)

        if(foodToDeleteId != null){

            if(foodToDeleteId != 0){

                changePosition = true
                deleteFoodItem(foodToDeleteId)
            }
        }

        changePosition = false
    }

    /**called to show summary of food items with similar tag name
     * @param arr reference an ArrayList of type Double
     * @param text reference food tag name */
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
