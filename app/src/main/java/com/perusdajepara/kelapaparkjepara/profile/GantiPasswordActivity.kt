package com.perusdajepara.kelapaparkjepara.profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.perusdajepara.kelapaparkjepara.R

class GantiPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ganti_password)
        supportActionBar?.title = "Ganti Password"
    }
}
