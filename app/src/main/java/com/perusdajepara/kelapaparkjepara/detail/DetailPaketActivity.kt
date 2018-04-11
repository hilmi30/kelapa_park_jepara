package com.perusdajepara.kelapaparkjepara.detail

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.FirebaseModel
import com.perusdajepara.kelapaparkjepara.MainActivity
import com.perusdajepara.kelapaparkjepara.MainSliderAdapter
import com.perusdajepara.kelapaparkjepara.R
import com.perusdajepara.kelapaparkjepara.reservasi.ReservasiActivity
import com.squareup.picasso.Picasso
import com.uncopt.android.widget.text.justify.JustifiedTextView
import com.viewpagerindicator.CirclePageIndicator
import org.fabiomsr.moneytextview.MoneyTextView
import java.text.NumberFormat

class DetailPaketActivity : AppCompatActivity(), ValueEventListener {

    val ID_PAKET = "id_paket"
    val NAMA_PAKET = "nama_paket"
    var item: ArrayList<String>? = null
    val NAMA = "nama"
    val HARGA = "harga"
    val KET = "keterangan"
    val DESKRIPSI = "deskripsi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_paket)
        val paket_id = intent.getStringExtra(ID_PAKET)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mDataRef = FirebaseDatabase.getInstance().reference
        val mPaketTerusan = mDataRef.child("paket_terusan").child(paket_id)

        val reservasiPaket = findViewById<Button>(R.id.reservasi_paket)
        reservasiPaket.setOnClickListener {
            val intent = Intent(this, ReservasiActivity::class.java)
            intent.putExtra(ReservasiActivity().ID, paket_id)
            intent.putExtra(ReservasiActivity().KEY, "paket_terusan")
            startActivity(intent)
        }

        val pager = findViewById<ViewPager>(R.id.detail_paket_pager)
        val circle = findViewById<CirclePageIndicator>(R.id.detail_paket_circle_indicator)
        val density = resources.displayMetrics.density

        val paketDesc = mPaketTerusan.child(DESKRIPSI)
        paketDesc.addValueEventListener(this)

        val paketNama = mPaketTerusan.child(NAMA)
        paketNama.addValueEventListener(this)

        val paketHarga = mPaketTerusan.child(HARGA)
        paketHarga.addValueEventListener(this)

        val paketKet = mPaketTerusan.child(KET)
        paketKet.addValueEventListener(this)

        val paketItem = mPaketTerusan.child("item_foto")
        paketItem?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Log.w(MainActivity().ERROR_READ_VALUE, "error_read_value")
            }

            override fun onDataChange(p0: DataSnapshot?) {
                item = ArrayList()
                for(data in p0?.children!!){
                    val value = data.value.toString()
                    item?.add(value)
                }

                // view pager untuk image slider
                val adapter = PaketSliderAdapter(item!!)
                pager.adapter = adapter

                // cicrle indikator
                circle.setViewPager(pager)
                circle.radius = 5 * density
            }

        })
    }

    override fun onCancelled(p0: DatabaseError?) {
        Log.w(MainActivity().ERROR_READ_VALUE, "error_read_value")
    }

    override fun onDataChange(p0: DataSnapshot?) {
        when(p0?.key){
            NAMA -> {
                val namaPaket = findViewById<TextView>(R.id.detail_paket_nama)
                namaPaket.text = p0.value.toString()
            }
            KET -> {
                val ketPaket = findViewById<TextView>(R.id.detail_paket_ket)
                ketPaket.text = p0.value.toString()
            }
            DESKRIPSI -> {
                val descPaket = findViewById<JustifiedTextView>(R.id.detail_paket_desc)
                descPaket.text = p0.value.toString()
            }
            HARGA -> {
                val hargaPaket = findViewById<TextView>(R.id.detail_paket_harga)
                val valueStr = p0.value.toString()
                val lengthStr = valueStr.length
                if(valueStr.length >= 4){
                    val result = "Rp" + valueStr.substring(0, lengthStr - 3) + "." + valueStr.substring(lengthStr - 3, lengthStr)
                    hargaPaket?.text = result
                } else {
                    val result = "Rp" + valueStr
                    hargaPaket?.text = result
                }
            }
        }
    }

    class PaketSliderAdapter(val imgData: ArrayList<String>): PagerAdapter(){
        override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
            return view == `object` as RelativeLayout
        }

        override fun getCount(): Int {
            return imgData.size
        }

        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            val v = LayoutInflater.from(container?.context).inflate(R.layout.row_detail_paket_item, container, false)
            val img = v.findViewById<ImageView>(R.id.detail_item_paket_img)
            Glide.with(container?.context).load(imgData[position]).into(img)

            container?.addView(v)
            return v
        }

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            container?.removeView(`object`as RelativeLayout)
        }

    }
}
