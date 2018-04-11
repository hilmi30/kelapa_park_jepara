package com.perusdajepara.kelapaparkjepara.mainfragments


import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.FragmentManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.DatePickerFragment
import com.perusdajepara.kelapaparkjepara.R
import de.cketti.mailto.EmailIntentBuilder
import org.jetbrains.anko.alert
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ReservasiFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_reservasi, container, false)
        return v
    }
}
