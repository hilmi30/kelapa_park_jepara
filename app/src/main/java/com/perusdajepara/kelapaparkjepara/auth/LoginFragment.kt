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
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.perusdajepara.kelapaparkjepara.MainActivity
import com.perusdajepara.kelapaparkjepara.R
import io.paperdb.Paper

class LoginFragment : Fragment() {

    var mAuth: FirebaseAuth? = null
    var mDatabase: DatabaseReference? = null
    var emailUser: EditText? = null
    var passwordUser: EditText? = null
    var loginBtn: Button? = null
    var spinLoading: SpinKitView? = null

    val PASSWORD = "password"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_login, container, false)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        spinLoading = v.findViewById(R.id.spin_kit_login)

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

    private fun allIsEnabled(bool: Boolean){
        emailUser?.isEnabled = bool
        passwordUser?.isEnabled = bool
    }

    private fun hideLoading(b: Boolean, visible: Int) {
        allIsEnabled(b)
        spinLoading?.visibility = visible
    }

    private fun signInWithEmail(email: String, password: String) {

        hideLoading(false, View.VISIBLE)

        if(!validate(email, password)){
            hideLoading(true, View.GONE)
            Toast.makeText(context, "Gagal login, silahkan cek kembali", Toast.LENGTH_LONG).show()
        } else {
            mAuth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(activity, {
                        if(it.isSuccessful){
                            hideLoading(true, View.GONE)
                            checkEmailVerified(password)
                        } else {
                            hideLoading(true, View.GONE)
                            Toast.makeText(context, "Login Gagal", Toast.LENGTH_LONG).show()
                        }
                    })
        }
    }

    private fun checkEmailVerified(password: String) {

        hideLoading(false, View.VISIBLE)

        val user = mAuth?.currentUser
        if (user != null) {
            if(user.isEmailVerified){
                hideLoading(true, View.GONE)

                val userUid = user.uid
                val userData = mDatabase?.child("user")?.child(userUid)
                userData?.child("isVerified")?.setValue(user.isEmailVerified)
                // simpan password untuk validasi edit profil
                Paper.book().write(PASSWORD, password)

                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                activity.finish()
            } else {
                hideLoading(true, View.GONE)
                Toast.makeText(context, "Email anda belum terverifikasi", Toast.LENGTH_LONG).show()
            }
        } else {
            hideLoading(true, View.GONE)
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
