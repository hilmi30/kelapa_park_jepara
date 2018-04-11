package com.perusdajepara.kelapaparkjepara.detail

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.MainActivity
import com.perusdajepara.kelapaparkjepara.R
import com.squareup.picasso.Picasso

class DetailPromoActivity : AppCompatActivity(), ValueEventListener {

    val PROMO_ID = "promo_id"
    var mDatabaseRef: DatabaseReference? = null
    val GAMBAR = "gambar"
    val DESKRIPSI = "deskripsi"
    val TITLE = "title"
    val BERLAKU = "berlaku"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_promo)
        supportActionBar?.title = "Promo"

        val idPromo = intent.getStringExtra(PROMO_ID)
        mDatabaseRef = FirebaseDatabase.getInstance().reference
        val promoRef = mDatabaseRef?.child("promo")?.child(idPromo)

        val promoGambar = promoRef?.child(GAMBAR)
        promoGambar?.addValueEventListener(this)
        val promoTitle = promoRef?.child(TITLE)
        promoTitle?.addValueEventListener(this)
        val promoDesc = promoRef?.child(DESKRIPSI)
        promoDesc?.addValueEventListener(this)
        val promoBerlaku = promoRef?.child(BERLAKU)
        promoBerlaku?.addValueEventListener(this)
    }

    override fun onCancelled(p0: DatabaseError?) {
        Log.w(MainActivity().ERROR_READ_VALUE, "Failed to read value.", p0?.toException())
    }

    override fun onDataChange(p0: DataSnapshot?) {
        val value = p0?.value.toString()
        when(p0?.key){
            TITLE -> {
                val title = findViewById<TextView>(R.id.promo_title)
                title.text = value
            }
            GAMBAR -> {
                val gambar = findViewById<ImageView>(R.id.promo_img)
                Glide.with(this).load(value).into(gambar)
                Log.d("gambar", value)
            }
            DESKRIPSI -> {
                val desc = findViewById<TextView>(R.id.promo_desc)
                desc.text = value
            }
            BERLAKU -> {
                val berlaku = findViewById<TextView>(R.id.promo_berlaku)
                berlaku.text = value
            }
        }
    }
}
