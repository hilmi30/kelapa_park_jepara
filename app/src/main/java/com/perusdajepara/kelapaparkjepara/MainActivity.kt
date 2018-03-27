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
import android.view.MenuItem
import android.view.View
import com.perusdajepara.kelapaparkjepara.mainfragments.*
import com.viewpagerindicator.CirclePageIndicator
import java.util.*

class MainActivity : AppCompatActivity(){


    var mToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.nav_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        mToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navbar_open, R.string.navbar_close)
        drawerLayout?.addDrawerListener(mToggle!!)
        mToggle?.syncState()

        val img = listOf<String>(
                "http://www.kelapapark.com/images/24210246_1619590538105124_628262070599682957_o.jpg",
                "http://www.kelapapark.com/images/terusan-2.jpg",
                "http://www.kelapapark.com/images/terusan-5-6.jpg",
                "http://www.kelapapark.com/images/24210246_1619590538105124_628262070599682957_o.jpg",
                "http://www.kelapapark.com/images/terusan-2.jpg",
                "http://www.kelapapark.com/images/terusan-5-6.jpg"
        )

        val pager = findViewById<ViewPager>(R.id.main_view_pager)
        val adapter = MainSliderAdapter(img)
        pager.adapter = adapter

        val circle = findViewById<CirclePageIndicator>(R.id.main_circle_indicator)
        circle.setViewPager(pager)
        val density = resources.displayMetrics.density
        circle.radius = 5 * density

        // set swipe otomatis untuk image slider
        autoSwipeSlider(img, pager)

        val viewPagerKategori = findViewById<ViewPager>(R.id.main_kategori_view_pager)
        val tabLayoutKategori = findViewById<TabLayout>(R.id.main_tab_layout)

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
