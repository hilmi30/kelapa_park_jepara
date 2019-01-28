package com.perusdajepara.kelapaparkjepara.restofragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.perusdajepara.kelapaparkjepara.model.FirebaseModel

import com.perusdajepara.kelapaparkjepara.R

class MinumanFragment : Fragment() {

    var mDatabase: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_minuman, container, false)

        mDatabase = FirebaseDatabase.getInstance().reference
        val minumanDatabase = mDatabase?.child("resto")?.child("minuman")

        val minumanRecy = v.findViewById<RecyclerView>(R.id.minuman_recy)
        minumanRecy.layoutManager = LinearLayoutManager(context)
        minumanRecy.setHasFixedSize(true)

        val firebaseAdapter = object : FirebaseRecyclerAdapter<FirebaseModel, MinumanViewHolder>(
                FirebaseModel::class.java,
                R.layout.row_minuman,
                MinumanViewHolder::class.java,
                minumanDatabase

        ){
            override fun populateViewHolder(viewHolder: MinumanViewHolder?, model: FirebaseModel?, position: Int) {
                viewHolder?.setGambar(model?.gambar!!)
                viewHolder?.setHarga(model?.harga!!)
                viewHolder?.setNama(model?.nama!!)
            }

        }

        minumanRecy.adapter = firebaseAdapter

        return v
    }

    class MinumanViewHolder(var view: View): RecyclerView.ViewHolder(view){
        fun setGambar(gambar: String){
            val minumanGambar = view.findViewById<ImageView>(R.id.minuman_img)
            Glide.with(view.context).load(gambar).into(minumanGambar)
        }

        fun setNama(nama: String){
            val minumanNama = view.findViewById<TextView>(R.id.minuman_title)
            minumanNama.text = nama
        }

        fun setHarga(harga: Int){
            val minumanHarga = view.findViewById<TextView>(R.id.minuman_harga)
            val hargaStr = harga.toString()
            if(hargaStr.length >= 4){
                val result = "Rp" + hargaStr.substring(0, hargaStr.length - 3) + "." + hargaStr.substring(hargaStr.length - 3, hargaStr.length)
                minumanHarga?.text = result
            } else {
                val result = "Rp"+harga
                minumanHarga?.text = result
            }
        }
    }
}
