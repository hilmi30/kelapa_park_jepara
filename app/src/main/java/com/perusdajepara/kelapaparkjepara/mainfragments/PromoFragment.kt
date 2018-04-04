package com.perusdajepara.kelapaparkjepara.mainfragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.MainActivity
import com.perusdajepara.kelapaparkjepara.R
import com.perusdajepara.kelapaparkjepara.detail.DetailPaketActivity


/**
 * A simple [Fragment] subclass.
 */
class PromoFragment : Fragment() {

    var mDatabase: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_promo, container, false)

        val recy = v.findViewById<RecyclerView>(R.id.promo_recy)
        recy.layoutManager = LinearLayoutManager(context)
        recy.setHasFixedSize(true)

        mDatabase = FirebaseDatabase.getInstance().reference
        val promo = mDatabase?.child("promo")
        promo?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Log.w(MainActivity().ERROR_READ_VALUE, "error read value", p0?.toException())
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val item = ArrayList<String>()
                for(data in p0?.children!!){
                    val value = data.value.toString()
                    item.add(value)
                }

                val adapter = PromoAdapter(item)
                recy.adapter = adapter
            }

        })

        return v
    }

    class PromoAdapter(var itemData: ArrayList<String>): RecyclerView.Adapter<PromoAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent?.context).inflate(R.layout.row_promo, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return itemData.size
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val data = itemData[position]
            holder?.promoNotif?.text = data
        }

        class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
            val promoNotif = v.findViewById<TextView>(R.id.promo_notif)
        }

    }
}
