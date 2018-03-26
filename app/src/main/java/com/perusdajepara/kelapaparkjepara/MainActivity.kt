package com.perusdajepara.kelapaparkjepara

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem

class MainActivity : AppCompatActivity(){

//    var drawerLayout: DrawerLayout? = null
    var mToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        mToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navbar_open, R.string.navbar_close)
        drawerLayout?.addDrawerListener(mToggle!!)
        mToggle?.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if(mToggle?.onOptionsItemSelected(item)!!){
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
