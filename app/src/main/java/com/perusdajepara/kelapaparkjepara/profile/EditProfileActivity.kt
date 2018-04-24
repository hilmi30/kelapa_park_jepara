package com.perusdajepara.kelapaparkjepara.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.DatePickerCustom
import com.perusdajepara.kelapaparkjepara.DatePickerFragment
import com.perusdajepara.kelapaparkjepara.R
import com.perusdajepara.kelapaparkjepara.auth.LoginFragment
import io.paperdb.Paper
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, OnCompleteListener<Void>, ValueEventListener {

    var namaUser: EditText? = null
    var emailUser: EditText? = null
    var telpUser: EditText? = null
    var tanggalLahir: TextView? = null
    var pilihTanggal: Button? = null
    var rdGroup: RadioGroup? = null
    var passUser: EditText? = null
    var pass: String? = null
    var gender: String? = null
    var choosenDate: String? = null
    var radioValue: Int? = null
    var editLoading: ProgressBar? = null

    var mAuth: FirebaseAuth? = null
    var mDatabase: DatabaseReference? = null
    var user: FirebaseUser? = null
    val GENDER = "gender"
    val LAHIR = "lahir"
    val YEAR = "year"
    val MONTH = "month"
    val DAY = "day"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        supportActionBar?.title = "Edit Profil"

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        namaUser = findViewById(R.id.edit_profile_nama)
        emailUser = findViewById(R.id.edit_profile_email)
        telpUser = findViewById(R.id.edit_profile_telp)
        tanggalLahir = findViewById(R.id.edit_profile_tanggal)
        pilihTanggal = findViewById(R.id.edit_profile_tanggal_btn)
        rdGroup = findViewById(R.id.rd_group_kelamin)
        passUser = findViewById(R.id.edit_profile_password)
        editLoading = findViewById(R.id.edit_profile_loading)

        pass = Paper.book().read(LoginFragment().PASSWORD)
//        gender = Paper.book().read(GENDER)

        rdGroup?.setOnCheckedChangeListener { p0, p1 ->
            when(p1){
                R.id.rd_button_pria -> {
                    radioValue = 1
                }
                R.id.rd_button_wanita -> {
                    radioValue = 0
                }
            }
        }

        pilihTanggal?.setOnClickListener {
            val datePicker = DatePickerCustom()
            datePicker.show(supportFragmentManager, "datepicker")
        }
    }

    override fun onStart() {
        super.onStart()

        user = mAuth?.currentUser
        namaUser?.setText(user?.displayName, TextView.BufferType.EDITABLE)
        emailUser?.setText(user?.email, TextView.BufferType.EDITABLE)

        val userRef = mDatabase?.child("user")?.child(user?.uid)

        val telpRef = userRef?.child("telepon")
        telpRef?.addValueEventListener(this)
        val lahir = Paper.book().read<String>(LAHIR)
        tanggalLahir?.text = lahir

        val genderRef = userRef?.child("gender")
        genderRef?.addValueEventListener(this)
        when(gender){
            "1" -> {
                val rbPria = findViewById<RadioButton>(R.id.rd_button_pria)
                rbPria.isChecked = true
            }
            "0" -> {
                val rbWanita = findViewById<RadioButton>(R.id.rd_button_wanita)
                rbWanita.isChecked = true
            }
        }
    }

    override fun onCancelled(p0: DatabaseError?) {

    }

    override fun onDataChange(p0: DataSnapshot?) {
        val value = p0?.value.toString()
        when(p0?.key){
            "telepon" -> {
                telpUser?.setText(value, TextView.BufferType.EDITABLE)
            }
            "gender" -> {
                gender = value
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, p1)
        Paper.book().write(YEAR, p1)

        c.set(Calendar.MONTH, p2)
        Paper.book().write(MONTH, p2)

        c.set(Calendar.DAY_OF_MONTH, p3)
        Paper.book().write(DAY, p3)
        choosenDate = SimpleDateFormat("dd/MM/yyyy").format(c.time)
        tanggalLahir?.text = choosenDate
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val itemId = item?.itemId

        when(itemId){
            R.id.menu_simpan -> {
                editLoading?.visibility = View.VISIBLE
                val nama = namaUser?.text.toString()
                val tanggalLahir = tanggalLahir?.text.toString()
                val noTelp = telpUser?.text.toString()

                if(nama.isEmpty() || nama == ""){
                    namaUser?.error = "Nama tidak boleh kosong"
                    editLoading?.visibility = View.VISIBLE
                } else {
                    saveUserProfileToFirebase(nama, tanggalLahir, radioValue, noTelp, user?.uid)
                    editLoading?.visibility = View.VISIBLE
                }

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveUserProfileToFirebase(namaUser: String, tanggalLahir: String?,
                                          radioValue: Int?, noTelp: String, uid: String?) {
        val userRef = mDatabase?.child("user")?.child(uid)
        userRef?.child("nama")?.setValue(namaUser)?.addOnCompleteListener(this)
        userRef?.child("telepon")?.setValue(noTelp)
        userRef?.child("gender")?.setValue(radioValue)
//        Paper.book().write(GENDER, radioValue)
        userRef?.child("tanggal_lahir")?.setValue(tanggalLahir)
        Paper.book().write(LAHIR, tanggalLahir)
    }

    override fun onComplete(p0: Task<Void>) {
        if(p0.isSuccessful){
            alert {
                message("Perubahan disimpan")
                negativeButton("Tutup")
            }
        } else {
            Toast.makeText(this, "Terjadi Kesalahan", Toast.LENGTH_LONG).show()
        }
    }
}
