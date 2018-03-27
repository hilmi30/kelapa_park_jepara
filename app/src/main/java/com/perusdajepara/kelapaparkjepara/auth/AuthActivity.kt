package com.perusdajepara.kelapaparkjepara.auth

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.perusdajepara.kelapaparkjepara.R

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val fragmentManager = supportFragmentManager
        val fragmentTrans = fragmentManager.beginTransaction()
        val loginFragment = LoginFragment()
        fragmentTrans.add(R.id.auth_container, loginFragment, LoginFragment::class.java.simpleName)
        fragmentTrans.commit()
    }
}
