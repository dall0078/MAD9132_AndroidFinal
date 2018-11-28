package com.algonquinlive.groupawesome.mad9132_androidfinal


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

class FoodDetailsFragment : Fragment() {

    lateinit var foodSnackbarMessage:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        var dataPassed = arguments //get data back
        var foodNameFragment = dataPassed?.getString("Food-Name")
        var foodCaloriesFragment = dataPassed?.getString("Food-Calories")
        var foodFatFragment = dataPassed?.getString("Food-Fat")
        var foodTagFragment = dataPassed?.getString("Food-Tag")
        var id = dataPassed?.getLong("ID")


        var foodFragmentScreen = inflater?.inflate(R.layout.activity_food_details, container, false)

        var foodName = foodFragmentScreen.findViewById<TextView>(R.id.foodNameDetails)
        var foodCalories = foodFragmentScreen.findViewById<TextView>(R.id.foodCaloriesContent)
        var foodFat = foodFragmentScreen.findViewById<TextView>(R.id.foodFatContent)
        var foodTag = foodFragmentScreen.findViewById<TextView>(R.id.foodTagDetails)

        foodSnackbarMessage = foodNameFragment.toString()
        foodName.text = "Food Name:  $foodNameFragment"
        foodCalories.text = foodCaloriesFragment
        foodFat.text = foodFatFragment

        if(foodTagFragment != null){

            foodTag.text = "Food Tag:  $foodTagFragment"
            foodTag.visibility = View.VISIBLE
        }

        var foodCancelBtn = foodFragmentScreen.findViewById<Button>(R.id.foodCancelBtn)
        foodCancelBtn.setOnClickListener {

            foodFragmentScreen.visibility = View.INVISIBLE
        }

        var foodDeleteBtn = foodFragmentScreen.findViewById<Button>(R.id.foodDelBtn)
        foodDeleteBtn.setOnClickListener {

            Snackbar.make(foodDeleteBtn, "Do you still want to delete $foodSnackbarMessage +?", Snackbar.LENGTH_LONG)
                .setAction("Yes", {
                        e -> Toast.makeText(getView()?.getContext(), "$foodSnackbarMessage Deleted Successfully", Toast.LENGTH_SHORT).show()
                        foodFragmentScreen.visibility = View.INVISIBLE})
                .show()

            //delete from array
            //upload arrayList


        }

        return foodFragmentScreen
    }

}