package com.perusdajepara.kelapaparkjepara.detail

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.R
import com.perusdajepara.kelapaparkjepara.model.WahanaModel
import com.perusdajepara.kelapaparkjepara.reservasi.ReservasiActivity
import com.viewpagerindicator.CirclePageIndicator
import kotlinx.android.synthetic.main.activity_detail_wahana.*
import org.jetbrains.anko.toast
import java.text.NumberFormat
import java.util.*

class DetailWahanaActivity : AppCompatActivity() {

    val ID_WAHANA = "wahana_id"

    var mWahanaRef: DatabaseReference? = null
    var wahana_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_wahana)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        wahana_id = intent.getStringExtra(ID_WAHANA)
        supportActionBar?.title = ""

        val circle = findViewById<CirclePageIndicator>(R.id.detail_circle_indicator)
        val viewPager = findViewById<ViewPager>(R.id.detail_view_pager)
        val density = resources.displayMetrics.density

        val locale = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(locale)

        mWahanaRef = FirebaseDatabase.getInstance().reference.child("wahana/$wahana_id")
        mWahanaRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                toast(getString(R.string.terjadi_kesalahan))
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val wahanaDetail = p0?.getValue(WahanaModel::class.java)

                detail_nama_wahana.text = wahanaDetail?.nama
                detail_desc_wahana.text = wahanaDetail?.deskripsi
                detail_harga_wahana.text = numberFormat.format(wahanaDetail?.harga)
                detail_ket_wahana.text = wahanaDetail?.keterangan

                // view pager untuk image slider
                val adapter = WahanaSliderAdapter(wahanaDetail?.foto as List<String>)
                viewPager.adapter = adapter

                // cicrle indikator
                circle.setViewPager(viewPager)
                circle.radius = 5 * density
            }

        })

        val status = intent.getBooleanExtra("status", false)
        reservasi_wahana_btn.isEnabled = status

        reservasi_wahana_btn.setOnClickListener {
            val intent = Intent(this, ReservasiActivity::class.java)
            intent.putExtra(ReservasiActivity().ID, wahana_id)
            intent.putExtra(ReservasiActivity().KEY, "wahana")
            startActivity(intent)
        }
    }

    class WahanaSliderAdapter(private val imgData: List<String>): PagerAdapter(){
        override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
            return view == `object` as RelativeLayout
        }

        override fun getCount(): Int {
            return imgData.size
        }

        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            val v = LayoutInflater.from(container?.context).inflate(R.layout.row_image_slider_wahana, container, false)
            val img = v.findViewById<ImageView>(R.id.detail_image_slider)
            Glide.with(container?.context).load(imgData[position]).into(img)

            container?.addView(v)
            return v
        }

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            container?.removeView(`object`as RelativeLayout)
        }
    }
}
