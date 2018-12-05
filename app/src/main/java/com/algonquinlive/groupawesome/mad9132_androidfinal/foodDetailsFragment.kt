package com.algonquinlive.groupawesome.mad9132_androidfinal


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.util.*
import kotlin.concurrent.schedule

    /**
    * FoodDetailsFragment is a kotlin file that inherits from Fragment()
    * @param foodSnackbarMessage is used to hold the food name to be used for Toast in Snackbar
    * @param dataPassed is a bundle that holds the all food data passed from FoodSearch activity
    * @param foodNameFragment holds the value for food name gotten from datapassed bundle
     * @param foodCaloriesFragment holds the value for food calories gotten from datapassed bundle
     * @param foodFatFragment holds the value for food fat gotten from datapassed bundle
     * @param foodTagFragment holds the value for food tag gotten from datapassed bundle
     * @param id holds the value for food id gotten from datapassed bundle
     * @param foodFragmentScreen is a view that holds the inflated fragment layout
     * @param foodName references a textView in the inflated layout
     * @param foodCalories references a textView in the inflated layout
     * @param foodFat references a textView in the inflated layout
     * @param foodtag references a Button in the inflated layout
     * */

class FoodDetailsFragment : Fragment() {
    lateinit var foodSnackbarMessage:String
    lateinit var parentDocument: FoodSearch

//        var amITablet = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        var dataPassed = arguments //get data back
        var foodNameFragment = dataPassed?.getString("Food-Name")
        var foodCaloriesFragment = dataPassed?.getInt("Food-Calories")
        var foodFatFragment = dataPassed?.getInt("Food-Fat")
        var foodTagFragment = dataPassed?.getString("Food-Tag")
        var id = dataPassed?.getInt("ID")


        var foodFragmentScreen = inflater?.inflate(R.layout.activity_food_details, container, false)

        var foodName = foodFragmentScreen.findViewById<TextView>(R.id.foodNameDetails)
        var foodCalories = foodFragmentScreen.findViewById<TextView>(R.id.foodCaloriesContent)
        var foodFat = foodFragmentScreen.findViewById<TextView>(R.id.foodFatContent)
        var foodTag = foodFragmentScreen.findViewById<Button>(R.id.foodTagDetails)

        foodSnackbarMessage = foodNameFragment.toString()
        foodName.text = "Food Name:  $foodNameFragment"
        foodCalories.text = foodCaloriesFragment.toString()
        foodFat.text = foodFatFragment.toString()

        if(foodTagFragment != null){

            foodTag.text = foodTagFragment
            foodTag.visibility = View.VISIBLE
        }

        /**
         * click on foodTag to get a list of all foodItems with similar tags from the database
         * */
        foodTag.setOnClickListener {


                parentDocument.showFoodItemWithSameTag(foodTag.text.toString())


        }

        /**
         * @param foodCancelBtn references cancel button in the inflated layout
         * click on foodCancelBtn to make the inflated layout invisible
         * */
        var foodCancelBtn = foodFragmentScreen.findViewById<Button>(R.id.foodCancelBtn)
        foodCancelBtn.setOnClickListener {

                foodFragmentScreen.visibility = View.INVISIBLE
        }

        /**
         * click on foodDeleteBtn to delete foodItem database and the foodArray
         * */
        var foodDeleteBtn = foodFragmentScreen.findViewById<Button>(R.id.foodDelBtn)
        foodDeleteBtn.setOnClickListener {

            Snackbar.make(foodDeleteBtn, "Do you still want to delete $foodSnackbarMessage?", Snackbar.LENGTH_LONG)
                .setAction("Yes", {
                        e -> Toast.makeText(getView()?.getContext(), "$foodSnackbarMessage Deleted Successfully", Toast.LENGTH_SHORT).show()
                        foodFragmentScreen.visibility = View.INVISIBLE
                        //delete from database and array
                        parentDocument.deleteFoodItem(id)})
                .show()

        }

        return foodFragmentScreen
    }

        /**
         * setting the context of this view as FoodSearch Activity
         * */
        override fun onAttach(context: Activity?) {
            super.onAttach(context)

                parentDocument = context as FoodSearch //need parent to remove fragment later

        }
}