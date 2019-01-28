package com.perusdajepara.kelapaparkjepara.mainfragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.FirebaseDatabase
import com.perusdajepara.kelapaparkjepara.R
import com.google.firebase.database.DatabaseReference
import com.perusdajepara.kelapaparkjepara.detail.DetailWahanaActivity
import com.perusdajepara.kelapaparkjepara.model.FirebaseModel
import com.perusdajepara.kelapaparkjepara.model.WahanaModel
import kotlinx.android.synthetic.main.fragment_wahana.*
import kotlinx.android.synthetic.main.fragment_wahana.view.*
import kotlinx.android.synthetic.main.row_wahana.view.*
import java.text.NumberFormat
import java.util.*


class WahanaFragment : Fragment() {

    var wahanaRef: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_wahana, container, false)

        wahanaRef = FirebaseDatabase.getInstance().reference.child("wahana")

        v.wahana_recy.layoutManager = LinearLayoutManager(context)

        val firebaseAdapter = object: FirebaseRecyclerAdapter<WahanaModel, WahanaViewHolder>(
                WahanaModel::class.java,
                R.layout.row_wahana,
                WahanaViewHolder::class.java,
                wahanaRef
        ){
            override fun populateViewHolder(viewHolder: WahanaViewHolder?, model: WahanaModel?, position: Int) {
                viewHolder?.setGambar(model?.gambar!!)
                viewHolder?.setNama(model?.nama!!)
                viewHolder?.setHarga(model?.harga!!)
                viewHolder?.mView?.setOnClickListener {
                    val id = getRef(position).key
                    val intent = Intent(context, DetailWahanaActivity::class.java)
                    intent.putExtra(DetailWahanaActivity().ID_WAHANA, id)
                    intent.putExtra("status", model?.aktif)
                    startActivity(intent)
                }
                viewHolder?.setStatus(model?.aktif as Boolean)
            }

        }

        v.wahana_recy.adapter = firebaseAdapter

        return v
    }

    class WahanaViewHolder(var mView: View) : RecyclerView.ViewHolder(mView){
        fun setNama(nama: String){
            mView.wahana_title.text = nama
        }

        fun setGambar(gambar: String){
            Glide.with(mView.context).load(gambar).into(mView.wahana_img)
        }

        fun setHarga(harga: Long){
            val locale = Locale("in", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(locale)

            mView.wahana_harga.text = numberFormat.format(harga)
        }

        fun setStatus(status: Boolean) {
            mView.status.text = if (status) "(Tersedia)" else "(Tidak Tersedia)"
        }
    }
}
