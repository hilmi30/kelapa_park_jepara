package com.perusdajepara.kelapaparkjepara

import android.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.mainfragments.*
import com.viewpagerindicator.CirclePageIndicator
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(){

    val ERROR_READ_VALUE = "error_read_value"

    var mToggle: ActionBarDrawerToggle? = null
    var mDataRef: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set toolbar
        val toolbar = findViewById<Toolbar>(R.id.nav_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mDataRef = FirebaseDatabase.getInstance().reference

        // properti navigation drawer
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        mToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navbar_open, R.string.navbar_close)
        drawerLayout?.addDrawerListener(mToggle!!)
        mToggle?.syncState()

        val pager = findViewById<ViewPager>(R.id.main_view_pager)
        val circle = findViewById<CirclePageIndicator>(R.id.main_circle_indicator)
        val density = resources.displayMetrics.density

        val sliderRef = mDataRef?.child("front_page_image_slider")
        sliderRef?.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Log.w(ERROR_READ_VALUE, "Failed to read value.", p0?.toException())
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val image = ArrayList<String>()

                for (snapshot in p0?.children!!) {
                    val value = snapshot.value.toString()

                    image.add(value)
                }

                // view pager untuk image slider
                val adapter = MainSliderAdapter(image)
                pager.adapter = adapter

                // cicrle indikator
                circle.setViewPager(pager)
                circle.radius = 5 * density

                // set swipe otomatis untuk image slider
                autoSwipeSlider(image, pager)
            }

        })

        val viewPagerKategori = findViewById<ViewPager>(R.id.main_kategori_view_pager)
        val tabLayoutKategori = findViewById<TabLayout>(R.id.main_tab_layout)

        // tambah tab text dan icon
        tabLayoutKategori.addTab(tabLayoutKategori.newTab().setText("WAHANA").setIcon(R.drawable.ic_jeep))
        tabLayoutKategori.addTab(tabLayoutKategori.newTab().setText("PAKET").setIcon(R.drawable.ic_tickets))
        tabLayoutKategori.addTab(tabLayoutKategori.newTab().setText("RESTO").setIcon(R.drawable.ic_serve))
        tabLayoutKategori.addTab(tabLayoutKategori.newTab().setText("PROMO").setIcon(R.drawable.ic_promotions))
        tabLayoutKategori.addTab(tabLayoutKategori.newTab().setText("RESERVASI").setIcon(R.drawable.ic_booking))

        val tabAdapter = MainTabAdapter(supportFragmentManager)
        tabAdapter.addFragment(WahanaFragment())
        tabAdapter.addFragment(PaketFragment())
        tabAdapter.addFragment(RestoFragment())
        tabAdapter.addFragment(PromoFragment())
        tabAdapter.addFragment(ReservasiFragment())

        viewPagerKategori.offscreenPageLimit = 4
        viewPagerKategori.adapter = tabAdapter
        viewPagerKategori.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayoutKategori))
        tabLayoutKategori.setOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPagerKategori))
    }

    private fun autoSwipeSlider(img: List<String>, pager: ViewPager) {
        var currentPages = 0

        val numPages = img.size
        val update = Runnable {
            kotlin.run {
                if(currentPages == numPages){
                    currentPages = 0
                }
                pager.setCurrentItem(currentPages++, true)
            }
        }

        val timer = Timer()
        val handler = Handler()
        timer.schedule(object : TimerTask(){
            override fun run() {
                handler.post(update)
            }

        }, 3000, 3000)

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                currentPages = position
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if(mToggle?.onOptionsItemSelected(item)!!){
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
