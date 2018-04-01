package com.perusdajepara.kelapaparkjepara.detail

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
import android.widget.TextView
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.MainActivity
import com.perusdajepara.kelapaparkjepara.R
import com.squareup.picasso.Picasso
import com.viewpagerindicator.CirclePageIndicator

class DetailWahanaActivity : AppCompatActivity(), ValueEventListener {

    val ID_WAHANA = "wahana_id"
    val DESKRIPSI = "deskripsi"
    val NAMA = "nama"
    val HARGA = "harga"
    val KET = "keterangan"

    var mWahanaRef: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_wahana)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val wahana_id = intent.getStringExtra(ID_WAHANA)
        supportActionBar?.title = ""

        val circle = findViewById<CirclePageIndicator>(R.id.detail_circle_indicator)
        val viewPager = findViewById<ViewPager>(R.id.detail_view_pager)
        val density = resources.displayMetrics.density

        mWahanaRef = FirebaseDatabase.getInstance().reference.child("wahana").child(wahana_id)

        // detail wahana item
        val mWahanaNama = mWahanaRef?.child(NAMA)
        mWahanaNama?.addValueEventListener(this)
        val mWahanaDesc = mWahanaRef?.child(DESKRIPSI)
        mWahanaDesc?.addValueEventListener(this)
        val mWahanaHarga = mWahanaRef?.child(HARGA)
        mWahanaHarga?.addValueEventListener(this)
        val mWahanaKet = mWahanaRef?.child(KET)
        mWahanaKet?.addValueEventListener(this)

        // image slider detail wahana
        val mFotoWahana = mWahanaRef?.child("foto")
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

    override fun onCancelled(p0: DatabaseError?) {
        Log.w(MainActivity().ERROR_READ_VALUE, "Failed to read value.", p0?.toException())
    }

    override fun onDataChange(p0: DataSnapshot?) {
        when(p0?.key){
            NAMA -> {
                val namaWahana = findViewById<TextView>(R.id.detail_nama_wahana)
                namaWahana.text = p0.value.toString()
            }
            DESKRIPSI -> {
                val descWahana = findViewById<TextView>(R.id.detail_desc_wahana)
                descWahana.text = p0.value.toString()
            }
            HARGA -> {
                val hargaWahana = findViewById<TextView>(R.id.detail_harga_wahana)
                val hargaStr = p0.value.toString()
                val result = hargaStr.substring(0, hargaStr.length - 3)
                hargaWahana.text = "${result}K"
            }
            KET -> {
                val ketWahana = findViewById<TextView>(R.id.detail_ket_wahana)
                ketWahana.text = p0.value.toString()
            }
        }
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
