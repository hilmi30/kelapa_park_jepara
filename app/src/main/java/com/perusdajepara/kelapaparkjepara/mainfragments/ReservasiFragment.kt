package com.perusdajepara.kelapaparkjepara.mainfragments


import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.FragmentManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.*
import com.perusdajepara.kelapaparkjepara.DatePickerFragment
import com.perusdajepara.kelapaparkjepara.R
import de.cketti.mailto.EmailIntentBuilder
import org.jetbrains.anko.alert
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ReservasiFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    val EMAIL_CODE = 0
    var mDatabaseRef: DatabaseReference? = null
    var mReservasi: DatabaseReference? = null
    var nama: String? = null
    var email: String? = null
    var nomor: String? = null

    var namaPemesan: EditText? = null
    var emailPemesan: EditText? = null
    var noPemesan: EditText? = null
    var nama_wahana: String? = null
    var emailTo: String? = null
    var hari: CharSequence? = null
    var waktu: String? = null
    var choosenDate: String? = null
    var spinnerWaktu: Spinner? = null
    var dateText: TextView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_reservasi, container, false)

        mDatabaseRef = FirebaseDatabase.getInstance().reference
        mReservasi = FirebaseDatabase.getInstance().reference.child("reservasi")

        namaPemesan = v.findViewById(R.id.reservasi_tab_nama)
        emailPemesan = v.findViewById(R.id.reservasi_tab_email)
        noPemesan = v.findViewById(R.id.reservasi_tab_nomor)

        dateText = v.findViewById(R.id.reservasi_frag_hari)

        val emailReceiver = FirebaseDatabase.getInstance().reference.child("email")
        emailReceiver.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(context, "error read value", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                emailTo = p0?.value.toString()
            }

        })

        val spinner = v.findViewById<Spinner>(R.id.reservasi_frag_wahana)
        val spinnerItem = mDatabaseRef?.child("spinner_item")
        spinnerItem?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(context, "error read value", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val dataSpinner = ArrayList<String>()
                for(data in p0?.children!!){
                    for(nama in data.children){
                        val namaWahana = nama.value.toString()
                        dataSpinner.add(namaWahana)
                    }
                }

                val adapter = ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, dataSpinner)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }

        })

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                nama_wahana = p0?.getItemAtPosition(p2).toString()
            }

        }

        spinnerWaktu = v.findViewById(R.id.detail_spinner_waktu)
        spinnerWaktu?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                waktu = p0?.getItemAtPosition(p2).toString()
            }

        }

        val reservasiBtn = v.findViewById<Button>(R.id.reservasi_tab_pesan_btn)
        reservasiBtn.setOnClickListener {
            nama = namaPemesan?.text.toString()
            email = emailPemesan?.text.toString()
            nomor = noPemesan?.text.toString()

            sendEmailToKelapaPark(nama!!, email!!, nomor!!, waktu, choosenDate)
        }

        val tanggalBtn = v.findViewById<Button>(R.id.reservasi_frag_tanggal)
        tanggalBtn.setOnClickListener {
            val datePicker = DatePickerFragment()
            datePicker.show(fragmentManager, "datepicker")
        }

        return v
    }

    @SuppressLint("SimpleDateFormat")
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, p1)
        c.set(Calendar.MONTH, p2)
        c.set(Calendar.DAY_OF_MONTH, p3)
        choosenDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.time)
        dateText?.text = choosenDate

        val dateFormat = SimpleDateFormat("EEEE")
        val day = dateFormat.format(c.time)
        hari = changeDay(day)

        val dataRef = FirebaseDatabase.getInstance().reference.child("waktu").child(nama_wahana)
        val waktuRef = dataRef.child(hari.toString())
        waktuRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(context, "error read value", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val spinnerDataWaktu = ArrayList<String>()
                for(data in p0?.children!!){
                    val value = data.value.toString()
                    spinnerDataWaktu.add(value)
                }

                val adapterWaktu = ArrayAdapter<String>(context,
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

    // KIRIM EMAIL PEMESANAN
    private fun sendEmailToKelapaPark(nama: String, email: String, nomor: String, waktu: String?, date: String?) {
        if(!validate(nama, email, nomor, waktu, date)){
            Toast.makeText(context, "Gagal pesan, silahkan cek form anda", Toast.LENGTH_LONG).show()
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

            val emailIntent = EmailIntentBuilder.from(context)
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

    // HASIL DARI KIRIM EMAIL
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

            context.alert("Terima kasih telah memesan") {
                title("Terima Kasih")
                negativeButton("Tutup"){
                }
            }.show()
        } else {
            Toast.makeText(context, "Pemesanan dibatalkan", Toast.LENGTH_LONG).show()
        }
    }

    // VALIDASI INPUT PEMESANAN
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
        if(waktu!!.isEmpty()){

            Toast.makeText(context, "Waktu tidak boleh kosong", Toast.LENGTH_LONG).show()
        }
        if(date!!.isEmpty()){
            Toast.makeText(context, "Tanggal tidak boleh kosong", Toast.LENGTH_LONG).show()
        }
        return valid
    }
}
