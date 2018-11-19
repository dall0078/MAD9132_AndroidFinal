package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.NotificationCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem

class StartActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)


        var nav_toolbar = findViewById<Toolbar>(R.id.nav_toolbar)
        setSupportActionBar(nav_toolbar)

        //add navigation toolbar
        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        var toggle = ActionBarDrawerToggle(this, drawer, nav_toolbar, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        var navView = findViewById<NavigationView>(R.id.navigationView)
        navView.setNavigationItemSelectedListener(this)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

            R.id.item_food -> {
                //system notification
//                var mbuilder = NotificationCompat.Builder(this, "Channel_name")
//                    .setSmallIcon(R.drawable.food)
//                    .setAutoCancel(true)
//                    .setContentTitle("Open Food Search")
//                    .setContentText("I'm Hungry")

                var resultIntent = Intent(this, FoodSearch::class.java)
                startActivity(resultIntent)

//                var resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//                mbuilder.setContentIntent(resultPendingIntent)
//
//                var mNotificationId = 1
//                var mNotifyMgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//                mNotifyMgr.notify(mNotificationId, mbuilder.build())
            }
            R.id.item_cbc -> {
                //system notification
//                var mbuilder = NotificationCompat.Builder(this, "Channel_name")
//                    .setSmallIcon(R.drawable.news)
//                    .setAutoCancel(true)
//                    .setContentTitle("List Items")
//                    .setContentText("List")

                var resultIntent = Intent(this, NewsList::class.java)
                startActivity(resultIntent)

//                var resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//                mbuilder.setContentIntent(resultPendingIntent)
//
//                var mNotificationId = 2
//                var mNotifyMgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//                mNotifyMgr.notify(mNotificationId, mbuilder.build())
            }
            R.id.item_movie -> {
                //system notification
//                var mbuilder = NotificationCompat.Builder(this, "Channel_name")
//                    .setSmallIcon(R.drawable.movie)
//                    .setAutoCancel(true)
//                    .setContentTitle("Movie List")
//                    .setContentText("Choose one")

                var resultIntent = Intent(this, MovieSearch::class.java)
                startActivity(resultIntent)

//                var resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//                mbuilder.setContentIntent(resultPendingIntent)
//
//                var mNotificationId = 3
//                var mNotifyMgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//                mNotifyMgr.notify(mNotificationId, mbuilder.build())
            }
            R.id.item_bus -> {
                //system notification
//                var mbuilder = NotificationCompat.Builder(this, "Channel_name")
//                    .setSmallIcon(R.drawable.bus)
//                    .setAutoCancel(true)
//                    .setContentTitle("Find a Bus")
//                    .setContentText("And Kill yourself")

                var resultIntent = Intent(this, BusSearch::class.java)
                startActivity(resultIntent)

//                var resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//                mbuilder.setContentIntent(resultPendingIntent)
//
//                var mNotificationId = 4
//                var mNotifyMgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//                mNotifyMgr.notify(mNotificationId, mbuilder.build())
            }

        }

        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
