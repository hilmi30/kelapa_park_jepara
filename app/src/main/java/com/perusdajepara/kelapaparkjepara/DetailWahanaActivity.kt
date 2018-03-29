package com.perusdajepara.kelapaparkjepara

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.viewpagerindicator.CirclePageIndicator

class DetailWahanaActivity : AppCompatActivity() {

    val WAHANA_ID = "wahana_id"
    val WAHANA_NAME = "wahana_name"

    var mFotoWahana: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_wahana)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val wahana_id = intent.getStringExtra(WAHANA_ID)
        val wahana_name = intent.getStringExtra(WAHANA_NAME)
        supportActionBar?.title = wahana_name

        val circle = findViewById<CirclePageIndicator>(R.id.detail_circle_indicator)
        val viewPager = findViewById<ViewPager>(R.id.detail_view_pager)
        val density = resources.displayMetrics.density

        mFotoWahana = FirebaseDatabase.getInstance().reference.child("wahana").child(wahana_id).child("foto")
        mFotoWahana?.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Log.w(MainActivity().ERROR_READ_VALUE, "Failed to read value.", p0?.toException())
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val foto = ArrayList<String>()
                for(data in p0?.children!!){
                    val value = data.value.toString()
                    foto.add(value)
                }

                // view pager untuk image slider
                val adapter = WahanaSliderAdapter(foto)
                viewPager.adapter = adapter

                // cicrle indikator
                circle.setViewPager(viewPager)
                circle.radius = 5 * density
            }

        })

    }

    class WahanaSliderAdapter(val imgData: ArrayList<String>): PagerAdapter(){
        override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
            return view == `object` as RelativeLayout
        }

        override fun getCount(): Int {
            return imgData.size
        }

        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            val v = LayoutInflater.from(container?.context).inflate(R.layout.row_image_slider_wahana, container, false)
            val img = v.findViewById<ImageView>(R.id.detail_image_slider)
            Picasso.get().load(imgData[position]).into(img)

            container?.addView(v)
            return v
        }

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            container?.removeView(`object`as RelativeLayout)
        }

    }
}
