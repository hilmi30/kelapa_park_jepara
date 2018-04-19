package com.perusdajepara.kelapaparkjepara.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.DatePickerFragment
import com.perusdajepara.kelapaparkjepara.R
import com.perusdajepara.kelapaparkjepara.auth.LoginFragment
import io.paperdb.Paper
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    var namaUser: EditText? = null
    var emailUser: EditText? = null
    var telpUser: EditText? = null
    var tanggalLahir: TextView? = null
    var pilihTanggal: Button? = null
    var rdGroup: RadioGroup? = null
    var passUser: EditText? = null
    var pass: String? = null
    var choosenDate: String? = null

    var mAuth: FirebaseAuth? = null
    var mDatabase: DatabaseReference? = null
    var user: FirebaseUser? = null

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

        pilihTanggal?.setOnClickListener {
            val datePicker = DatePickerFragment()
            datePicker.show(supportFragmentManager, "datepicker")
        }
    }

    override fun onStart() {
        super.onStart()

        user = mAuth?.currentUser
        namaUser?.setText(user?.displayName, TextView.BufferType.EDITABLE)
        emailUser?.setText(user?.email, TextView.BufferType.EDITABLE)

        val telpRef = mDatabase?.child("user")?.child(user?.uid)?.child("telepon")
        telpRef?.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val value = p0?.value.toString()
                telpUser?.setText(value, TextView.BufferType.EDITABLE)
            }
        })

        pass = Paper.book().read(LoginFragment().PASSWORD)
        passUser?.setText(pass, TextView.BufferType.EDITABLE)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, p1)
        c.set(Calendar.MONTH, p2)
        c.set(Calendar.DAY_OF_MONTH, p3)
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
                val selectedRadio = rdGroup?.checkedRadioButtonId
                val radioValue = findViewById<RadioButton>(selectedRadio!!)
                val namaUser = namaUser?.text.toString()
                val tanggalLahir = tanggalLahir?.text.toString()
                val noTelp = telpUser?.text.toString()

                saveUserProfileToFirebase(namaUser, tanggalLahir, radioValue, noTelp, user?.uid)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveUserProfileToFirebase(namaUser: String, tanggalLahir: String?,
                                          radioValue: RadioButton?, noTelp: String, uid: String?) {

    }
}
