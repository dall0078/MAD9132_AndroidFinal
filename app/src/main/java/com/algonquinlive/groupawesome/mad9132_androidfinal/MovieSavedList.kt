package com.algonquinlive.groupawesome.mad9132_androidfinal


//----------------------------------------------------------------//
//                        MAD9132 Android Final
//                      Movie Saved List Activity
//                              Nov 2018
//----------------------------------------------------------------//
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView

class MovieSavedList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_saved_list)


        //THIS IS A TEST

        //manipulating list views playground


        val listView = findViewById<ListView>(R.id.movie_saved_listView)

        listView.adapter = MyCustomAdapter(this) //custom adapter










    }

    private class MyCustomAdapter(context: Context): BaseAdapter() {


        private val mContext: Context

        //create local array, hardcoded info for now
        private val names = arrayListOf<String>(
            "Scary Movie", "Avatar", "Reservoir Dogs"
        )

        private val descriptions = arrayListOf<String>(
            "A dumb Movie", "A Dope Movie", "My Favorite Movie"
        )

        init {
            this.mContext = context
        }

        //responsible for amount of rows in list
        override fun getCount(): Int {

            return names.size
        }




        //ignore for now
        override fun getItemId(position: Int): Long {

            return position.toLong()
        }




        //ignore for now
        override fun getItem(position: Int): Any {

            return "Test String"
        }




        //responsible for rendering out each row
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {


            val layoutInflater = LayoutInflater.from(mContext)

            val rowMain = layoutInflater.inflate(R.layout.activity_movie_saved_list_main_row, viewGroup, false)

            val nameTextView = rowMain.findViewById<TextView>(R.id.name_textView)
            nameTextView.text = names.get(position)

            val positionTextView = rowMain.findViewById<TextView>(R.id.position_textview)
            positionTextView.text = descriptions.get(position)

//            val textView = TextView(mContext)
//            textView.text = " Here is the row for the list view"
//            return textView
            return rowMain
        }



    }




    //THIS IS THE END OF THE TEST


}