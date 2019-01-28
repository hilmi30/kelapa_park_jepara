package com.perusdajepara.kelapaparkjepara.mainfragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.detail.DetailPromoActivity
import com.perusdajepara.kelapaparkjepara.model.FirebaseModel
import com.perusdajepara.kelapaparkjepara.R


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

        val firebaseAdapter = object : FirebaseRecyclerAdapter<FirebaseModel, PromoViewHolder>(
                FirebaseModel::class.java,
                R.layout.row_promo,
                PromoViewHolder::class.java,
                promo
        ){
            override fun populateViewHolder(viewHolder: PromoViewHolder?, model: FirebaseModel?, position: Int) {
                viewHolder?.setTitle(model?.title!!)
                viewHolder?.view?.setOnClickListener {
                    val id = getRef(position).key
                    val intent = Intent(context, DetailPromoActivity::class.java)
                    intent.putExtra(DetailPromoActivity().PROMO_ID, id)
                    startActivity(intent)
                }
            }

        }

        recy.adapter = firebaseAdapter

        return v
    }

    class PromoViewHolder(var view: View): RecyclerView.ViewHolder(view){
        fun setTitle(title: String){
            val promoTitle = view.findViewById<TextView>(R.id.promo_notif)
            promoTitle.text = title
        }
    }
}
