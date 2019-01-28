package com.perusdajepara.kelapaparkjepara

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.auth.AuthActivity
import com.perusdajepara.kelapaparkjepara.mainfragments.*
import com.perusdajepara.kelapaparkjepara.profile.ProfileActivity
import com.perusdajepara.kelapaparkjepara.tentang.TentangActivity
import com.viewpagerindicator.CirclePageIndicator
import io.paperdb.Paper
import io.salyangoz.updateme.UpdateMe
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_header.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.util.*
import kotlin.collections.ArrayList



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val ERROR_READ_VALUE = "error_read_value"

    var mToggle: ActionBarDrawerToggle? = null
    var mDataRef: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set toolbar
        setSupportActionBar(nav_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val type = Typeface.createFromAsset(assets, "paradise.ttf")
        title_toolbar.typeface = type

        mDataRef = FirebaseDatabase.getInstance().reference

        // properti navigation drawer
        mToggle = ActionBarDrawerToggle(this, drawer_layout, R.string.navbar_open, R.string.navbar_close)
        drawer_layout.addDrawerListener(mToggle as ActionBarDrawerToggle)
        mToggle?.syncState()

        nav_view?.setNavigationItemSelectedListener(this)

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
                main_view_pager.adapter = adapter

                // cicrle indikator
                main_circle_indicator.setViewPager(main_view_pager)
                main_circle_indicator.radius = 5 * density

                // set swipe otomatis untuk image slider
                autoSwipeSlider(image, main_view_pager)
            }

        })

        // tambah tab text dan icon
        main_tab_layout.addTab(main_tab_layout.newTab().setText("WAHANA").setIcon(R.drawable.ic_jeep))
        main_tab_layout.addTab(main_tab_layout.newTab().setText("PAKET").setIcon(R.drawable.ic_tickets))
        main_tab_layout.addTab(main_tab_layout.newTab().setText("RESTO").setIcon(R.drawable.ic_serve))
        main_tab_layout.addTab(main_tab_layout.newTab().setText("PROMO").setIcon(R.drawable.ic_promotions))

        val tabAdapter = MainTabAdapter(supportFragmentManager)
        tabAdapter.addFragment(WahanaFragment())
        tabAdapter.addFragment(PaketFragment())
        tabAdapter.addFragment(RestoFragment())
        tabAdapter.addFragment(PromoFragment())
        tabAdapter.addFragment(ReservasiFragment())

        main_kategori_view_pager.offscreenPageLimit = 4
        main_kategori_view_pager.adapter = tabAdapter
        main_kategori_view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(main_tab_layout))
        main_tab_layout.setOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(main_kategori_view_pager))

        mDataRef?.child("kondisi")?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                toast(getString(R.string.terjadi_kesalahan))
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0?.exists() as Boolean) {
                    val cuaca = p0.child("cuaca").value.toString().toUpperCase()
                    val suhu = p0.child("suhu").value.toString() + " \u00B0C"

                    val headerView = nav_view.getHeaderView(0)
                    headerView.cuaca.text = "$cuaca, $suhu"
                }
            }

        })
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_peta -> {

                mDataRef?.child("lokasi")?.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        toast(getString(R.string.terjadi_kesalahan))
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0?.exists() as Boolean) {
                            val lat = p0.child("lat").value.toString()
                            val lng = p0.child("lng").value.toString()

                            val coor = Uri.parse("geo:0,0?q=$lat, $lng(Kelapa Park Jepara)")
                            val intent = Intent(Intent.ACTION_VIEW, coor)
                            startActivity(intent)
                        }
                    }

                })
            }
            R.id.nav_tentang -> {
                val intent = Intent(this, TentangActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_website -> {
                try {
                    mDataRef?.child("socialMediaLink/web")?.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                            toast(getString(R.string.terjadi_kesalahan))
                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0?.exists() as Boolean) {
                                val url = p0.value.toString()
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            }
                        }
                    })
                } catch (e: ActivityNotFoundException){
                    Toast.makeText(this, "Browser not found.", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_fb -> {
                try {
                    mDataRef?.child("socialMediaLink/fb")?.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                            toast(getString(R.string.terjadi_kesalahan))
                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0?.exists() as Boolean) {
                                val url = p0.value.toString()
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            }
                        }
                    })
                } catch (e: ActivityNotFoundException){
                    Toast.makeText(this, "facebook not found.", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_instagram -> {
                try {
                    mDataRef?.child("socialMediaLink/ig")?.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                            toast(getString(R.string.terjadi_kesalahan))
                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0?.exists() as Boolean) {
                                val url = p0.value.toString()
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            }
                        }
                    })
                } catch (e: ActivityNotFoundException){
                    Toast.makeText(this, "instagram not found.", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_youtube -> {
                try {
                    mDataRef?.child("socialMediaLink/yt")?.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                            toast(getString(R.string.terjadi_kesalahan))
                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0?.exists() as Boolean) {
                                val url = p0.value.toString()
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            }
                        }
                    })
                } catch (e: ActivityNotFoundException){
                    Toast.makeText(this, "youtube not found.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
