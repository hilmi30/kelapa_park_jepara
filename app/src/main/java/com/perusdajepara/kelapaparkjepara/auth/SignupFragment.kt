package com.perusdajepara.kelapaparkjepara.auth


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.perusdajepara.kelapaparkjepara.R
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.paperdb.Paper
import org.jetbrains.anko.alert

/**
 * A simple [Fragment] subclass.
 */
class SignupFragment : Fragment() {

    var mAuth: FirebaseAuth? = null
    var mDatabase: DatabaseReference? = null
    var nama: EditText? = null
    var email: EditText? = null
    var telp: EditText? = null
    var password: EditText? = null
    var passRepeat: EditText? = null
    var spinLoading: SpinKitView? = null
    var daftarBtn: Button? = null

    val TELP = "telp"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_signup, container, false)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        spinLoading = v.findViewById(R.id.spin_kit)

        nama = v.findViewById(R.id.signup_nama)
        email = v.findViewById(R.id.signup_email)
        telp = v.findViewById(R.id.signup_telp)
        password = v.findViewById(R.id.signup_password)
        passRepeat = v.findViewById(R.id.signup_passrepeat)

        daftarBtn = v.findViewById(R.id.signup_daftar_btn)
        daftarBtn?.setOnClickListener {

            val namaText = nama?.text.toString()
            val emailText = email?.text.toString()
            val telpText = telp?.text.toString()
            val passText = password?.text.toString()
            val passRepeatText = passRepeat?.text.toString()

            createUserWithEmail(namaText, emailText, telpText, passText, passRepeatText)
        }

        return v
    }

    private fun createUserWithEmail(namaText: String, emailText: String, telpText: String, passText: String, passRepeatText: String) {

        hideLoading(false, View.VISIBLE)

        if(!validate(namaText, emailText, telpText, passText, passRepeatText)){

            hideLoading(true, View.GONE)

            Toast.makeText(context, "Gagal daftar, silahkan cek kembali", Toast.LENGTH_LONG).show()

        } else {
            mAuth?.createUserWithEmailAndPassword(emailText, passText)
                    ?.addOnCompleteListener(activity, {
                        if(it.isSuccessful){
                            hideLoading(true, View.GONE)
                            sendEmailVerification(namaText, telpText)
                        } else {
                            hideLoading(true, View.GONE)
                            Toast.makeText(context, "Email yang anda masukkan sudah terdaftar", Toast.LENGTH_LONG).show()
                        }
                    })
        }
    }

    private fun hideLoading(b: Boolean, visible: Int) {
        allIsEnabled(b)
        spinLoading?.visibility = visible
    }

    private fun sendEmailVerification(namaText: String, telpText: String) {

        hideLoading(false, View.VISIBLE)

        val currentUser = mAuth?.currentUser
        if(currentUser != null){

            val email = currentUser.email
            val uid = currentUser.uid
            val isVerified = currentUser.isEmailVerified

            saveUserDataToFirebase(namaText, email, uid, telpText, isVerified)

            // set display name firebase user
            val updateUser = UserProfileChangeRequest.Builder()
                    .setDisplayName(namaText).build()
            currentUser.updateProfile(updateUser)

            currentUser.sendEmailVerification().addOnCompleteListener({
                if(it.isSuccessful){
                    hideLoading(true, View.GONE)
                    activity.alert("Email verifikasi terkirim, silahkan cek email anda") {
                        negativeButton("Tutup"){

                        }
                    }.show()
                } else {
                    hideLoading(true, View.GONE)
                    Toast.makeText(context, "Email verifikasi gagal dikirim", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            hideLoading(true, View.GONE)
            Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveUserDataToFirebase(namaText: String, email: String?, uid: String, telpText: String,
                                       verified: Boolean) {

        val userData = mDatabase?.child("user")?.child(uid)
        userData?.child("nama")?.setValue(namaText)
        userData?.child("email")?.setValue(email)
        userData?.child("uid")?.setValue(uid)
        userData?.child("telepon")?.setValue(telpText)
        userData?.child("isVerified")?.setValue(verified)
    }

    private fun allIsEnabled(bool: Boolean){
        nama?.isEnabled = bool
        email?.isEnabled = bool
        telp?.isEnabled = bool
        password?.isEnabled = bool
        passRepeat?.isEnabled = bool
        daftarBtn?.isEnabled = bool
    }

    private fun validate(namaText: String, emailText: String, telpText: String, passText: String, passRepeatText: String): Boolean {

        var valid = true

        val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()
        val telpValid = android.util.Patterns.PHONE.matcher(telpText).matches()

//        nama
        if(namaText.isEmpty() || namaText == ""){
            nama?.error = "Nama tidak boleh kosong"
            valid = false
        }
//        email
        if(emailText.isEmpty() || emailText == ""){
            email?.error = "Email tidak boleh kosong"
            valid = false
        } else if(!emailValid){
            email?.error = "Email tidak benar"
            valid = false
        }
//        telepon
        if(telpText.isEmpty() || telpText == ""){
            telp?.error = "No telp tidak boleh kosong"
            valid = false
        } else if(!telpValid){
            telp?.error = "No telp tidak benar"
            valid = false
        }
//        password
        if(passText.isEmpty() || passText == ""){
            password?.error = "Password tidak boleh kosong"
            valid = false
        } else if(passText.length < 8){
            password?.error = "Password minimal 8 karakter"
            valid = false
        } else if(!TextUtils.equals(passText, passRepeatText)){
            password?.error = "Password tidak sama"
            passRepeat?.error = "Password tidak sama"
            valid = false
        }
//        password repeat
        if(passRepeatText.isEmpty() || passRepeatText == ""){
            passRepeat?.error = "Password tidak boleh kosong"
            valid = false
        }

        return valid
    }

}
