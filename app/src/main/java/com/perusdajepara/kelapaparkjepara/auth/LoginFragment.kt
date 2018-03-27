package com.perusdajepara.kelapaparkjepara.auth


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.perusdajepara.kelapaparkjepara.R


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

        val goToResetPass = v.findViewById<TextView>(R.id.login_lupa_password)
        goToResetPass.setOnClickListener {
            toResetPass()
        }

        return v
    }

    private fun toResetPass() {
        val resetFragment = ResetPasswordFragment()
        val fragmentManager = fragmentManager
        val fragmentTrans = fragmentManager.beginTransaction()
        fragmentTrans.setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_left,
                R.anim.slide_in_right, R.anim.slide_out_left)
        fragmentTrans.replace(R.id.auth_container, resetFragment, ResetPasswordFragment::class.java.simpleName)
        fragmentTrans.addToBackStack(null)
        fragmentTrans.commit()
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

}
