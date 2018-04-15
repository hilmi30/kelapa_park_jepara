package com.perusdajepara.kelapaparkjepara.auth


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.perusdajepara.kelapaparkjepara.MainActivity
import com.perusdajepara.kelapaparkjepara.R


/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    var mAuth: FirebaseAuth? = null
    var emailUser: EditText? = null
    var passwordUser: EditText? = null
    var loginBtn: Button? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_login, container, false)

        mAuth = FirebaseAuth.getInstance()

        emailUser = v.findViewById(R.id.login_email)
        passwordUser = v.findViewById(R.id.login_password)
        loginBtn = v.findViewById(R.id.login_btn)
        loginBtn?.setOnClickListener {
            val email = emailUser?.text.toString()
            val password = passwordUser?.text.toString()

            signInWithEmail(email, password)
        }

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

    private fun signInWithEmail(email: String, password: String) {
        if(!validate(email, password)){
            Toast.makeText(context, "Gagal login, silahkan cek kembali", Toast.LENGTH_LONG).show()
        } else {
            mAuth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(activity, {
                        if(it.isSuccessful){
                            checkEmailVerified()
                        } else {
                            Toast.makeText(context, "Login Gagal", Toast.LENGTH_LONG).show()
                        }
                    })
        }
    }

    private fun checkEmailVerified() {
        val user = mAuth?.currentUser
        if (user != null) {
            if(user.isEmailVerified){
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                activity.finish()
            } else {
                Toast.makeText(context, "Email anda belum terverifikasi", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_LONG).show()
        }
    }

    private fun validate(email: String, password: String): Boolean {
        var valid = true

        val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

        if(email.isEmpty() || email == ""){
            emailUser?.error = "Email tidak boleh kosong"
            valid  = false
        } else if(!emailValid){
            emailUser?.error = "Email tidak benar"
            valid = false
        }

        if(password.isEmpty() || password == ""){
            passwordUser?.error = "Password tidak boleh kosong"
            valid = false
        } else if(password.length < 8){
            passwordUser?.error = "Password minimal 8 karakter"
            valid = false
        }

        return valid
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
