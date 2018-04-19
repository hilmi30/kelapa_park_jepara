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
import org.jetbrains.anko.alert
import java.util.*
import kotlin.collections.ArrayList



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val ERROR_READ_VALUE = "error_read_value"

    var mToggle: ActionBarDrawerToggle? = null
    var mDataRef: DatabaseReference? = null
    var drawerLayout: DrawerLayout? = null

    var mAuth: FirebaseAuth? = null
    var spinLoading: SpinKitView? = null
    var headerName: TextView? = null
    var navView: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UpdateMe.with(this, 60)
                .onPositiveButtonClick{
                    Log.d("Update", "Update")
                }
                .onNegativeButtonClick{
                    Log.d("update", "update batal")
                }
                .setPositiveButtonColorRes(R.color.colorAccent)
                .check()

        headerName = findViewById(R.id.nama_user_navbar)
        spinLoading = findViewById(R.id.spin_kit_main)

        mAuth = FirebaseAuth.getInstance()

        // set toolbar
        val toolbar = findViewById<Toolbar>(R.id.nav_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val type = Typeface.createFromAsset(assets, "paradise.ttf")
        val titleText = findViewById<TextView>(R.id.title_toolbar)
        titleText.setTypeface(type)

        mDataRef = FirebaseDatabase.getInstance().reference

        // properti navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        mToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navbar_open, R.string.navbar_close)
        drawerLayout?.addDrawerListener(mToggle!!)
        mToggle?.syncState()

        navView = findViewById(R.id.nav_view)
        navView?.setNavigationItemSelectedListener(this)

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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_peta -> {
                val coor = Uri.parse("geo:0,0?q=-6.5741287, 110.7405415(Kelapa Park Jepara)")
                val intent = Intent(Intent.ACTION_VIEW, coor)
                startActivity(intent)
            }
            R.id.nav_tentang -> {
                val intent = Intent(this, TentangActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_website -> {
                try {
                    val url = "http://www.kelapapark.com/"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } catch (e: ActivityNotFoundException){
                    Toast.makeText(this, "Browser not found.", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_fb -> {
                try {
                    val url = "https://www.facebook.com/kelapapark/"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } catch (e: ActivityNotFoundException){
                    Toast.makeText(this, "facebook not found.", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_instagram -> {
                try {
                    val url = "https://www.instagram.com/kelapapark/?hl=id"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } catch (e: ActivityNotFoundException){
                    Toast.makeText(this, "instagram not found.", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_youtube -> {
                try {
                    val url = "https://www.youtube.com/channel/UCTmZb4hyppphoH_MnNh8YLw"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } catch (e: ActivityNotFoundException){
                    Toast.makeText(this, "youtube not found.", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                alert("Apakah anda yakin ingin keluar"){
                    positiveButton("Ya"){
                        spinLoading?.visibility = View.VISIBLE
                        mAuth?.signOut()
                        val handler = Handler()
                        handler.postDelayed({
                            Paper.book().destroy()
                            val intent = Intent(this@MainActivity, AuthActivity::class.java)
                            startActivity(intent)
                            finish()
                            spinLoading?.visibility = View.GONE
                        }, 3000)
                    }
                    negativeButton("Tidak"){
                    }
                }.show()
            }
        }

        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth?.currentUser
        if(currentUser == null || !currentUser.isEmailVerified){
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val headerView = navView?.getHeaderView(0)
            val headerNameUser = headerView?.findViewById<TextView>(R.id.nama_user_navbar)
            headerNameUser?.text = currentUser.displayName
        }
    }

    override fun onBackPressed() {
        if(drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
