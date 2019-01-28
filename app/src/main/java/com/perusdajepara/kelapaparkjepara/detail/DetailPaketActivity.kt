package com.perusdajepara.kelapaparkjepara.detail

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.model.PaketModel
import com.perusdajepara.kelapaparkjepara.reservasi.ReservasiActivity
import kotlinx.android.synthetic.main.activity_detail_paket.*
import kotlinx.android.synthetic.main.row_detail_paket_item.view.*
import org.jetbrains.anko.toast
import java.text.NumberFormat
import java.util.*
import com.perusdajepara.kelapaparkjepara.R

class DetailPaketActivity : AppCompatActivity() {

    val ID_PAKET = "id_paket"
    val NAMA_PAKET = "nama_paket"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_paket)

        val paket_id = intent.getStringExtra(ID_PAKET)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mDataRef = FirebaseDatabase.getInstance().reference
        val mPaketTerusan = mDataRef.child("paket_terusan/$paket_id")

        reservasi_paket.setOnClickListener {
            val intent = Intent(this, ReservasiActivity::class.java)
            intent.putExtra(ReservasiActivity().ID, paket_id)
            intent.putExtra(ReservasiActivity().KEY, "paket_terusan")
            startActivity(intent)
        }

        val density = resources.displayMetrics.density

        val locale = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(locale)

        mPaketTerusan.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                toast(getString(R.string.terjadi_kesalahan))
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val paketModel = p0?.getValue(PaketModel::class.java)

                detail_paket_nama.text = paketModel?.nama
                detail_paket_ket.text = paketModel?.keterangan
                detail_paket_desc.text = paketModel?.keterangan
                detail_paket_harga.text = numberFormat.format(paketModel?.harga)

                // view pager untuk image slider
                val adapter = PaketSliderAdapter(paketModel?.item_foto as List<String>)
                detail_paket_pager.adapter = adapter

                // cicrle indikator
                detail_paket_circle_indicator.setViewPager(detail_paket_pager)
                detail_paket_circle_indicator.radius = 5 * density
            }

        })
    }

    class PaketSliderAdapter(private val imgData: List<String>): PagerAdapter(){
        override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
            return view == `object` as RelativeLayout
        }

        override fun getCount(): Int {
            return imgData.size
        }

        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            val v = LayoutInflater.from(container?.context).inflate(R.layout.row_detail_paket_item, container, false)
            Glide.with(container?.context).load(imgData[position]).into(v.detail_item_paket_img)

            container?.addView(v)
            return v
        }

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            container?.removeView(`object`as RelativeLayout)
        }

    }
}
