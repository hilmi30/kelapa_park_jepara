package com.perusdajepara.kelapaparkjepara.testing

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.perusdajepara.kelapaparkjepara.R

class PostToFirebaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_to_firebase)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
