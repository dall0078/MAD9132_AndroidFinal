package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*

class FoodSearch : AppCompatActivity() {

    var favouritesListArray = ArrayList<Food>()

    lateinit var listAdapter: FoodAdapter

    var searchIsSucessful = false

    data class Food(var name: String?, var calContent: String?, var fatContent: String?, var tag: String?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_search)

        var foodSearchToolbar = findViewById<Toolbar>(R.id.foodSearchToolbar)
        setSupportActionBar(foodSearchToolbar)

        var foodName = findViewById<TextView>(R.id.foodName)
        var foodCalories = findViewById<TextView>(R.id.foodCalories)
        var foodFat = findViewById<TextView>(R.id.foodFat)
        var foodAddBtn = findViewById<ImageButton>(R.id.foodAddBtn)
        foodAddBtn.setOnClickListener {

            if(searchIsSucessful){

                var food = Food(
                    foodName.text.toString(),
                    foodCalories.text.toString(),
                    foodFat.text.toString(),
                    null
                )

                favouritesListArray.add(food)
                listAdapter.notifyDataSetChanged()

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
            builder.setPositiveButton(R.string.food_dialog_ok, {dialog, id ->
                //user clicked ok button
//                currentMessage = editText.text.toString()
            })
            builder.setNegativeButton(R.string.food_dialog_cancel, {dialog, id ->
                //user cancelled the dialog

            })
            // Create the AlertDialog
            var dialog = builder.create()
            dialog.show()
        }

        val listItem = findViewById<ListView>(R.id.foodListView)
        listItem?.setOnItemClickListener {_, _, position, _ ->

            val intent = Intent(this, FoodDetails::class.java)

            intent.putExtra("name",favouritesListArray.get(position).name)
            intent.putExtra("calContent",favouritesListArray.get(position).calContent)
            intent.putExtra("fatContent",favouritesListArray.get(position).fatContent)
            intent.putExtra("tag",favouritesListArray.get(position).tag)

            startActivity(intent)
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

                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                //Task HERE
                //async task here to fetch data
                return false
            }


        })
        return true
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

}
