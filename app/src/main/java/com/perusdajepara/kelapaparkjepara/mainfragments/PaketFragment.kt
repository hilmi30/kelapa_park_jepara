package com.perusdajepara.kelapaparkjepara.mainfragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.perusdajepara.kelapaparkjepara.FirebaseModel

import com.perusdajepara.kelapaparkjepara.R
import com.perusdajepara.kelapaparkjepara.detail.DetailPaketActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.fabiomsr.moneytextview.MoneyTextView


/**
 * A simple [Fragment] subclass.
 */
class PaketFragment : Fragment() {

    var mDatabase: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_paket, container, false)

        mDatabase = FirebaseDatabase.getInstance().reference

        val recy = v.findViewById<RecyclerView>(R.id.paket_recy)
        recy.layoutManager = LinearLayoutManager(context)
        recy.setHasFixedSize(true)

        val mPaketTerusan = mDatabase?.child("paket_terusan")

        val firebaseAdapter = object : FirebaseRecyclerAdapter<FirebaseModel, PaketViewHolder>(
                FirebaseModel::class.java,
                R.layout.row_paket,
                PaketViewHolder::class.java,
                mPaketTerusan
        ){
            override fun populateViewHolder(vh: PaketViewHolder?, model: FirebaseModel?, position: Int) {
                vh?.setGambar(model?.gambar!!)
                vh?.setNama(model?.nama!!)
                vh?.setHarga(model?.harga!!)
                vh?.view?.setOnClickListener {
                    val intent = Intent(context, DetailPaketActivity::class.java)
                    val id = getRef(position).key
                    intent.putExtra(DetailPaketActivity().ID_PAKET, id)
                    intent.putExtra(DetailPaketActivity().NAMA_PAKET, model?.nama)
                    startActivity(intent)
                }
            }
        }

        recy.adapter = firebaseAdapter

        return v
    }

    class PaketViewHolder(var view: View): RecyclerView.ViewHolder(view){
        fun setNama(nama: String){
            val namaPaket = view.findViewById<TextView>(R.id.paket_title)
            namaPaket.text = nama
        }

        fun setGambar(gambar: String){
            val gambarPaket = view.findViewById<ImageView>(R.id.paket_img)
            Picasso.get().load(gambar).into(gambarPaket)
        }

        fun setHarga(harga: Int){
            val hargaPaket = view.findViewById<TextView>(R.id.paket_harga)
            val hargaStr = harga.toString()
            if(hargaStr.length >= 4){
                val result = "Rp" + hargaStr.substring(0, hargaStr.length - 3) + "." + hargaStr.substring(hargaStr.length - 3, hargaStr.length)
                hargaPaket?.text = result
            } else {
                val result = "Rp"+harga
                hargaPaket?.text = result
            }
        }
    }

}
