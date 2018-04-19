package com.perusdajepara.kelapaparkjepara.profile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.auth.FirebaseAuth
import com.perusdajepara.kelapaparkjepara.R
import com.perusdajepara.kelapaparkjepara.auth.AuthActivity
import org.jetbrains.anko.alert

class ProfileActivity : AppCompatActivity() {

    var namaUser: TextView? = null
    var pemesanan: TextView? = null
    var settingUserRecy: RecyclerView? = null
    var mAuth: FirebaseAuth? = null
    var emailUser: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.elevation = 0F
        supportActionBar?.title = "Profil Akun"

        mAuth = FirebaseAuth.getInstance()

        namaUser = findViewById(R.id.nama_user_profile)
        emailUser = findViewById(R.id.user_email_profile)

        pemesanan = findViewById(R.id.riwayat_pemesanan)
        pemesanan?.setOnClickListener {
            val intent = Intent(this, PemesananActivity::class.java)
            startActivity(intent)
        }

        settingUserRecy = findViewById(R.id.setting_user_recy)
        settingUserRecy?.layoutManager = LinearLayoutManager(this)
        settingUserRecy?.setHasFixedSize(true)

        val data = ArrayList<String>()
        data.add("Edit Profil")
        data.add("Ganti Email")
        data.add("Ganti Password")

        val adapter = UserPengaturanAdapter(data)
        settingUserRecy?.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth?.currentUser
        if(currentUser == null || !currentUser.isEmailVerified){
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            namaUser?.text = currentUser.displayName
            emailUser?.text = currentUser.email
        }
    }

    class UserPengaturanAdapter(val settingData: ArrayList<String>): RecyclerView.Adapter<UserPengaturanAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent?.context).inflate(R.layout.row_pengaturan_user, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return settingData.size
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val data = settingData[position]
            holder?.settingItem?.text = data
            holder?.settingItem?.setOnClickListener {
                when(position){
                    0 -> {
                        val view = it.context
                        val intent = Intent(view, EditProfileActivity::class.java)
                        view.startActivity(intent)
                    }
                    1 -> {
                        val view = it.context
                        val intent = Intent(view, GantiPasswordActivity::class.java)
                        view.startActivity(intent)
                    }
                    2 -> {
                        val view = it.context
                        val intent = Intent(view, GantiPasswordActivity::class.java)
                        view.startActivity(intent)
                    }
                }
            }
        }

        class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
            val settingItem = v.findViewById<TextView>(R.id.setting_item)
        }
    }
}
