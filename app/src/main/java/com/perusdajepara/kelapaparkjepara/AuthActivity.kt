package com.perusdajepara.kelapaparkjepara

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

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
