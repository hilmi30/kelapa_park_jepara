package com.perusdajepara.kelapaparkjepara.auth


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.perusdajepara.kelapaparkjepara.R


/**
 * A simple [Fragment] subclass.
 */
class ResetPasswordFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_reset_password, container, false)

        return v
    }

}// Required empty public constructor
