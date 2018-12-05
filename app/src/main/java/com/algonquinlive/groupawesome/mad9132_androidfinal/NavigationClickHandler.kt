package com.algonquinlive.groupawesome.mad9132_androidfinal

import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem


class NavigationClickHandler(val page : AppCompatActivity): NavigationView.OnNavigationItemSelectedListener
{

    fun initializePage()
    {
        var nav_toolbar = page.findViewById<Toolbar>(R.id.nav_toolbar)
        page.setSupportActionBar(nav_toolbar)

        //add navigation toolbar
        var drawer = page.findViewById<DrawerLayout>(R.id.drawer_layout)
        var toggle = ActionBarDrawerToggle(page, drawer, nav_toolbar, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        var navView = page.findViewById<NavigationView>(R.id.navigationView)
        navView.setNavigationItemSelectedListener(  this  )

    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

            R.id.item_food -> {
                var resultIntent = Intent(page, FoodSearch::class.java)
                page.startActivity(resultIntent)
            }
            R.id.item_cbc -> {
                var resultIntent = Intent(page, NewsList::class.java)
                page.startActivity(resultIntent)
            }
            R.id.item_movie -> {
                var resultIntent = Intent(page, MovieSearch::class.java)
                page.startActivity(resultIntent)
            }
            R.id.item_bus -> {
                var resultIntent = Intent(page, BusSearch::class.java)
                page.startActivity(resultIntent)
            }

        }

        var drawer = page.findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}