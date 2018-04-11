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
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.FirebaseDatabase
import com.perusdajepara.kelapaparkjepara.R
import com.squareup.picasso.Picasso
import com.google.firebase.database.DatabaseReference
import com.perusdajepara.kelapaparkjepara.detail.DetailWahanaActivity
import com.perusdajepara.kelapaparkjepara.FirebaseModel


/**
 * A simple [Fragment] subclass.
 */
class WahanaFragment : Fragment() {

    var wahanaRef: DatabaseReference? = null
    var wahanaRecy: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_wahana, container, false)

        wahanaRef = FirebaseDatabase.getInstance().reference.child("wahana")

        wahanaRecy = v.findViewById(R.id.wahana_recy)
        wahanaRecy?.setHasFixedSize(true)
        wahanaRecy?.layoutManager = LinearLayoutManager(context)

        val firebaseAdapter = object: FirebaseRecyclerAdapter<FirebaseModel, WahanaViewHolder>(
                FirebaseModel::class.java,
                R.layout.row_wahana,
                WahanaViewHolder::class.java,
                wahanaRef
        ){
            override fun populateViewHolder(viewHolder: WahanaViewHolder?, model: FirebaseModel?, position: Int) {
                viewHolder?.setGambar(model?.gambar!!)
                viewHolder?.setNama(model?.nama!!)
                viewHolder?.setHarga(model?.harga!!)
                viewHolder?.mView?.setOnClickListener {
                    val id = getRef(position).key
                    val intent = Intent(context, DetailWahanaActivity::class.java)
                    intent.putExtra(DetailWahanaActivity().ID_WAHANA, id)
                    startActivity(intent)
                }
            }

        }

        wahanaRecy?.adapter = firebaseAdapter

        return v
    }

    class WahanaViewHolder(var mView: View) : RecyclerView.ViewHolder(mView){
        fun setNama(nama: String){
            val wahanaTitle = mView.findViewById<TextView>(R.id.wahana_title)
            wahanaTitle?.text = nama
        }

        fun setGambar(gambar: String){
            val wahanaGambar = mView.findViewById<ImageView>(R.id.wahana_img)
            Glide.with(mView.context).load(gambar).into(wahanaGambar)
        }

        fun setHarga(harga: Int){
            val wahanaHarga = mView.findViewById<TextView>(R.id.wahana_harga)
            val hargaStr = harga.toString()
            if(hargaStr.length >= 4){
                val result = "Rp" + hargaStr.substring(0, hargaStr.length - 3) + "." + hargaStr.substring(hargaStr.length - 3, hargaStr.length)
                wahanaHarga?.text = result
            } else {
                val result = "Rp"+harga
                wahanaHarga?.text = result
            }
        }
    }
}
