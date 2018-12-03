package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*





class FoodSearch : AppCompatActivity() {

    var favouritesListArray = ArrayList<Food>()

    lateinit var listAdapter: FoodAdapter
    lateinit var foodName: TextView
    lateinit var foodCalories : TextView
    lateinit var foodFat : TextView
    lateinit var foodSearchInputValue : String
    lateinit var listItem: ListView
    lateinit var foodSearchProgressBar: ProgressBar
    lateinit var foodDB: SQLiteDatabase
    lateinit var foodResults: Cursor
    lateinit var foodDbHelper: FoodDatabaseHelper

    var foodItemPosition = 0

    var searchIsSucessful = false

    data class Food(var name: String?, var calContent: String?, var fatContent: String?, var tag: String?)

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
        foodResults = foodDB.query( TABLE_NAME, arrayOf("_id", FOODITEMKEY),
            null, null,
            null, null, null,
            null)

        var foodAddBtn = findViewById<ImageButton>(R.id.foodAddBtn)
        foodAddBtn.setOnClickListener {

            if(searchIsSucessful){


                var food = Food(
                        foodName.text.toString().removePrefix("Food Name:  "),
                        foodCalories.text.toString(),
                        foodFat.text.toString(),
                        null
                    )

                favouritesListArray.add(food)
                listAdapter.notifyDataSetChanged()

                //write to a database
                val foodNewRow = ContentValues()
                foodNewRow.put(FOODITEMKEY, food.toString()) //not sure about this

                foodDB.insert(TABLE_NAME, "", foodNewRow)

                Log.i("added", foodNewRow.toString())
                foodResults = foodDB.query( TABLE_NAME, arrayOf("_id", FOODITEMKEY),
                    null, null,
                    null, null, null,
                    null)

                searchIsSucessful = false
            }
        }

        val foodTagBtn = findViewById(R.id.foodTagEditBtn) as? ImageButton
        foodTagBtn?.setOnClickListener{

            var foodDialogStuff = layoutInflater.inflate(R.layout.food_tag_dialog, null)
            var foodDialogAnswer = foodDialogStuff.findViewById<EditText>(R.id.foodDialogAnswer)
            var foodDialogTagName = foodDialogStuff.findViewById<EditText>(R.id.foodDialogTagNameEditText)


            var builder =  AlertDialog.Builder(this)
            builder.setTitle(R.string.food_dialog_title)
            builder.setView(foodDialogStuff) //insert view into dialog

            // Add the buttons
            builder.setPositiveButton(R.string.food_dialog_ok, {dialog, id -> })
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
                dataToPass.putString("Food-Name", foodArray.name)
                dataToPass.putString("Food-Calories", foodArray.calContent)
                dataToPass.putString("Food-Fat", foodArray.fatContent)
                dataToPass.putString("Food-Tag", foodArray.tag)
                dataToPass.putLong("ID", id)

                val newFoodFragment = FoodDetailsFragment()
                newFoodFragment.arguments = dataToPass //passing bundle to fragment
                supportFragmentManager.beginTransaction().replace(R.id.food_fragment_location, newFoodFragment).commit()
            }
            else{
            val intent = Intent(this, FoodDetails::class.java)

            intent.putExtra("name",favouritesListArray.get(position).name)
            intent.putExtra("calContent",favouritesListArray.get(position).calContent)
            intent.putExtra("fatContent",favouritesListArray.get(position).fatContent)
            intent.putExtra("tag",favouritesListArray.get(position).tag)

            startActivity(intent)
            }
        }

        //test data
        favouritesListArray. add(Food("Apple", "200kcal", "23grams", "Fruit"))

        favouritesListArray. add(Food("Bread", "200kcal", "23grams", "Carbohydrate"))

        listAdapter = FoodAdapter(this)
        listItem?.adapter = listAdapter
    }

    override fun  onCreateOptionsMenu (menu: Menu) : Boolean {

        menuInflater.inflate(R.menu.food_tool_bar_menu, menu )

        var foodSearchItem = menu.findItem(R.id.food_action_search)
        var foodSearchView = foodSearchItem.actionView as SearchView


        foodSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {

                foodSearchInputValue = newText

                return false
            }

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

        var foodSearchCalories : Double? = null
        var foodSearchFat : Double? = null
        var foodSearchProgress = 0

        override fun doInBackground(vararg params: String?): String {

            try{

                //connect to server
                var url =
                    URL("https://api.edamam.com/api/food-database/parser?app_id=36c47bc9&app_key=f7383e1f243345dcd50c7e3aef8f9742&ingr=$foodSearchInputValue" )
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

        override fun onPostExecute(result: String?) { //run when thread is done and going away

//            foodSearchProgressBar.setProgress(foodSearchProgress)

            foodName.text = "Food Name:  $foodSearchInputValue"
            foodCalories.text = "Calories Content:  " + foodSearchCalories.toString()
            foodFat.text = "Fat Content:  " + foodSearchFat.toString()

            foodSearchProgressBar.visibility = View.INVISIBLE

        }

    }

    inner class FoodAdapter(val ctx: Context) : ArrayAdapter<Food>(ctx, 0) {

        override fun getCount(): Int {
            return favouritesListArray.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var inflater = LayoutInflater.from(parent.getContext())
            var result: View
            result = inflater.inflate(R.layout.food_fav_list_item, parent, false)
            val foodName = result.findViewById(R.id.foodListItem) as? TextView

            val food = getItem(position)

            foodName?.text = food.name

            return result
        }

        override fun getItem(position: Int): Food {

            return favouritesListArray.get(position)
        }

        override fun getItemId(position: Int): Long {
            return 0
        }
    }

    ///creating databases

    val DATABASE_NAME = "FoodFav.db"
    val VERSION_NUM = 1
    val TABLE_NAME = "FavoriteFoodItem"
    val FOODITEMKEY = "FoodItems"

    inner class FoodDatabaseHelper : SQLiteOpenHelper(this@FoodSearch, DATABASE_NAME, null, VERSION_NUM) {

        override fun onCreate(db: SQLiteDatabase) {

            db.execSQL("CREATE TABLE $TABLE_NAME ( _id INTEGER PRIMARY KEY AUTOINCREMENT, $FOODITEMKEY TEXT)") //creates table
            Log.i("FoodDatabaseHelper", "Calling onCreate")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME") //deletes old data

            //creates new table
            onCreate(db)
            Log.i("FoodDatabaseHelper", "Calling onUpgrade, oldVersion=" + oldVersion + "newVersion=" + newVersion)
        }

    }


    fun deleteFoodItem(id:Long)
    {
       foodDB.delete(TABLE_NAME, "_id=$id", null)
        foodResults = foodDB.query( TABLE_NAME, arrayOf("_id", FOODITEMKEY),
            null, null,
            null, null, null,
            null)
        favouritesListArray.removeAt(foodItemPosition)
        Log.i("FoodSearch", favouritesListArray.toString())
        listAdapter.notifyDataSetChanged() //reload the list

    }

}
