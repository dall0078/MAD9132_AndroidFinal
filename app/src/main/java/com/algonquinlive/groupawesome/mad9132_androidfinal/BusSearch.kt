package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import java.io.Serializable

data class BusStop(var routeNumber: String?, var routeDirection: String?, var routeName: String?, var routeID: Int?) :
    Serializable

data class BusRoute(var busRoute1: BusStop?, var busRoute2: BusStop? = null) : Serializable

class BusSearch : AppCompatActivity() {

    var listArray: ArrayList<BusStop?> = ArrayList<BusStop?>()
    var busStopArray: ArrayList<BusRoute> = ArrayList<BusRoute>()
    var savedArray: ArrayList<String> = ArrayList<String>()
    var savedIDArray: ArrayList<Int> = ArrayList<Int>()

    lateinit var db: SQLiteDatabase
    lateinit var results: Cursor
    lateinit var dbHelper: BusDatabaseHelper

    lateinit var savedListAdapter: SavedRouteAdapter
    lateinit var bus_view: ListView
    lateinit var progress_bar: ProgressBar
    lateinit var notFound: TextView
    lateinit var searchButton: Button
    lateinit var searchBar: EditText
    lateinit var searchResult: String

    /**
     *
     * adding adding searchButton and listarray and into code and connecting the bus list adapter
     *
     * @param savedInstanceState saving instance state
     */
    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_search)

        NavigationClickHandler(this)

        // Bus search
        searchBar = findViewById<EditText>(R.id.BusSearchBar)
        searchButton = findViewById<Button>(R.id.BusSearchButton)
        notFound = findViewById<TextView>(R.id.BusStopNotFound)

        dbHelper = BusDatabaseHelper()
        db = dbHelper.writableDatabase // now my database object

        results = db.query(
            TABLE_NAME, arrayOf("_id", BUS_ROUTES),
            null, null, null, null, null
        )

        results.moveToFirst()

        val idIndex = results.getColumnIndex("_id")
        val busIndex = results.getColumnIndex(BUS_ROUTES)


        while (!results.isAfterLast) { // while not end
            val thisID = results.getInt(idIndex)
            val thisStop = results.getString(busIndex)
            savedIDArray.add(thisID)
            savedArray.add(thisStop)
            results.moveToNext() // moves to next entry
        }


        searchButton.setOnClickListener {
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
            searchResult = searchBar.text.toString()

            hideKeyboard()

            if (listArray.isNotEmpty()) {
                listArray.clear()
                busStopArray.clear()

            }

            notFound.visibility = View.INVISIBLE

            savedArray.add(searchResult)
            savedListAdapter.notifyDataSetChanged()

            // write to database
            val newRow = ContentValues() // import new row into table
            newRow.put(BUS_ROUTES, searchResult)
            db.insert(TABLE_NAME, "", newRow) // insert

            val id = db.query(
                TABLE_NAME, arrayOf("_id"), "$BUS_ROUTES = ?", arrayOf(searchResult),
                null, null, null
            )

            id.moveToFirst()
            val idIndex = id.getColumnIndex("_id")
            val theId = id.getInt(idIndex)
            Log.i("This is the id", id.getInt(idIndex).toString())
            savedIDArray.add(theId)
            searchBar.setText("")
        }

        progress_bar = findViewById<ProgressBar>(R.id.bus_progressBar)
        bus_view = findViewById<ListView>(R.id.BusView)

        savedListAdapter = SavedRouteAdapter(this)
        bus_view.adapter = savedListAdapter


        bus_view.setOnItemClickListener { _, _, position, _ ->
            val selectedBusStop = savedArray[position]
            val detailIntent = Intent(this, BusList::class.java)
            detailIntent.putExtra("Bus Stop", selectedBusStop)
            startActivity(detailIntent)
        }
    }

    inner class SavedRouteAdapter(ctx: Context) : ArrayAdapter<String>(ctx, 0) { // one i'm using
        override fun getCount(): Int {
            return savedArray.size
        }

        override fun getItem(position: Int): String {
            return savedArray.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder", "InflateParams")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val inflater = LayoutInflater.from(parent.context)

            val result: View
            result = inflater.inflate(R.layout.busview_saved_list_item, null)

            val bus_stop_number = result.findViewById<TextView>(R.id.SavedBusItem)
            val bus_stop_delete = result.findViewById<TextView>(R.id.BusTrash)

            bus_stop_delete.setOnClickListener {
                val builder = android.app.AlertDialog.Builder(this@BusSearch)
                builder.setTitle("Are you sure you want to delete this from favorites?")

                builder.setPositiveButton("OK") { dialog, id ->
                    //                var s = results.getCount()
                    val stop_id = savedIDArray.get(position)
                    Log.i("Item", "Item Number $stop_id")
                    deleteStop(stop_id.toLong(), position)
                    Snackbar.make(bus_stop_delete, "Deleted From Favorites", Snackbar.LENGTH_LONG).show()
                }
                builder.setNegativeButton("Cancel") { dialog, id ->

                }

                val dialog = builder.create()
                dialog.show()

            }

            bus_stop_number.text = getItem(position)
            return result
        }
    }

    val DATABASE_NAME = "OCTranspo.db"
    val VERSION_NUM = 1
    val TABLE_NAME = "BusRoutes"

    val BUS_ROUTES = "Routes"


    inner class BusDatabaseHelper : SQLiteOpenHelper(this, DATABASE_NAME, null, VERSION_NUM) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE $TABLE_NAME (_id INTEGER PRIMARY KEY AUTOINCREMENT, $BUS_ROUTES TEXT); ")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)

            // create new table
            onCreate(db)
        }
    }

    @SuppressLint("Recycle")
    fun deleteStop(id: Long, position: Int) {
        db.delete(TABLE_NAME, "_id=$id", null)
        results = db.query(
            TABLE_NAME, arrayOf("_id", BUS_ROUTES),
            null, null, null, null, null
        )
        savedArray.removeAt(position)
        savedIDArray.removeAt(position)
        savedListAdapter.notifyDataSetChanged()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.bus_tool_bar_menu, menu)
        return true
    }

    @SuppressLint("InflateParams")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.foodHelpIcon -> {

                val dialogStuff = layoutInflater.inflate(R.layout.bus_help_dialog, null)

                val builder = AlertDialog.Builder(this)
                builder.setTitle("About Bus App")
                builder.setView(dialogStuff) //insert view into dialog

                // Add the buttons
                builder.setPositiveButton(R.string.food_help_dialog_done, { dialog, id -> })

                // Create the AlertDialog
                val dialog = builder.create()
                dialog.show()
            }
        }
        return true
    }

    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
    }
}
