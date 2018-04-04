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
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.perusdajepara.kelapaparkjepara.FirebaseModel
import com.perusdajepara.kelapaparkjepara.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.fabiomsr.moneytextview.MoneyTextView

class MakananFragment : Fragment() {

    var mDatabase: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_makanan, container, false)

        mDatabase = FirebaseDatabase.getInstance().reference.child("resto")

        val makananDatabase = mDatabase?.child("makanan")

        val makananRecy = v.findViewById<RecyclerView>(R.id.makanan_recy)
        makananRecy.layoutManager = LinearLayoutManager(context)
        makananRecy.setHasFixedSize(true)

        val firebaseAdapter = object : FirebaseRecyclerAdapter<FirebaseModel, MakananViewHolder>(
                FirebaseModel::class.java,
                R.layout.row_makanan,
                MakananViewHolder::class.java,
                makananDatabase
        ){
            override fun populateViewHolder(viewHolder: MakananViewHolder?, model: FirebaseModel?, position: Int) {
                viewHolder?.setNama(model?.nama!!)
                viewHolder?.setGambar(model?.gambar!!)
                viewHolder?.setHarga(model?.harga!!)
            }

        }

        makananRecy.adapter = firebaseAdapter

        return v
    }

    class MakananViewHolder(var view: View): RecyclerView.ViewHolder(view){
        fun setNama(nama: String){
            val namaMakanan = view.findViewById<TextView>(R.id.makanan_title)
            namaMakanan.text = nama
        }

        fun setHarga(harga: Int){
            val hargaMakanan = view.findViewById<TextView>(R.id.makanan_harga)
            val hargaStr = harga.toString()
            if(hargaStr.length >= 4){
                val result = "Rp" + hargaStr.substring(0, hargaStr.length - 3) + "." + hargaStr.substring(hargaStr.length - 3, hargaStr.length)
                hargaMakanan?.text = result
            } else {
                val result = "Rp"+harga
                hargaMakanan?.text = result
            }
        }

        fun setGambar(gambar: String){
            val gambarMakanan = view.findViewById<ImageView>(R.id.makanan_img)
            Picasso.get().load(gambar).into(gambarMakanan)
        }
    }
}
