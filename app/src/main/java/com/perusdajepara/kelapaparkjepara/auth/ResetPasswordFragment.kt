package com.perusdajepara.kelapaparkjepara.auth


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.perusdajepara.kelapaparkjepara.R
import org.jetbrains.anko.alert


/**
 * A simple [Fragment] subclass.
 */
class ResetPasswordFragment : Fragment() {

    var mAuth: FirebaseAuth? = null
    var resetEmail: EditText? = null
    var resetBtn: Button? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_reset_password, container, false)

        mAuth = FirebaseAuth.getInstance()

        resetEmail = v.findViewById(R.id.reset_pass_email)
        resetBtn = v.findViewById(R.id.reset_btn)

        resetBtn?.setOnClickListener {
            val email = resetEmail?.text.toString()
            resetPassEmail(email)
        }

        return v
    }

    private fun resetPassEmail(email: String) {
        val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if(email.isEmpty() || email == "" || !emailValid){
            resetEmail?.error = "Email tidak boleh kosong"
            Toast.makeText(context, "Permintaan gagal", Toast.LENGTH_LONG).show()
        } else {
            mAuth?.sendPasswordResetEmail(email)?.addOnCompleteListener({
                if(it.isSuccessful){
                    activity.alert("Email reset password terkirim, silahkan cek email anda") {
                        negativeButton("Tutup"){
                        }
                    }.show()
                } else {
                    Toast.makeText(activity, "Terjadi kesalahan", Toast.LENGTH_LONG).show()
                }
            })
        }

    }

}
