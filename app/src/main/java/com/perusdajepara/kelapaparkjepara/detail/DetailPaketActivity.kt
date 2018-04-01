package com.perusdajepara.kelapaparkjepara.detail

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.FirebaseModel
import com.perusdajepara.kelapaparkjepara.MainActivity
import com.perusdajepara.kelapaparkjepara.R
import com.squareup.picasso.Picasso
import org.fabiomsr.moneytextview.MoneyTextView
import java.text.NumberFormat

class DetailPaketActivity : AppCompatActivity(), ValueEventListener {

    val ID_PAKET = "id_paket"
    var item: ArrayList<String>? = null
    val NAMA = "nama"
    val HARGA = "harga"
    val KET = "keterangan"
    val DESKRIPSI = "deskripsi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_paket)
        val paket_id = intent.getStringExtra(ID_PAKET)
        supportActionBar?.title = paket_id
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mDataRef = FirebaseDatabase.getInstance().reference
        val mPaketTerusan = mDataRef.child("paket_terusan").child(paket_id)

        val detailPaketRecy = findViewById<RecyclerView>(R.id.detail_paket_recy)
        detailPaketRecy.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        detailPaketRecy.setHasFixedSize(true)

        val paketDesc = mPaketTerusan.child(DESKRIPSI)
        paketDesc.addValueEventListener(this)

        val paketNama = mPaketTerusan.child(NAMA)
        paketNama.addValueEventListener(this)

        val paketHarga = mPaketTerusan.child(HARGA)
        paketHarga.addValueEventListener(this)

        val paketKet = mPaketTerusan.child(KET)
        paketKet.addValueEventListener(this)

        val paketItem = mPaketTerusan.child("item_paket")
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

                val adapter = PaketItemAdapter(item!!)
                detailPaketRecy.adapter = adapter
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
                val descPaket = findViewById<TextView>(R.id.detail_paket_desc)
                descPaket.text = p0.value.toString()
            }
            HARGA -> {
                val hargaPaket = findViewById<MoneyTextView>(R.id.detail_paket_harga)
                val valueStr = p0.value.toString()
                hargaPaket.amount = valueStr.toFloat()
            }
        }
    }

    class PaketItemAdapter(var itemData: ArrayList<String>): RecyclerView.Adapter<PaketItemAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent?.context).inflate(R.layout.row_detail_paket_item, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return itemData.size
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val item = itemData[position]
            Picasso.get().load(item).into(holder?.gambar)
        }

        class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
            val gambar = v.findViewById<ImageView>(R.id.detail_item_paket_img)
        }

    }
}
