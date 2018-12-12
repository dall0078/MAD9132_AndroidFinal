package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.busview_trip_item.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class BusRouteOneFragment : Fragment() {

    lateinit var tripArray: java.util.ArrayList<Trip>
    lateinit var tripAdapter: TripAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val screen = inflater.inflate(R.layout.fragment_bus_route_one, container, false)
        val noBusses = screen.findViewById<TextView>(R.id.noBusses)
        val averageText = screen.findViewById<TextView>(R.id.average1)
        noBusses.visibility = View.INVISIBLE
        val dataPassed = arguments
        tripArray = dataPassed!!.getParcelableArrayList("tripList")!!
        val average = dataPassed.getFloat("average").toString()
        averageText.text = "Average Adjusted Schedule Time: $average"

        if (tripArray.isEmpty()) {
            noBusses.visibility = View.VISIBLE
            averageText.visibility = View.INVISIBLE

        }


        val trip_view = screen.findViewById<RecyclerView>(R.id.BusRoute1CardListView)
        tripAdapter = TripAdapter(tripArray, activity!!.applicationContext)
        trip_view?.adapter = tripAdapter
        trip_view.layoutManager = LinearLayoutManager(activity)

        return screen
    }

    inner class TripAdapter(val items: ArrayList<Trip>, val ctx: Context) : RecyclerView.Adapter<ViewHolder>() {
        override fun getItemCount(): Int {
            return items.size
        }

        @SuppressLint("InflateParams")
        override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)

            val result: View
            result = inflater.inflate(R.layout.busview_trip_item, null)

            return ViewHolder(result)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val latitude = items[position].latitude
            val longitude = items[position].longitude

            holder.destination?.text = items[position].destination
            holder.location?.text = "($latitude, $longitude)"
            holder.gpsText?.text = items[position].speed
            holder.startTimeText?.text = items[position].startTime
            holder.scheduleText?.text = items[position].adjTime

        }

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to

        val destination = view.busDestinationText
        val location = view.position
        val gpsText = view.gpsText
        val startTimeText = view.startTimeText
        val scheduleText = view.scheduleText
    }


}
