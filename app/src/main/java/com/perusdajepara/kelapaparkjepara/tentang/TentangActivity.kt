package com.perusdajepara.kelapaparkjepara.tentang

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.perusdajepara.kelapaparkjepara.R
import java.util.*

class TentangActivity : AppCompatActivity(), ValueEventListener {

    var phone1Tv: TextView? = null
    var phone2Tv: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tentang)

        val mDataRef = FirebaseDatabase.getInstance().reference
        val phone1 = mDataRef.child("phone1")
        phone1.addValueEventListener(this)
        val phone2 = mDataRef.child("phone2")
        phone2.addValueEventListener(this)

        val year = Calendar.getInstance().get(Calendar.YEAR)
        val copy = findViewById<TextView>(R.id.copyright)
        val text = getString(R.string.copyright) + " $year"
        copy.text = text

        phone1Tv = findViewById(R.id.phone1)
        phone2Tv = findViewById(R.id.phone2)

    }

    override fun onCancelled(p0: DatabaseError?) {
        Toast.makeText(this, "Error Read Value", Toast.LENGTH_LONG).show()
    }

    override fun onDataChange(p0: DataSnapshot?) {
        val value = p0?.value.toString()
        when(p0?.key){
            "phone1" -> {
                phone1Tv?.text = value
            }
            "phone2" -> {
                phone2Tv?.text = value
            }
        }
    }
}
