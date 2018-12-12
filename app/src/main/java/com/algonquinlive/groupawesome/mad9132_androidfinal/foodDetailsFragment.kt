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
     * @author Oluwakemi Mafe
     * FoodDetailsFragment is a kotlin file that inherits from Fragment()*/
class FoodDetailsFragment : Fragment() {

        /**foodSnackbarMessage is used to hold the food name to be used for Toast in Snackbar*/
        lateinit var foodSnackbarMessage:String

        /**parentDocument is a reference to the FoodSearch activity*/
        lateinit var parentDocument: FoodSearch

        /** called to create an initial instance of this fragment
         * @param inflater is used to inflate layout for food details activity
         * @param container is used
         * @param savedInstanceState references the bundle object to be passed to the inflated activity
         * @return created View*/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
            /**dataPassed is a bundle that holds the all food data passed from FoodSearch activity*/
        var dataPassed = arguments //get data back

            /**foodNameFragment holds the value for food name gotten from datapassed bundle*/
        var foodNameFragment = dataPassed?.getString("Food-Name")

            /**foodCaloriesFragment holds the value for food calories gotten from datapassed bundle*/
        var foodCaloriesFragment = dataPassed?.getInt("Food-Calories")

            /**foodFatFragment holds the value for food fat gotten from datapassed bundle*/
        var foodFatFragment = dataPassed?.getInt("Food-Fat")

            /**foodTagFragment holds the value for food tag gotten from datapassed bundle*/
        var foodTagFragment = dataPassed?.getString("Food-Tag")
            /**id holds the value for food id gotten from datapassed bundle*/
        var id = dataPassed?.getInt("ID")

        /**foodFragmentScreen is a view that holds the inflated fragment layout*/
        var foodFragmentScreen = inflater?.inflate(R.layout.activity_food_details, container, false)

            /**foodName references a textView in the inflated layout*/
        var foodName = foodFragmentScreen.findViewById<TextView>(R.id.foodNameDetails)

           /**foodCalories references a textView in the inflated layout*/
        var foodCalories = foodFragmentScreen.findViewById<TextView>(R.id.foodCaloriesContent)

            /**foodFat references a textView in the inflated layout*/
        var foodFat = foodFragmentScreen.findViewById<TextView>(R.id.foodFatContent)

            /**foodtag references a Button in the inflated layout*/
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
         * foodCancelBtn references cancel button in the inflated layout
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
         * @param context to specify an activity */
        override fun onAttach(context: Activity?) {
            super.onAttach(context)

                parentDocument = context as FoodSearch //need parent to remove fragment later

        }
}