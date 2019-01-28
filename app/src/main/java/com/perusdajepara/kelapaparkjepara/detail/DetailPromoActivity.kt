package com.perusdajepara.kelapaparkjepara.detail

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.model.PromoModel
import com.perusdajepara.kelapaparkjepara.R
import kotlinx.android.synthetic.main.activity_detail_promo.*
import org.jetbrains.anko.toast

class DetailPromoActivity : AppCompatActivity() {

    val PROMO_ID = "promo_id"
    var mDatabaseRef: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_promo)
        supportActionBar?.title = "Promo"

        val idPromo = intent.getStringExtra(PROMO_ID)
        mDatabaseRef = FirebaseDatabase.getInstance().reference
        val promoRef = mDatabaseRef?.child("promo/$idPromo")

        promoRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                toast(getString(R.string.terjadi_kesalahan))
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val paketDetail = p0?.getValue(PromoModel::class.java)

                promo_title.text = paketDetail?.title
                Glide.with(this@DetailPromoActivity).load(paketDetail?.gambar).into(promo_img)
                promo_desc.text = paketDetail?.deskripsi
                promo_berlaku.text = paketDetail?.berlaku
            }

        })
    }
}
