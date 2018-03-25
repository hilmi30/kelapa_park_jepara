package com.perusdajepara.kelapaparkjepara


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_login, container, false)

        val goToSignup = v.findViewById<TextView>(R.id.login_go_to_signup)
        goToSignup.setOnClickListener {
            toSignup()
        }

        return v
    }

    private fun toSignup() {
        val signupFragment = SignupFragment()
        val fragmentManager = fragmentManager
        val fragmentTrans = fragmentManager.beginTransaction()
        fragmentTrans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_out_right, R.anim.slide_in_left)
        fragmentTrans.replace(R.id.auth_container, signupFragment, SignupFragment::class.java.simpleName)
        fragmentTrans.addToBackStack(null)
        fragmentTrans.commit()
    }

}// Required empty public constructor
