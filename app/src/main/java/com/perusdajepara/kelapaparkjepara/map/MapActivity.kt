package com.perusdajepara.kelapaparkjepara.map

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.R

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        supportActionBar?.title = "Peta Kelapa Park Jepara"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mDatabase = FirebaseDatabase.getInstance().reference.child("zoom_map_level")

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!

        val kelapa = LatLng(-6.5741287, 110.7405415)
        mMap.addMarker(MarkerOptions().position(kelapa).title("Kelapa Park Jepara"))
        mDatabase?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(this@MapActivity, "Error when read value", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val value = p0?.value.toString().toFloat()
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kelapa, value))
            }

        })
    }
}
