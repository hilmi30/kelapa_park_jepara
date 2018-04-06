package com.perusdajepara.kelapaparkjepara.reservasi

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.DatePickerFragment
import com.perusdajepara.kelapaparkjepara.MainActivity
import com.perusdajepara.kelapaparkjepara.R
import com.squareup.picasso.Picasso
import de.cketti.mailto.EmailIntentBuilder
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.alert
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReservasiActivity : AppCompatActivity(), ValueEventListener, DatePickerDialog.OnDateSetListener {
    val ID = "wahana_id"
    val KEY = "key"
    val NAMA = "nama"
    val HARGA = "harga"
    val GAMBAR = "gambar"
    val EMAIL_CODE = 0

    var mWahanaRef: DatabaseReference? = null
    var mReservasi: DatabaseReference? = null
    var namaPemesan: EditText? = null
    var emailPemesan: EditText? = null
    var noPemesan: EditText? = null
    var wahanaId: String? = null
    var nama_wahana: String? = null
    var key: String? = null
    var nama: String? = null
    var email: String? = null
    var nomor: String? = null
    var emailTo: String? = null
    var hari: CharSequence? = null
    var waktu: String? = null
    var choosenDate: String? = null
    var spinnerWaktu: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservasi)
        supportActionBar?.title = "Pemesanan"

        wahanaId = intent.getStringExtra(ID)
        key = intent.getStringExtra(KEY)
        mWahanaRef = FirebaseDatabase.getInstance().reference.child(key).child(wahanaId)
        mReservasi = FirebaseDatabase.getInstance().reference.child("reservasi")

        val emailReceiver = FirebaseDatabase.getInstance().reference.child("email")
        emailReceiver.addValueEventListener(this)

        val mWahanaNama = mWahanaRef?.child(NAMA)
        mWahanaNama?.addValueEventListener(this)
        val mWahanaGambar = mWahanaRef?.child(GAMBAR)
        mWahanaGambar?.addValueEventListener(this)
        val mWahanaHarga = mWahanaRef?.child(HARGA)
        mWahanaHarga?.addValueEventListener(this)

        namaPemesan = findViewById(R.id.reservasi_nama_pemesan)
        emailPemesan = findViewById(R.id.reservasi_email_pemesan)
        noPemesan = findViewById(R.id.reservasi_nomor_pemesan)

        spinnerWaktu = findViewById(R.id.detail_spinner_waktu)
        spinnerWaktu?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                waktu = p0?.getItemAtPosition(p2).toString()
            }

        }

        val tanggalBtn = findViewById<Button>(R.id.reservasi_tanggal)
        tanggalBtn.setOnClickListener {
            val datePicker = DatePickerFragment()
            datePicker.show(supportFragmentManager, "datepicker")
        }

        val pesanBtn = findViewById<Button>(R.id.reservasi_pesan_btn)
        pesanBtn.setOnClickListener{
            nama = namaPemesan?.text.toString()
            email = emailPemesan?.text.toString()
            nomor = noPemesan?.text.toString()

            sendEmailToKelapaPark(nama!!, email!!, nomor!!, waktu, choosenDate)
        }

    }

    @SuppressLint("SimpleDateFormat")
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, p1)
        c.set(Calendar.MONTH, p2)
        c.set(Calendar.DAY_OF_MONTH, p3)
        choosenDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.time)
        val dateText = findViewById<TextView>(R.id.reservasi_hari)
        dateText.text = choosenDate

        val dateFormat = SimpleDateFormat("EEEE")
        val day = dateFormat.format(c.time)
        hari = changeDay(day)

        val dataRef = FirebaseDatabase.getInstance().reference.child("waktu").child(wahanaId)
        val waktuRef = dataRef.child(hari.toString())
        waktuRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(this@ReservasiActivity, "error read value", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val spinnerDataWaktu = ArrayList<String>()
                for(data in p0?.children!!){
                    val value = data.value.toString()
                    spinnerDataWaktu.add(value)
                }

                val adapterWaktu = ArrayAdapter<String>(this@ReservasiActivity,
                        R.layout.support_simple_spinner_dropdown_item, spinnerDataWaktu)
                adapterWaktu.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                spinnerWaktu?.adapter = adapterWaktu

            }

        })
    }

    private fun changeDay(day: String?): CharSequence? {
        var hari = day
        when(day){
            "Monday" -> {
                hari = "senin"
            }
            "Tuesday" -> {
                hari = "selasa"
            }
            "Wednesday" -> {
                hari = "rabu"
            }
            "Thursday" -> {
                hari = "kamis"
            }
            "Friday" -> {
                hari = "jumat"
            }
            "Saturday" -> {
                hari = "sabtu"
            }
            "Sunday" -> {
                hari = "minggu"
            }
        }

        return hari?.toLowerCase()
    }

    override fun onCancelled(p0: DatabaseError?) {
        Log.w(MainActivity().ERROR_READ_VALUE, "Error read value")
    }

    override fun onDataChange(p0: DataSnapshot?) {
        when(p0?.key){
            NAMA -> {
                val namaWahana = findViewById<TextView>(R.id.reservasi_title)
                nama_wahana = p0.value.toString()
                namaWahana.text = nama_wahana
            }
            GAMBAR -> {
                val gambarWahana = findViewById<CircleImageView>(R.id.reservasi_circle_img)
                val gambar = p0.value.toString()
                Picasso.get().load(gambar).into(gambarWahana)
            }
            HARGA -> {
                val hargaWahana = findViewById<TextView>(R.id.reservasi_harga)
                val valueStr = p0.value.toString()
                val lengthStr = valueStr.length
                if(valueStr.length >= 4){
                    val result = "Rp" + valueStr.substring(0, lengthStr - 3) + "." + valueStr.substring(lengthStr - 3, lengthStr)
                    hargaWahana?.text = result
                } else {
                    val result = "Rp" + valueStr
                    hargaWahana?.text = result
                }
            }
            "email" -> {
                emailTo = p0.value.toString()
            }
        }
    }

    private fun sendEmailToKelapaPark(nama: String, email: String, nomor: String, waktu: String?, date: String?) {
        if(!validate(nama, email, nomor, waktu, date)){
            Toast.makeText(this, "Gagal pesan, silahkan cek form anda", Toast.LENGTH_LONG).show()
        } else {
            val currentTime = Calendar.getInstance().time
            val body = "Saya ingin memesan ${nama_wahana} di Kelapa Park\n\n" +
                    "Waktu Pemesanan : \n\n" +
                    "Hari/Tanggal : ${date}\n" +
                    "Waktu : ${waktu}\n\n" +
                    "Data Pemesan :\n\n" +
                    "Nama : ${nama}\n" +
                    "Email : ${email}\n" +
                    "No Telepon : ${nomor}\n" +
                    "Created at : ${currentTime}"

            val emailIntent = EmailIntentBuilder.from(this)
                    .to(emailTo.toString())
                    .subject("Pemesanan ${nama_wahana}")
                    .body(body)
                    .build()
            try {
                startActivityForResult(emailIntent, EMAIL_CODE)
            } catch (e: ActivityNotFoundException){
                Log.w("email error", e.toString())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == EMAIL_CODE){
            val currentTime = Calendar.getInstance().time
            val newReservasi = mReservasi?.push()
            newReservasi?.child("nama")?.setValue(nama)
            newReservasi?.child("email")?.setValue(email)
            newReservasi?.child("nomor")?.setValue(nomor)
            newReservasi?.child("item")?.setValue(nama_wahana)
            newReservasi?.child("created_at")?.setValue(currentTime.toString())
            newReservasi?.child("tanggal_pesanan")?.setValue(choosenDate)
            newReservasi?.child("waktu_pesanan")?.setValue(waktu)

            alert("Terima kasih telah memesan") {
                title("Terima Kasih")
                negativeButton("Tutup"){
                }
            }.show()
        } else {
            Toast.makeText(this, "Pemesanan dibatalkan", Toast.LENGTH_LONG).show()
        }
    }

    private fun validate(nama: String, email: String, nomor: String, waktu: String?, date: String?): Boolean {

        var valid = true
        val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val noValid = android.util.Patterns.PHONE.matcher(nomor).matches()

        if(nama.isEmpty() || nama == ""){
            namaPemesan?.setError("Nama harus diisi")
            valid = false
        }
        if(email.isEmpty() || email == ""){
            emailPemesan?.setError("Email tidak boleh kosong")
            valid = false
        }
        if(!emailValid){
            emailPemesan?.setError("Email tidak benar")
            valid = false
        }
        if(nomor.isEmpty() || nomor == ""){
            noPemesan?.setError("Nomor telepon tidak boleh kosong")
            valid = false
        }
        if(!noValid){
            noPemesan?.setError("nomor tidak benar")
            valid = false
        }
        if(waktu == null){
            valid = false
        }
        if(date == null){
            valid = false
        }
        return valid
    }
}
